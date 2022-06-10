package com.company.iendo.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SocketUtils;
import com.google.gson.Gson;
import com.hjq.gson.factory.GsonFactory;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.AbsWorkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 保活的Service通讯服务
 * <p>
 * 一直开启这握手,
 */

public class HandService extends AbsWorkService {
    private static final String TAG = "握手服务===";
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static boolean isFirstIn = false;
    public static Disposable sDisposable10s;
    public static Disposable sDisposable60s;
    private Gson mGson = GsonFactory.getSingletonGson();
    public static boolean UDP_HAND_GLOBAL_TAG = false; //握手成功表示  true 成功
    private WifiManager.MulticastLock lock;
    private long startTime;
    private long sendCount = 0;
    private long currentIndex = 0;
    private long endTime;


    public static void stopService() {

        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable10s != null) sDisposable10s.dispose();
        if (sDisposable60s != null) sDisposable60s.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
//        EventBus.getDefault().unregister(this);

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


    private volatile static boolean isRuning = true;
    private String mAppIP;
    private SimpleDateFormat format;

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
        start10STask();


    }

    private void start60STask() {
        sDisposable60s = Observable
                .interval(30, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
                //取消任务时取消定时唤醒
                .doOnDispose(() -> {
//                    cancelJobAlarmSub();
                })
                .subscribe(count -> {
                    sendHandLinkMessage();
                    sendCount = count;
                    long tag = sendCount - currentIndex;
                    if (tag == 2 && !UDP_HAND_GLOBAL_TAG) {
                        sDisposable60s.dispose();
                        //两次长时间握手失败,告诉用户稍后再试!,从新开始短时间轮训握手,并且重启线程
                        start10STask();
                        SocketRefreshEvent event = new SocketRefreshEvent();
                        event.setUdpCmd(Constants.UDP_CUSTOM_TOAST);
                        event.setData(Constants.HAVE_HAND_FAIL_OFFLINE);
                        EventBus.getDefault().post(event);
                        LogUtils.e(TAG + "握手失败(60s)且tag=2,开始10s轮询");
                        LogUtils.e(TAG + "握手状态:" + UDP_HAND_GLOBAL_TAG);

                    } else if (UDP_HAND_GLOBAL_TAG) {
                        sDisposable60s.dispose();
                        LogUtils.e(TAG + "握手失败(60s),取消60s轮询,开始10s轮询");
                        LogUtils.e(TAG + "握手状态:" + UDP_HAND_GLOBAL_TAG);
                        start10STask();
                    }
                });
    }

    private void start10STask() {
//        Observable  类似于  button   被观察者
//        Observer    类似于  OnClickListener    是观察者

//        两者通过setOnClickListener来实现绑定关系,当Button有被click的动作，就会通知OnClickListener进行onClick里面定义的do something操作。

//        subscribe   类似于  setOnClickListener


//        subscribeOn是来设定我们的被观察者的操作是在哪个线程中进行   Schedulers.io()

//        observeOn是来设定我们的观察者的操作是在哪个线程执行   AndroidSchedulers.mainThread()

        sDisposable10s = Observable
                .interval(5, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
                //取消任务时取消定时唤醒
                .doOnDispose(() -> {
//                    cancelJobAlarmSub();
                }).subscribeOn(Schedulers.io())
                .subscribe(count -> {
                    sendCount = count;
                    sendHandLinkMessage();
                    long tag = sendCount - currentIndex;
                    if (tag == 5 && !UDP_HAND_GLOBAL_TAG) {
                        sDisposable10s.dispose();
                        start60STask();
                        LogUtils.e(TAG + "握手失败(10s)且tag=5,开始60s轮询");
                        LogUtils.e(TAG + "握手状态:" + UDP_HAND_GLOBAL_TAG);


                    } else if ((tag == 1 || tag == 2) && !UDP_HAND_GLOBAL_TAG) {//第二次就握手失败 我就重启下监听线程
                        MMKV mmkv = MMKV.defaultMMKV();
                        boolean b = mmkv.decodeBool(Constants.KEY_Login_Tag);
                        String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
                        LogUtils.e(TAG + "握手状态:" + UDP_HAND_GLOBAL_TAG);

                        if (b) {//如果是登录状态,重启登入时候的监听
                            ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                            receiveSocketService.setSettingReceiveThread(mAppIP, Integer.parseInt(mSocketPort), getApplicationContext());
                            LogUtils.e(TAG + "握手失败(10s),重启,登入时候的,监听服务");
                        } else {//不是登录状态,重启广播搜索监听
                            ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                            receiveSocketService.initFirstThread(mAppIP);
                            LogUtils.e(TAG + "握手失败(10s),重启,没有登入时候的,广播搜索监听服务");

                        }
                    }
                });


    }


    /**
     * eventbus 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://握手
                UDP_HAND_GLOBAL_TAG = true;
                LogUtils.e(TAG + "握手状态(socket回调):" + UDP_HAND_GLOBAL_TAG);
                //计入当前收到回调的次数
                currentIndex = sendCount;
                break;
        }

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        MMKV mmkv = MMKV.defaultMMKV();
        int mCurrentTypeNum = mmkv.decodeInt(Constants.KEY_Device_Type_Num, 0x07);
        Boolean mLoginTag = mmkv.decodeBool(Constants.KEY_Login_Tag);
        String mCurrentReceiveDeviceCode = mmkv.decodeString(Constants.KEY_DeviceCode);
        String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
        String mSocketOrLiveIP = mmkv.decodeString(Constants.KEY_Device_Ip);


        if (mLoginTag && null != mLoginTag) {
            HandBean handBean = new HandBean();
            handBean.setHelloPc("");
            handBean.setComeFrom("");
            if (!"".equals(mCurrentTypeNum + "") && !"".equals(mCurrentReceiveDeviceCode) && !("".equals(mSocketPort))) {
                byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum+"", mCurrentReceiveDeviceCode,
                        Constants.UDP_HAND);
                startTime = System.currentTimeMillis();
                if (null != sendByteData) {
                    SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), this);
                } else {

                }
            }


        }
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
        //若还没有取消订阅, 就说明任务仍在运行.
        boolean b1 = sDisposable10s != null && !sDisposable10s.isDisposed();
        boolean b2 = sDisposable60s != null && !sDisposable60s.isDisposed();

        return b1 || b2;
//        return sDisposable != null && !sDisposable.isDisposed();
    }


    public static Boolean getWorkStatue() {
        boolean b1 = sDisposable10s != null && !sDisposable10s.isDisposed();
        boolean b2 = sDisposable60s != null && !sDisposable60s.isDisposed();
        return b1 || b2;
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
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

}
