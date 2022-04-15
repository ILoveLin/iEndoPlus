package com.company.iendo.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.mineui.activity.login.LoginActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 保活的Service通讯服务
 * <p>
 * 一直开启这监听线程,监听Socket
 */

public class HandService extends AbsWorkService {
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

        LogUtils.e("保活服务开启HandService==stopService------ + stopService... stopService = ");


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

        LogUtils.e("保活服务开启HandService======startWork=====startWork");
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock = wifiManager.createMulticastLock("test wifi");
        //用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩

        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mAppIP = getIpString(wifiInfo.getIpAddress());
        }
        LogUtils.e("保活服务开启HandService==----AAA--" + Thread.currentThread().getName());
        start10STask();


    }

    private void start60STask() {
        sDisposable60s = Observable
                .interval(60, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
                //取消任务时取消定时唤醒
                .doOnDispose(() -> {
                    LogUtils.e("保活服务开启HandService==---取消了--每 60 秒采集一次数据--===doOnDispose=取消了");
//                    cancelJobAlarmSub();
                })
                .subscribe(count -> {
                    LogUtils.e("保活服务开启HandService==------每 60 秒采集一次数据... count = " + count + "==UDP_HAND_TAG==" + UDP_HAND_GLOBAL_TAG);
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

                    } else if (UDP_HAND_GLOBAL_TAG) {
                        sDisposable60s.dispose();
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
                .interval(10, TimeUnit.SECONDS)//定时器操作符，这里三秒打印一个log
                //取消任务时取消定时唤醒
                .doOnDispose(() -> {
                    LogUtils.e("保活服务开启HandService==---取消了--每 10 秒采集一次数据--===doOnDispose=取消了");
//                    cancelJobAlarmSub();
                }).subscribeOn(Schedulers.io())
                .subscribe(count -> {
                    sendCount = count;
                    LogUtils.e("保活服务开启HandService==------每 10 秒采集一次数据... count = " + count + "==UDP_HAND_TAG==" + UDP_HAND_GLOBAL_TAG);
                    sendHandLinkMessage();
                    long tag = sendCount - currentIndex;
                    if (tag == 5 && !UDP_HAND_GLOBAL_TAG) {
                        sDisposable10s.dispose();
                        start60STask();

                    } else if (tag == 0 && !UDP_HAND_GLOBAL_TAG) {
                        MMKV mmkv = MMKV.defaultMMKV();
                        boolean b = mmkv.decodeBool(Constants.KEY_Login_Tag);
                        String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
                        LogUtils.e("保活服务开启HandService==------第一次采集一次数据... count = 超过次数,直接重启接收线程!!!!!!!");
                        LogUtils.e("保活服务开启HandService==------第一次采集一次数据... count = 超过次数,直接重启接收线程!!!!!!!" + b);
                        LogUtils.e("保活服务开启HandService==------第一次采集一次数据... count = 超过次数,直接重启接收线程!!!!!!!" + mSocketPort);
                        if (b) {//如果是登录状态,重启登入时候的监听
                            ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                            receiveSocketService.setSettingReceiveThread(mAppIP, Integer.parseInt(mSocketPort), getApplicationContext());
                        } else {//不是登录状态,重启广播搜索监听
                            ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                            receiveSocketService.initFirstThread(mAppIP);
                        }
                    }

                });


    }


    /**
     * eventbus 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://握手
                UDP_HAND_GLOBAL_TAG = true;
                //计入当前收到回调的次数
                currentIndex = sendCount;
                LogUtils.e("保活服务开启HandService==------SocketRefreshEvent... UDP_HAND_TAG = " + UDP_HAND_GLOBAL_TAG);
                break;
        }

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        MMKV mmkv = MMKV.defaultMMKV();
        String mCurrentTypeNum = mmkv.decodeString(Constants.KEY_Device_Type_Num);
        Boolean mLoginTag = mmkv.decodeBool(Constants.KEY_Login_Tag);
        String mCurrentReceiveDeviceCode = mmkv.decodeString(Constants.KEY_DeviceCode);
        String mSocketPort = mmkv.decodeString(Constants.KEY_Device_SocketPort);
        String mSocketOrLiveIP = mmkv.decodeString(Constants.KEY_Device_Ip);

        LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mCurrentTypeNum==" + mCurrentTypeNum);
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mLoginTag==" + mLoginTag);
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mCurrentReceiveDeviceCode==" + mCurrentReceiveDeviceCode);
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mSocketPort==" + mSocketPort);
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mSocketOrLiveIP==" + mSocketOrLiveIP);

        if (mLoginTag && null != mLoginTag) {
            HandBean handBean = new HandBean();
            handBean.setHelloPc("");
            handBean.setComeFrom("");
            if (!"".equals(mCurrentTypeNum) && !"".equals(mCurrentReceiveDeviceCode) && !("".equals(mSocketPort))) {
                byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                        Constants.UDP_HAND);
                LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==sendByteData==" + sendByteData);
                LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==mSocketPort==" + mSocketPort);
                startTime = System.currentTimeMillis();
                if (null != sendByteData) {
                    SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), this);
                } else {
                    LogUtils.e("SocketUtils==HandService===发送消息==点对点==HandService==握手数据为null==" + sendByteData);

                }
            }


        }
    }

    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        LogUtils.e("保活服务开启HandService=====关闭了====stopWork。");
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
        LogUtils.e("保活服务开启HandService=====getWorkStatue====b1==" + b1);
        LogUtils.e("保活服务开启HandService=====getWorkStatue====b2==" + b2);

        return b1 || b2;
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        LogUtils.e("保活服务HandService=====保存数据到磁盘===onServiceKilled。");
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
        LogUtils.e("保活服务HandService====保存数据到磁盘===onDestroy。");
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

}
