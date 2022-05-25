package com.company.iendo.app;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.SharePreferenceUtil;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.company.iendo.R;
import com.company.iendo.action.TitleBarAction;
import com.hjq.gson.factory.GsonFactory;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/31
 *    desc   : 带标题栏的 Fragment 业务基类
 */
public abstract class TitleBarFragment<A extends AppActivity> extends AppFragment<A>
        implements TitleBarAction {

    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;
    public static String currentIP;
    public String mCaseID,mLoginUserName;
    public  Gson mGson;
    public String endoType;
    public String mBaseUrl;  //当前用户的头部url
    public String mUserID;
    public String mCurrentTypeDes;    //当前选择设备的==比如:一代一体机==07,此处mCurrentTypeDes==一代一体机
    public String mCurrentTypeNum;    //当前选择设备的==比如:一代一体机==07,此处mCurrentTypeNum==07
    public String mCurrentReceiveDeviceCode; //当前选择设备的==唯一设备码
    public String mSocketOrLiveIP;       //socket或者直播通讯的ip
    public String mSocketPort;           //socket通讯端口
    public MMKV mMMKVInstace;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGson = GsonFactory.getSingletonGson();
        mMMKVInstace = MMKV.defaultMMKV();

        // 设置标题栏点击监听
        if (getTitleBar() != null) {
            getTitleBar().setOnTitleBarListener(this);
        }

        if (isStatusBarEnabled()) {
            // 初始化沉浸式状态栏
            getStatusBarConfig().init();

            if (getTitleBar() != null) {
                // 设置标题栏沉浸
                ImmersionBar.setTitleBar(this, getTitleBar());
            }
        }
        endoType = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_EndoType, "3");
        mCaseID = mMMKVInstace.decodeString(Constants.KEY_CurrentCaseID);
        mLoginUserName = mMMKVInstace.decodeString(Constants.KEY_CurrentLoginUserName);
        mBaseUrl = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_BaseUrl, "192.167.132.102");
        endoType = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_EndoType, "3");
        mUserID = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Login_UserID, "3");
        mCurrentTypeDes = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Type, "妇科治疗台");
        mCurrentTypeNum = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_Type_Num, "07");
        mCurrentReceiveDeviceCode = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_DeviceCode, "00000000000000000000000000000000");
        mSocketOrLiveIP = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_IP, "192.168.132.102");
        mSocketPort = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_SocketPort, "8005");
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) getAttachActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            currentIP = getIpString(wifiInfo.getIpAddress());
        }

    }

    /**
     *解决:its super classes have no public methods with the @Subscribe annotation的BUG
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStatusBarEnabled()) {
            // 重新初始化状态栏
            getStatusBarConfig().init();
        }
    }

    /**
     * 是否在 Fragment 使用沉浸式
     */
    public boolean isStatusBarEnabled() {
        return false;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    protected ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式
     */
    @NonNull
    protected ImmersionBar createStatusBarConfig() {
        return ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(isStatusBarDarkFont())
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .autoDarkModeEnable(true, 0.2f);
    }

    /**
     * 获取状态栏字体颜色
     */
    protected boolean isStatusBarDarkFont() {
        // 返回真表示黑色字体
        return getAttachActivity().isStatusBarDarkFont();
    }

    @Override
    @Nullable
    public TitleBar getTitleBar() {
        if (mTitleBar == null || !isLoading()) {
            mTitleBar = obtainTitleBar((ViewGroup) getView());
        }
        return mTitleBar;
    }
    /**
     * 将获取到的int型ip转成string类型
     */
    public static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }
    //获取状态栏的高度
    public int getStatusBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}