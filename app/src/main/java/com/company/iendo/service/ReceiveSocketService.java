package com.company.iendo.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.company.iendo.bean.UserReloChanged;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.DeleteUserBean;
import com.company.iendo.bean.socket.DeletedPictureBean;
import com.company.iendo.bean.socket.DeletedVideoBean;
import com.company.iendo.bean.socket.MicResponseBean;
import com.company.iendo.bean.socket.RecodeBean;
import com.company.iendo.bean.socket.UpdateCaseBean;
import com.company.iendo.bean.socket.getpicture.ColdPictureBean;
import com.company.iendo.bean.socket.getpicture.EditPictureBean;
import com.company.iendo.bean.socket.getpicture.LookReportBean;
import com.company.iendo.bean.socket.getpicture.PrintReportBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureCallBlackBean;
import com.company.iendo.bean.socket.getpicture.UserIDBean;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.google.gson.Gson;
import com.hjq.gson.factory.GsonFactory;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.AbsWorkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import io.reactivex.disposables.Disposable;


/**
 * 保活的Service通讯服务
 * <p>
 * 一直开启这监听线程,监听Socket
 * <p>
 * 监听线程有两种模式
 * <p>
 * 登入成功之后,监听线程的port 是授权接入成功之后当前设备返回的port
 * <p>
 * 广播是默认的端口,或者自己配置的端口
 */

public class ReceiveSocketService extends AbsWorkService {

    private static final String TAG = "Socket监听===";
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static boolean isFirstIn = false;
    public static Disposable sDisposable;
    private Gson mGson;
    private WifiManager.MulticastLock lock;


    public void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();

    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */
    private volatile static boolean isRuning = true;
    private String mAppIP;


    @Override
    public void startWork(Intent intent, int flags, int startId) {
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock = wifiManager.createMulticastLock("test wifi");
        //用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mAppIP = getIpString(wifiInfo.getIpAddress());
        }
//        sDisposable = Observable
//                .interval(3, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
//                //取消任务时取消定时唤醒
//                .doOnDispose(() -> {
//                    LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--doOnDispose");
//                    cancelJobAlarmSub();
//                })
//                .subscribe(count -> {
//                    LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--每 3 秒采集一次数据... count = " + count);
//                    if (count > 0 && count % 18 == 0)
//                        LogUtils.e("ReceiveSocketService--数据监听服务--接收线程--保存数据到磁盘。 saveCount = " + (count / 18 - 1));
//                });

        /**
         * App启动的时候初始化第一次默认端口线程
         */
        initFirstThread(mAppIP);

    }

    /**
     * eventbus 刷新数据--重启监听线程
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        switch (event.getUdpCmd()) {
            case Constants.UDP_CUSTOM_RESTART://重启监听线程

                //Wifi状态判断
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                lock = wifiManager.createMulticastLock("test wifi");
                //用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩
                if (wifiManager.isWifiEnabled()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    mAppIP = getIpString(wifiInfo.getIpAddress());
                }

                //此处重启监听线程
                MMKV mmkv = MMKV.defaultMMKV();
                boolean b = mmkv.decodeBool(Constants.KEY_Login_Tag);
                String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
                if (b) {//如果是登录状态,重启登入时候的监听
                    ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                    receiveSocketService.setSettingReceiveThread(mAppIP, Integer.parseInt(mSocketPort), getApplicationContext());
                } else {//不是登录状态,重启广播搜索监听
                    ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                    receiveSocketService.initFirstThread(mAppIP);
                }
                break;
        }

    }

    /**
     * 此为接收线程具体解析
     * ip 本地app的ip地址
     * port 本地监听的端口
     */
    public class ReceiveThread extends Thread {
        private int settingReceivePort;
        private int count = 0;
        private String AppIP;
        private Context context;
        DatagramSocket mSettingDataSocket = null;
        DatagramPacket mSettingDataPacket = null;

        public ReceiveThread(String ip, int port, Context context) {
            this.settingReceivePort = port;
            this.AppIP = ip;
            this.context = context;
            mGson = GsonFactory.getSingletonGson();
        }

        public void run() {
            byte[] receiveData = new byte[1024];
            mSettingDataPacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                if (mSettingDataSocket == null) {
                    mSettingDataSocket = new DatagramSocket(null);
                    mSettingDataSocket.setReuseAddress(true);
                    mSettingDataSocket.bind(new InetSocketAddress(settingReceivePort));
                }
            } catch (Exception e) {
                e.printStackTrace();
                SocketRefreshEvent event1 = new SocketRefreshEvent();
                event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
                EventBus.getDefault().postSticky(event1);

            }
            while (true) {
                if (isRuning) {
                    try {
                        LogUtils.e(TAG + "开始,接收数据");
                        //不是自己的IP不接受
                        if (!AppIP.equals(mSettingDataPacket.getAddress())) {
                            //申请开启
//                            lock.acquire();
                            mSettingDataSocket.receive(mSettingDataPacket);
                            int localPort = mSettingDataSocket.getLocalPort();
                            MMKV mmkv = MMKV.defaultMMKV();
                            int stringint = mmkv.decodeInt(Constants.KEY_RECEIVE_PORT);
                            /**
                             * 此处做处理
                             * 实时获取当前本地设置的监听端口和服务器端口是否一致,不一致关闭多余线程,优化性能
                             */
                            if (stringint == mSettingDataPacket.getPort()) {
                                String rec = CalculateUtils.byteArrayToHexString(mSettingDataPacket.getData()).trim();
                                //过滤不是发送给我的消息全部不接受
                                int length = mSettingDataPacket.getLength() * 2;
                                String resultData = rec.substring(0, length);

//                                937a5f204dc43a14                   设备码   数据库存入的
//                                39333761356632303464633433613134   设备码   广播接收到的
                                if (mSettingDataPacket != null) {
                                    String hostAddressIP = mSettingDataPacket.getAddress().getHostAddress();
                                    int port = mSettingDataPacket.getPort();
                                    String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
                                    String deviceType = CalculateUtils.getSendDeviceType(resultData);
                                    String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
                                    String currentCMD = CalculateUtils.getCMD(resultData);
                                    SocketRefreshEvent event = new SocketRefreshEvent();
                                    //设置接收端口
                                    event.setReceivePort(settingReceivePort + "");
                                    Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, context);
                                    String dataString = CalculateUtils.getReceiveDataString(resultData);
                                    LogUtils.e(TAG + "命令CMD==" + currentCMD);
                                    LogUtils.e(TAG + "上位机ip==" + hostAddressIP);
                                    LogUtils.e(TAG + "上位机ip==" + hostAddressIP);
                                    LogUtils.e(TAG + "上位机port==" + port);
                                    LogUtils.e(TAG + "上位机deviceType==" + deviceType);
                                    LogUtils.e(TAG + "上位机deviceCode==" + deviceOnlyCode);
                                    //16进制直接转换成为字符串
                                    String str = CalculateUtils.hexStr2Str(dataString);
                                    LogUtils.e(TAG + "是否接收数据==" + dataIfForMe);
                                    LogUtils.e(TAG + "协议allData==" + resultData);
                                    LogUtils.e(TAG + "协议data==" + dataString);
                                    if (dataIfForMe) {
                                        switch (currentCMD) {
                                            case Constants.UDP_HAND://握手
                                                try {
                                                    LogUtils.e(TAG + "回调==握手");
                                                    //判断数据是否是发个自己的
                                                    Long startTime = System.currentTimeMillis();
                                                    //设备在线握手成功
                                                    event.setTga(true);
                                                    event.setData(startTime + "");
                                                    event.setIp(hostAddressIP);
                                                    event.setReceivePort(settingReceivePort + "");
                                                    event.setUdpCmd(Constants.UDP_HAND);
                                                    EventBus.getDefault().postSticky(event);
//                                                HandService.UDP_HAND_GLOBAL_TAG = true;
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==握手==Exception====");
                                                }
                                                break;

                                            case Constants.UDP_FD: //广播
                                                try {
                                                    LogUtils.e(TAG + "回调==广播");
                                                    event.setTga(true);
                                                    event.setData(resultData);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_FD);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==广播==Exception====");
                                                }
                                                break;
                                            case Constants.UDP_FC://授权接入
                                                try {
                                                    LogUtils.e(TAG + "回调==授权接入");
                                                    //获取到病例的ID是十六进制的,需要转成十进制
                                                    event.setTga(true);
                                                    event.setData(resultData);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_FC);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==授权接入==Exception====");
                                                }

                                                break;
                                            case Constants.UDP_F0://获取当前病例
                                                try {
                                                    LogUtils.e(TAG + "回调==获取当前病例");
                                                    //获取到病例的ID是十六进制的,需要转成十进制
//                                                    String jsonID = CalculateUtils.hex16To10(dataString) + "";
                                                    UserIDBean mUserIDBean = mGson.fromJson(str, UserIDBean.class);
//                                                    LogUtils.e("======GetPictureActivity==回调===CMD=jsonID==" + jsonID);
                                                    String jsonID = CalculateUtils.hex16To10(mUserIDBean.getRecordid()) + "";
                                                    //必须从新取数据不然会错乱
                                                    String spCaseID = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentCaseID);
                                                    if (spCaseID.equals(jsonID)) {
                                                        //id相等才能操作截图等功能
                                                        event.setData("true");
                                                    } else {
                                                        event.setData("false");
                                                    }
                                                    event.setTga(true);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F0);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==获取当前病例==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_F3://冻结与解冻:00冻结，01解冻,未调试
                                                try {
                                                    LogUtils.e(TAG + "回调==冻结与解冻");
                                                    ColdPictureBean mColdBean = mGson.fromJson(str, ColdPictureBean.class);
                                                    String jsonString = CalculateUtils.hex16To10(mColdBean.getFreeze()) + "";
                                                    event.setTga(true);
                                                    event.setData(jsonString);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F3);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==冻结与解冻==Exception====");
                                                }

                                                break;
                                            case Constants.UDP_F1://预览报告
                                                try {
                                                    LogUtils.e(TAG + "回调==预览报告");
                                                    LookReportBean lookBean = mGson.fromJson(str, LookReportBean.class);
                                                    event.setTga(true);
                                                    event.setData(lookBean.getReporturl());
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F1);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==预览报告==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_F2://打印报告
                                                try {
                                                    LogUtils.e(TAG + "回调==打印报告");
                                                    PrintReportBean portBean = mGson.fromJson(str, PrintReportBean.class);
                                                    event.setTga(true);
                                                    event.setData(portBean.getPrintcode());
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F2);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==打印报告==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_18://录像
                                                try {
                                                    LogUtils.e(TAG + "回调==录像");
                                                    RecodeBean recodeBean = mGson.fromJson(str, RecodeBean.class);
                                                    event.setTga(true);
                                                    event.setData(recodeBean.getQrycode());
                                                    event.setIp(CalculateUtils.hex16To10(recodeBean.getRecordid()) + "");//16进制转10进制
                                                    event.setUdpCmd(Constants.UDP_18);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==录像==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_13://更新病例
                                                try {
                                                    LogUtils.e(TAG + "回调==更新病例");
                                                    UpdateCaseBean updateBean = mGson.fromJson(str, UpdateCaseBean.class);
                                                    //hex转成十进制
                                                    String caseID = CalculateUtils.hex16To10(updateBean.getRecordid()) + "";
                                                    event.setTga(true);
                                                    event.setData(caseID);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_13);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==更新病例==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_15://采图
                                                try {
                                                    LogUtils.e(TAG + "回调==采图");
                                                    ShotPictureCallBlackBean pictureCallBlackBean = mGson.fromJson(str, ShotPictureCallBlackBean.class);
                                                    //hex转成十进制
                                                    String picCaseID = CalculateUtils.hex16To10(pictureCallBlackBean.getRecordid()) + "";
                                                    String imageID = CalculateUtils.hex16To10(pictureCallBlackBean.getImageid()) + "";
                                                    event.setTga(true);
                                                    event.setData(picCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_15);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==采图==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_16://删除图片
                                                try {
                                                    LogUtils.e(TAG + "回调==删除图片");
                                                    DeletedPictureBean deletePictureBean = mGson.fromJson(str, DeletedPictureBean.class);
                                                    //hex转成十进制
                                                    String deleteBeanID = CalculateUtils.hex16To10(deletePictureBean.getRecordid()) + "";
                                                    //必须从新取数据不然会错乱
                                                    String mkCurrentID = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentCaseID);
                                                    if (mkCurrentID.equals(deleteBeanID)) {
                                                        //id相等才能操作截图等功能
                                                        event.setData("true");
                                                        event.setData(deleteBeanID);
                                                        event.setIp(hostAddressIP);
                                                        event.setUdpCmd(Constants.UDP_16);
                                                        EventBus.getDefault().postSticky(event);
                                                    }
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==删除图片==Exception==str==" + str);
                                                }
                                                break;
                                            case Constants.UDP_20://删除视频
                                                try {
                                                    LogUtils.e(TAG + "回调==删除视频");
                                                    DeletedVideoBean videoBean = mGson.fromJson(str, DeletedVideoBean.class);
                                                    //hex转成十进制
                                                    String deleteVideoBeanID = CalculateUtils.hex16To10(videoBean.getRecordid()) + "";
                                                    //必须从新取数据不然会错乱
                                                    String mkCurrentVideoID = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentCaseID);
                                                    if (mkCurrentVideoID.equals(deleteVideoBeanID)) {
                                                        //id相等才能操作截图等功能
                                                        event.setData("true");
                                                        event.setData(mkCurrentVideoID);
                                                        event.setIp(hostAddressIP);
                                                        event.setUdpCmd(Constants.UDP_20);
                                                        EventBus.getDefault().postSticky(event);
                                                    }
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==删除视频==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_17://编辑图片
                                                try {
                                                    LogUtils.e(TAG + "回调==编辑图片");
                                                    EditPictureBean editBean = mGson.fromJson(str, EditPictureBean.class);
                                                    //hex转成十进制
                                                    String editCaseID = CalculateUtils.hex16To10(editBean.getRecordid()) + "";
                                                    String editCaseImageID = CalculateUtils.hex16To10(editBean.getImageid()) + "";
                                                    event.setTga(true);
                                                    event.setData(editCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(editCaseImageID);  //此处设置为图片ID
                                                    event.setUdpCmd(Constants.UDP_17);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==编辑图片==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_14://删除病例
                                                try {
                                                    LogUtils.e(TAG + "回调==删除病例");
                                                    DeleteUserBean deleteBean = mGson.fromJson(str, DeleteUserBean.class);
                                                    //hex转成十进制
                                                    String deleteCaseID = CalculateUtils.hex16To10(deleteBean.getRecordid()) + "";
                                                    event.setTga(true);
                                                    event.setData(deleteCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);  //此处设置为图片ID
                                                    event.setUdpCmd(Constants.UDP_14);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==删除病例==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_12://新增病例
                                                try {
                                                    LogUtils.e(TAG + "回调==新增病例");
                                                    DeleteUserBean addBean = mGson.fromJson(str, DeleteUserBean.class);
                                                    //hex转成十进制
                                                    String addBeanCaseID = CalculateUtils.hex16To10(addBean.getRecordid()) + "";
                                                    event.setTga(true);
                                                    event.setData(addBeanCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_12);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==新增病例==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_F4://语音接入
                                                try {
                                                    LogUtils.e(TAG + "回调==语音接入");
                                                    MicResponseBean micResponseBean = mGson.fromJson(str, MicResponseBean.class);
                                                    event.setTga(true);
                                                    event.setData(micResponseBean.getUrl());//传递url
                                                    event.setIp(micResponseBean.getOnline());//传递是否在线(0：离线 1:上线)
                                                    event.setUdpCmd(Constants.UDP_F4);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==语音接入==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_F5://查询设备参数
                                                try {
                                                    LogUtils.e(TAG + "回调==查询设备参数");
                                                    event.setTga(true);
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F5);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==查询设备参数==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_F7://通知权限变动
                                                try {
                                                    LogUtils.e(TAG + "回调==通知权限变动");
                                                    String mLoginUserName = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentLoginUserName);
                                                    UserReloChanged reloBean = mGson.fromJson(str, UserReloChanged.class);
                                                    if (mLoginUserName.equals(reloBean.getUsername())) {
                                                        event.setTga(true);
                                                    } else {
                                                        event.setTga(false);
                                                    }
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F7);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==通知权限变动==Exception==str==" + str);
                                                }

                                                break;
                                            case Constants.UDP_40://刷新医院信息
                                                try {
                                                    LogUtils.e(TAG + "回调==刷新医院信息");
                                                    event.setTga(true);
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_40);
                                                    EventBus.getDefault().postSticky(event);
                                                } catch (Exception e) {
                                                    LogUtils.e(TAG + "回调==刷新医院信息==Exception==str==");
                                                }
                                                break;

                                        }
                                    }
                                    LogUtils.e(TAG + "===!=========================华丽的分割线============================");
                                    LogUtils.e(TAG + "===!=========================华丽的分割线============================");
                                }

                                //及时释放资源不然次数多了会报错
//                            lock.release();
                            } else {
                                SocketRefreshEvent event = new SocketRefreshEvent();
                                event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
                                HandService.UDP_HAND_GLOBAL_TAG = false;
                                event.setData("");
                                event.setData("code=0,监听port端口不一致,退出多余的监听线程!!");
                                EventBus.getDefault().postSticky(event);
                                LogUtils.e(TAG + "code=0,监听port端口不一致,退出多余的监听线程!!");
//                                SocketRefreshEvent event1 = new SocketRefreshEvent();
//                                event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
//                                EventBus.getDefault().postSticky(event1);
                                break;//不相等的直接跳出接收,关闭线程
                            }

                        }

                    } catch (Exception e) {
                        SocketRefreshEvent event1 = new SocketRefreshEvent();
                        event1.setUdpCmd(Constants.UDP_CUSTOM_RESTART);
                        EventBus.getDefault().postSticky(event1);
                        SocketRefreshEvent event = new SocketRefreshEvent();
                        event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
                        event.setData("");
                        //java.lang.NullPointerException:
                        // Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference
                        event.setData("code=1,循环监听异常,错误,退出监听线程!!");
                        LogUtils.e(TAG + "code=1,循环监听异常,错误,退出监听线程!!");
                        MMKV mmkv = MMKV.defaultMMKV();
                        EventBus.getDefault().postSticky(event);
                        HandService.UDP_HAND_GLOBAL_TAG = false;
                        String port = mmkv.decodeString(Constants.KEY_RECEIVE_PORT, "7006");
                        String IP = mmkv.decodeString(Constants.KEY_Device_Ip);
                        e.printStackTrace();
                        break;//捕获到异常之后，执行break跳出循环
                    }

                }
            }


        }
    }

    /**
     * App启动的时候初始化第一次默认端口线程
     *
     * @param currentIP App的ip
     */
    public void initFirstThread(String currentIP) {
        MMKV kv = MMKV.defaultMMKV();
        int mDefaultReceivePort = kv.decodeInt(Constants.KEY_RECEIVE_PORT);
        int mReceivePort = kv.decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
        int mDefaultCastSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
        //是否开启过接收线程,开启过为true,避免初始化的时候创建三个接受线程
        boolean b = kv.decodeBool(Constants.KEY_SOCKET_RECEIVE_FIRST_IN);

        if (!b) {
            LogUtils.e(TAG + "第一次初始化监听服务,ip=:" + currentIP + ",port=" + mReceivePort);
            kv.encode(Constants.KEY_RECEIVE_PORT_BY_SEARCH, mReceivePort); //当前设置的,本地监听端口
            kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);
            kv.encode(Constants.KEY_RECEIVE_PORT, mReceivePort); //设置的,本地监听端口,不管是广播还是通讯都需要设置
            ReceiveThread receiveThread = new ReceiveThread(currentIP, mReceivePort, this);
            receiveThread.start();


        }

    }

    /**
     * 用户设置可广播端口开启的接收线程
     *
     * @param currentIP          本地app的ip
     * @param settingReceivePort 设置的发送接收端口
     * @param context            上下文
     */
    public void setSettingReceiveThread(String currentIP, int settingReceivePort, Context context) {
        //获取当前开启的接收端口
        ReceiveThread receiveThread = new ReceiveThread(currentIP, settingReceivePort, context);
        receiveThread.start();
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);
        kv.encode(Constants.KEY_RECEIVE_PORT, settingReceivePort); //设置的,本地监听端口
        int i = kv.decodeInt(Constants.KEY_RECEIVE_PORT);
        LogUtils.e(TAG + "用户设置,重新开启监听服务,ip=:" + currentIP + ",port=" + settingReceivePort);

    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
    }

    /**
     * 将获取到的int型ip转成string类型
     */
    private static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        isRuning = true;
    }

    @Override
    public void onDestroy() {
        isFirstIn = false;
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

}
