package com.company.iendo.mineui.activity.login.device.search;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import com.company.iendo.mineui.socket.ThreadManager;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.lzh.easythread.EasyThread;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private InetAddress mInetAddress;
    private ArrayList<String> mReceiveHexStringList;    //广播接收线程,获取到hexString的数据


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_search;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mRefreshLayout = findViewById(R.id.rl_device_search_refresh);
        mRecyclerView = findViewById(R.id.rv_device_search_recyclerview);


        for (int i = 0; i < 10; i++) {
            BroadCastReceiveBean bean = new BroadCastReceiveBean();
            bean.setSelected(false);
            bean.setAccept("1");
            bean.setTitle("标题==" + i);
            mReceiveList.add(bean);
        }

        mAdapter = new DeviceSearchAdapter(this, mRecyclerView, mReceiveList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mReceiveList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DeviceSearchActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
//        7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2230227D
        SocketManage.getSocketManageInstance();
        //必须打开不然再次进入界面 不能能接受收据哦

    }

    /**
     * 点击了就默认选中当前设备,
     * 再次发送消息,如果需要授权,授权后再添加到数据库中,不需要授权直接添加到数据库
     * 并且当前添加到设备中的mSelected 设置为true,数据库中其他的设备mSelected为flase
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        //刷新UI界面
        refreshUIStatus(position);
        //存入数据库
//        saveDataToDB(position);
    }

    private void saveDataToDB(int position) {
        BroadCastReceiveBean bean = mReceiveList.get(position);
        String accept = bean.getAccept();//1准许 0不准
        if ("1".equals(accept)) {

            SocketManage.setOnSocketReceiveListener(new SocketManage.OnSocketReceiveListener() {
                @Override
                public void onSuccess(String str) {
                    //此处获取到Data数据,并且存入数据库
                    String receiveDataString = CalculateUtils.getReceiveDataString(str);
                    PutInDeviceMsgBean putMsgBean = mGson.fromJson(receiveDataString, PutInDeviceMsgBean.class);
                    //存入数据库里
                    LogUtils.e("存入数据库putMsgBean====" + putMsgBean.toString());


                }

                @Override
                public void onFailed(Throwable throwable) {

                }
            });
//            SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);
            try {
                mInetAddress = InetAddress.getByName(Constants.BROADCAST_IP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            PutInBean putBean = new PutInBean();
            putBean.setBroadcaster(Constants.BROADCASTER);                              //设备名字
            putBean.setPinAccess(bean.getPinAccess());
            putBean.setSpt(Constants.RECEIVE_PORT + "");

            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(putBean), "FF",
                    bean.getDeviceCode(), "FD");
            LogUtils.e("sendByteData==点对点==" + sendByteData);
            SocketManage.startSendMessageBySocket(sendByteData, mInetAddress, Constants.BROADCAST_PORT, false);


//            String deviceType = bean.getDeviceType();
//            switch (deviceType) {
//                case "妇科治疗台":
//                    DeviceDBBean deviceDBBean = new DeviceDBBean();
//                    deviceDBBean.setDeviceName(bean.getTitle());  //设备名
//                    deviceDBBean.setDeviceCode(bean.getDeviceCode()); //设备码---//mDeviceCode  这个是智能搜索之后返回过来的设备码
//                    deviceDBBean.setMsg(bean.getRemark());//备注信息
//                    deviceDBBean.setIp("192.168.1.200");          //ip
//                    deviceDBBean.setUsername("root");//设备账号
//                    deviceDBBean.setPassword("root");//设备密码
//
//            }

        } else {
            //弹出对话框输入校验密码才能添加数据库
        }


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

        mReceiveHexStringList = new ArrayList<>();
        SocketManage.startNorReceive(Constants.RECEIVE_PORT, DeviceSearchActivity.this);
//        设置广播监听
        SocketManage.setOnSocketReceiveListener(new SocketManage.OnSocketReceiveListener() {
            @Override
            public void onSuccess(String str) {
                LogUtils.e("SocketManage回调==onSuccess====" + str);
                //此处截取需hexstring
                String mRun2DDString = CalculateUtils.getReceiveRun2DDString(str);
                String deviceType = CalculateUtils.getDeviceType(str);
                String deviceOnlyCode = CalculateUtils.getDeviceOnlyCode(str);
                LogUtils.e("SocketManage回调==mRun2DDString===" + mRun2DDString);
                LogUtils.e("SocketManage回调==deviceType===" + deviceType);
                LogUtils.e("SocketManage回调==deviceOnlyCode===" + deviceOnlyCode);
                if (!mReceiveHexStringList.contains(mRun2DDString)) {
//                    //此处获取到Data数据
//                    String receiveDataString = CalculateUtils.getReceiveDataString(str);
//                    //转成Bean对象
//                    BroadCastReceiveBean bean = mGson.fromJson(CalculateUtils.hexStr2Str(receiveDataString), BroadCastReceiveBean.class);
                    mReceiveHexStringList.add(mRun2DDString);
                    LogUtils.e("SocketManage回调==mReceiveHexStringList.size()===" + mReceiveHexStringList.size());


                }


//                if (!mReceiveList.contains(str)) {
//                    BroadCastReceiveBean bean = mGson.fromJson(str, BroadCastReceiveBean.class);
//                    bean.setSelected(false); //默认都是未选中
//                    bean.setDeviceType(CalculateUtils.getDeviceType(str));
//                    bean.setDeviceCode(CalculateUtils.getDeviceOnlyCode(str));
//                    mReceiveList.add(bean);
//                }
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
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
                    toast("取消了~~~");
                    showComplete();
                }
            });
            //发送广播
            BroadCastDataBean bean = new BroadCastDataBean();
            bean.setBroadcaster("szcme");                              //设备名字
            bean.setRamdom(CalculateUtils.getCurrentTimeString());     //时间戳
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), "FF",
                    "00000000000000000000000000000000", "FD");
            LogUtils.e("sendByteData====" + sendByteData);
            //发送广播消息
            SocketManage.startSendMessageBySocket(sendByteData, inetAddress, Constants.BROADCAST_PORT, true);

        } else {
            toast("稍安勿躁,搜索中...");
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
