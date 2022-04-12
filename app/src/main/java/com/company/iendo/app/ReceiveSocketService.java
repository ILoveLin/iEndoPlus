package com.company.iendo.app;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.company.iendo.bean.UserReloChanged;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.DeleteUserBean;
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import io.reactivex.disposables.Disposable;


/**
 * 保活的Service通讯服务
 * <p>
 * 一直开启这监听线程,监听Socket
 */

public class ReceiveSocketService extends AbsWorkService {


    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static boolean isFirstIn = false;
    public static Disposable sDisposable;
    private Gson mGson;
    private WifiManager.MulticastLock lock;


    public void stopService() {
        LogUtils.e("保活服务开启------===关闭了");
        LogUtils.e("保活服务开启------===线程=interrupt=");
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();

        LogUtils.e("保活服务开启stopService------ + stopService... stopService = ");


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
        LogUtils.e("保活服务开启====startWork=====startWork");
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock = wifiManager.createMulticastLock("test wifi");
        //用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩

        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mAppIP = getIpString(wifiInfo.getIpAddress());
        }
        LogUtils.e("保活服务开启----AAA--" + Thread.currentThread().getName());
//        sDisposable = Observable
//                .interval(3, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
//                //取消任务时取消定时唤醒
//                .doOnDispose(() -> {
//                    LogUtils.e("保活服务开启------===doOnDispose");
//                    cancelJobAlarmSub();
//                })
//                .subscribe(count -> {
//                    LogUtils.e("保活服务开启------每 3 秒采集一次数据... count = " + count);
//                    if (count > 0 && count % 18 == 0)
//                        LogUtils.e("保活服务开启------保存数据到磁盘。 saveCount = " + (count / 18 - 1));
//                });

        /**
         * App启动的时候初始化第一次默认端口线程
         */
        initFirstThread(mAppIP);

    }

    /**
     * 备注
     *
     *
     * 此时这边需要需要处理两个
     */

    /**
     * 此为接收线程具体解析
     * ip 本地app的ip地址
     * port 本地监听的端口
     */
    public class ReceiveThread extends Thread {
        private int settingReceivePort;
        private int count=0;
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
            LogUtils.e("正在执行Runnable任务,线程名=：%s" + Thread.currentThread().getName());
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
                LogUtils.e("保活服务开启=====退出线程==Exception==new DatagramPacket的时候异常" + e);

            }
            while (true) {
                if (isRuning) {
//                    if (count<15){
                        try {
                            LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的===开始====");
                            //不是自己的IP不接受
                            if (!AppIP.equals(mSettingDataPacket.getAddress())) {
                                //申请开启
//                            lock.acquire();
                                mSettingDataSocket.receive(mSettingDataPacket);
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket==AppIP==" + AppIP);
                                int localPort = mSettingDataSocket.getLocalPort();
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket==localPort==" + localPort);
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getLength()==" + mSettingDataPacket.getLength());
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getLength()==" + mSettingDataPacket.getLength());
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getAddress()==" + mSettingDataPacket.getAddress());
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getHostAddress()==" + mSettingDataPacket.getAddress().getHostAddress());
                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getPort()==" + mSettingDataPacket.getPort());
                                int currentSearchPort = MMKV.defaultMMKV().decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
                                LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的=port==本地监听的端口====" + settingReceivePort);
                                LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的=port==后台通讯的端口====" + mSettingDataPacket.getPort());
                                LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的=port==本地监听的端口====" + currentSearchPort);
                                LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的====AppIP" + AppIP);
                                /**
                                 * 此处做处理
                                 * 实时获取当前本地设置的(搜索)监听端口和服务器端口是否一致,不一致关闭多余线程,优化性能
                                 */
                                if (currentSearchPort == mSettingDataPacket.getPort()) {
                                    LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的====相等");
                                    String rec = CalculateUtils.byteArrayToHexString(mSettingDataPacket.getData()).trim();
                                    //过滤不是发送给我的消息全部不接受
                                    int length = mSettingDataPacket.getLength() * 2;
                                    String resultData = rec.substring(0, length);
                                    LogUtils.e("======LiveServiceImpl=====获取长度==length==" + length);
                                    LogUtils.e("======LiveServiceImpl=====获取长度数据==substring==" + resultData);
                                    LogUtils.e("======LiveServiceImpl=====接受到数据==原始数据====mReceivePacket.getData()=" + mSettingDataPacket.getData());
                                    LogUtils.e("======LiveServiceImpl=====3333==" + mSettingDataPacket.getData());
                                    LogUtils.e("======LiveServiceImpl=====3333==" + mSettingDataPacket.getData());
                                    count++;
                                    LogUtils.e("======LiveServiceImpl==count===count==" + count);

                                    if (mSettingDataPacket != null) {
                                        String hostAddressIP = mSettingDataPacket.getAddress().getHostAddress();
                                        LogUtils.e("======LiveServiceImpl=====mReceivePacket!= flag==" + hostAddressIP);
                                        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
                                        String deviceType = CalculateUtils.getSendDeviceType(resultData);
                                        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
                                        String currentCMD = CalculateUtils.getCMD(resultData);
                                        LogUtils.e("======LiveServiceImpl==回调===随机数之后到data结尾的String=mRun2End4==" + mRun2End4);
                                        LogUtils.e("======LiveServiceImpl==回调===设备类型deviceType==" + deviceType);
                                        LogUtils.e("======LiveServiceImpl==回调===设备ID=deviceOnlyCode==" + deviceOnlyCode);
                                        LogUtils.e("======LiveServiceImpl==回调===CMD=currentCMD==" + currentCMD);
                                        SocketRefreshEvent event = new SocketRefreshEvent();
                                        //设置接收端口
                                        event.setReceivePort(settingReceivePort + "");
                                        Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, context);
                                        String dataString = CalculateUtils.getReceiveDataString(resultData);
                                        //16进制直接转换成为字符串
                                        String str = CalculateUtils.hexStr2Str(dataString);
                                        if (dataIfForMe) {
                                            switch (currentCMD) {
                                                case Constants.UDP_HAND://握手
                                                    LogUtils.e("======LiveServiceImpl==回调===握手==0");
                                                    //判断数据是否是发个自己的
                                                    LogUtils.e("======LiveServiceImpl=====dataIfForMe==" + dataIfForMe);
                                                    //设备在线握手成功
                                                    event.setTga(true);
                                                    event.setData(resultData);
                                                    event.setIp(hostAddressIP);
                                                    event.setReceivePort(settingReceivePort + "");
                                                    event.setUdpCmd(Constants.UDP_HAND);
                                                    EventBus.getDefault().post(event);
                                                    break;

                                                case Constants.UDP_FD: //广播
                                                    LogUtils.e("======LiveServiceImpl==回调===广播==");
                                                    event.setTga(true);
                                                    event.setData(resultData);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_FD);
                                                    EventBus.getDefault().post(event);

                                                    break;
                                                case Constants.UDP_FC://授权接入
                                                    LogUtils.e("======LiveServiceImpl==回调===授权接入==");
                                                    //获取到病例的ID是十六进制的,需要转成十进制
                                                    event.setTga(true);
                                                    event.setData(resultData);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_FC);
                                                    EventBus.getDefault().post(event);

                                                    break;
                                                case Constants.UDP_F0://获取当前病例
                                                    LogUtils.e("======LiveServiceImpl==回调===获取当前病例==");
                                                    //获取到病例的ID是十六进制的,需要转成十进制

                                                    LogUtils.e("======GetPictureActivity==回调===CMD=getReceiveDataString==" + dataString);
//                                                    String jsonID = CalculateUtils.hex16To10(dataString) + "";
                                                    LogUtils.e("======GetPictureActivity==回调===CMD=CalculateUtils.hexStr2Str(dataString)==" + CalculateUtils.hexStr2Str(dataString));
                                                    UserIDBean mUserIDBean = mGson.fromJson(str, UserIDBean.class);
//                                                    LogUtils.e("======GetPictureActivity==回调===CMD=jsonID==" + jsonID);
                                                    String jsonID = CalculateUtils.hex16To10(mUserIDBean.getRecordid()) + "";
                                                    //必须从新取数据不然会错乱
                                                    String spCaseID = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentCaseID);
                                                    LogUtils.e("======GetPictureActivity==回调===itemID=spCaseID=" + spCaseID);
                                                    LogUtils.e("======GetPictureActivity==回调===jsonID=jsonID=" + jsonID);
                                                    if (spCaseID.equals(jsonID)) {
                                                        //id相等才能操作截图等功能
                                                        event.setData("true");
                                                    } else {
                                                        event.setData("false");
                                                    }
                                                    event.setTga(true);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F0);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F3://冻结与解冻:00冻结，01解冻,未调试
                                                    LogUtils.e("======LiveServiceImpl==回调===冻结与解冻==");
                                                    ColdPictureBean mColdBean = mGson.fromJson(str, ColdPictureBean.class);
                                                    String jsonString = CalculateUtils.hex16To10(mColdBean.getFreeze()) + "";
                                                    event.setTga(true);
                                                    event.setData(jsonString);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F3);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F1://预览报告
                                                    LogUtils.e("======LiveServiceImpl==回调===预览报告==" + str);
                                                    LookReportBean lookBean = mGson.fromJson(str, LookReportBean.class);
                                                    LogUtils.e("======LiveServiceImpl==回调===预览报告==" + lookBean.toString());

                                                    event.setTga(true);
                                                    event.setData(lookBean.getReporturl());
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F1);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F2://打印报告
                                                    LogUtils.e("======LiveServiceImpl==回调===打印报告==");
                                                    PrintReportBean portBean = mGson.fromJson(str, PrintReportBean.class);
                                                    event.setTga(true);
                                                    event.setData(portBean.getPrintcode());
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F2);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_18://录像
                                                    LogUtils.e("======LiveServiceImpl==回调===录像==");
                                                    RecodeBean recodeBean = mGson.fromJson(str, RecodeBean.class);
                                                    event.setTga(true);
                                                    event.setData(recodeBean.getQrycode());
                                                    event.setIp(CalculateUtils.hex16To10(recodeBean.getRecordid()) + "");//16进制转10进制
                                                    event.setUdpCmd(Constants.UDP_18);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_13://更新病例
                                                    LogUtils.e("======LiveServiceImpl==回调===更新病例==");
                                                    UpdateCaseBean updateBean = mGson.fromJson(str, UpdateCaseBean.class);
                                                    //hex转成十进制
                                                    String caseID = CalculateUtils.hex16To10(updateBean.getRecordid()) + "";
                                                    event.setTga(true);
                                                    event.setData(caseID);
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_13);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_15://采图
                                                    LogUtils.e("======LiveServiceImpl==回调===采图==");
                                                    ShotPictureCallBlackBean pictureCallBlackBean = mGson.fromJson(str, ShotPictureCallBlackBean.class);
                                                    //hex转成十进制
                                                    String picCaseID = CalculateUtils.hex16To10(pictureCallBlackBean.getRecordid()) + "";
                                                    String imageID = CalculateUtils.hex16To10(pictureCallBlackBean.getImageid()) + "";
                                                    LogUtils.e("======LiveServiceImpl==回调===采图==picCaseID==" + picCaseID);
                                                    LogUtils.e("======LiveServiceImpl==回调===采图==imageID==" + imageID);
                                                    event.setTga(true);
                                                    event.setData(picCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_15);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_17://编辑图片
                                                    LogUtils.e("======LiveServiceImpl==回调===编辑图片==");
                                                    EditPictureBean editBean = mGson.fromJson(str, EditPictureBean.class);
                                                    //hex转成十进制
                                                    String editCaseID = CalculateUtils.hex16To10(editBean.getRecordid()) + "";
                                                    String editCaseImageID = CalculateUtils.hex16To10(editBean.getImageid()) + "";
                                                    LogUtils.e("======LiveServiceImpl==回调===编辑图片==picCaseID==" + editCaseID);
                                                    LogUtils.e("======LiveServiceImpl==回调===编辑图片==imageID==" + editCaseImageID);
                                                    event.setTga(true);
                                                    event.setData(editCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(editCaseImageID);  //此处设置为图片ID
                                                    event.setUdpCmd(Constants.UDP_17);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_14://删除病例
                                                    LogUtils.e("======LiveServiceImpl==回调===删除病例==");
                                                    DeleteUserBean deleteBean = mGson.fromJson(str, DeleteUserBean.class);
                                                    //hex转成十进制
                                                    String deleteCaseID = CalculateUtils.hex16To10(deleteBean.getRecordid()) + "";
                                                    LogUtils.e("======LiveServiceImpl==回调===删除病例==deleteCaseID==" + deleteCaseID);
                                                    event.setTga(true);
                                                    event.setData(deleteCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);  //此处设置为图片ID
                                                    event.setUdpCmd(Constants.UDP_14);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_12://新增病例
                                                    LogUtils.e("======LiveServiceImpl==回调===新增病例==");
                                                    DeleteUserBean addBean = mGson.fromJson(str, DeleteUserBean.class);
                                                    //hex转成十进制
                                                    String addBeanCaseID = CalculateUtils.hex16To10(addBean.getRecordid()) + "";
                                                    LogUtils.e("======LiveServiceImpl==回调===删除病例==deleteCaseID==" + addBeanCaseID);
                                                    event.setTga(true);
                                                    event.setData(addBeanCaseID);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_12);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F4://语音接入
                                                    LogUtils.e("======LiveServiceImpl==回调===语音接入==");
                                                    MicResponseBean micResponseBean = mGson.fromJson(str, MicResponseBean.class);
                                                    event.setTga(true);
                                                    event.setData(micResponseBean.getUrl());//传递url
                                                    event.setIp(micResponseBean.getOnline());//传递是否在线(0：离线 1:上线)
                                                    event.setUdpCmd(Constants.UDP_F4);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F5://查询设备参数
                                                    LogUtils.e("======LiveServiceImpl==回调===设备参数下发==" + str);
                                                    event.setTga(true);
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F5);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_F7://通知权限变动
                                                    LogUtils.e("======LiveServiceImpl==回调===通知权限变动==");
                                                    String  mLoginUserName = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentLoginUserName);
                                                    UserReloChanged reloBean = mGson.fromJson(str, UserReloChanged.class);
                                                    if (mLoginUserName.equals(reloBean.getUsername())) {
                                                        event.setTga(true);
                                                    } else {
                                                        event.setTga(false);
                                                    }
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_F7);
                                                    EventBus.getDefault().post(event);
                                                    break;
                                                case Constants.UDP_40://刷新医院信息
                                                    LogUtils.e("======LiveServiceImpl==回调===刷新医院信息==");
                                                    event.setTga(true);
                                                    event.setData(str);//此处直接把数据bean的string回传到GetPictureActivity界面
                                                    event.setIp(hostAddressIP);
                                                    event.setUdpCmd(Constants.UDP_40);
                                                    EventBus.getDefault().post(event);
                                                    break;

                                            }
                                        }
                                    }

                                    //及时释放资源不然次数多了会报错
//                            lock.release();
                                } else {
                                    SocketRefreshEvent event = new SocketRefreshEvent();
                                    event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
                                    event.setData("错误码==00===监听port端口不一致,退出监听线程!!");
                                    EventBus.getDefault().post(event);
                                    /**
                                     * 用户设置可广播端口开启的接收线程
                                     *
                                     * @param currentIP          本地app的ip
                                     * @param settingReceivePort 设置的发送接收端口
                                     * @param context            上下文
                                     */
                                    MMKV mmkv = MMKV.defaultMMKV();
                                    String port = mmkv.decodeString(Constants.KEY_RECEIVE_PORT, "7006");
                                    String IP = mmkv.decodeString(Constants.KEY_Device_Ip);
                                    setSettingReceiveThread(IP,Integer.parseInt(port),context);
                                    LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的==不!==相等");
                                    LogUtils.e("保活服务开启======LiveServiceImpl==回调==Thread监听的==不!==相等==线程名=：%s" + Thread.currentThread().getName() + ",关闭线程");
                                    break;//不相等的直接跳出接收,关闭线程
                                }

                            }

                        } catch (Exception e) {
                            SocketRefreshEvent event = new SocketRefreshEvent();
                            event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
                            event.setData("错误码==11===循环监听错误,退出监听线程!!");
                            EventBus.getDefault().post(event);
                            LogUtils.e("保活服务开启=====退出线程==Exception==while循环处理消息的时候异常" + e);
                            MMKV mmkv = MMKV.defaultMMKV();
                            String port = mmkv.decodeString(Constants.KEY_RECEIVE_PORT, "7006");
                            String IP = mmkv.decodeString(Constants.KEY_Device_Ip);
                            setSettingReceiveThread(IP,Integer.parseInt(port),context);
                            e.printStackTrace();
                            break;//捕获到异常之后，执行break跳出循环
                        }
//                    }else {
//                        SocketRefreshEvent event = new SocketRefreshEvent();
//                        event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
//                        event.setData("错误码==11===循环超过15次,退出监听线程!!,并且重启");
//                        EventBus.getDefault().post(event);
//                        LogUtils.e("保活服务开启=====退出线程==Exception==while循环处理消息的时候异常" );
//                        MMKV mmkv = MMKV.defaultMMKV();
//                        String port = mmkv.decodeString(Constants.KEY_RECEIVE_PORT, "7006");
//                        String IP = mmkv.decodeString(Constants.KEY_Device_Ip);
//                        setSettingReceiveThread(IP,Integer.parseInt(port),context);
//                        break;//捕获到异常之后，执行break跳出循环
//                    }

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

        LogUtils.e("保活服务开启-startWork--原本port---===i===" + mDefaultReceivePort);
        LogUtils.e("保活服务开启-startWork--原本广播port---===i===" + mDefaultCastSendPort);
        LogUtils.e("保活服务开启-startWork---b---===mReceivePort===" + mReceivePort);


        if (!b) {
            ReceiveThread receiveThread = new ReceiveThread(currentIP, mReceivePort, this);
            receiveThread.start();
            kv.encode(Constants.KEY_RECEIVE_PORT_BY_SEARCH, mReceivePort); //当前设置的,本地监听端口
            kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);


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
        LogUtils.e("保活服务开启-My-startWork---bbAA---===bbAA=00==");
        LogUtils.e("保活服务开启-My-startWork---bbAA---===bbAA=00==" + settingReceivePort);
        ReceiveThread receiveThread = new ReceiveThread(currentIP, settingReceivePort, context);
        receiveThread.start();

        MMKV kv = MMKV.defaultMMKV();
        kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);
        kv.encode(Constants.KEY_RECEIVE_PORT, settingReceivePort); //设置的,本地监听端口
        int i = kv.decodeInt(Constants.KEY_RECEIVE_PORT);
        LogUtils.e("保活服务开启-My-startWork---bbAA---===bbAA=i==" + i);


    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        LogUtils.e("保活服务开启===关闭了====stopWork。");
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
        LogUtils.e("保活服务===保存数据到磁盘===onServiceKilled。");
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
        isRuning = true;
    }

    @Override
    public void onDestroy() {
        isFirstIn = false;
        LogUtils.e("保活服务==保存数据到磁盘===onDestroy。");
        super.onDestroy();

    }


    /**
     * 老版,不能更改端口的模式--->开启消息接收线程
     */
//    private void initReceiveThread() {
//        //获取接收线程,设置了最高优先级10
//        mReceiveThread = ThreadManager.getIO();
//        Runnable mReceiveRunnable = new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
//                byte[] receiveData = new byte[1024];
//                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
//                try {
//                    if (mReceiveSocket == null) {
////                        mReceiveSocket = new DatagramSocket(Constants.RECEIVE_PORT);  //端口绑定异常
//                        mReceiveSocket = new DatagramSocket(null);
//                        mReceiveSocket.setReuseAddress(true);
//                        mReceiveSocket.bind(new InetSocketAddress(Constants.RECEIVE_PORT));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                while (true) {
//                    if (isRuning) {
//                        try {
//                            LogUtils.e("======LiveServiceImpl=====000==");
//                            if (!currentIP.equals(mReceivePacket.getAddress())) {   //不是自己的IP不接受
//                                mReceiveSocket.receive(mReceivePacket);
//                                String hostAddress = getOutboundAddress(mReceivePacket.getSocketAddress()).getHostAddress();
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket==currentIP==" + currentIP);
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getLength()==" + mReceivePacket.getLength());
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.hostAddress()==" + hostAddress);
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getAddress()==" + mReceivePacket.getAddress());
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getHostAddress()==" + mReceivePacket.getAddress().getHostAddress());
//                                LogUtils.e("======LiveServiceImpl=====mReceivePacket.getPort()==" + mReceivePacket.getPort());
//                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
////                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
//                                //过滤不是发送给我的消息全部不接受
//                                int length = mReceivePacket.getLength() * 2;
//                                String resultData = rec.substring(0, length);
//                                LogUtils.e("======LiveServiceImpl=====获取长度==length==" + length);
//                                LogUtils.e("======LiveServiceImpl=====获取长度数据==substring==" + resultData);
//                                LogUtils.e("======LiveServiceImpl=====接受到数据==原始数据====mReceivePacket.getData()=" + mReceivePacket.getData());
//                                LogUtils.e("======LiveServiceImpl=====3333==" + mReceivePacket.getData());
//                                if (mReceivePacket != null) {
//                                    String hostAddressIP = mReceivePacket.getAddress().getHostAddress();
//                                    LogUtils.e("======LiveServiceImpl=====mReceivePacket!= flag==" + hostAddressIP);
//                                    String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
//                                    String deviceType = CalculateUtils.getSendDeviceType(resultData);
//                                    String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
//                                    String currentCMD = CalculateUtils.getCMD(resultData);
//                                    LogUtils.e("======LiveServiceImpl==回调===随机数之后到data结尾的String=mRun2End4==" + mRun2End4);
//                                    LogUtils.e("======LiveServiceImpl==回调===设备类型deviceType==" + deviceType);
//                                    LogUtils.e("======LiveServiceImpl==回调===设备ID=deviceOnlyCode==" + deviceOnlyCode);
//                                    LogUtils.e("======LiveServiceImpl==回调===CMD=currentCMD==" + currentCMD);
//                                    SocketRefreshEvent event = new SocketRefreshEvent();
//                                    Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, getApplicationContext());
//                                    String dataString = CalculateUtils.getReceiveDataString(resultData);
//                                    //16进制直接转换成为字符串
//                                    String str = CalculateUtils.hexStr2Str(dataString);
//                                    if (dataIfForMe) {
//                                        switch (currentCMD) {
//                                            case Constants.UDP_HAND://握手
//                                                LogUtils.e("======LiveServiceImpl==回调===握手==");
//                                                //判断数据是否是发个自己的
//                                                LogUtils.e("======LiveServiceImpl=====dataIfForMe==" + dataIfForMe);
//                                                //设备在线握手成功
//                                                event.setTga(true);
//                                                event.setData(resultData);
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_HAND);
//                                                EventBus.getDefault().post(event);
//                                                break;
//
//                                            case Constants.UDP_FD: //广播
//                                                LogUtils.e("======LiveServiceImpl==回调===广播==");
//                                                event.setTga(true);
//                                                event.setData(resultData);
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_FD);
//                                                EventBus.getDefault().post(event);
//
//                                                break;
//                                            case Constants.UDP_FC://授权接入
//                                                LogUtils.e("======LiveServiceImpl==回调===授权接入==");
//                                                //获取到病例的ID是十六进制的,需要转成十进制
//                                                event.setTga(true);
//                                                event.setData(resultData);
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_FC);
//                                                EventBus.getDefault().post(event);
//
//                                                break;
//                                            case Constants.UDP_F0://获取当前病例
//                                                LogUtils.e("======LiveServiceImpl==回调===获取当前病例==");
//                                                //获取到病例的ID是十六进制的,需要转成十进制
//
//                                                LogUtils.e("======GetPictureActivity==回调===CMD=getReceiveDataString==" + dataString);
////                                                    String jsonID = CalculateUtils.hex16To10(dataString) + "";
//                                                LogUtils.e("======GetPictureActivity==回调===CMD=CalculateUtils.hexStr2Str(dataString)==" + CalculateUtils.hexStr2Str(dataString));
//                                                UserIDBean mUserIDBean = mGson.fromJson(str, UserIDBean.class);
////                                                    LogUtils.e("======GetPictureActivity==回调===CMD=jsonID==" + jsonID);
//                                                String jsonID = CalculateUtils.hex16To10(mUserIDBean.getRecordid()) + "";
//                                                //必须从新取数据不然会错乱
//                                                String spCaseID = (String) SharePreferenceUtil.get(getApplicationContext(), SharePreferenceUtil.Current_Chose_CaseID, "");
//                                                LogUtils.e("======GetPictureActivity==回调===itemID=spCaseID=" + spCaseID);
//                                                LogUtils.e("======GetPictureActivity==回调===jsonID=jsonID=" + jsonID);
//                                                if (spCaseID.equals(jsonID)) {
//                                                    //id相等才能操作截图等功能
//                                                    event.setData("true");
//                                                } else {
//                                                    event.setData("false");
//                                                }
//                                                event.setTga(true);
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_F0);
//                                                EventBus.getDefault().post(event);
//                                                break;
//                                            case Constants.UDP_F3://冻结与解冻:00冻结，01解冻,未调试
//                                                LogUtils.e("======LiveServiceImpl==回调===冻结与解冻==");
//                                                ColdPictureBean mColdBean = mGson.fromJson(str, ColdPictureBean.class);
//                                                String jsonString = CalculateUtils.hex16To10(mColdBean.getFreeze()) + "";
//                                                event.setTga(true);
//                                                event.setData(jsonString);
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_F3);
//                                                EventBus.getDefault().post(event);
//                                                break;
//                                            case Constants.UDP_F1://预览报告
//                                                LogUtils.e("======LiveServiceImpl==回调===预览报告==" + str);
//                                                LookReportBean lookBean = mGson.fromJson(str, LookReportBean.class);
//                                                LogUtils.e("======LiveServiceImpl==回调===预览报告==" + lookBean.toString());
//
//                                                event.setTga(true);
//                                                event.setData(lookBean.getReporturl());
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_F1);
//                                                EventBus.getDefault().post(event);
//                                                break;
//                                            case Constants.UDP_F2://打印报告
//                                                LogUtils.e("======LiveServiceImpl==回调===打印报告==");
//                                                PrintReportBean portBean = mGson.fromJson(str, PrintReportBean.class);
//                                                event.setTga(true);
//                                                event.setData(portBean.getPrintcode());
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_F2);
//                                                EventBus.getDefault().post(event);
//                                                break;
//                                            case Constants.UDP_18://录像
//                                                LogUtils.e("======LiveServiceImpl==回调===录像==");
//                                                RecodeBean recodeBean = mGson.fromJson(str, RecodeBean.class);
//                                                event.setTga(true);
//                                                event.setData(recodeBean.getQrycode());
//                                                event.setIp(hostAddressIP);
//                                                event.setUdpCmd(Constants.UDP_18);
//                                                EventBus.getDefault().post(event);
//                                                break;
//
//
//                                        }
//                                    }
//                                }
//                            }
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//        };
//
//        mReceiveThread.execute(mReceiveRunnable);
//
//    }
}
