package com.company.iendo.mineui.activity.login.device.search;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.socket.BroadCastReceiveBean;
import com.company.iendo.bean.socket.PutInBean;
import com.company.iendo.bean.socket.PutInDeviceMsgBean;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceSearchAdapter;
import com.company.iendo.mineui.socket.BroadCastDataBean;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/14 9:22
 * desc：设备搜索
 */
public class DeviceSearchActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private InetAddress inetAddress = null;
    private ArrayList<BroadCastReceiveBean> mReceiveList = new ArrayList<>();
    private DeviceSearchAdapter mAdapter;
    private InetAddress mPointInetAddress;
    private int currentItemPosition = -1;
    private ArrayList<String> mReceiveBroadCastList = new ArrayList<>();    //接收线程,获取到广播hexString的数据
    private ArrayList<String> mReceivePointList = new ArrayList<>();    //接收线程,获取到点对点hexString的数据
    private static final int UDP_BroadCast_Over = 112;
    private static final int UDP_Point_Over = 113;
    private static final int UDP_Anim = 114;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UDP_BroadCast_Over: //广播结束
                    showComplete();
                    getAdapterBeanData();
                    //模拟获取到点对点数据回传
//                    String str = "AAC50100CA78EE0700000000000000005618B1F96D92837Ca03399cbe9a32d4786abf24e39d3cad576FC7b226970223a223139322e3136382e36342e3133222c227a7074223a2237373838222c226964223a22726f6f74222c227077223a22726f6f74222c2266726f6d223a2241494f2d454e54222c22737470223a2238303035222c22687074223a2237303031222c2272656d61726b223a2231E58FB7E58685E9959CE5AEA4222c2274797065223a223037222c226574223a2233222c22726574636f6465223a2230227dd5DD==192.168.132.102";
//                    String mRun2End4 = CalculateUtils.getReceiveRun2End4String(str);//随机数之后到data结尾的String
//                    mReceivePointList.add(mRun2End4);
//                    mHandler.sendEmptyMessage(UDP_Point_Over);
                    break;
                case UDP_Point_Over:     //点对点授权结束
//                    EE0700000000000000005618B1F96D92837Ca03399cbe9a32d4786abf24e39d3cad576FC
//                    7b226970223a223139322e3136382e36342e3133222c227a7074223a2237373838222c226964223a22726f6f74222c227077223a22726f6f74222c2266726f6d223a2241494f2d454e54222c22737470223a2238303035222c22687074223a2237303031222c2272656d61726b223a2231E58FB7E58685E9959CE5AEA4222c2274797065223a223037222c226574223a2233222c22726574636f6465223a2230227d
                    toast("存入数据库,并且刷新设备搜索界面");
                    getDataInsertDB();

                    break;
                case UDP_Anim:
                    try {
                        inetAddress = InetAddress.getByName(Constants.BROADCAST_IP);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    //加载动画,发送广播
                    if (!mStatusLayout.isShow()) {
                        showSearchLayout(R.raw.anim_search_loading04, R.string.status_layout_search, new StatusLayout.OnDismissListener() {
                            @Override
                            public void onDismiss(StatusLayout layout) {
                                mHandler.sendEmptyMessage(UDP_BroadCast_Over);
                            }
                        });
                        //开启计时器
                        countDownTimer.start();

                        //发送广播
                        BroadCastDataBean bean = new BroadCastDataBean();
                        bean.setBroadcaster("szcme");                              //设备名字
                        bean.setRamdom(CalculateUtils.getCurrentTimeString());     //时间戳
                        byte[] sendByteData = CalculateUtils.getSendByteData(DeviceSearchActivity.this, mGson.toJson(bean), "FF",
                                "00000000000000000000000000000000", "FD");
                        LogUtils.e("sendByteData====" + sendByteData);
                        //发送广播消息
                        SocketManage.startSendMessageBySocket(sendByteData, inetAddress, Constants.BROADCAST_PORT, true);

                        mHandler.sendEmptyMessageDelayed(UDP_BroadCast_Over, 3000);
                    } else {
                        toast("稍安勿躁,搜索中...");
                    }
                    break;
            }
        }
    };

    //获取数据并且,校验成功,存入设备表
    private void getDataInsertDB() {

        for (int i = 0; i < mReceivePointList.size(); i++) {
            String str = mReceivePointList.get(i);
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=1===" + str);

            String receiveDataStringFromRoom = CalculateUtils.getReceiveDataStringFromRoomForPoint(str);
            String s = receiveDataStringFromRoom.toUpperCase();

            //获取设备码
            String deviceOnlyCodeFromRoom = CalculateUtils.getSendID(str);

            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=2===" + s);
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=deviceOnlyCodeFromRoom===" + deviceOnlyCodeFromRoom);

            PutInDeviceMsgBean bean = mGson.fromJson(CalculateUtils.hexStr2Str(s), PutInDeviceMsgBean.class);

            LogUtils.e("SocketManage回调==模拟数据==PutInDeviceMsgBean.toString==" + bean.toString());
            String retcode = bean.getRetcode();

            if ("0".equals(retcode)) {
                toast("接入成功");
                //存入数据库
                insertData2DB(bean, deviceOnlyCodeFromRoom);
            } else if ("1".equals(retcode)) {
                toast("密码错误");

            } else if ("2".equals(retcode)) {
                toast("不准接入");

            }


        }


    }

    //授权成功存入数据库
    private void insertData2DB(PutInDeviceMsgBean bean, String deviceOnlyCodeFromRoom) {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        //备注,我们确定当前选中设备是通过后去数据库bean的type 字段判断的 (type=一代一体机=endoType=3=扫码的结果对应数字是7)
        switch (bean.getType()) {
            case "07":  //（一代一体机）         扫码的结果对应数字是7
                bean.setType("一代一体机");        //设置设备类型
                break;
            case "8": //（耳鼻喉治疗台）     扫码的结果对应数字是8
                bean.setType("耳鼻喉治疗台");
                break;
            case "9"://（妇科治疗台）                扫码的结果对应数字是9
                bean.setType("妇科治疗台");
                break;
            case "10"://（泌尿治疗台）             扫码的结果对应数字是10
                bean.setType("泌尿治疗台");
                break;
        }
        String tag = bean.getIp() + deviceOnlyCodeFromRoom + bean.getType();
        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
//        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByCode(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
//        DeviceDBBean typeBean = DeviceDBUtils.getQueryBeanByType(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
        LogUtils.e("SocketManage回调==模拟数据==old=设备表长度==" + deviceDBBeans.size());
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==codeBean===" + codeBean);
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==bean.toString()===" + bean.toString());
//        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==typeBean===" + typeBean);
        //获取当前界面被点击的数据item
        BroadCastReceiveBean currentClickItem = mAdapter.getItem(currentItemPosition);

        if (null != codeBean) {  //数据库表存在更新数据即可
            codeBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
            codeBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
            codeBean.setUsername(bean.getId()); //设置直播账号
            codeBean.setPassword(bean.getPw()); //设置直播密码
            codeBean.setDeviceName(bean.getFrom()); //设置设备名称
            codeBean.setIp(bean.getIp());       //设置RTSP直播IP地址；
            codeBean.setLivePort(bean.getZpt());//设置直播端口号；
            codeBean.setSocketPort(bean.getStp());  //设置接收端口；
            codeBean.setMsg(bean.getRemark());      //设置备注-描述信息
            codeBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
            codeBean.setType(bean.getType());
            codeBean.setEndoType(bean.getEt());   //设置科室类型---endoType
            codeBean.setMSelected(false);   //默认未选中
            /**
             * 此处为什么不用bean的ip?
             * 因为校验的是存入广播地址值的ip,所以广播之后存入这个ip做校验
             */
            codeBean.setAcceptAndInsertDB(currentClickItem.getIp() + bean.getEt() + deviceOnlyCodeFromRoom + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性
            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            DeviceDBUtils.update(DeviceSearchActivity.this, codeBean);
            mAdapter.notifyDataSetChanged();
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==更新===" + codeBean.toString());

        } else { //暂无数据添加到数据库
            DeviceDBBean deviceDBBean = new DeviceDBBean();
            deviceDBBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
            deviceDBBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
            deviceDBBean.setUsername(bean.getId()); //设置直播账号
            deviceDBBean.setPassword(bean.getPw()); //设置直播密码
            deviceDBBean.setDeviceName(bean.getFrom()); //设置设备名称
            deviceDBBean.setIp(bean.getIp());       //设置RTSP直播IP地址；
            deviceDBBean.setLivePort(bean.getZpt());//设置直播端口号；
            deviceDBBean.setSocketPort(bean.getStp());  //设置接收端口；
            deviceDBBean.setMsg(bean.getRemark());      //设置备注-描述信息
            deviceDBBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
            deviceDBBean.setType(bean.getType());
            deviceDBBean.setEndoType(bean.getEt());   //设置科室类型---endoType
            deviceDBBean.setMSelected(false);   //默认未选中
            //依次存入ip,endotype,deviceCode,deviceType
            deviceDBBean.setAcceptAndInsertDB(currentClickItem.getIp() + bean.getEt() + deviceOnlyCodeFromRoom + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性

            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.bean.getIp()===" + bean.getIp());//192.168.64.13
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.currentClickItem.getIp()===" + currentClickItem.getIp());//192.168.132.102
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==新增===" + deviceDBBean.toString());

            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            mAdapter.notifyDataSetChanged();
            DeviceDBUtils.insertOrReplace(DeviceSearchActivity.this, deviceDBBean);
        }


//        if (null != codeBean && null != typeBean) {  //数据库表存在更新数据即可
//            codeBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
//            codeBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
//            codeBean.setUsername(bean.getId()); //设置直播账号
//            codeBean.setPassword(bean.getPw()); //设置直播密码
//            codeBean.setDeviceName(bean.getFrom()); //设置设备名称
//            codeBean.setIp(bean.getIp());       //设置RTSP直播IP地址；
//            codeBean.setLivePort(bean.getZpt());//设置直播端口号；
//            codeBean.setSocketPort(bean.getStp());  //设置接收端口；
//            codeBean.setMsg(bean.getRemark());      //设置备注-描述信息
//            codeBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
//            codeBean.setType(bean.getType());
//            codeBean.setEndoType(bean.getEt());   //设置科室类型---endoType
//            codeBean.setMSelected(false);   //默认未选中
//            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
//            currentClickItem.setInDB(true);
//            currentClickItem.setCheckAccess(true);
//            DeviceDBUtils.update(DeviceSearchActivity.this, codeBean);
//            mAdapter.notifyDataSetChanged();
//            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==更新===" + codeBean.toString());
//
//        } else { //暂无数据添加到数据库
//            DeviceDBBean deviceDBBean = new DeviceDBBean();
//            deviceDBBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
//            deviceDBBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
//            deviceDBBean.setUsername(bean.getId()); //设置直播账号
//            deviceDBBean.setPassword(bean.getPw()); //设置直播密码
//            deviceDBBean.setDeviceName(bean.getFrom()); //设置设备名称
//            deviceDBBean.setIp(bean.getIp());       //设置RTSP直播IP地址；
//            deviceDBBean.setLivePort(bean.getZpt());//设置直播端口号；
//            deviceDBBean.setSocketPort(bean.getStp());  //设置接收端口；
//            deviceDBBean.setMsg(bean.getRemark());      //设置备注-描述信息
//            deviceDBBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
//            deviceDBBean.setType(bean.getType());
//            deviceDBBean.setEndoType(bean.getEt());   //设置科室类型---endoType
//            deviceDBBean.setMSelected(false);   //默认未选中
//            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==新增===" + deviceDBBean.toString());
//
//            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
//            currentClickItem.setInDB(true);
//            currentClickItem.setCheckAccess(true);
//            mAdapter.notifyDataSetChanged();
//            DeviceDBUtils.insertOrReplace(DeviceSearchActivity.this, deviceDBBean);
//        }


        List<DeviceDBBean> deviceDBBeanss = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        LogUtils.e("SocketManage回调==模拟数据==new=设备表长度==" + deviceDBBeanss.size());

    }

    /**
     * 把hexString的集合数据转化成BroadCastReceiveBean数据给适配器
     */
    private void getAdapterBeanData() {
        for (int i = 0; i < mReceiveBroadCastList.size(); i++) {
            String str = mReceiveBroadCastList.get(i);
            String receiveDataStringFromRoom = CalculateUtils.getReceiveDataStringFromRoomForBroadCast(str);
            String s = receiveDataStringFromRoom.toUpperCase();
            LogUtils.e("SocketManage回调==模拟数据==mRun2DDString==" + str);
            LogUtils.e("SocketManage回调==模拟数据==deviceType===" + CalculateUtils.getDeviceTypeFromRoom(str));
            LogUtils.e("SocketManage回调==模拟数据==deviceOnlyCode===" + CalculateUtils.getDeviceOnlyCodeFromRoom(str));
            LogUtils.e("SocketManage回调==模拟数据==data===" + s);
            LogUtils.e("SocketManage回调==模拟数据==data=16进制直接转换成为字符串==" + CalculateUtils.hexStr2Str(s));
            //需要先截取==之后的ip
            int startIndex = str.indexOf("==") + 2;
            String ip = str.substring(startIndex);
            LogUtils.e("SocketManage回调==模拟数据==ip==" + ip);
            BroadCastReceiveBean bean = mGson.fromJson(CalculateUtils.hexStr2Str(s), BroadCastReceiveBean.class);
            LogUtils.e("SocketManage回调==广播回调数据Bean==ip==" + bean.toString());

            bean.setSelected(false); //默认都是未选中
            bean.setDeviceType(CalculateUtils.getDeviceTypeFromRoom(str));
            bean.setDeviceCode(CalculateUtils.getReceiveID(str));
            bean.setItemId(CalculateUtils.getDeviceOnlyCodeFromRoom(str));
            bean.setCheckAccess(false); //默认都没校验接入过
            bean.setIp(ip + "");
            bean.setReceiveType(CalculateUtils.getReceiveType(str));    //接收方设备类型
            bean.setReceiveID(CalculateUtils.getReceiveID(str));    //接收方设备类型
            LogUtils.e("SocketManage回调==模拟数据==bean.toString==" + bean.toString());

            mReceiveList.add(bean);
        }
        mAdapter.setData(mReceiveList);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_search;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mRefreshLayout = findViewById(R.id.rl_device_search_refresh);
        mRecyclerView = findViewById(R.id.rv_device_search_recyclerview);

        mAdapter = new DeviceSearchAdapter(this, mRecyclerView, mReceiveList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mReceiveList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DeviceSearchActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
//        7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2230227D
        SocketManage.getSocketManageInstance();
        //必须打开不然再次进入界面 不能能接受收据哦

//        String str = "AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";
//        String str2 = "AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d434e54222c2272656d61726b223a22746f77686f6d65222c22656e646f74797065223a2233222c22616363657074223a2231227db4DD";
//        String str3 = "AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d434e54222c2272656d61726b223a227468726565686f6d65222c22656e646f74797065223a2233222c22616363657074223a2232227db4DD";
//        String mRun2DDString1 = CalculateUtils.getReceiveRun2End4String(str) + "==" + "192.168.130.113";
//        String mRun2DDString2 = CalculateUtils.getReceiveRun2End4String(str2) + "==" + "192.168.132.102";
//        String mRun2DDString3 = CalculateUtils.getReceiveRun2End4String(str3) + "==" + "192.168.132.102";
//        mReceiveBroadCastList.add(mRun2DDString1.toUpperCase()); //直接接入
//        mReceiveBroadCastList.add(mRun2DDString2.toUpperCase()); //直接接入
//        mReceiveBroadCastList.add(mRun2DDString3.toUpperCase()); //直接接入


//        mHandler.sendEmptyMessage(UDP_BroadCast_Over);
    }

    /**
     * 点击了---授权接入
     * accept：是否准许接入 0直接接入 1准许接入，需要密码   2不准接入
     * 0直接接入-->直接存入数据库
     * 0直接接入-->
     * 0直接接入-->
     * 再次发送消息,如果需要授权,授权后再添加到数据库中,不需要授权直接添加到数据库
     * 并且当前添加到设备中的mSelected 设置为true,数据库中其他的设备mSelected为flase
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        //刷新UI界面
//        refreshUIStatus(position);
        //存入数据库
//        saveDataToDB(position);
        /**
         * 校验是否
         */
        this.currentItemPosition = position;
        toast("onItemClick");
        BroadCastReceiveBean item = mAdapter.getItem(position);
        String accept = item.getAccept();
        String receiveType = item.getReceiveType();
        String receiveID = item.getReceiveID();
        String ip = item.getIp();
        LogUtils.e("sendByteData==onItemClick==" + item.toString());
        LogUtils.e("sendByteData==onItemClick==" + ip);

        PutInBean putBean = new PutInBean();
        putBean.setBroadcaster(Constants.BROADCASTER);                              //设备名字
        putBean.setPinAccess(item.getPinAccess());
        putBean.setSpt(Constants.RECEIVE_PORT + "");

        String tag = item.getIp() + item.getEndotype() + item.getDeviceCode() + item.getDeviceType();

        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);

        LogUtils.e("sendByteData==onItemClick===deviceDBBeans==" + deviceDBBeans.size());
//        LogUtils.e("sendByteData==onItemClick===deviceDBBeans==" + deviceDBBeans.get(0).getAcceptAndInsertDB());
        LogUtils.e("sendByteData==onItemClick===codeBean==" + codeBean);
        LogUtils.e("sendByteData==onItemClick===item.toString()==" + item.toString());


        if (null != codeBean) {
            if (tag.equals(codeBean.getAcceptAndInsertDB())) {
                LogUtils.e("sendByteData==onItemClick===DB已经存在==");
                item.setInDB(true);
                mAdapter.notifyDataSetChanged();
                toast("已经存入过数据库了!");
                return;
            } else {
                LogUtils.e("sendByteData==onItemClick===NO存在==");
            }
        }

        if (!item.getCheckAccess()) { //没有被授权登入过
            if ("0".equals(accept)) {
                putBean.setPinAccess("");
                sendSocketPointMsg(putBean, receiveType, receiveID, ip);
            } else if ("1".equals(accept)) {
                checkInPutPassword(ip, position, putBean, receiveType, receiveID);
            } else if ("2".equals(accept)) {
                toast("不准接入");
            }
        } else {
            toast("已经被授权登入过了");
        }


    }

    private void sendSocketPointMsg(PutInBean putBean, String receiveType, String receiveID, String ip) {

        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(putBean), receiveType,
                receiveID, Constants.UDP_FC);
        LogUtils.e("sendByteData==点对点消息数据=数据bean=" + mGson.toJson(putBean));
        LogUtils.e("sendByteData==点对点消息数据==" + sendByteData);
        LogUtils.e("sendByteData==点对点消息===ip==" + ip);
        try {
            mPointInetAddress = InetAddress.getByName(ip);
//            mPointInetAddress = InetAddress.getByName("192.168.132.102");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LogUtils.e("sendByteData==点对点消息===mPointInetAddress==" + mPointInetAddress);

        SocketManage.startSendMessageBySocket(sendByteData, mPointInetAddress, Constants.BROADCAST_PORT, false);


    }


    private void checkInPutPassword(String ip, int position, PutInBean putBean, String receiveType, String receiveID) {

        // 输入对话框
        new InputDialog.Builder(this)
                // 标题可以不用填写
                .setTitle("提示!")
                // 内容可以不用填写
                // 提示可以不用填写
                .setHint("请输入接入密码")
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
//                        //更新这个位置的item数据,校验成功之后更新isCheckAccess字段为true,下次点击不用校验,在Socket回调里面处理
//                        item.setCheckAccess(true);
//                        mAdapter.setItem(position, item);
                        putBean.setPinAccess(content);
                        sendSocketPointMsg(putBean, receiveType, receiveID, ip);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
    }

    private void refreshUIStatus(int position) {
        BroadCastReceiveBean bean = mReceiveList.get(position);
        bean.setSelected(true);
        String string = bean.toString();
        for (int i = 0; i < mReceiveList.size(); i++) {
            BroadCastReceiveBean mBean = mReceiveList.get(i);
            String tag = mBean.toString();
            if (!tag.equals(string)) {//不相等,说明不是当前选择的,把Selected设置为flase
                mBean.setSelected(false);
            } else {
                mBean.setSelected(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initData() {
        mReceiveBroadCastList.clear();
        mReceivePointList.clear();

        SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);
//        设置广播监听
        SocketManage.setOnSocketReceiveListener(new SocketManage.OnSocketReceiveListener() {
            @Override
            public void onSuccess(String str, String ip) {
//                String str = "AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";
                LogUtils.e("SocketManage回调==onSuccess====" + str);
                //此处截取需hexstring
                String mRun2End4 = CalculateUtils.getReceiveRun2End4String(str);//随机数之后到data结尾的String
                String deviceType = CalculateUtils.getDeviceType(str);
                String deviceOnlyCode = CalculateUtils.getDeviceOnlyCode(str);
                String currentCMD = CalculateUtils.getCMD(str);

                LogUtils.e("SocketManage回调==mRun2DDString===" + mRun2End4);
                LogUtils.e("SocketManage回调==ip===" + ip);
                LogUtils.e("SocketManage回调==currentCMD===" + currentCMD);
                LogUtils.e("SocketManage回调==deviceType===" + deviceType);
                LogUtils.e("SocketManage回调==deviceOnlyCode===" + deviceOnlyCode);

                if (Constants.UDP_FD.equals(currentCMD)) {//发送广播消息搜索设备命令
                    if (!mReceiveBroadCastList.contains(mRun2End4 + "==" + ip)) {
//                    //此处获取到Data数据
//                    //转成Bean对象
                        //末尾用==拼接ip
                        mReceiveBroadCastList.add(mRun2End4 + "==" + ip);
                        LogUtils.e("SocketManage回调==mReceiveHexStringList.size()===" + mReceiveBroadCastList.size());
                        LogUtils.e("SocketManage回调==mReceiveHexStringList.size()===" + mReceiveBroadCastList.get(0));

                    }
                } else if (Constants.UDP_FC.equals(currentCMD)) { //授权接入
//                    授权接入模拟的死数据
//               AAC50100CA78EE0700000000000000005618B1F96D92837Ca03399cbe9a32d4786abf24e39d3cad576FC7b226970223a223139322e3136382e36342e3133222c227a7074223a2237373838222c226964223a22726f6f74222c227077223a22726f6f74222c2266726f6d223a2241494f2d454e54222c22737470223a2238303035222c22687074223a2237303031222c2272656d61726b223a2231E58FB7E58685E9959CE5AEA4222c2274797065223a223037222c226574223a2233222c22726574636f6465223a2230227dd5DD
                    if (!mReceivePointList.contains(mRun2End4)) {
                        mReceivePointList.add(mRun2End4 + "==" + ip);
                        LogUtils.e("SocketManage回调==mReceivePointList.size()===" + mReceivePointList.size());
                        //发消息,存入数据库,并且刷新设备搜索界面
                        mHandler.sendEmptyMessage(UDP_Point_Over);


                    }


                }

            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });

        mHandler.sendEmptyMessage(UDP_Anim);
    }

    /**
     * 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后10秒之后会回调onFinish方法
     * 你想在范围[5, 21]之间产生随机数，只需这样：netInt(21 - 5 + 1) + 5;
     */
    private CountDownTimer countDownTimer = new CountDownTimer(1000 * 4, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            //秒转化成 00:00形式一
//            timeView2.setText(formatTime1(millisUntilFinished) + "");
            //秒转化成 00:00形式二
            Log.e("hehehe ", millisUntilFinished + " ");
        }

        @Override
        public void onFinish() {
            //显示主界面
            mHandler.sendEmptyMessage(UDP_BroadCast_Over);

        }
    };

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //暂时不明确
//        SocketManage.setIsRuning(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //暂时不明确
//        SocketManage.setIsRuning(true);


    }
}
