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
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.green.db.CaseDBUtils;
import com.company.iendo.green.db.UserDBBean;
import com.company.iendo.green.db.UserDBUtils;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.green.db.downcase.CaseImageListBean;
import com.company.iendo.http.glide.GlideApp;
import com.company.iendo.manager.InputTextManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.offline.AFragment;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.other.KeyboardWatcher;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengLogin;
import com.hjq.widget.view.PasswordEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
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
    private int screenWidth;
    private TextView mLoginType;

    private void initRememberPassword() {

        Boolean isSave = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, false);
        LogUtils.e("initRememberPassword====isSave:" + isSave);
        String userName = mPhoneView.getText().toString().trim();
        LogUtils.e("initRememberPassword====isSave:" + isSave);

        if (isSave) {//存过设备之后才能查询本地数据库用户表--并且选中了当前用户
            List<UserDBBean> userDBBeans = UserDBUtils.queryAll(getApplicationContext());
            if (!userDBBeans.isEmpty()) {
                UserDBBean userDBBean = userDBBeans.get(0);
                mCheckbox.setChecked(true);
                mPhoneView.setText("" + userDBBean.getUserName());
                mPasswordView.setText("" + userDBBean.getPassword());
            }

        } else {
            mCheckbox.setChecked(false);
            mPhoneView.setText("");
            mPasswordView.setText("");
        }
//        if (isSave && !"".equals(userName)) {//存过设备之后才能查询本地数据库用户表--并且选中了当前用户
//            if (UserDBUtils.queryListIsExist(getApplicationContext(), userName)) {
//                UserDBBean userDBBean = UserDBUtils.queryListByName(getApplicationContext(), userName);
//                mCheckbox.setChecked(true);
//                mPhoneView.setText("" + userDBBean.getUserName());
//                mPasswordView.setText("" + userDBBean.getPassword());
//            }
//        } else {
//            mCheckbox.setChecked(false);
//            mPhoneView.setText("");
//            mPasswordView.setText("");
//        }

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
        EventBus.getDefault().register(this);
        mLogoView = findViewById(R.id.iv_login_logo);
        mBodyLayout = findViewById(R.id.ll_login_body);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        username_right = findViewById(R.id.username_right);
        mCommitView = findViewById(R.id.btn_login_commit);
        mSettingView = findViewById(R.id.btn_login_setting);
        mDeviceType = findViewById(R.id.btn_device_type);
        mLoginType = findViewById(R.id.login_type);
        mTopLogoAnim = findViewById(R.id.linear_top_logo);
        mCheckbox = findViewById(R.id.checkbox_remember);
        setOnClickListener(R.id.btn_login_commit, R.id.btn_login_setting, R.id.checkbox_remember, R.id.login_type);
        screenWidth = ScreenSizeUtil.getScreenWidth(LoginActivity.this);
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

    @SuppressLint("ResourceType")
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

                LogUtils.e("==========screenWidth======screenWidth===" + screenWidth);

                String string = getResources().getString(R.dimen.dp_74);
                String dip = string.replace("dip", "");
                Float mFloatDate = Float.valueOf(dip).floatValue();
                LogUtils.e("==========screenWidth======mPhoneView===" + mPhoneView.getWidth());
                LogUtils.e("==========screenWidth======string===" + dip);
                LogUtils.e("==========screenWidth======mFloatDate===" + mFloatDate);
                LogUtils.e("==========screenWidth======screenWidth - mFloatDate===" + (screenWidth - mFloatDate));

                if (!getListData().isEmpty()) {
                    historyBuilder = new ListPopup.Builder(LoginActivity.this);
                    historyBuilder.setList(getListData())
                            .setGravity(Gravity.CENTER_VERTICAL)
                            .setAutoDismiss(true)
                            .setOutsideTouchable(false) //80dp
                            .setWidth(mPhoneView.getWidth())
                            .setAnimStyle(AnimAction.ANIM_SCALE)
                            .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
                                        Message tempMsg = mHandler.obtainMessage();
                                        tempMsg.what = 1;
                                        tempMsg.obj = str;
                                        mHandler.sendMessage(tempMsg);
                                    }

                            )
                            .showAsDropDown(mPhoneView);
                } else {
                    toast("暂无数据哦!");

                }

//                historyBuilder.setList(getListData())
//                        .setGravity(Gravity.CENTER_VERTICAL)
//                        .setAutoDismiss(true)
//                        .setOutsideTouchable(false)
//                        .setWidth(mPhoneViewWidth + 60)
//                        .setXOffset(-30)
//                        .setHeight(650)
//                        .setAnimStyle(AnimAction.ANIM_SCALE)
//                        .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
//                                    Message tempMsg = mHandler.obtainMessage();
//                                    tempMsg.what = 1;
//                                    tempMsg.obj = str;
//                                    mHandler.sendMessage(tempMsg);
//                                }
//
//                        )
//                        .showAsDropDown(mPhoneView);
                if (null != historyBuilder) {
                    historyBuilder.getPopupWindow().addOnDismissListener(new BasePopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss(BasePopupWindow popupWindow) {
                            username_right.setTag("close");
                            username_right.setImageResource(R.drawable.login_icon_down);
                        }
                    });
                }
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
                                        /**
                                         * 测试离线病例显示
                                         *
                                         */

                                        setTestOffCaseData();
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
                    }
                });
                break;
            case R.id.login_type:
                showLoginType();
                break;
            case R.id.btn_login_setting: //设备管理界面
                startActivity(DeviceActivity.class);
                break;
        }


    }

    //选择登录模式
    private void showLoginType() {
        //默认是在线登录
        new SelectDialog.Builder(getActivity())
                .setTitle("请选择")
                .setList("在线登录", "离线登录")
                .setSingleSelect()
                .setSelect(0)
                .setListener(new SelectDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, HashMap data) {
                        String string = data.toString();
                        int size = data.size();
                        int i = string.indexOf("=");
                        String value = string.substring(i + 1, string.length() - 1);
                        LogUtils.e("下载===value=" + value);
                        if (value.equals("在线登录")) {  //在线登录
                            mLoginType.setText("在线登录");
                            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
                        } else if (value.equals("离线登录")) {//离线登录
                            mLoginType.setText("离线登录");
                            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, false);
                        }
                    }
                }).show();
    }

    //存点假数据
    private void setTestOffCaseData() {
        List<CaseDBBean> mCaseDBList = CaseDBUtils.queryAll(getActivity());
        if (0 == mCaseDBList.size()) {
            for (int i = 0; i < 10; i++) {
                CaseDBBean caseDBBean = new CaseDBBean();
                caseDBBean.setDeviceCaseID(i + "");  //用户表和设备表进行绑定, //用户表和设备表进行绑定, //用户表和设备表进行绑定
                caseDBBean.setName("姓名" + i);    // 姓名
                caseDBBean.setOccupatior(i + "--职业");    // 职业
                caseDBBean.setRecord_date("2022-01-" + i);    // 创建时间
                CaseDBUtils.insertOrReplaceInTx(getActivity(), caseDBBean);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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


        /**
         * 这里判断
         *
         */
        //选择设备之后,回到此界面默认会写记住密码的第一个用户
        initRememberPassword();

        String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";
        String strr = "AAC5 01 006A 22 EE 07 00000000000000005618B1F96D92837Ca1 f9432b11b93e8bb4ae34539b7472c20e FD 7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";

        int fd = str.indexOf("FD");
        String substring = str.substring(fd);
        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==fd===" + fd);
        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==fd=substring==" + substring);
        String currentCMD = CalculateUtils.getCMD(strr);
        LogUtils.e("SocketManage回调==currentCMD===" + currentCMD);

    }

    /**
     * eventbus 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        String mType = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Type, "耳鼻喉治疗台");
        if (mDeviceType != null) {
            mDeviceType.setText("" + mType);
        } else {
            mDeviceType.setText("未选择设备!");

        }
        LogUtils.e("========当前设备的备注信息~~~~====eventbus==eventbus===" + mType);


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