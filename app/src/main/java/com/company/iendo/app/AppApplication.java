package com.company.iendo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.company.iendo.green.db.DaoMaster;
import com.company.iendo.green.db.DaoSession;
import com.company.iendo.other.Constants;
import com.company.iendo.service.ReceiveSocketService;
import com.company.iendo.utils.db.DBManager;
import com.company.iendo.utils.db.MyOpenHelper;
import com.didichuxing.doraemonkit.DoKit;
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

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * author : Android ?????????
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : ????????????
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

    @Log("????????????")
    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(this);

        new DoKit.Builder(this)
                .build();
        initLiveService();

        initOkHttp();
        initGreenDao();
//        Bugly.init(getApplicationContext(), "ed2196268b", false);

    }

    /**
     * ????????????
     */
    private void initLiveService() {
        //?????????
        WeakReference<Context> mWeakContext = new WeakReference<>(this);
        DaemonEnv.initialize(mWeakContext.get(), ReceiveSocketService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //?????? ????????????, ?????????????????????????
        ReceiveSocketService.sShouldStopService = false;
        //????????????
        DaemonEnv.startServiceMayBind(ReceiveSocketService.class);
    }


    /**
     * ?????????GreenDao,
     * ??????????????????????????????
     */
    public void initGreenDao() {
        // 1?????????????????????????????????
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "green.db");
        //devOpenHelper  ?????????????????????
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        // 2????????????????????????
//        DaoMaster daoMaster = new DaoMaster(db);
        DaoMaster daoMaster = getDaoMaster(getApplicationContext());
        // 3????????????????????????
        mSession = daoMaster.newSession();
    }

    // ???????????????
    public static DaoSession getDaoSession() {
        return mSession;
    }

    private void initOkHttp() {
        //Okhttp?????????
        //????????????????????????  ,??????????????????,????????????????????????https??????,??????https://www.jianshu.com/p/64cc92c52650
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))                  //????????????cookie
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new MyInterceptor(this))                      //?????????,????????????header ????????????
                .readTimeout(10000L, TimeUnit.MILLISECONDS)

                .hostnameVerifier(new HostnameVerifier() {//????????????https??????,???????????????
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
        // ??????????????????????????????
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // ??????????????????????????????????????????????????????
        GlideApp.get(this).onTrimMemory(level);
    }

    /**
     * ??????????????????????????????
     */
    public static void initSdk(Application application) {


        // ???????????????????????????
        TitleBar.setDefaultStyle(new TitleBarStyle());

        // ??????????????? Header ?????????
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) ->
                new MaterialHeader(application).setColorSchemeColors(ContextCompat.getColor(application, R.color.common_accent_color)));
        // ??????????????? Footer ?????????
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(application));
        // ????????????????????????
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // ????????????????????????????????????
            layout.setEnableHeaderTranslationContent(true)
                    // ????????????????????????????????????
                    .setEnableFooterTranslationContent(true)
                    // ????????????????????????????????????
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // ???????????????????????????????????????????????????
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // ???????????????????????????
                    .setEnableOverScrollDrag(false);
        });


        // ???????????????
        ToastUtils.init(application, new ToastStyle());
        // ??????????????????
        ToastUtils.setDebugMode(AppConfig.isDebug());
        // ?????? Toast ?????????
        ToastUtils.setInterceptor(new ToastLogInterceptor());

        // ??????????????????
        CrashHandler.register(application);

        // ?????????????????????????????? SDK
        UmengClient.init(application, AppConfig.isLogEnable());

        // Bugly ????????????
//        CrashReport.initCrashReport(application, "cc9cba912f", AppConfig.isDebug());

        //Bugly??????????????? ????????????
        Bugly.init(application, "f67a6c664d", false);

        // Activity ??????????????????
        ActivityManager.getInstance().init(application);

        // MMKV ?????????
        MMKV.initialize(application);
        MMKV kv = MMKV.defaultMMKV();
        //?????????????????????App?????????,???????????????????????????????????????
        kv.encode(Constants.KEY_SOCKET_RECEIVE_FIRST_IN, false);
//        int i = kv.decodeInt(Constants.KEY_BROADCAST_RECEIVE_PORT);
//        int i3 = kv.decodeInt(Constants.KEY_LOGIN_RECEIVE_PORT);

        int i2 = kv.decodeInt(Constants.KEY_BROADCAST_SERVER_PORT);
        int i4 = kv.decodeInt(Constants.KEY_LOCAL_RECEIVE_PORT);  //?????????????????????????????????
//
//        if ("".equals(i + "") || i == 0) {
//            kv.encode(Constants.KEY_BROADCAST_RECEIVE_PORT, Constants.BROADCAST_RECEIVE_PORT);
//        }
        if ("".equals(i2 + "") || i2 == 0) {
            kv.encode(Constants.KEY_BROADCAST_SERVER_PORT, Constants.BROADCAST_SERVER_PORT);
        }
//        if ("".equals(i3 + "") || i3 == 0) {
//            kv.encode(Constants.KEY_LOGIN_RECEIVE_PORT, Constants.KEY_LOGIN_RECEIVE_PORT);
//        }
        if ("".equals(i4 + "") || i4 == 0) {
            kv.encode(Constants.KEY_LOCAL_RECEIVE_PORT, Constants.LOCAL_RECEIVE_PORT); //??????????????????????????????
        }


        // ???????????????????????????
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // ??????????????????
                .setLogEnabled(AppConfig.isLogEnable())
                // ?????????????????????
                .setServer(new RequestServer())
                // ????????????????????????
                .setHandler(new RequestHandler(application))
                // ????????????????????????
                .setRetryCount(1)
                .setInterceptor((api, params, headers) -> {
                    // ?????????????????????
                    headers.put("token", "66666666666");
                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    headers.put("versionName", AppConfig.getVersionName());
                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // ????????????????????????
                    // params.put("6666666", "6666666");
                })
                .into();

        // ?????? Json ??????????????????
        GsonFactory.setJsonCallback((typeToken, fieldName, jsonToken) -> {
            // ????????? Bugly ????????????
            CrashReport.postCatchedException(new IllegalArgumentException(
                    "?????????????????????" + typeToken + "#" + fieldName + "??????????????????????????????" + jsonToken));
        });

        // ?????????????????????
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // ??????????????????????????????
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