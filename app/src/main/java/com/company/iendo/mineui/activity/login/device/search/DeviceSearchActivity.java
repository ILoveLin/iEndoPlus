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
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.socket.searchdevice.BroadCastReceiveBean;
import com.company.iendo.bean.socket.searchdevice.PutInBean;
import com.company.iendo.bean.socket.searchdevice.PutInDeviceMsgBean;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceSearchAdapter;
import com.company.iendo.mineui.socket.BroadCastDataBean;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private ArrayList<BroadCastReceiveBean> mReceiveList = new ArrayList<>();
    private DeviceSearchAdapter mAdapter;
    private int currentItemPosition = -1;
    private String serverItemIP;
    private TitleBar mTitleBar;
    private ArrayList<String> mReceiveBroadCastList = new ArrayList<>();    //接收线程,获取到广播hexString的数据
    private HashMap<String, String> mReceiveBroadMap = new HashMap<>();    //接收线程,获取到广播hexString的数据
    private ArrayList<String> mReceivePointList = new ArrayList<>();    //接收线程,获取到点对点hexString的数据
    private int isIntUIAdapterCount = 0;
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
                    isIntUIAdapterCount++;
                    LogUtils.e("SocketManage回调==模拟数据==mRun2DDString==" + isIntUIAdapterCount);

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
                        SocketManage.startSendMessageBySocket(sendByteData, Constants.BROADCAST_IP, Constants.BROADCAST_PORT, true);
                    } else {
                        toast("稍安勿躁,搜索中...");
                    }
                    break;
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_search;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mRefreshLayout = findViewById(R.id.rl_device_search_refresh);
        mRecyclerView = findViewById(R.id.rv_device_search_recyclerview);
        mTitleBar = findViewById(R.id.search_titlebar);

        mAdapter = new DeviceSearchAdapter(this, mRecyclerView, mReceiveList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mReceiveList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DeviceSearchActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        mReceiveBroadMap.clear();
        mReceiveBroadCastList.clear();
        mReceivePointList.clear();

//        SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);
//        设置广播监听
        SocketManage.setOnSocketReceiveListener(new SocketManage.OnSocketReceiveListener() {
            @Override
            public void onSuccess(String str, String ip) {
//                String str = "AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";
                LogUtils.e("SocketManage回调==onSuccess====" + str);
                //此处截取需hexstring
                String mRun2End4 = CalculateUtils.getReceiveRun2End4String(str);//随机数之后到data结尾的String
                String deviceType = CalculateUtils.getSendDeviceType(str);
                String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(str);
                String currentCMD = CalculateUtils.getCMD(str);

                LogUtils.e("SocketManage回调==mRun2DDString===" + mRun2End4);
                LogUtils.e("SocketManage回调==ip===" + ip);
                LogUtils.e("SocketManage回调==currentCMD===" + currentCMD);
                LogUtils.e("SocketManage回调==deviceType===" + deviceType);
                LogUtils.e("SocketManage回调==deviceOnlyCode===" + deviceOnlyCode);
                LogUtils.e("SocketManage回调==测试ip==ip==" + ip);
                LogUtils.e("SocketManage回调==测试ip==deviceType==" + deviceType);
                LogUtils.e("SocketManage回调==测试ip==deviceOnlyCode==" + deviceOnlyCode);

                if (Constants.UDP_FD.equals(currentCMD)) {//发送广播消息搜索设备命令
                    //判断是否包含指定的key:设备码+设备类型
                    boolean flag = mReceiveBroadMap.containsKey(deviceOnlyCode + deviceType);
                    LogUtils.e("SocketManage回调==包含===" + flag);

                    if (!flag) {
                        mReceiveBroadMap.put(deviceOnlyCode + deviceType, mRun2End4 + "==" + ip);
                    }

                } else if (Constants.UDP_FC.equals(currentCMD)) { //授权接入
                    if (!mReceivePointList.contains(mRun2End4)) {
                        mReceivePointList.add(mRun2End4 + "==" + ip);
                        LogUtils.e("SocketManage回调==mReceivePointList.size()===" + mReceivePointList.size());
                        LogUtils.e("SocketManage回调==mRun2End4 + + ip===" + mRun2End4 + "==" + ip);
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

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                //发送消息刷新设备界面
                EventBus.getDefault().post(new RefreshEvent("refresh"));

                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {

            }
        });
    }


    /**
     * 广播结束,刷新UI界面
     * 把hexString的集合数据转化成BroadCastReceiveBean数据给适配器
     */
    private void getAdapterBeanData() {
        //此处只做一次广播结束之后界面数据的刷新,不然会出现数据重复
        if (isIntUIAdapterCount != 1) {
            return;
        }
        Iterator<Map.Entry<String, String>> iterator = mReceiveBroadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            LogUtils.e("SocketManage回调==测试ip==遍历key==" + key);
            String value = entry.getValue();
            mReceiveBroadCastList.add(value);
        }
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
        if (mReceiveList.size() == 0) {
            showEmpty();
        } else {
            mAdapter.setData(mReceiveList);
            showComplete();
        }
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

        /**
         * 校验是否
         */
        this.currentItemPosition = position;
        BroadCastReceiveBean item = mAdapter.getItem(position);
        String accept = item.getAccept();
        String receiveType = item.getReceiveType();
        String receiveID = item.getReceiveID();
        serverItemIP = item.getIp();
        LogUtils.e("sendByteData==onItemClick==" + item.toString());
        LogUtils.e("sendByteData==onItemClick==" + serverItemIP);

        PutInBean putBean = new PutInBean();
        //点击对话框的时候在存入pinAccess密码
        putBean.setBroadcaster(Constants.BROADCASTER);                              //设备名字
        putBean.setSpt(Constants.RECEIVE_PORT + "");
        String tag = item.getEndotype() + item.getDeviceCode() + item.getDeviceType();
        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);

        LogUtils.e("sendByteData==onItemClick===deviceDBBeans==" + deviceDBBeans.size());
        LogUtils.e("sendByteData==onItemClick===codeBean==" + codeBean);
        LogUtils.e("sendByteData==onItemClick===item.toString()==" + item.toString());
        LogUtils.e("sendByteData==onItemClick===item.getCheckAccess()==" + item.getCheckAccess());


        if (null != codeBean) {
            if (tag.equals(codeBean.getAcceptAndInsertDB())) {  //数据库有该条数据
                LogUtils.e("sendByteData==onItemClick===DB已经存在==");
                showRefreshDBDataDialog(codeBean, item);
                refreshCurrentUIStatus(position);
                return;
            } else { //数据库没有有该条数据
                LogUtils.e("sendByteData==onItemClick===NO存在==");
            }
        }

        if (!item.getCheckAccess()) { //没有被授权登入过
            if ("0".equals(accept)) {
                putBean.setPinAccess("");
                sendSocketPointMsg(putBean, receiveType, receiveID, serverItemIP);
            } else if ("1".equals(accept)) {
                showPasswordDialog(serverItemIP, position, putBean, receiveType, receiveID);
            } else if ("2".equals(accept)) {
                toast("不准接入");
            }
        } else {
            toast("已经被授权登入过了");
            refreshCurrentUIStatus(position);

        }

    }


    /**
     * 显示刷新数据库已经存在的数据
     *
     * @param codeBean 数据库bean
     * @param bean     当前item的bean
     */

    private void showRefreshDBDataDialog(DeviceDBBean codeBean, BroadCastReceiveBean bean) {

        // 消息对话框
        new MessageDialog.Builder(getActivity())
                // 标题可以不用填写
                .setTitle("提示!")
                // 内容必须要填写
                .setMessage("设备已经存在,是否刷新数据?")
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        refreshDBBeanData(codeBean, bean);

                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();


    }

    /**
     * 数据库存在此条数据,刷新db数据
     *
     * @param codeBean 数据库bean
     * @param bean     当前item的bean
     */
    private void refreshDBBeanData(DeviceDBBean codeBean, BroadCastReceiveBean bean) {
//        BroadCastReceiveBean{title='AIO-ENT', remark='1号内镜室', endotype='3', accept='1', mSelected=false, ip='192.168.131.43', receiveType='07',
//        receiveID='0000000000000000546017FE6BC28949', deviceType='一代一体机',
//        deviceCode='0000000000000000546017FE6BC28949', itemId='F9432B11B93E8BB4AE34539B7472C20E',
//        inDB=null, isCheckAccess=false}
        Long id = codeBean.getId();
        codeBean.setId(id);
        codeBean.setDeviceCode(codeBean.getDeviceCode());  //设置设备码
        codeBean.setDeviceID(codeBean.getDeviceCode());  //设置设备id
        codeBean.setUsername(codeBean.getUsername()); //设置直播账号
        codeBean.setPassword(codeBean.getPassword()); //设置直播密码
        codeBean.setDeviceName(bean.getDeviceType()); //设置设备名称
        codeBean.setIp(bean.getIp());       //设置直播和通讯ip  常用
        codeBean.setLiveIp(codeBean.getLiveIp());       //设置直播ip    不常用
        codeBean.setLivePort(codeBean.getLivePort());//设置直播端口号；
        codeBean.setSocketPort(codeBean.getSocketPort());  //设置接收端口；
        codeBean.setMsg(bean.getRemark());      //设置备注-描述信息
        codeBean.setType_num(bean.getReceiveType());   //设置设备中文说明对应的数字,比如type=一代一体机  数字对应07
        codeBean.setHttpPort(codeBean.getHttpPort());    //设置node js 服务端口  ===httpPort
        codeBean.setType(bean.getDeviceType());
        codeBean.setEndoType(bean.getEndotype());   //设置科室类型---endoType
        codeBean.setAcceptAndInsertDB(codeBean.getEndoType() + codeBean.getDeviceCode() + codeBean.getType());    //存入回调数据bean,标识数据在数据库的唯一性
        //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
        bean.setInDB(true);
        bean.setCheckAccess(true);//授权接入过
//        DeviceDBUtils.update(DeviceSearchActivity.this, codeBean);
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        for (int i = 0; i < deviceDBBeans.size(); i++) {
            DeviceDBBean deviceDBBean = deviceDBBeans.get(i);
            if (id.equals(deviceDBBean.getId())) {
                deviceDBBean.setMSelected(true);   //此处被选中所以设置为true
                DeviceDBUtils.insertOrReplaceInTx(DeviceSearchActivity.this, deviceDBBean);
            } else {
                deviceDBBean.setMSelected(false);//其他的该为未选中状态
                DeviceDBUtils.insertOrReplaceInTx(DeviceSearchActivity.this, deviceDBBean);
            }
        }

        mAdapter.notifyDataSetChanged();
        toast("数据更新完毕!");
    }


    /**
     * 授权接入成功
     * 获取数据并且,校验成功,存入设备表
     */
    private void getDataInsertDB() {

        for (int i = 0; i < mReceivePointList.size(); i++) {
            String str = mReceivePointList.get(i);
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=1=mReceivePointList.size()==" + mReceivePointList.size());
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=1===" + str);

            String receiveDataStringFromRoom = CalculateUtils.getReceiveDataStringFromRoomForPoint(str);
            String s = receiveDataStringFromRoom.toUpperCase();

            //获取设备码
            String deviceOnlyCodeFromRoom = CalculateUtils.getSendID(str);
            String sendDeviceType = CalculateUtils.getSendDeviceType(str);

            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=sendDeviceType===" + sendDeviceType);
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=deviceOnlyCodeFromRoom===" + deviceOnlyCodeFromRoom);
            LogUtils.e("SocketManage回调==模拟数据==获取数据并且=s===" + s);
            String s1 = CalculateUtils.hexStr2Str(s);
            PutInDeviceMsgBean bean = mGson.fromJson(s1, PutInDeviceMsgBean.class);

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

    /**
     * 存入数据库
     *
     * @param bean                   包含了授权成功返回的liveip,一般不用,  当前itembean的 通讯直播ip ,一般使用这个ip
     * @param deviceOnlyCodeFromRoom
     */
    //授权成功存入数据库
    private void insertData2DB(PutInDeviceMsgBean bean, String deviceOnlyCodeFromRoom) {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        //备注,我们确定当前选中设备是通过后去数据库bean的type 字段判断的 (type=一代一体机=扫码的结果对应数字是7)
        switch (bean.getType()) {
            case "07":  //（一代一体机）         扫码的结果对应数字是07
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
        String tag = bean.getEt() + deviceOnlyCodeFromRoom + bean.getType();
        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
//        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByCode(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
//        DeviceDBBean typeBean = DeviceDBUtils.getQueryBeanByType(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
        LogUtils.e("SocketManage回调==模拟数据==old=设备表长度==" + deviceDBBeans.size());
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==tag===" + tag);
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==codeBean===" + codeBean);
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==bean.toString()===" + bean.toString());
        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==bean.toString()==serverItemIP=" + serverItemIP);
//        LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==typeBean===" + typeBean);
        //获取当前界面被点击的数据item
        BroadCastReceiveBean currentClickItem = mAdapter.getItem(currentItemPosition);

        if (null != codeBean) {  //数据库表存在更新数据即可,只针对主键
            Long id = codeBean.getId();
            codeBean.setId(id);
            codeBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
            codeBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
            codeBean.setUsername(bean.getId()); //设置直播账号
            codeBean.setPassword(bean.getPw()); //设置直播密码
            codeBean.setDeviceName(bean.getFrom()); //设置设备名称
            codeBean.setIp(codeBean.getIp());       //设置直播和通讯ip  常用
            codeBean.setLiveIp(currentClickItem.getIp());       //设置直播ip    不常用
            codeBean.setLivePort(bean.getZpt());//设置直播端口号；
            codeBean.setSocketPort(bean.getStp());  //设置接收端口；
            codeBean.setMsg(bean.getRemark());      //设置备注-描述信息
            codeBean.setType_num(getDeviceTypeNum(bean.getType()));   //设置设备中文说明对应的数字,比如type=一代一体机  数字对应07
            codeBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
            codeBean.setType(bean.getType());
            codeBean.setEndoType(bean.getEt());   //设置科室类型---endoType
            codeBean.setMSelected(false);   //默认未选中
            codeBean.setAcceptAndInsertDB(bean.getEt() + deviceOnlyCodeFromRoom + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性
            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            DeviceDBUtils.update(DeviceSearchActivity.this, codeBean);
            mAdapter.notifyDataSetChanged();
            toast("更新=");
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==更新===" + codeBean.toString());

        } else { //暂无数据添加到数据库
            DeviceDBBean deviceDBBean = new DeviceDBBean();
            deviceDBBean.setDeviceCode(deviceOnlyCodeFromRoom);  //设置设备码
            deviceDBBean.setDeviceID(deviceOnlyCodeFromRoom);  //设置设备id
            deviceDBBean.setUsername(bean.getId()); //设置直播账号
            deviceDBBean.setPassword(bean.getPw()); //设置直播密码
            deviceDBBean.setDeviceName(bean.getFrom()); //设置设备名称
            deviceDBBean.setIp(currentClickItem.getIp());       //设置直播和通讯ip  常用
            deviceDBBean.setLiveIp(bean.getIp());       //设置直播ip    不常用
            deviceDBBean.setLivePort(bean.getZpt());//设置直播端口号；
            deviceDBBean.setSocketPort(bean.getStp());  //设置接收端口；
            deviceDBBean.setMsg(bean.getRemark());      //设置备注-描述信息
            deviceDBBean.setHttpPort(bean.getHpt());    //设置node js 服务端口  ===httpPort
            deviceDBBean.setType(bean.getType());       //设置设备中文说明比如比如type=07  中文说明=一代一体机
            deviceDBBean.setType_num(getDeviceTypeNum(bean.getType()));   //设置设备中文说明对应的数字,比如type=一代一体机  数字对应07
            deviceDBBean.setEndoType(bean.getEt());   //设置科室类型---endoType
            deviceDBBean.setMSelected(false);   //默认未选中
            //依次存入endotype,deviceCode,deviceType
            deviceDBBean.setAcceptAndInsertDB(bean.getEt() + deviceOnlyCodeFromRoom + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性

            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.bean.getIp()===" + bean.getIp());//192.168.64.13
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.currentClickItem.getIp()===" + currentClickItem.getIp());//192.168.132.102
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==新增===" + deviceDBBean.toString());
            toast("新增=");

            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            mAdapter.notifyDataSetChanged();
            DeviceDBUtils.insertOrReplace(DeviceSearchActivity.this, deviceDBBean);
        }
        List<DeviceDBBean> deviceDBBeanss = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        LogUtils.e("SocketManage回调==模拟数据==new=设备表长度==" + deviceDBBeanss.size());


    }

    /**
     * 发送点对点消息
     *
     * @param putBean
     * @param receiveType
     * @param receiveID
     * @param ip
     */
    private void sendSocketPointMsg(PutInBean putBean, String receiveType, String receiveID, String ip) {

        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(putBean), receiveType,
                receiveID, Constants.UDP_FC);
        LogUtils.e("sendByteData==点对点消息数据=数据bean=" + mGson.toJson(putBean));
        LogUtils.e("sendByteData==点对点消息数据==" + sendByteData);
        LogUtils.e("sendByteData==点对点消息===ip==" + ip);

        SocketManage.startSendMessageBySocket(sendByteData, ip, Constants.BROADCAST_PORT, false);


    }

    /**
     * 授权对话框
     *
     * @param ip
     * @param position
     * @param putBean
     * @param receiveType
     * @param receiveID
     */
    private void showPasswordDialog(String ip, int position, PutInBean putBean, String receiveType, String receiveID) {
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

    /**
     * 刷新当前界面选中的设备
     *
     * @param position 当前item的位置
     */
    private void refreshCurrentUIStatus(int position) {
        for (int i = 0; i < mReceiveList.size(); i++) {
            BroadCastReceiveBean mBean = mReceiveList.get(i);
            if (position != i) {
                mBean.setSelected(false);
            } else {
                mBean.setSelected(true);
            }

        }
        mAdapter.notifyDataSetChanged();
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


    /**
     * 根据设备名-中文获取对应数字
     * 一代一体机=07
     *
     * @param str
     * @return
     */
    public String getDeviceTypeNum(String str) {
        if ("一代一体机".equals(str)) {
            return "07";
        } else if ("耳鼻喉治疗台".equals(str)) {
            return "8";
        } else if ("妇科治疗台".equals(str)) {
            return "9";
        } else if ("泌尿治疗台".equals(str)) {
            return "10";
        }
        return "07";
    }

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
        SocketManage.setIsRuning(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //暂时不明确
        SocketManage.setIsRuning(true);
        //必须打开不然再次进入界面 不能能接受收据哦
        SocketManage.getSocketManageInstance();
        mReceiveBroadMap.clear();
        mReceiveBroadCastList.clear();
        mReceivePointList.clear();
        SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);

    }
}
