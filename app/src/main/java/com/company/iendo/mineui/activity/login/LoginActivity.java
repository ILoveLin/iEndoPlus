package com.company.iendo.mineui.activity.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.company.iendo.app.ReceiveSocketService;
import com.company.iendo.bean.LoginBean;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.green.db.UserDBBean;
import com.company.iendo.green.db.UserDBUtils;
import com.company.iendo.manager.InputTextManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.fragment.casemanage.CaseManageFragment;
import com.company.iendo.mineui.offline.fragment.CaseManageOfflineFragment;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.other.KeyboardWatcher;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.CommonUtil;
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
import com.tencent.mmkv.MMKV;
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
    //    private List<UserListBean.DataDTO> mUserOflineListData = new ArrayList<UserListBean.DataDTO>();
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
                    //首先查询当前设备下,记住密码存入数据库的数据
                    refreshUI(userName);
                    break;
            }
        }
    };

    //历史列表点击之后刷新UI
    private void refreshUI(String userName) {
        Boolean loginType = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
        if (loginType) {//在线登录
            List<UserDBBean> userDBBeans = UserDBUtils.getQueryBeanByTow(getApplicationContext(), deviceID, userName);
            //数据库没有
            if (userDBBeans.size() != 0) {
                for (int i = 0; i < userDBBeans.size(); i++) {
                    UserDBBean userDBBean = userDBBeans.get(0);
                    String currentName = userDBBean.getUserName();
                    if (currentName.equals(userName)) {
                        mPhoneView.setText("" + userDBBean.getUserName());
                        mPasswordView.setText("" + userDBBean.getPassword());
                        if (userDBBean.getIsRememberPassword()) {
                            mCheckbox.setChecked(true);
                        } else {
                            mCheckbox.setChecked(false);
                        }
                    } else {
                        mPhoneView.setText(userName);
                        mPasswordView.setText("");
                        mCheckbox.setChecked(false);

                    }

                }
            } else {
                mPhoneView.setText(userName);
                mPasswordView.setText("");
                mCheckbox.setChecked(false);
            }
        } else {//离线登录
            List<UserDBBean> userList = UserDBUtils.getQueryBeanByTow(getActivity(), mCurrentReceiveDeviceCode, userName);
            if (null != userList && userList.size() > 0) {
                UserDBBean bean = userList.get(0);
                mPhoneView.setText(userName);
                //记住密码
                if (bean.getIsRememberPassword()) {
                    mPasswordView.setText(bean.getPassword());
                    mCheckbox.setChecked(true);
                } else {
                    mPasswordView.setText("");
                    mCheckbox.setChecked(false);
                }
            }
        }


    }

    private int screenWidth;
    private TextView mLoginType;
    private String deviceID;

    /**
     * 登录成功之后,判断是否记录密码,记住密码就存入数据库
     *
     * @param mBean
     */
    private void saveRememberPassword(LoginBean mBean) {
        //获取当前设备码

        if (mCheckbox.isChecked()) { //记住密码状态下存入数据库
            /**
             * 设备ID
             * 这个用户是在哪个设备上的     用户和病例都是和设备绑定的     :用户表设置的是这个字段 deviceID ==  deviceUserID  病历表设置的是这个字段deviceCaseID
             * 当前选中设备的主键id,因为离线模式下就能通过这个主键id查找这个设备下的所有用户
             */
            String deviceID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");
            LoginBean.DataDTO bean = mBean.getData();

            //查询当前设备,当前用户ID下的登入账户
            List<UserDBBean> beanList = UserDBUtils.getQueryBeanByTowCodeUserID(LoginActivity.this, deviceID, bean.getUserID());
            if (beanList.size() > 0) {//当前用户存过密码
                for (int i = 0; i < beanList.size(); i++) {
                    UserDBBean dbBean = beanList.get(i);
                    //是当前用户ID,更新数据
                    if ((bean.getUserID()).equals(dbBean.getDeviceUserID())) {
                        UserDBBean newBean = new UserDBBean();
                        newBean.setId(dbBean.getId());
                        newBean.setUserName(mPhoneView.getText().toString().trim());
                        newBean.setPassword(mPasswordView.getText().toString().trim());
                        newBean.setDeviceUserID(bean.getUserID());
                        newBean.setRelo(bean.getRole() + "");
                        newBean.setDeviceID(deviceID + "");
                        newBean.setMake01(dbBean.getMake01());
                        newBean.setIsRememberPassword(true);
                        UserDBUtils.insertOrReplaceInTx(LoginActivity.this, newBean);
                        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, true);
                        SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);
                    }
                }

            } else {
                UserDBBean newBean = new UserDBBean();
                newBean.setUserName(mPhoneView.getText().toString().trim());
                newBean.setPassword(mPasswordView.getText().toString().trim());
                newBean.setDeviceUserID(bean.getUserID());
                newBean.setDeviceID(deviceID + "");
                newBean.setRelo(bean.getRole() + "");
                newBean.setMake01("false");
                newBean.setIsRememberPassword(true);
                //记住密码
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, true);
                SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);

                LogUtils.e("initRememberPassword==存入的==deviceID:" + deviceID);
                UserDBUtils.insertOrReplaceInTx(LoginActivity.this, newBean);
            }


        } else {
            //没有记住密码
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, false);
        }

    }

    private void initRememberPassword() {

        String deviceid = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");
//        LogUtils.e("initRememberPassword====isSave:" + isSave);
        String userName = mPhoneView.getText().toString().trim();
        LogUtils.e("initRememberPassword==当前设备==deviceid:" + deviceid);
        LogUtils.e("initRememberPassword==当前设备==deviceID:" + deviceID);
        //电脑  0000000000000000ED3A93DA80A9BA8B
        //一体机0000000000000000546017FE6BC28949
//        if (isSave) {//存过设备之后才能查询本地数据库用户表--并且选中了当前用户,此处存在不同设备名字相同的时候密码相同的bug
        //先查询deviceID设备下,存储过的用户
        List<UserDBBean> userDBBeans = UserDBUtils.getQueryByDeviceID(getApplicationContext(), deviceID);
        LogUtils.e("initRememberPassword==当前设备==userDBBeans.size()=:" + userDBBeans.size());
        for (int i = 0; i < userDBBeans.size(); i++) {
            UserDBBean userDBBean = userDBBeans.get(i);
            userDBBean.getUserName();
            userDBBean.getPassword();
            userDBBean.getDeviceID();
            userDBBean.getIsRememberPassword();
            LogUtils.e("initRememberPassword==当前设备==userDBBean.toString=:" + userDBBean.toString());


        }

        //数据库没有
        if (userDBBeans.size() != 0) {
            for (int i = 0; i < userDBBeans.size(); i++) {
                UserDBBean userDBBean = userDBBeans.get(i);
                String currentName = userDBBean.getUserName();
                if (currentName.equals(userName)) {
                    mPhoneView.setText("" + userDBBean.getUserName());
                    mPasswordView.setText("" + userDBBean.getPassword());
                    if (userDBBean.getIsRememberPassword()) {
                        mCheckbox.setChecked(true);
                    } else {
                        mCheckbox.setChecked(false);
                    }
                } else {
                    mPhoneView.setText(userName);
                    mPasswordView.setText("");
                    mCheckbox.setChecked(false);
                }
            }
        } else {
            mPhoneView.setText(userName);
            mPasswordView.setText("");
            mCheckbox.setChecked(false);
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
        EventBus.getDefault().register(this);
        deviceID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");

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
                .setMain(mCommitView)
                .build();
        mPhoneView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                mPhoneViewWidth = mPhoneView.getWidth();

            }
        });


        initRememberPassword();

    }

    /**
     * 获取列表数据,登录的时候使用
     *
     * @param mBaseUrl
     */

    private void sendRequestLoginData(String mBaseUrl) {
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
                        LogUtils.e("用户列表==onError=" + e);

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mUserListData.clear();
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
                        LogUtils.e("用户列表==onError=" + e);

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mUserListData.clear();
                        if ("" != response) {
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                mUserListData = mBean.getData();
                                setOnLineFirstName(mUserListData);
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

    /**
     * 请求成功如果用户列表有用户就写入界面
     *
     * @param mUserListData 网络请求过来的用户列表
     */
    private void setOnLineFirstName(List<UserListBean.DataDTO> mUserListData) {
        if (mUserListData.size() > 0) {
            UserListBean.DataDTO bean = mUserListData.get(0);
            //查询数据库是否有存入过该用户
            List<UserDBBean> dbList = UserDBUtils.getQueryBeanByTowCodeUserID(LoginActivity.this, deviceID, bean.getUserID());
            //存入过
            if (dbList.size() > 0) {
                UserDBBean userDBBean = dbList.get(0);
                //再次校验一下用户名是否相等
                if (userDBBean.getUserName().equals(bean.getUserName())) {
                    mPhoneView.setText(bean.getUserName());
                    mPasswordView.setText(userDBBean.getPassword());
                    if (userDBBean.getIsRememberPassword()) {
                        mCheckbox.setChecked(true);
                    } else {
                        mCheckbox.setChecked(false);
                    }
                }
            } else {
                //未存入过
                mPhoneView.setText(bean.getUserName());
                mPasswordView.setText("");
            }


        }

    }

    @Override
    protected void initData() {
        postDelayed(() -> {
            KeyboardWatcher.with(LoginActivity.this)
                    .setListener(LoginActivity.this);
        }, 500);

        showHistoryDialog();

        mLoginType.setText("在线登录");
        SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
    }


    private ListPopup.Builder historyBuilder;

    @SuppressLint("ResourceType")
    private void showHistoryDialog() {

        username_right.setImageResource(R.drawable.login_icon_down);
        boolean fastClick = CommonUtil.isFastClick();
        if (fastClick) {
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

    }

    /**
     * 设置离线用户列表
     */
    private void setOfflineHistoryData() {
        mCurrentReceiveDeviceCode = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceCode, "00000000000000000000000000000000");
//        showHistoryDialog
//        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);
        //查询当前设备是否下载过病例
        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);

        //动态清零用户列表
//        mUserOflineListData.clear();
        if (null != userList && userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                UserDBBean bean = userList.get(i);
                LogUtils.e("用户表====登录====" + bean.getUserName());
                //此时,当前设备下用户表有两种状态一种是下载过的,一种是记住密码的,所以要赛选

                if ("true".equals(bean.getMake01())) {  //存在下载过该用户的数据,获取当前数据设置到UI上
                    mPhoneView.setText("" + bean.getUserName());
                    mPasswordView.setText("" + bean.getPassword());

                    UserListBean.DataDTO mOfflineHistoryBean = new UserListBean.DataDTO();
                    mOfflineHistoryBean.setUserName(bean.getUserName());//caseDBBean.getUserName()   操作这个病例的操作员的名字  用户名字
                    return;
//                    mUserOflineListData.add(mOfflineHistoryBean);
//                    mUserOflineListData.size();

                } else {
                    mPhoneView.setText("");
                    mPasswordView.setText("");
                }


            }
        } else {
            mPhoneView.setText("");
            mPasswordView.setText("");
        }


    }

    private ArrayList<String> getListData() {
        ArrayList<String> mList = new ArrayList<>();
        Boolean loginType = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
        mList.clear();
        if (loginType) { //在线登录
            for (int i = 0; i < mUserListData.size(); i++) {
                mList.add(mUserListData.get(i).getUserName() + "");
                LogUtils.e("用户表===getListData=用户名:" + mUserListData.get(i).getUserName());
            }
            return mList;

        } else {  //离线登录

            List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);

            if (null != userList && userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    UserDBBean bean = userList.get(i);
                    if ("true".equals(bean.getMake01())) {  //存在下载过该用户的数据,获取当前数据设置到UI上
                        LogUtils.e("用户表===mUserOflineListData=用户名:" + bean.getUserName());
                        mList.add(bean.getUserName());
                    }
                }
            }
        }
        return mList;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_commit:
                CharSequence text = mLoginType.getText();
                if (text.equals("在线登录")) {  //在线登录
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
                    LoginByOnline();
                } else if (text.equals("离线登录")) {//离线登录
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, false);
                    setOfflineFirstName();
                    LoginByOffline();
                }


                break;
            case R.id.checkbox_remember: //记住密码
                mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    }
                });
                break;
            case R.id.login_type:
                choseLoginType();
                break;
            case R.id.btn_login_setting: //设备管理界面
                startActivity(DeviceActivity.class);
                break;
        }


    }


    /**
     * 在线登录
     */
    private void LoginByOnline() {
        //动态清零用户列表
        mUserListData.clear();
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
                .url(mUrl + HttpConstant.UserManager_getCurrentRelo)
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
                                //此次需要存入当前登入用户具有的操作权限
                                MMKV kv = MMKV.defaultMMKV();
                                LoginBean.DataDTO.PurviewDTO purviewBean = mBean.getData().getPurview();

                                kv.encode(Constants.KEY_UserMan, purviewBean.isUserMan());//用户管理(用户管理界面能不能进)
                                kv.encode(Constants.KEY_CanPsw, purviewBean.isCanPsw());//设置口令(修改别人密码)
                                kv.encode(Constants.KEY_SnapVideoRecord, purviewBean.isSnapVideoRecord());//拍照录像
                                kv.encode(Constants.KEY_CanNew, purviewBean.isCanNew());  //登记病人(新增病人)
                                kv.encode(Constants.KEY_CanEdit, purviewBean.isCanEdit());//修改病历
                                kv.encode(Constants.KEY_CanDelete, purviewBean.isCanDelete());//删除病历
                                kv.encode(Constants.KEY_CanPrint, purviewBean.isCanPrint()); //打印病历
                                kv.encode(Constants.KEY_UnPrinted, purviewBean.isUnPrinted()); //未打印病历
                                kv.encode(Constants.KEY_OnlySelf, purviewBean.isOnlySelf());//本人病历
                                kv.encode(Constants.KEY_HospitalInfo, purviewBean.isHospitalInfo());//医院信息(不能进入医院信息界面)


                                //存入用户表
                                saveRememberPassword(mBean);

                                /**
                                 * 登入成功的时候切换成监听 当前设备授权登入的socket端口--->Constants.KEY_RECEIVE_PORT
                                 * 退出登入的时候切换成监听 当前广播发送端口(或者设置设备搜索界面设置成功赋值)----->Constants.KEY_RECEIVE_PORT_BY_SEARCH
                                 */
                                ReceiveSocketService receiveSocketService = new ReceiveSocketService();
                                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                if (wifiManager.isWifiEnabled()) {
                                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                                    mAppIP = getIpString(wifiInfo.getIpAddress());
                                }
                                int mCastSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                                if ("".equals(mSocketPort)) {
                                    toast("本地广播发送端口不能为空");
                                    return;
                                } else {
                                    LogUtils.e("AppActivity=login==port====" + mSocketPort);
                                    receiveSocketService.initSettingReceiveThread(mAppIP, Integer.parseInt(mSocketPort), LoginActivity.this);

                                }
                                MainActivity.start(getContext(), CaseManageFragment.class);
                                finish();
                            } else {
//
                                toast("密码错误!!");
                            }

                        } else {
                            showError();
                            toast("返回数据为空!");
                        }

                    }
                });
    }


    //初始化设置离线模式第一个用户UI显示
    private void setOfflineFirstName() {

        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), deviceID);
        //有数据
        if (userList.size() > 0) {
            UserDBBean userDBBean = userList.get(0);
            //第一条数据记住了密码
            if (userDBBean.getIsRememberPassword()) {
                mPhoneView.setText(userDBBean.getUserName() + "");
                mPasswordView.setText(userDBBean.getPassword() + "");
                mCheckbox.setChecked(true);
            } else {
                mPhoneView.setText(userDBBean.getUserName() + "");
            }
        } else {
            mPhoneView.setText("");
            mPasswordView.setText("");
        }

    }

    /**
     * 离线登录
     */
    private void LoginByOffline() {
        // 隐藏软键盘
        hideKeyboard(getCurrentFocus());
        setOfflineHistoryData();

        String username = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        List<UserDBBean> userList = UserDBUtils.getQueryBeanByUserName(getActivity(), username);
        if (null != userList && userList.size() > 0) {
            UserDBBean bean = userList.get(0);
            String password1 = bean.getPassword();
            if (password1.equals(password)) {
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Role, bean.getRelo() + "");
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserName, username);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Password, password);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);
                SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);

                if (mCheckbox.isChecked()) {
                    //记住密码
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, true);
                } else {
                    //没有记住密码
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, false);
                }

                /**
                 * 登入成功的时候切换成监听 当前设备授权登入的socket端口--->Constants.KEY_RECEIVE_PORT
                 * 退出登入的时候切换成监听 当前广播发送端口(或者设置设备搜索界面设置成功赋值)----->Constants.KEY_RECEIVE_PORT_BY_SEARCH
                 */
                MainActivity.start(getContext(), CaseManageOfflineFragment.class);
                finish();
            } else {
                toast("密码错误");
            }


        }


    }


    //选择登录模式
    private void choseLoginType() {
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
                            sendRequest(mBaseUrl);
                        } else if (value.equals("离线登录")) {//离线登录
                            mLoginType.setText("离线登录");
                            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, false);
                            setOfflineHistoryData();
                        }
                    }
                }).show();
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

//        GlideApp.with(this)
//                .load(data.getAvatar())
//                .circleCrop()
//                .into(mLogoView);

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
        deviceID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");


        /**
         * 这里判断
         *
         */
        //选择设备之后,回到此界面默认会写记住密码的第一个用户

//        String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";
//        String strr = "AAC5 01 006A 22 EE 07 00000000000000005618B1F96D92837Ca1 f9432b11b93e8bb4ae34539b7472c20e FD 7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";
//
//        int fd = str.indexOf("FD");
//        String substring = str.substring(fd);
//        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==fd===" + fd);
//        LogUtils.e("========当前设备的备注信息~~~~====LoginActivity==fd=substring==" + substring);
//        String currentCMD = CalculateUtils.getCMD(strr);
//        LogUtils.e("SocketManage回调==currentCMD===" + currentCMD);

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