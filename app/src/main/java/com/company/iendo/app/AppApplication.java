package com.company.iendo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.company.iendo.green.db.DaoMaster;
import com.company.iendo.green.db.DaoSession;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.db.DBManager;
import com.company.iendo.utils.db.MyOpenHelper;
import com.hjq.bar.TitleBar;
import com.company.iendo.R;
import com.company.iendo.aop.Log;
import com.company.iendo.http.glide.GlideApp;
import com.company.iendo.http.model.RequestHandler;
import com.company.iendo.http.model.RequestServer;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.other.AppConfig;
import com.company.iendo.other.CrashHandler;
import com.company.iendo.other.DebugLoggerTree;
import com.company.iendo.other.MaterialHeader;
import com.company.iendo.other.SmartBallPulseFooter;
import com.company.iendo.other.TitleBarStyle;
import com.company.iendo.other.ToastLogInterceptor;
import com.company.iendo.other.ToastStyle;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyConfig;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.UmengClient;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 应用入口
 */
public final class AppApplication extends Application {
    public static DaoSession mSession;
    private static DaoMaster mDaoMaster;

    public static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DBManager.class) {
                if (null == mDaoMaster) {
                    MyOpenHelper helper = new MyOpenHelper(context, "green.db", null);
                    mDaoMaster = new DaoMaster(helper.getWritableDatabase());
                }
            }
        }
        return mDaoMaster;
    }

    @Log("启动耗时")
    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(this);
        initLiveService();

        initOkHttp();
        initGreenDao();
//        Bugly.init(getApplicationContext(), "ed2196268b", false);

    }

    /**
     * 保活服务
     */
    private void initLiveService() {
        //初始化
        DaemonEnv.initialize(this, ReceiveSocketService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //是否 任务完成, 不再需要服务运行?
        ReceiveSocketService.sShouldStopService = false;
        //开启服务
        DaemonEnv.startServiceMayBind(ReceiveSocketService.class);
    }


    /**
     * 初始化GreenDao,
     * 连接数据库并创建会话
     */
    public void initGreenDao() {
        // 1、获取需要连接的数据库
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "green.db");
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        // 2、创建数据库连接
//        DaoMaster daoMaster = new DaoMaster(db);
        DaoMaster daoMaster = getDaoMaster(getApplicationContext());
        // 3、创建数据库会话
        mSession = daoMaster.newSession();
    }

    // 供外接使用
    public static DaoSession getDaoSession() {
        return mSession;
    }

    private void initOkHttp() {
        //Okhttp请求头
        //请求工具的拦截器  ,可以设置证书,设置可访问所有的https网站,参考https://www.jianshu.com/p/64cc92c52650
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))                  //内存存储cookie
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new MyInterceptor(this))                      //拦截器,可以添加header 一些信息
                .readTimeout(10000L, TimeUnit.MILLISECONDS)

                .hostnameVerifier(new HostnameVerifier() {//允许访问https网站,并忽略证书
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        OkHttpUtils.initClient(okHttpClientBuilder.build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {
        // 设置标题栏初始化器
        TitleBar.setDefaultStyle(new TitleBarStyle());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) ->
                new MaterialHeader(application).setColorSchemeColors(ContextCompat.getColor(application, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(application));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });


        // 初始化吐司
        ToastUtils.init(application, new ToastStyle());
        // 设置调试模式
        ToastUtils.setDebugMode(AppConfig.isDebug());
        // 设置 Toast 拦截器
        ToastUtils.setInterceptor(new ToastLogInterceptor());

        // 本地异常捕捉
        CrashHandler.register(application);

        // 友盟统计、登录、分享 SDK
        UmengClient.init(application, AppConfig.isLogEnable());

        // Bugly 异常捕捉
//        CrashReport.initCrashReport(application, "cc9cba912f", AppConfig.isDebug());

        //Bugly异常捕捉、 版本升级
        Bugly.init(application, "f67a6c664d", false);

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(application);

        // MMKV 初始化
        MMKV.initialize(application);
        MMKV kv = MMKV.defaultMMKV();
        //设置第一次启动App的时候,是否第一次初始化过接收线程
        kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN,false);
        int i = kv.decodeInt(Constants.KEY_RECEIVE_PORT);
        int i2 = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
        int i3 = kv.decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
//
        if ("".equals(i+"")){
            kv.encode(Constants.KEY_RECEIVE_PORT,Constants.RECEIVE_PORT);
        }
        if ("".equals(i2+"")){
            kv.encode(Constants.KEY_BROADCAST_PORT,Constants.BROADCAST_PORT);
        }
        if ("".equals(i3+"")){
            kv.encode(Constants.KEY_RECEIVE_PORT_BY_SEARCH,Constants.BROADCAST_PORT);
        }


        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(new RequestServer())
                // 设置请求处理策略
                .setHandler(new RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(1)
                .setInterceptor((api, params, headers) -> {
                    // 添加全局请求头
                    headers.put("token", "66666666666");
                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    headers.put("versionName", AppConfig.getVersionName());
                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // 添加全局请求参数
                    // params.put("6666666", "6666666");
                })
                .into();

        // 设置 Json 解析容错监听
        GsonFactory.setJsonCallback((typeToken, fieldName, jsonToken) -> {
            // 上报到 Bugly 错误列表
            CrashReport.postCatchedException(new IllegalArgumentException(
                    "类型解析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken));
        });

        // 初始化日志打印
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (!(topActivity instanceof LifecycleOwner)) {
                        return;
                    }

                    LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                    if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                        return;
                    }

                    ToastUtils.show(R.string.common_network_error);
                }
            });
        }
    }


}