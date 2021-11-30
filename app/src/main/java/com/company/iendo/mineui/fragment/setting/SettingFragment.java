package com.company.iendo.mineui.fragment.setting;

import android.view.View;

import com.company.iendo.R;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.UserListActivity;
import com.company.iendo.mineui.activity.login.LoginActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.Input2Dialog;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第3个tab-fragment
 */
public class SettingFragment extends TitleBarFragment<MainActivity> {

    private String mLoginUserID;
    private String mLoginPassword;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_c;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.exit_bar, R.id.user_bar, R.id.password_bar);
    }

    @Override
    protected void initData() {
        mLoginUserID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserID, "");
        mLoginPassword = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_Password, "");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_bar:
                SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
                startActivity(LoginActivity.class);
                // 进行内存优化，销毁除登录页之外的所有界面  --传入相对于的activity
                // 进行内存优化，销毁掉所有的界面
                finish();
//                ActivityManager.getInstance().finishAllActivities();
                break;
            case R.id.user_bar:
                startActivity(UserListActivity.class);
                break;
            case R.id.password_bar:
                showChangePasswordDialog();
                break;

        }
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
                    public void onConfirm(BaseDialog dialog, String password,String newPassword) {
                        LogUtils.e("旧密码=="+password);
                        LogUtils.e("新密码=="+newPassword);
                        sendChangeMineRequest( MD5ChangeUtil.Md5_32(password),MD5ChangeUtil.Md5_32(newPassword));
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();




    }

    private void sendChangeMineRequest(String oldPassword, String newPassword) {
        showLoading();
        OkHttpUtils.post()
                .url(HttpConstant.UserManager_ChangeMinePassword)
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
                            LogUtils.e("修改自己的密码===="+mBean.getMsg() );
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
