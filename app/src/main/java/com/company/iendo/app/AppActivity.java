package com.company.iendo.app;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.company.iendo.R;
import com.company.iendo.action.TitleBarAction;
import com.company.iendo.action.ToastAction;
import com.company.iendo.http.model.HttpData;
import com.company.iendo.ui.dialog.WaitDialog;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.listener.OnHttpListener;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : Activity 业务基类
 */
public abstract class AppActivity extends BaseActivity
        implements ToastAction, TitleBarAction, OnHttpListener<Object> {

    /**
     * 标题栏对象
     */
    private TitleBar mTitleBar;
    /**
     * 状态栏沉浸
     */
    private ImmersionBar mImmersionBar;

    /**
     * 加载对话框
     */
    private BaseDialog mDialog;
    /**
     * 对话框数量
     */
    private int mDialogCount;
    public Gson mGson;
    public String mBaseUrl;  //当前用户的头部url
    public String endoType;
    public static String mAppIP;
    public String mCurrentTypeDes;    //当前选择设备的==比如:一代一体机==07,此处mCurrentTypeDes==一代一体机
    public String mCurrentTypeNum;    //当前选择设备的==比如:一代一体机==07,此处mCurrentTypeNum==07
    public String mCurrentTypeMsg;     //当前选择设备的==的描述比如:1号内镜室
    public String mCurrentReceiveDeviceCode; //当前选择设备的==唯一设备码
    public String mSocketOrLiveIP;       //socket或者直播通讯的ip
    public String mSocketPort;           //socket通讯端口
    public String mUsername;            //直播账号
    public String mPassword;            //直播密码
    public String mLivePort;            //直播端口
    public String mUserID;              //用户ID
    public String mBaseUrlPort;
    public String mLoginUserName;
    public MMKV mMMKVInstace;

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        mDialogCount++;
        postDelayed(() -> {
            if (mDialogCount <= 0 || isFinishing() || isDestroyed()) {
                return;
            }

            if (mDialog == null) {
                mDialog = new WaitDialog.Builder(this)
                        .setCancelable(false)
                        .create();
            }
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }, 300);
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (mDialogCount > 0) {
            mDialogCount--;
        }

        if (mDialogCount != 0 || mDialog == null || !mDialog.isShowing()) {
            return;
        }

        mDialog.dismiss();
    }

    /**
     * 解决:its super classes have no public methods with the @Subscribe annotation的BUG
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {

    }

    /**
     * 将获取到的int型ip转成string类型
     */

    @Override
    protected void initLayout() {
        super.initLayout();
        if (getTitleBar() != null) {
            getTitleBar().setOnTitleBarListener(this);
        }
        mMMKVInstace = MMKV.defaultMMKV();

        mBaseUrl = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_BaseUrl, "192.167.132.102");
        mBaseUrlPort = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_HttpPort, "7001");
        endoType = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_EndoType, "3");
        mUserID = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_Login_UserID, "3");
        mLoginUserName = mMMKVInstace.decodeString(Constants.KEY_CurrentLoginUserName);
        mCurrentTypeDes = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_Type, "妇科治疗台");
        mCurrentTypeMsg = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_Type_Msg, "1号内镜室");
        mCurrentTypeNum = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_Type_Num, "07");
        mCurrentReceiveDeviceCode = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_DeviceCode, "00000000000000000000000000000000");
        mSocketOrLiveIP = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_IP, "192.168.132.102");
        mSocketPort = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_SocketPort, "7006");
        mUsername = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_DeviceUsername, "root");
        mPassword = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_DevicePassword, "root");
        mLivePort = (String) SharePreferenceUtil.get(AppActivity.this, SharePreferenceUtil.Current_LivePort, "7788");


        LogUtils.e("AppActivity===mSocketOrLiveIP====" + mSocketOrLiveIP);
        LogUtils.e("AppActivity===mSocketPort====" + mSocketPort);
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init();

            // 设置标题栏沉浸
            if (getTitleBar() != null) {
                ImmersionBar.setTitleBar(this, getTitleBar());
            }
        }
        mGson = GsonFactory.getSingletonGson();
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mAppIP = getIpString(wifiInfo.getIpAddress());
        }

    }

    @Nullable
    @Override
    public CharSequence onCreateDescription() {
        return super.onCreateDescription();
    }

    /**
     * 将获取到的int型ip转成string类型
     */
    public static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * 状态栏字体深色模式
     */
    protected boolean isStatusBarDarkFont() {
        return true;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    public ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式状态栏
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
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (getTitleBar() != null) {
            getTitleBar().setTitle(title);
        }
    }

    @Override
    @Nullable
    public TitleBar getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = obtainTitleBar(getContentView());
        }
        return mTitleBar;
    }

    @Override
    public void onLeftClick(View view) {
        onBackPressed();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.right_in_activity, R.anim.right_out_activity);
    }

    @Override
    public void finish() {
        super.finish();
        //转场动画
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity);
    }

    /**
     * {@link OnHttpListener}
     */

    @Override
    public void onStart(Call call) {
        showDialog();
    }

    @Override
    public void onSucceed(Object result) {
        if (result instanceof HttpData) {
            toast(((HttpData<?>) result).getMessage());
        }
    }

    @Override
    public void onFail(Exception e) {
        toast(e.getMessage());
    }

    @Override
    public void onEnd(Call call) {
        hideDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (isShowDialog()) {
            hideDialog();
        }
        mDialog = null;

    }
}