package com.company.iendo.mineui.activity.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.company.iendo.R;
import com.company.iendo.aop.Log;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.LoginBean;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.green.db.UserDBBean;
import com.company.iendo.green.db.UserDBUtils;
import com.company.iendo.http.glide.GlideApp;
import com.company.iendo.manager.InputTextManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.fragment.AFragment;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.other.KeyboardWatcher;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengLogin;
import com.hjq.widget.view.PasswordEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 登录界面
 */
public final class LoginActivity extends AppActivity implements UmengLogin.OnLoginListener, KeyboardWatcher.SoftKeyboardStateListener, TextView.OnEditorActionListener {
    private static final String INTENT_KEY_IN_PHONE = "Admin";
    private static final String INTENT_KEY_IN_PASSWORD = "123";
    private ImageButton username_right;
    private int mPhoneViewWidth;
    private WaitDialog.Builder mWaitDialog;
    private List<UserListBean.DataDTO> mUserListData = new ArrayList<UserListBean.DataDTO>();
    private TextView mSettingView;
    private TextView mDeviceType;
    private String mBaseUrl;
    private LinearLayout mTopLogoAnim;
    private AppCompatCheckBox mCheckbox;
    private ImageView mLogoView;
    private ViewGroup mBodyLayout;
    private EditText mPhoneView;
    private PasswordEditText mPasswordView;
    private Button mCommitView;
    private final float mLogoScale = 0.8f;
    private final int mAnimTime = 300;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mPhoneView.setText("admin");
                    mPasswordView.setText("");
                    break;
                case 1:  //点击历史记录之后的操作
                    username_right.setTag("close");
                    username_right.setImageResource(R.drawable.login_icon_down);
                    String userName = (String) msg.obj;
                    mPhoneView.setText("" + (String) msg.obj);
                    //之前本地数据库存入了保存密码(存入本地数据库)
                    Boolean isSave = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, false);
                    if (isSave) {//存过设备之后才能查询本地数据库用户表
                        if (UserDBUtils.queryListIsExist(getApplicationContext(), userName)) {
                            UserDBBean userDBBean = UserDBUtils.queryListByName(getApplicationContext(), userName);
                            mPhoneView.setText("" + userDBBean.getUserName());
                            mPasswordView.setText("" + userDBBean.getPassword());
                            mCheckbox.setChecked(true);

                        } else {
                            mPhoneView.setText("" + userName);
                            mPasswordView.setText("");
                            mCheckbox.setChecked(false);
                        }
                    }
                    break;
            }
        }
    };

    private void initRememberPassword() {
        Boolean isSave = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, false);
        LogUtils.e("initRememberPassword====isSave:" + isSave);
        String userName = mPhoneView.getText().toString().trim();
        if (isSave && !"".equals(userName)) {//存过设备之后才能查询本地数据库用户表--并且选中了当前用户
            if (UserDBUtils.queryListIsExist(getApplicationContext(), userName)) {
                UserDBBean userDBBean = UserDBUtils.queryListByName(getApplicationContext(), userName);
                mCheckbox.setChecked(true);
                mPhoneView.setText("" + userDBBean.getUserName());
                mPasswordView.setText("" + userDBBean.getPassword());
            }
        } else {
            mCheckbox.setChecked(false);
            mPhoneView.setText("");
            mPasswordView.setText("");
        }

    }

    @Log
    public static void start(Context context, String phone, String password) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(INTENT_KEY_IN_PHONE, phone);
        intent.putExtra(INTENT_KEY_IN_PASSWORD, password);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mLogoView = findViewById(R.id.iv_login_logo);
        mBodyLayout = findViewById(R.id.ll_login_body);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        username_right = findViewById(R.id.username_right);
        mCommitView = findViewById(R.id.btn_login_commit);
        mSettingView = findViewById(R.id.btn_login_setting);
        mDeviceType = findViewById(R.id.btn_device_type);
        mTopLogoAnim = findViewById(R.id.linear_top_logo);
        mCheckbox = findViewById(R.id.checkbox_remember);
        setOnClickListener(R.id.btn_login_commit, R.id.btn_login_setting, R.id.checkbox_remember, R.id.login_type);
        mPasswordView.setOnEditorActionListener(this);
        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mPasswordView)
                .setMain(mCommitView)
                .build();
        mPhoneView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                mPhoneViewWidth = mPhoneView.getWidth();

            }
        });
    }


    /**
     * 获取列表数据
     *
     * @param mBaseUrl
     */

    private void sendRequest(String mBaseUrl) {
        showLoading();
        String mUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
        LogUtils.e("登录==url==0001=" + mBaseUrl);
        LogUtils.e("登录==url==0001=" + mUrl + HttpConstant.UserManager_List);
        OkHttpUtils.get()
                .url(mUrl + HttpConstant.UserManager_List)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError();
                        showComplete();
                        mPasswordView.setText("");
                        mPhoneView.setText("");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                mUserListData = mBean.getData();
                                LogUtils.e("用户列表===" + response);

                            } else {
                                showError();
                            }
                        } else {
                            showError();
                        }
                    }
                });

    }


    @Override
    protected void initData() {
        postDelayed(() -> {
            KeyboardWatcher.with(LoginActivity.this)
                    .setListener(LoginActivity.this);
        }, 500);

        initRememberPassword();
        showHistoryDialog();
    }


    private ListPopup.Builder historyBuilder;

    private void showHistoryDialog() {

        username_right.setImageResource(R.drawable.login_icon_down);
        username_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("==========Tag======Tag===" + username_right.getTag());
                if ("close".equals(username_right.getTag())) {
                    username_right.setTag("open");
                    username_right.setImageResource(R.drawable.login_icon_up);

                } else {
                    username_right.setTag("close");
                    username_right.setImageResource(R.drawable.login_icon_down);
                }
                historyBuilder = new ListPopup.Builder(LoginActivity.this);
                historyBuilder.setList(getListData())
                        .setGravity(Gravity.CENTER)
                        .setAutoDismiss(true)
                        .setOutsideTouchable(false)
                        .setWidth(mPhoneViewWidth + 60)
                        .setXOffset(-30)
                        .setHeight(650)
                        .setAnimStyle(AnimAction.ANIM_SCALE)
                        .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
                                    Message tempMsg = mHandler.obtainMessage();
                                    tempMsg.what = 1;
                                    tempMsg.obj = str;
                                    mHandler.sendMessage(tempMsg);
                                }

                        )
                        .showAsDropDown(mPhoneView);


                historyBuilder.getPopupWindow().addOnDismissListener(new BasePopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss(BasePopupWindow popupWindow) {
                        username_right.setTag("close");
                        username_right.setImageResource(R.drawable.login_icon_down);
                    }
                });
            }
        });

    }

    private ArrayList<String> getListData() {
        ArrayList<String> mList = new ArrayList<>();

        for (int i = 0; i < mUserListData.size(); i++) {
            mList.add(mUserListData.get(i).getUserName() + "");
            LogUtils.e("用户名:" + mUserListData.get(i).getUserName());
        }
        return mList;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_commit:
                // 隐藏软键盘
                hideKeyboard(getCurrentFocus());
                LogUtils.e("登录===" + MD5ChangeUtil.Md5_16(mPasswordView.getText().toString()));
                LogUtils.e("登录===" + MD5ChangeUtil.Md5_32(mPasswordView.getText().toString()));
                LogUtils.e("登录==url=" + mBaseUrl + HttpConstant.UserManager_Login);
                String mUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
                LogUtils.e("登录==url==02=" + mBaseUrl);
                LogUtils.e("登录==url==02=" + mBaseUrl + HttpConstant.UserManager_Login);
//                登录按钮动画
                showLoading();
                OkHttpUtils.post()
                        .url(mUrl + HttpConstant.UserManager_Login)
                        .addParams("UserName", mPhoneView.getText().toString())
                        .addParams("Password", MD5ChangeUtil.Md5_32(mPasswordView.getText().toString()))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                LogUtils.e("登录===" + e);
                                showError();
                                showComplete();
                                mPasswordView.setText("");
                                mPhoneView.setText("");
                            }

                            @Override
                            public void onResponse(String response, int id) {
//                                mCommitView.showProgress();
                                showComplete();
                                LogUtils.e("登录===" + response);
                                if (!"".equals(response)) {
                                    LoginBean mBean = mGson.fromJson(response, LoginBean.class);
                                    if (0 == mBean.getCode()) {
                                        LogUtils.e("登录==role==" + mBean.getData().getRole());
                                        LogUtils.e("登录==userid==" + mBean.getData().getUserID());
                                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Role, mBean.getData().getRole() + "");
                                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserID, mBean.getData().getUserID() + "");
                                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserName, mPhoneView.getText().toString());
                                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Password, mPasswordView.getText().toString());
                                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);
                                        SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);
                                        saveRememberPassword(mBean);
                                        MainActivity.start(getContext(), AFragment.class);
                                        finish();
//                                        postDelayed(() -> {
////                                            mCommitView.showSucceed();
//                                            postDelayed(() -> {
//                                                MainActivity.start(getContext(), AFragment.class);
//                                                finish();
//                                            }, 1000);
//                                        }, 2000);
                                    } else {
//                                        postDelayed(() -> {
//                                            mCommitView.showError(1500);
//                                        }, 1000);
                                        toast("密码错误!!");
                                    }

                                } else {
                                    showError();
                                    toast("返回数据为空!");
                                }

                            }
                        });
                break;
            case R.id.checkbox_remember: //记住密码
                mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        toast(isChecked);
                    }
                });
                break;
            case R.id.login_type:
                toast("功能暂定!");
                break;
            case R.id.btn_login_setting:
                startActivity(DeviceActivity.class);
                break;
        }


    }

    /**
     * 登录成功之后,判断是否记录密码,记住密码就存入数据库
     *
     * @param mBean
     */
    private void saveRememberPassword(LoginBean mBean) {
        if (mCheckbox.isChecked()) { //记住密码状态下存入数据库
            LoginBean.DataDTO bean = mBean.getData();
            UserDBBean userDBBean = new UserDBBean();
            userDBBean.setUserName(mPhoneView.getText().toString().trim());
            userDBBean.setPassword(mPasswordView.getText().toString().trim());
            userDBBean.setDeviceUserID(bean.getUserID());
            userDBBean.setRelo(bean.getRole() + "");
            userDBBean.setIsRememberPassword(true);
            /**
             * 设备ID
             * 这个用户是在哪个设备上的     用户和病例都是和设备绑定的
             * 当前选中设备的主键id,因为离线模式下就能通过这个主键id查找这个设备下的所有用户
             */
            String mMainID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_MainID, "1");
            userDBBean.setDeviceID(mMainID + "");
            UserDBUtils.insertOrReplaceInTx(LoginActivity.this, userDBBean);
            SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);

        }

    }


    /**
     * 授权成功的回调
     *
     * @param platform 平台名称
     * @param data     用户资料返回
     */
    @Override
    public void onSucceed(Platform platform, UmengLogin.LoginData data) {

        GlideApp.with(this)
                .load(data.getAvatar())
                .circleCrop()
                .into(mLogoView);

        toast("昵称：" + data.getName() + "\n" +
                "性别：" + data.getSex() + "\n" +
                "id：" + data.getId() + "\n" +
                "token：" + data.getToken());
    }

    /**
     * 授权失败的回调
     *
     * @param platform 平台名称
     * @param t        错误原因
     */
    @Override
    public void onError(Platform platform, Throwable t) {
        toast("第三方登录出错：" + t.getMessage());
    }

    /**
     * {@link KeyboardWatcher.SoftKeyboardStateListener}
     */

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -mCommitView.getHeight());
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        // 执行缩小动画
        mTopLogoAnim.setPivotX(mTopLogoAnim.getWidth() / 2f);
        mTopLogoAnim.setPivotY(mTopLogoAnim.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mTopLogoAnim, "scaleX", 1f, mLogoScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTopLogoAnim, "scaleY", 1f, mLogoScale);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mTopLogoAnim, "translationY", 0f, -mCommitView.getHeight());
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    @Override
    public void onSoftKeyboardClosed() {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0f);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        if (mTopLogoAnim.getTranslationY() == 0) {
            return;
        }

        // 执行放大动画
        mTopLogoAnim.setPivotX(mTopLogoAnim.getWidth() / 2f);
        mTopLogoAnim.setPivotY(mTopLogoAnim.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mTopLogoAnim, "scaleX", mLogoScale, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTopLogoAnim, "scaleY", mLogoScale, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mTopLogoAnim, "translationY", mTopLogoAnim.getTranslationY(), 0f);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==onResume===");
        mBaseUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==mBaseUrl===" + mBaseUrl);
        postDelayed(() -> {
            sendRequest(mBaseUrl);
        }, 500);

        String mType = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Type, "耳鼻喉治疗台");
        mDeviceType.setText("" + mType);

    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击登录按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    private void showError() {
        // 失败对话框
        new TipsDialog.Builder(this)
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
            mWaitDialog = new WaitDialog.Builder(this);
            // 消息文本可以不用填写
            mWaitDialog.setMessage(getString(R.string.common_loading))
                    .create();
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
//            postDelayed(mWaitDialog::dismiss, 2000);
        }
    }

}