package com.company.iendo.mineui.activity.login.device.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.service.ReceiveSocketService;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.searchdevice.BroadCastReceiveBean;
import com.company.iendo.bean.socket.searchdevice.PutInBean;
import com.company.iendo.bean.socket.searchdevice.PutInDeviceMsgBean;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceSearchAdapter;
import com.company.iendo.bean.socket.BroadCastDataBean;
import com.company.iendo.other.Constants;
import com.company.iendo.ui.dialog.Input2SettingDialog;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.king.view.circleprogressview.CircleProgressView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    //    private TitleBar mTitleBar;
    private ArrayList<String> mReceiveBroadCastList = new ArrayList<>();    //接收线程,获取到广播hexString的数据,之后在存给mReceiveList,设置界面adapter
    private HashMap<String, String> mReceiveBroadMap = new HashMap<>();    //判断是否包含指定的key:设备码+设备类型,value是hexstring数据
    private ArrayList<String> mReceivePointList = new ArrayList<>();    //授权之后存入的数据,获取到点对点hexString的数据
    private static final int UDP_BroadCast_Over = 112;
    private static final int UDP_Point_Over = 113;
    private static final int UDP_Anim = 114;
    private static Boolean Set_Data = false;
    private static Boolean CancleSearchDialog = false;   //主动取消搜索动画框
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UDP_BroadCast_Over: //广播结束
                    getAdapterBeanData();
                    //模拟获取到点对点数据回传
                    break;
                case UDP_Point_Over:     //点对点授权结束
//                    toast("存入数据库,并且刷新设备搜索界面");
                    getDataInsertDB();
                    break;
                case UDP_Anim:
                    showSearchDialog();
                    break;
            }
        }
    };


    private BaseDialog.Builder<BaseDialog.Builder<?>> mSearchDialog;
    private ReceiveSocketService receiveSocketService;
    private TextView statusBarView;

    private void showSearchDialog() {

        mReceiveList.clear();   //界面列表的list
        mReceiveBroadMap.clear();//广播接收到的数据map
        mReceivePointList.clear();//授权的list
        mReceiveBroadCastList.clear();//界面列表的list的中转list
        Set_Data = false;
        showEmpty();
        // 自定义搜索对话框
        mSearchDialog = new BaseDialog.Builder<>(this);
        mSearchDialog.setContentView(R.layout.dialog_search)
                .setCanceledOnTouchOutside(false)
                .setAnimStyle(BaseDialog.ANIM_SCALE)
                //.setText(id, "我是预设置的文本")
                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
//                        mHandler.sendEmptyMessage(UDP_BroadCast_Over);
                        countDownTimer.cancel();
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener((dialog, event) -> {
                    toast("按键代码：" + event.getKeyCode());
                    return false;
                })
                .show();

        CircleProgressView mProgressView = mSearchDialog.getContentView().findViewById(R.id.progressview);

        //开启计时器
        //发送广播
        BroadCastDataBean bean = new BroadCastDataBean();
        bean.setBroadcaster("szcme");                              //设备名字
        bean.setRamdom(CalculateUtils.getCurrentTimeString());     //时间戳
        byte[] sendByteData = CalculateUtils.getSendByteData(DeviceSearchActivity.this, mGson.toJson(bean), "FF",
                "0000000000000000", "FD");
        //发送广播消息
        if (("".equals(Constants.BROADCAST_PORT))) {
            toast("通讯端口不能为空!");
            return;
        }

        SocketUtils.startSendBroadcastMessage(sendByteData, this);
        mProgressView.showAnimation((int) 4000, 4000);
        //是否显示外环刻度
        mProgressView.setShowTick(false);
        //是否旋转
        mProgressView.setTurn(false);
        mProgressView.setOnChangeListener(new CircleProgressView.OnChangeListener() {
            @Override
            public void onProgressChanged(float progress, float max) {
                if (0 == progress) {
                    countDownTimer.start();
                }
            }
        });

    }

    private static final String TAG = "Socket发送===";

    private void showSettingDialog() {
        MMKV kv = MMKV.defaultMMKV();
        int mCurrentReceivePort = kv.decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
        new Input2SettingDialog.Builder(getActivity())
                .setTitle("配置信息")
                .set2Content(mCurrentReceivePort + "")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new Input2SettingDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String settingPort) {
                        ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager.isWifiEnabled()) {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            mAppIP = getIpString(wifiInfo.getIpAddress());
                        }
                        if ("".equals(settingPort)) {
                            toast("本地广播发送端口不能为空");
                            return;
                        } else {


                            MMKV kv = MMKV.defaultMMKV();
                            //获取当前开启的接收端口
                            if (mCurrentReceivePort == Integer.parseInt(settingPort)) {//相等,此时不需要开启新的线程
                                toast("此端口已配置,请勿重复操作!");
                            } else {
                                //存入当前广播发送的port
                                kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, true);
                                kv.encode(Constants.KEY_RECEIVE_PORT, Integer.parseInt(settingPort)); //设置的,本地监听端口
                                kv.encode(Constants.KEY_RECEIVE_PORT_BY_SEARCH, Integer.parseInt(settingPort)); //设置的,广播本地监听端口
                                kv.encode(Constants.KEY_BROADCAST_PORT, Integer.parseInt(settingPort));
                                int mDefaultCastSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                                receiveSocketService.setSettingReceiveThread(mAppIP, Integer.parseInt(settingPort), DeviceSearchActivity.this);
                                //再次打开搜索动画
                                showSearchDialog();
                            }


                        }
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();
    }


    /**
     * 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后10秒之后会回调onFinish方法
     * 你想在范围[5, 21]之间产生随机数，只需这样：netInt(21 - 5 + 1) + 5;
     */
    private CountDownTimer countDownTimer = new CountDownTimer(1000 * 4, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            Log.e("hehehe ", millisUntilFinished + " ");
        }

        @Override
        public void onFinish() {
            //显示主界面
            mSearchDialog.dismiss();
            mHandler.sendEmptyMessage(UDP_BroadCast_Over);


        }
    };

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        /**
         * 获取接收socket的数据--随机数之后到data结尾的String
         * @param str 传入接收指令所有长度的,16进制的string
         * @return  返回-->随机数结尾,CMD_ID开始+发送设备+......+Data结尾,整个十六进制字符串(不包含Check_Sum,和DD)
         * random
         * getRandomEndData2DataEndResultHexString
         */
        String mRandom2DataResultData = CalculateUtils.getRandomEndData2DataEndResultHexString(event.getData());//随机数之后到data结尾的String
        //上位机的设备类型比如:一代一体机
        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
        //上位机的设备码
        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
        String currentCMD = CalculateUtils.getCMD(event.getData());
        switch (event.getUdpCmd()) {
            case Constants.UDP_FD://广播
                //判断是否包含指定的key:设备码+设备类型
                boolean flag = mReceiveBroadMap.containsKey(deviceOnlyCode + deviceType);
                LogUtils.e("设备搜索界面==flag==" + flag);
                LogUtils.e("设备搜索界面==key==" + deviceOnlyCode + deviceType);

                if (!flag) {
                    mReceiveBroadMap.put(deviceOnlyCode + deviceType, mRandom2DataResultData + "==" + event.getIp());
                }

                break;
            case Constants.UDP_FC://授权接入
                int size = mReceivePointList.size();
                LogUtils.e("设备搜索界面==授权接入==" );
                LogUtils.e("设备搜索界面==授权接入=="+size );

                if (!mReceivePointList.contains(mRandom2DataResultData)) {
                    mReceivePointList.add(mRandom2DataResultData + "==" + event.getIp());
                    //发消息,存入数据库,并且刷新设备搜索界面
                    LogUtils.e("设备搜索界面==授权接入==mRandom2DataResultData="+mRandom2DataResultData );
                    LogUtils.e("设备搜索界面==授权接入==getIp="+event.getIp() );
                    mHandler.sendEmptyMessage(UDP_Point_Over);

                }
                break;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_search;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        receiveSocketService = new ReceiveSocketService();
        mStatusLayout = findViewById(R.id.status_hint);
        statusBarView = findViewById(R.id.tv_statue_view);
        mRefreshLayout = findViewById(R.id.rl_device_search_refresh);
        mRecyclerView = findViewById(R.id.rv_device_search_recyclerview);
//        mTitleBar = findViewById(R.id.search_titlebar);
        setOnClickListener(R.id.tv_left, R.id.iv_right_search, R.id.iv_right_setting);
        mAdapter = new DeviceSearchAdapter(this, mRecyclerView, mReceiveList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mReceiveList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DeviceSearchActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                Set_Data = false;
                //发送消息刷新设备界面
                EventBus.getDefault().post(new RefreshEvent("refresh"));
                finish();
                break;

            case R.id.iv_right_search:
                if (null != mSearchDialog && mSearchDialog.isShowing()) {
                    return;
                }
                showSearchDialog();
                break;
            case R.id.iv_right_setting:
                //设置了新的端口重置搜索次数
                // 输入对话框
                showSettingDialog();
                break;

        }
    }


    @Override
    protected void initData() {
        setStatusBarHeight();
        mReceiveBroadMap.clear();
        mReceiveBroadCastList.clear();
        mReceivePointList.clear();
        mHandler.sendEmptyMessage(UDP_Anim);


    }


    /**
     * 广播结束,刷新UI界面
     * 把hexString的集合数据转化成BroadCastReceiveBean数据给适配器
     */
    private void getAdapterBeanData() {
        //此处只做一次广播结束之后界面数据的刷新,不然会出现数据重复
        if (mReceiveBroadMap.size() == 0) {
            showEmpty();
            return;
        }

        if (Set_Data) { //界面设置过数据的直接return
            return;
        }

        Iterator<Map.Entry<String, String>> iterator = mReceiveBroadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            mReceiveBroadCastList.add(value);
        }
        for (int i = 0; i < mReceiveBroadCastList.size(); i++) {
            String str = mReceiveBroadCastList.get(i);
            String receiveDataStringFromRoom = CalculateUtils.getReceiveDataStringFromRoomForBroadCast(str);
            String s = receiveDataStringFromRoom.toUpperCase();
            LogUtils.e("设备搜索界面==获取到广播hexString的数据==" + str);
            LogUtils.e("设备搜索界面==获取到广播hexString的数据==" + str);
            LogUtils.e("设备搜索界面==上位机,设备类型==" + CalculateUtils.getDeviceTypeFromRoom(str));
            LogUtils.e("设备搜索界面==上位机,设备码==" + CalculateUtils.getReceiveID(str));
            LogUtils.e("设备搜索界面==iPad ID==" + CalculateUtils.getDeviceOnlyCodeFromRoom(str));
            LogUtils.e("设备搜索界面==data==hexString==" + s);
            LogUtils.e("设备搜索界面==data==String==" + CalculateUtils.hexStr2Str(s));
            //需要先截取==之后的ip
            int startIndex = str.indexOf("==") + 2;
            String ip = str.substring(startIndex);
            BroadCastReceiveBean bean = mGson.fromJson(CalculateUtils.hexStr2Str(s), BroadCastReceiveBean.class);
            bean.setSelected(false); //默认都是未选中
            bean.setDeviceType(CalculateUtils.getDeviceTypeFromRoom(str));
//            bean.setDeviceCode(CalculateUtils.getReceiveID(str));
//            bean.setItemId(CalculateUtils.getDeviceOnlyCodeFromRoom(str));
            //把32位字符串设备码转化成16位
            String receiveID32 = CalculateUtils.getReceiveID(str);
            String receiveID16 = CalculateUtils.hexStr2Str(receiveID32);
            bean.setDeviceCode(receiveID16);
            bean.setItemId(CalculateUtils.getDeviceOnlyCodeFromRoom(str));
            bean.setCheckAccess(false); //默认都没校验接入过
            bean.setIp(ip + "");
            bean.setReceiveType(CalculateUtils.getReceiveType(str));    //接收方设备类型
            bean.setReceiveID(receiveID16);    //接收方设备类型
            mReceiveList.add(bean);
        }


        if (mReceiveList.size() == 0) {
            showEmpty();
        } else {
            mAdapter.setData(mReceiveList);
            Set_Data = true;
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
        PutInBean putBean = new PutInBean();
        //点击对话框的时候在存入pinAccess密码
        putBean.setBroadcaster(Constants.BROADCASTER);                              //设备名字
        putBean.setSpt(Constants.RECEIVE_PORT + "");
        //存入数据库的标识;endotype(数字)+devicecode(36位设备码)+devicetype(中文说明)
        String tag = item.getEndotype() + item.getDeviceCode() + item.getDeviceType();


        LogUtils.e("设备搜索界面==getDataInsertDB=item.getEndotype()===="+item.getEndotype() );
        LogUtils.e("设备搜索界面==getDataInsertDB=item.getDeviceCode()===="+item.getDeviceCode() );
        LogUtils.e("设备搜索界面==getDataInsertDB=item.getDeviceType()===="+item.getDeviceType() );
        LogUtils.e("设备搜索界面==getDataInsertDB=item.getIp()===="+item.getIp() );


        String endotype = item.getEndotype();

        //判断是否存入过该条数据到数据库中
        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceSearchActivity.this);
        if (null != codeBean) {
            if (tag.equals(codeBean.getAcceptAndInsertDB())) {  //数据库有该条数据
                showRefreshDBDataDialog(codeBean, item);
                refreshCurrentUIStatus(position);
                return;
            } else { //数据库没有有该条数据
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
                .setTitle("提示")
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

        /**
         *
         *
         *
         *
         * 这里需要做测试
         *
         *
         * codeBean.getType()
         * 改成
         * bean.getDeviceType()
         *
         *
         *
         *
         */
        codeBean.setAcceptAndInsertDB(codeBean.getEndoType() + codeBean.getDeviceCode() + bean.getDeviceType());    //存入回调数据bean,标识数据在数据库的唯一性
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


        /**
         * 一个个添加活重复新增之前的设备bug
         *
         */
        for (int i = 0; i < mReceivePointList.size(); i++) {

            LogUtils.e("设备搜索界面==getDataInsertDB=currentItemPosition===="+currentItemPosition );


            LogUtils.e("设备搜索界面==getDataInsertDB==size=="+mReceivePointList.size() );
            String str = mReceivePointList.get(i);
            LogUtils.e("设备搜索界面==getDataInsertDB==i=="+i );
            LogUtils.e("设备搜索界面==getDataInsertDB==str=="+str );

            String receiveDataStringFromRoom = CalculateUtils.getReceiveDataStringFromRoomForPoint(str);
            String s = receiveDataStringFromRoom.toUpperCase();

            //获取设备码
            String deviceOnlyCodeFromRoom = CalculateUtils.getSendID(str);
            String sendDeviceType = CalculateUtils.getSendDeviceType(str);
            String s1 = CalculateUtils.hexStr2Str(s);
            PutInDeviceMsgBean bean = mGson.fromJson(s1, PutInDeviceMsgBean.class);
            String retcode = bean.getRetcode();
            String devicecode11 = CalculateUtils.hexStr2Str(deviceOnlyCodeFromRoom);
            LogUtils.e("设备搜索界面==getDataInsertDB==deviceOnlyCodeFromRoom=="+devicecode11 );
            LogUtils.e("设备搜索界面==getDataInsertDB==bean=="+bean );
            LogUtils.e("设备搜索界面==getDataInsertDB======================================");

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
        int i = Integer.parseInt(bean.getType());
        switch (i) {
            case Constants.Type_07:  //（一代一体机）         扫码的结果对应数字是07
                bean.setType("一代一体机");        //设置设备类型
                break;
            case Constants.Type_08: //（耳鼻喉治疗台）     扫码的结果对应数字是8   这里需要统一添加0
                bean.setType("耳鼻喉治疗台");
                break;
            case Constants.Type_09://（妇科治疗台）                扫码的结果对应数字是9  这里需要统一添加0
                bean.setType("妇科治疗台");
                break;
            case Constants.Type_0A://（泌尿治疗台）             扫码的结果对应数字是10
                bean.setType("泌尿治疗台");
                break;
        }
        String tag = bean.getEt() + deviceOnlyCodeFromRoom + bean.getType();
        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceSearchActivity.this, tag);
//        DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByCode(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
//        DeviceDBBean typeBean = DeviceDBUtils.getQueryBeanByType(DeviceSearchActivity.this, deviceOnlyCodeFromRoom, bean.getType());
//        LogUtils.e("设备搜索界面==模拟数据==DeviceDBBean.toString==typeBean===" + typeBean);
        //获取当前界面被点击的数据item
        BroadCastReceiveBean currentClickItem = mAdapter.getItem(currentItemPosition);
        String deviceOnlyCode16 = CalculateUtils.hexStr2Str(deviceOnlyCodeFromRoom);
        if (null != codeBean) {  //数据库表存在更新数据即可,只针对主键
            Long id = codeBean.getId();
            codeBean.setId(id);
            codeBean.setDeviceCode(deviceOnlyCode16);  //设置设备码
            codeBean.setDeviceID(deviceOnlyCode16);  //设置设备id
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
            codeBean.setAcceptAndInsertDB(bean.getEt() + deviceOnlyCode16 + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性
            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            DeviceDBUtils.update(DeviceSearchActivity.this, codeBean);
            mAdapter.notifyDataSetChanged();
            toast("更新=");

        } else { //暂无数据添加到数据库
            DeviceDBBean deviceDBBean = new DeviceDBBean();
            deviceDBBean.setDeviceCode(deviceOnlyCode16);  //设置设备码
            deviceDBBean.setDeviceID(deviceOnlyCode16);  //设置设备id
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
            deviceDBBean.setAcceptAndInsertDB(bean.getEt() + deviceOnlyCode16 + bean.getType());    //存入回调数据bean,标识数据在数据库的唯一性

            //此处修改界面adapter数据bean(BroadCastReceiveBean)状态,是否检验接入过isCheckAccess->true;是否存入数据库inDB->true
            currentClickItem.setInDB(true);
            currentClickItem.setCheckAccess(true);
            mAdapter.notifyDataSetChanged();
            DeviceDBUtils.insertOrReplace(DeviceSearchActivity.this, deviceDBBean);
        }
        EventBus.getDefault().post(new RefreshEvent("refresh"));

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

        // 广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的返回的port的通讯
        MMKV kv = MMKV.defaultMMKV();
        int mSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
        SocketUtils.startSendPointMessage(sendByteData, ip, mSendPort, this);

//        SocketManage.startSendMessageBySocket(sendByteData, ip, Constants.BROADCAST_PORT, false);


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
                .setTitle("提示")
                // 内容可以不用填写
                // 提示可以不用填写
                .setHint("请输入授权码")
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
     * 根据设备名-中文获取对应数字
     * 一代一体机=07
     *
     * @param str
     * @return
     */
    public String getDeviceTypeNum(String str) {
        if ("一代一体机".equals(str)) {
            return Constants.Type_07 + "";
        } else if ("耳鼻喉治疗台".equals(str)) {
            return Constants.Type_08 + "";
        } else if ("妇科治疗台".equals(str)) {
            return Constants.Type_09 + "";
        } else if ("泌尿治疗台".equals(str)) {
            return Constants.Type_0A + "";
        }
        return Constants.Type_07 + "";
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
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //暂时不明确
    }

    @Override
    protected void onResume() {
        super.onResume();
        //暂时不明确
//        SocketManage.setIsRuning(true);
        //必须打开不然再次进入界面 不能能接受收据哦
//        SocketManage.getSocketManageInstance();
        mReceiveBroadMap.clear();
        mReceiveBroadCastList.clear();
        mReceivePointList.clear();
//        SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);

    }


    /**
     * 设置状态栏高度
     */
    private void setStatusBarHeight() {
        int statusBarHeight = getStatusBarHeight(DeviceSearchActivity.this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) statusBarView.getLayoutParams();
        if (statusBarHeight == 0) {
            float dimension = getResources().getDimension(R.dimen.dp_10);
            statusBarHeight = (int) dimension;
            layoutParams.height = statusBarHeight;
        } else {
            layoutParams.height = statusBarHeight;
        }
        statusBarView.setLayoutParams(layoutParams);
    }

}
