package com.company.iendo.mineui.activity.casemanage;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.getpicture.LookReportBean;
import com.company.iendo.bean.socket.getpicture.PrintReportBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/2 14:42
 * desc：报告预览界面
 */

public class ReportActivity extends AppActivity implements StatusAction {
    private static final int UDP_Receive = 135;
    private static final int UDP_Hand = 136;
    private static final int UDP_Report_Url = 137;
    private static final int UDP_Print_Report = 138;
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功
    private StatusLayout mStatusLayout;
    private ImageView mReport;
    private TitleBar mTitlebar;


    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);
        mTitlebar = findViewById(R.id.titlebar);
        mReport = findViewById(R.id.iv_report);

    }

    @Override
    protected void initData() {
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                if (UDP_HAND_TAG){
                    sendSocketPointMessage(Constants.UDP_F2);
                }else {
                    toast("暂未建立连接!");
                    sendHandLinkMessage();
                }

            }
        });
    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==DeviceSearchActivity==event.getData()==" + event.getData());
        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(event.getData());//随机数之后到data结尾的String
        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
        String currentCMD = CalculateUtils.getCMD(event.getData());
        LogUtils.e("Socket回调==DeviceSearchActivity==随机数之后到data的Str==mRun2End4==" + mRun2End4);
        LogUtils.e("Socket回调==DeviceSearchActivity==发送方设备类型==deviceType==" + deviceType);
        LogUtils.e("Socket回调==DeviceSearchActivity==获取发送方设备Code==deviceOnlyCode==" + deviceOnlyCode);
        LogUtils.e("Socket回调==DeviceSearchActivity==当前UDP命令==currentCMD==" + currentCMD);
        LogUtils.e("Socket回调==DeviceSearchActivity==当前UDP命令==event.getUdpCmd()==" + event.getUdpCmd());
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://握手
                toast("握手成功");
                UDP_HAND_TAG = true;
                sendSocketPointMessage(Constants.UDP_F1);
                break;
            case Constants.UDP_F1://预览报告
                if ("".equals(data)) {
                    showEmptyReport();
                } else {
                    showComplete();
                    Glide.with(getApplicationContext())
                            .load(data)
                            .error(R.mipmap.bg_loading_error)
                            .into(mReport);
                }
                break;
            case Constants.UDP_F2://打印报告
                if ("00".equals(data)) {
                    toast("报告打印成功!");
                } else {
                    toast("报告打印失败!");
                }
                break;
        }

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        HandBean handBean = new HandBean();
        handBean.setHelloPc("HelloPc");
        handBean.setComeFrom("Android");
        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_HAND);
        if (("".equals(mSocketPort))) {
            toast("通讯端口不能为空!");
            return;
        }
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
//        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
    }


    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (UDP_HAND_TAG) {
            ShotPictureBean shotPictureBean = new ShotPictureBean();
            String spCaseID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Chose_CaseID, "");
            String s = CalculateUtils.hex10To16Result4(Integer.parseInt(spCaseID));
            shotPictureBean.setRecordid(s);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(shotPictureBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空!");
                return;
            }
            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);
        } else {
            toast("请先建立握手链接!");
        }

    }

    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

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
    protected void onResume() {
        super.onResume();
//        initReceiveThread();
        //握手通讯
        LogUtils.e("onResume===ReportActivity===开始建立握手链接!");
        sendHandLinkMessage();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
