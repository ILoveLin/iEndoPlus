package com.company.iendo.mineui.fragment.setting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.TextView;

import com.company.iendo.R;
import com.company.iendo.app.ReceiveSocketService;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.UserListActivity;
import com.company.iendo.mineui.activity.login.LoginActivity;
import com.company.iendo.mineui.activity.setting.DeviceParamsActivity;
import com.company.iendo.mineui.activity.setting.HospitalActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.activity.CopyActivity;
import com.company.iendo.ui.dialog.Input2Dialog;
import com.company.iendo.ui.dialog.MessageAboutDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.FileUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.SettingBar;
import com.tencent.mmkv.MMKV;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第3个tab-fragment
 */
public class SettingFragment extends TitleBarFragment<MainActivity> {

    private String mLoginUserID;
    private String mLoginPassword, mLoginReol;
    private SettingBar current_user;
    private String mLoginUserName;
    private String mBaseUrl;
    private SettingBar memory_bar;
    private TextView mUserName;
    private TextView mRelo;
    private String mAppIP;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_c;
    }

    @Override
    protected void initView() {
        memory_bar = findViewById(R.id.memory_bar);
        mUserName = findViewById(R.id.tv_current_name);
        mRelo = findViewById(R.id.tv_current_relo);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "192.168.312.102");
        setOnClickListener( R.id.memory_bar,R.id.params_bar, R.id.hospital_bar, R.id.user_bar, R.id.about_bar, R.id.memory_bar, R.id.password_bar, R.id.linear_exit);

    }

    @Override
    protected void initData() {
        mLoginUserID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserID, "");
        mLoginPassword = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_Password, "");
        mLoginUserName = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserName, "");
        mLoginReol = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_Role, "");
        String romAvailableSize = FileUtil.getROMAvailableSize(getActivity());
        memory_bar.setRightText(romAvailableSize);
        mUserName.setText("" + mLoginUserName);
        switch (mLoginReol) {
            case "0":
                mRelo.setText("超级管理员");
                break;
            case "1":
                mRelo.setText("管理员");
                break;
            case "2":
                mRelo.setText("操作员");
                break;
            case "3":
                mRelo.setText("查询员");
                break;
            case "4":
                mRelo.setText("自定义");
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.memory_bar:
                MMKV kv = MMKV.defaultMMKV();
                int port = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                int port1 = kv.decodeInt(Constants.KEY_RECEIVE_PORT);
                int portt = kv.decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
                LogUtils.e("AppActivity=fragment==port===="+port);
                LogUtils.e("AppActivity=fragment==port1===="+port1);
                LogUtils.e("AppActivity=fragment==接收portt===="+portt);
                break;
            case R.id.params_bar:
                startActivity(DeviceParamsActivity.class);
                break;
            case R.id.hospital_bar:
                startActivity(HospitalActivity.class);
                break;
            case R.id.user_bar:
                startActivity(UserListActivity.class);
                break;
            case R.id.about_bar:
                showAboutDialog();
                break;
            case R.id.password_bar:
                showChangePasswordDialog();
                break;
            case R.id.linear_exit:
                showExitDialog();
                break;

        }
    }

    private void showAboutDialog() {
        String showCopyrightYear = "";
        String versionName = getVersionName();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if ("2020".equals(year + "")) {
            showCopyrightYear = "2020";
        } else {
            showCopyrightYear = "2020" + "-" + year;
        }
        new MessageAboutDialog.Builder(getActivity())
                .setVersion("版本:V" + versionName)
                .setCopyright("版权所有(C)：" + showCopyrightYear)
                .setUpdateDate("更新日期：2021年12月")
                .setConfirm("确定")
                .show();

    }

    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 退出登录
     */
    private void showExitDialog() {
        new MessageDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("确定退出登录吗?")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
                        startActivity(LoginActivity.class);
                        // 进行内存优化，销毁除登录页之外的所有界面  --传入相对于的activity
                        // 进行内存优化，销毁掉所有的界面


                        ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager.isWifiEnabled()) {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            mAppIP = getIpString(wifiInfo.getIpAddress());
                        }
                        MMKV kv = MMKV.defaultMMKV();
                        int port = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                        int searchPort = kv.decodeInt(Constants.KEY_RECEIVE_PORT_BY_SEARCH);
                        LogUtils.e("AppActivity=fragment==port===="+port);
                        LogUtils.e("AppActivity=fragment==接收searchPort===="+searchPort);

                        if ("".equals(searchPort)) {
                            toast("本地广播发送端口不能为空");
                            return;
                        } else {
                            receiveSocketService.initSettingReceiveThread(mAppIP, searchPort, getAttachActivity());

                        }


                        finish();
                    }
                }).show();
    }

    /**
     * 修改自己的密码
     */
    private void showChangePasswordDialog() {
        new Input2Dialog.Builder(getActivity())
                .setTitle("提示")
                .setHint("请输入旧密码")
                .set2Hint("请输入新密码")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new Input2Dialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String password, String newPassword) {
                        LogUtils.e("旧密码==" + password);
                        LogUtils.e("新密码==" + newPassword);
                        sendChangeMineRequest(MD5ChangeUtil.Md5_32(password), MD5ChangeUtil.Md5_32(newPassword));
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();


    }

    private void sendChangeMineRequest(String oldPassword, String newPassword) {
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_ChangeMinePassword)
                .addParams("UserID", mLoginUserID)//自己的ID
                .addParams("oldPassword", oldPassword)//原来的密码
                .addParams("newPassword", newPassword)//新密码
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError();
                        toast("修改失败");

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        showComplete();
                        if ("" != response) {
                            UserDeletedBean mBean = mGson.fromJson(response, UserDeletedBean.class);
                            LogUtils.e("修改自己的密码====" + mBean.getMsg());
                            toast(mBean.getMsg() + "");

                            if (mBean.getCode().equals("0")) {
                                toast(mBean.getMsg() + "");
                            }
                        } else {
                            showError();
                        }
                    }
                });

    }

    private WaitDialog.Builder mWaitDialog;

    private void showError() {
        // 失败对话框
        new TipsDialog.Builder(getActivity())
                .setIcon(TipsDialog.ICON_ERROR)
                .setMessage("错误")
                .show();

    }

    private void showComplete() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }

    private void showLoading() {
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog.Builder(getActivity());
            // 消息文本可以不用填写
            mWaitDialog.setMessage(getString(R.string.common_loading))
                    .create();
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
//            postDelayed(mWaitDialog::dismiss, 2000);
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
