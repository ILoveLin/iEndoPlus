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
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.getpicture.LookReportBean;
import com.company.iendo.bean.socket.getpicture.PrintReportBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UDP_Hand:
                    if ((Boolean) msg.obj) {
                        LogUtils.e("======ReportActivity=====握手成功======");
                        UDP_HAND_TAG = true;
                        //发送
                        sendSocketPointMessage(Constants.UDP_F1);
                    } else {
                        LogUtils.e("======ReportActivity=====握手失败======");
                        UDP_HAND_TAG = false;
                    }
                    break;
                case UDP_Report_Url:
                    LogUtils.e("======ReportActivity=====获取到URL==msg.obj====" + msg.obj);

                    if ("".equals(msg.obj)) {
                        showEmptyReport();
                    } else {
                        showComplete();
                        Glide.with(getApplicationContext())
                                .load(msg.obj)
                                .error(R.mipmap.bg_loading_error)
                                .into(mReport);
                    }
                    //接受msg传递过来的参数数据
//                    String ip = msg.getData().getString("ip");
//                    String resultData = msg.getData().getString("resultData");
//                    LogUtils.e("======ReportActivity=====Handler接受====ip==" + ip);
//                    LogUtils.e("======ReportActivity=====Handler接受====resultData==" + resultData);
                    break;
                case UDP_Print_Report:
                    LogUtils.e("======ReportActivity=====获取到URL==打印报告====" + msg.obj);

                    if ("00".equals(msg.obj)) {
                        toast("报告打印成功!");
                    } else {
                        toast("报告打印失败!");

                    }
                    break;
            }
        }
    };

    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void initView() {
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
                sendSocketPointMessage(Constants.UDP_F2);

            }
        });
    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */
    private static DatagramSocket mReceiveSocket = null;
    private volatile static boolean isRuning = true;

    /**
     * 开启消息接收线程
     */
    private void initReceiveThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
                byte[] receiveData = new byte[1024];
                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    if (mReceiveSocket == null) {
                        mReceiveSocket = new DatagramSocket(null);
                        mReceiveSocket.setReuseAddress(true);
                        mReceiveSocket.bind(new InetSocketAddress(Constants.RECEIVE_PORT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (isRuning) {
                        try {
                            LogUtils.e("======ReportActivity=====000==");
                            LogUtils.e("======ReportActivity=====mReceivePacket.getAddress()==" + mReceivePacket.getAddress());
                            LogUtils.e("======ReportActivity=====mReceivePacket.getData()==" + mReceivePacket.getAddress());
                            LogUtils.e("======ReportActivity=====currentIP==" + currentIP);
                            if (!currentIP.equals(mReceivePacket.getAddress())) {   //不是自己的IP不接受
                                mReceiveSocket.receive(mReceivePacket);
                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
//                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
                                //过滤不是发送给我的消息全部不接受
                                int length = mReceivePacket.getLength() * 2;
                                String resultData = rec.substring(0, length);
                                LogUtils.e("======ReportActivity=====获取长度==length==" + length);
                                LogUtils.e("======ReportActivity=====获取长度数据==substring==" + resultData);
                                LogUtils.e("======ReportActivity=====接受到数据==原始数据====mReceivePacket.getData()=" + mReceivePacket.getData());
                                LogUtils.e("======ReportActivity=====3333==" + mReceivePacket.getData());
                                if (mReceivePacket != null) {
                                    LogUtils.e("======ReportActivity=====66666==");
                                    boolean flag = false;//是否可用的ip---此ip是服务器ip
                                    String finalOkIp = "";
                                    if (CalculateUtils.getDataIfForMe(resultData, ReportActivity.this)) {
                                        finalOkIp = CalculateUtils.getOkIp(mReceivePacket.getAddress().toString());
                                        flag = true;//正确的服务器ip地址
                                    }
                                    //正确的服务器ip地址,才开始计算获取自己需要的数据
                                    if (flag) {
                                        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
                                        String deviceType = CalculateUtils.getSendDeviceType(resultData);
                                        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
                                        String currentCMD = CalculateUtils.getCMD(resultData);
                                        LogUtils.e("======ReportActivity==回调===随机数之后到data结尾的String=mRun2End4==" + mRun2End4);
                                        LogUtils.e("======ReportActivity==回调===设备类型deviceType==" + deviceType);
                                        LogUtils.e("======ReportActivity==回调===设备ID=deviceOnlyCode==" + deviceOnlyCode);
                                        LogUtils.e("======ReportActivity==回调===CMD=currentCMD==" + currentCMD);

                                        switch (currentCMD) {
                                            case Constants.UDP_HAND://握手
                                                LogUtils.e("======ReportActivity==回调===握手==");
                                                //判断数据是否是发个自己的
                                                Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, ReportActivity.this);
                                                LogUtils.e("======ReportActivity=====dataIfForMe==" + dataIfForMe);
                                                //设备在线握手成功
                                                if (dataIfForMe) {
                                                    Message message = new Message();
                                                    message.what = UDP_Hand;
                                                    message.obj = true;
                                                    mHandler.sendMessage(message);
                                                }
                                                break;
                                            case Constants.UDP_F1://获取报告预览
                                                //获取到病例的ID是十六进制的,需要转成十进制
                                                Boolean dataIfForF1 = CalculateUtils.getDataIfForMe(resultData, ReportActivity.this);
                                                LogUtils.e("======ReportActivity==回调===获取当前病例==" + mRun2End4);
                                                LogUtils.e("======ReportActivity==回调===获取当前病例dataIfForFO==" + dataIfForF1);
                                                if (dataIfForF1) {
                                                    String dataString = CalculateUtils.getReceiveDataString(resultData);
                                                    LogUtils.e("======ReportActivity==回调===CMD=getReceiveDataString==" + dataString);
//                                                    String jsonID = CalculateUtils.hex16To10(dataString) + "";
                                                    LogUtils.e("======ReportActivity==回调===CMD=CalculateUtils.hexStr2Str(dataString)==" + CalculateUtils.hexStr2Str(dataString));
                                                    String s = CalculateUtils.hexStr2Str(dataString);
                                                    LookReportBean bean = mGson.fromJson(s, LookReportBean.class);
                                                    LogUtils.e("url==bean=" + bean);
                                                    Message message = new Message();
                                                    message.what = UDP_Report_Url;
                                                    message.obj = bean.getReporturl();
                                                    mHandler.sendMessage(message);

                                                }

                                                break;
                                            case Constants.UDP_F2://打印报告
                                                //获取到病例的ID是十六进制的,需要转成十进制
                                                Boolean dataIfForF2 = CalculateUtils.getDataIfForMe(resultData, ReportActivity.this);
                                                LogUtils.e("======ReportActivity==回调===获取当前病例==" + mRun2End4);
                                                LogUtils.e("======ReportActivity==回调===获取当前病例dataIfForFO==" + dataIfForF2);
                                                if (dataIfForF2) {
                                                    String dataString = CalculateUtils.getReceiveDataString(resultData);
                                                    LogUtils.e("======ReportActivity==回调===CMD=getReceiveDataString==" + dataString);
//                                                    String jsonID = CalculateUtils.hex16To10(dataString) + "";
                                                    LogUtils.e("======ReportActivity==回调===CMD=CalculateUtils.hexStr2Str(dataString)==" + CalculateUtils.hexStr2Str(dataString));
                                                    String s = CalculateUtils.hexStr2Str(dataString);
                                                    PrintReportBean bean = mGson.fromJson(s, PrintReportBean.class);
                                                    Message message = new Message();
                                                    message.what = UDP_Print_Report;
                                                    message.obj = bean.getPrintcode();
                                                    mHandler.sendMessage(message);
                                                }

                                                break;


                                        }


                                    }

//                                    String finalOkIp1 = finalOkIp;
//                                        Message message = new Message();
//                                        Bundle bundle = new Bundle();
//                                        message.what = UDP_Receive;
//                                        bundle.putString("ip", finalOkIp1);
//                                        bundle.putString("resultData", resultData);
//                                        message.setData(bundle);
//                                        mHandler.sendMessage(message);
                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }


            }
        }.start();

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
        if (("".equals(mSocketPort))){
            toast("通讯端口不能为空!");
            return;
        }
        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
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
            if (("".equals(mSocketPort))){
                toast("通讯端口不能为空!");
                return;
            }
            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);
        } else {
            toast("请先建立握手链接!");
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
    protected void onResume() {
        super.onResume();
        isRuning = true;
        initReceiveThread();
        //握手通讯
        LogUtils.e("onResume===ReportActivity===开始建立握手链接!");
        sendHandLinkMessage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRuning = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRuning = false;
    }
}
