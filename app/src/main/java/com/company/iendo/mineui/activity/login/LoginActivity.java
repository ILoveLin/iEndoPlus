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
import com.company.iendo.bean.LoginBean;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.green.db.DeviceDBUtils;
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
import com.company.iendo.service.HandService;
import com.company.iendo.service.ReceiveSocketService;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengLogin;
import com.hjq.widget.view.PasswordEditText;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android ?????????
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : ????????????
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
                case 1:  //?????????????????????????????????
                    username_right.setTag("close");
                    username_right.setImageResource(R.drawable.login_icon_down);
                    String userName = (String) msg.obj;
                    mPhoneView.setText("" + (String) msg.obj);
                    //??????????????????????????????????????????(?????????????????????)
                    Boolean isSave = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, false);
                    //???????????????????????????,????????????????????????????????????
                    refreshUI(userName);
                    break;
            }
        }
    };
    private TextView mDeviceTitle;
    private UserReloBean.DataDTO mUserReloBean;

    //??????????????????????????????UI
    private void refreshUI(String userName) {
        Boolean loginType = (Boolean) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
        if (loginType) {//????????????
            List<UserDBBean> userDBBeans = UserDBUtils.getQueryBeanByTow(getApplicationContext(), deviceID, userName);
            //???????????????
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
        } else {//????????????
            List<UserDBBean> userList = UserDBUtils.getQueryBeanByTow(getActivity(), mCurrentReceiveDeviceCode, userName);
            if (null != userList && userList.size() > 0) {
                UserDBBean bean = userList.get(0);
                mPhoneView.setText(userName);
                //????????????
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
     * ??????????????????,????????????????????????,??????????????????????????????
     *
     * @param mBean
     */
    private void saveRememberPassword(LoginBean mBean) {
        //?????????????????????

        if (mCheckbox.isChecked()) { //????????????????????????????????????
            /**
             * ??????ID
             * ????????????????????????????????????     ???????????????????????????????????????     :????????????????????????????????? deviceID ==  deviceUserID  ?????????????????????????????????deviceCaseID
             * ???????????????????????????id,?????????????????????????????????????????????id????????????????????????????????????
             */
            String deviceID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");
            LoginBean.DataDTO bean = mBean.getData();

            //??????????????????,????????????ID??????????????????
            List<UserDBBean> beanList = UserDBUtils.getQueryBeanByTowCodeUserID(LoginActivity.this, deviceID, bean.getUserID());
            if (beanList.size() > 0) {//????????????????????????
                for (int i = 0; i < beanList.size(); i++) {
                    UserDBBean dbBean = beanList.get(i);
                    //???????????????ID,????????????
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
                //????????????
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, true);
                SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);

                UserDBUtils.insertOrReplaceInTx(LoginActivity.this, newBean);
            }


        } else {
            //??????????????????
            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, false);
        }

    }

    private void initRememberPassword() {

        String deviceid = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");
//        LogUtils.e("initRememberPassword====isSave:" + isSave);
        String userName = mPhoneView.getText().toString().trim();
        //??????  0000000000000000ED3A93DA80A9BA8B
        //?????????0000000000000000546017FE6BC28949
//        if (isSave) {//??????????????????????????????????????????????????????--???????????????????????????,????????????????????????????????????????????????????????????bug
        //?????????deviceID?????????,??????????????????
        List<UserDBBean> userDBBeans = UserDBUtils.getQueryByDeviceID(getApplicationContext(), deviceID);
        for (int i = 0; i < userDBBeans.size(); i++) {
            UserDBBean userDBBean = userDBBeans.get(i);
            userDBBean.getUserName();
            userDBBean.getPassword();
            userDBBean.getDeviceID();
            userDBBean.getIsRememberPassword();
        }

        //???????????????
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
        mDeviceTitle = findViewById(R.id.btn_device_title);
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
     * ??????????????????
     *
     * @param mBaseUrl
     */

    private void sendRequest(String mBaseUrl) {

        List queryBeanBySelected = DeviceDBUtils.getQueryBeanBySelected(LoginActivity.this, true);

        if (queryBeanBySelected.size() == 0) {
            return;
        }
        showLoading(getString(R.string.common_loading_user_list));
        String mUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
        OkHttpUtils.get()
                .url(mUrl + HttpConstant.UserManager_List)
                .addParams("type", "account")
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
                        mUserListData.clear();
                        if ("" != response) {
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
                            if (0 == mBean.getCode()) {  //??????
                                showComplete();
                                mUserListData = mBean.getData();
                                setOnLineFirstName(mUserListData);

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
     * ??????????????????????????????????????????????????????
     *
     * @param mUserListData ?????????????????????????????????
     */
    private void setOnLineFirstName(List<UserListBean.DataDTO> mUserListData) {
        if (mUserListData.size() > 0) {
            UserListBean.DataDTO bean = mUserListData.get(0);
            //??????????????????????????????????????????
            List<UserDBBean> dbList = UserDBUtils.getQueryBeanByTowCodeUserID(LoginActivity.this, deviceID, bean.getUserID());
            //?????????
            if (dbList.size() > 0) {
                UserDBBean userDBBean = dbList.get(0);
                //???????????????????????????????????????
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
                //????????????
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

        mLoginType.setText("????????????");
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
                    if ("close".equals(username_right.getTag())) {
                        username_right.setTag("open");
                        username_right.setImageResource(R.drawable.login_icon_up);

                    } else {
                        username_right.setTag("close");
                        username_right.setImageResource(R.drawable.login_icon_down);
                    }

                    String string = getResources().getString(R.dimen.dp_74);
                    String dip = string.replace("dip", "");
                    Float mFloatDate = Float.valueOf(dip).floatValue();
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
                        toast("???????????????");

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
     * ????????????????????????
     */
    private void setOfflineHistoryData() {
        mCurrentReceiveDeviceCode = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceCode, "00000000000000000000000000000000");
//        showHistoryDialog
//        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);
        //???????????????????????????????????????
        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);


        if (null != userList && userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                UserDBBean bean = userList.get(i);
                //??????,????????????????????????????????????????????????????????????,????????????????????????,???????????????

                if ("true".equals(bean.getMake01())) {  //?????????????????????????????????,???????????????????????????UI???
                    mPhoneView.setText("" + bean.getUserName());
                    mPasswordView.setText("" + bean.getPassword());

                    UserListBean.DataDTO mOfflineHistoryBean = new UserListBean.DataDTO();
                    mOfflineHistoryBean.setUserName(bean.getUserName());//caseDBBean.getUserName()   ???????????????????????????????????????  ????????????
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
        if (loginType) { //????????????
            for (int i = 0; i < mUserListData.size(); i++) {
                mList.add(mUserListData.get(i).getUserName() + "");
            }
            return mList;

        } else {  //????????????

            List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);

            if (null != userList && userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    UserDBBean bean = userList.get(i);
                    if ("true".equals(bean.getMake01())) {  //?????????????????????????????????,???????????????????????????UI???
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
                if (text.equals("????????????")) {  //????????????
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
                    LoginByOnline();
                } else if (text.equals("????????????")) {//????????????
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, false);
                    LoginByOffline();
                }


                break;
            case R.id.checkbox_remember: //????????????
                mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    }
                });
                break;
            case R.id.login_type:
                choseLoginType();
                break;
            case R.id.btn_login_setting: //??????????????????
                startActivity(DeviceActivity.class);
                break;
        }


    }


    /**
     * ????????????
     */
    private void LoginByOnline() {
        //????????????????????????
        // ???????????????
        hideKeyboard(getCurrentFocus());
        String mUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
//                ??????????????????
        showLoading(getString(R.string.common_loading_login));

//        requestReloSaveToApp();
        OkHttpUtils.post()
                .url(mUrl + HttpConstant.UserManager_Login)
                .addParams("UserName", mPhoneView.getText().toString())
                .addParams("Password", MD5ChangeUtil.Md5_32(mPasswordView.getText().toString()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mPasswordView.setText("");
                        mPhoneView.setText("");
                        showComplete();
                        if (null != mUserListData) {
                            boolean currentUsername = mUserListData.contains(mPasswordView.getText().toString());
                            if (!currentUsername) {
                                toast("???????????????");
                            } else {
                                toast("?????????????????????");
                            }
                        } else {
                            toast("?????????????????????");

                        }


                    }

                    @Override
                    public void onResponse(String response, int id) {
                        showComplete();
                        if (!"".equals(response)) {
                            LoginBean mBean = mGson.fromJson(response, LoginBean.class);
                            if (0 == mBean.getCode()) {
                                mUserListData.clear();
                                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Role, mBean.getData().getRole() + "");
                                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserID, mBean.getData().getUserID() + "");
//                                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserName, mPhoneView.getText().toString());
                                mMMKVInstace.encode(Constants.KEY_CurrentLoginUserName, mPhoneView.getText().toString());
                                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Password, mPasswordView.getText().toString());
                                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);
                                SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);
                                //?????????????????????????????????????????????????????????
                                MMKV kv = MMKV.defaultMMKV();
                                LoginBean.DataDTO.PurviewDTO purviewBean = mBean.getData().getPurview();
                                if (null != purviewBean) {
                                    kv.encode(Constants.KEY_Login_Tag, true);//??????????????????
                                    kv.encode(Constants.KEY_UserMan, purviewBean.isUserMan());//????????????(??????????????????????????????)
                                    kv.encode(Constants.KEY_CanPsw, purviewBean.isCanPsw());//????????????(??????????????????)
                                    kv.encode(Constants.KEY_SnapVideoRecord, purviewBean.isSnapVideoRecord());//????????????
                                    kv.encode(Constants.KEY_CanNew, purviewBean.isCanNew());  //????????????(????????????)
                                    kv.encode(Constants.KEY_CanEdit, purviewBean.isCanEdit());//????????????
                                    kv.encode(Constants.KEY_CanDelete, purviewBean.isCanDelete());//????????????
                                    kv.encode(Constants.KEY_CanPrint, purviewBean.isCanPrint()); //????????????
                                    kv.encode(Constants.KEY_UnPrinted, purviewBean.isUnPrinted()); //???????????????
                                    kv.encode(Constants.KEY_OnlySelf, purviewBean.isOnlySelf());//????????????
                                    kv.encode(Constants.KEY_HospitalInfo, purviewBean.isHospitalInfo());//????????????(??????????????????????????????)

                                }
                                //???????????????
                                saveRememberPassword(mBean);
                                /**
                                 * ???????????????????????????????????? ???????????????????????????socket??????--->Constants.KEY_RECEIVE_PORT
                                 * ???????????????????????????????????? ????????????????????????(????????????????????????????????????????????????)----->Constants.KEY_RECEIVE_PORT_BY_SEARCH
                                 */
                                MainActivity.start(getContext(), CaseManageFragment.class);
                                //???????????????????????????
                                initHandService();
                                //??????????????????
                                HandBean handBean = new HandBean();
                                handBean.setHelloPc("");
                                handBean.setComeFrom("");
                                byte[] sendByteData = CalculateUtils.getSendByteData(LoginActivity.this, mGson.toJson(handBean), mCurrentTypeNum+"", mCurrentReceiveDeviceCode,
                                        Constants.UDP_HAND);

                                if (("".equals(mSocketPort))) {
                                    toast("????????????????????????");
                                    return;
                                }

                                SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), LoginActivity.this);
                                SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), LoginActivity.this);
                                finish();
                            } else {
                                toast("????????????");
                            }

                        } else {
                            showError();
                            toast("?????????????????? ");
                        }

                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void initHandService() {

        //?????????
        WeakReference<Context> mWeakContext = new WeakReference<>(this);
        DaemonEnv.initialize(mWeakContext.get(), HandService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //?????? ????????????, ?????????????????????????
        HandService.sShouldStopService = false;
        //????????????
        DaemonEnv.startServiceMayBind(HandService.class);

    }

    /**
     * ????????????????????????app
     */
    private void requestReloSaveToApp() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_getCurrentRelo)
                .addParams("UserID", mUserID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {


                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!"".equals(response)) {
                            UserReloBean mBean = mGson.fromJson(response, UserReloBean.class);
                            if (0 == mBean.getCode()) {
                                mUserReloBean = mBean.getData();
//                                mMMKVInstace.encode(Constants.KEY_UserMan, bean.isUserMan());//????????????(??????????????????????????????)
//                                mMMKVInstace.encode(Constants.KEY_CanPsw, bean.isCanPsw());//????????????(??????????????????)
//                                mMMKVInstace.encode(Constants.KEY_SnapVideoRecord, bean.isSnapVideoRecord());//????????????
//                                mMMKVInstace.encode(Constants.KEY_CanNew, bean.isCanNew());  //????????????(????????????)
//                                mMMKVInstace.encode(Constants.KEY_CanEdit, bean.isCanEdit());//????????????
//                                mMMKVInstace.encode(Constants.KEY_CanDelete, bean.isCanDelete());//????????????
//                                mMMKVInstace.encode(Constants.KEY_CanPrint, bean.isCanPrint()); //????????????
//                                mMMKVInstace.encode(Constants.KEY_UnPrinted, bean.isUnPrinted()); //???????????????
//                                mMMKVInstace.encode(Constants.KEY_OnlySelf, bean.isOnlySelf());//????????????
//                                mMMKVInstace.encode(Constants.KEY_HospitalInfo, bean.isHospitalInfo());//????????????(??????????????????????????????)
                            }
                        }

                    }
                });
    }

    //??????????????????????????????????????????UI??????
    private void setOfflineFirstName() {

        List<UserDBBean> userList = UserDBUtils.getQueryBeanByCode(getActivity(), deviceID);
        //?????????
        if (userList.size() > 0) {
            UserDBBean userDBBean = userList.get(0);
            //??????????????????????????????
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
     * ????????????
     */
    private void LoginByOffline() {
        // ???????????????
        hideKeyboard(getCurrentFocus());
//        setOfflineHistoryData();

        String username = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();
        /**
         * ?????????????????????,???????????????????????????,????????????
         */
        List<UserDBBean> userList = UserDBUtils.getQueryBeanByTow(getActivity(), mCurrentReceiveDeviceCode, username);
        if (null != userList && userList.size() > 0) {
            UserDBBean bean = userList.get(0);
            String password1 = bean.getPassword();
            if (password1.equals(password)) {
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Role, bean.getRelo() + "");
//                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_UserName, username);
                mMMKVInstace.encode(Constants.KEY_CurrentLoginUserName, mPhoneView.getText().toString());
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Password, password);
                SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Flag_UserDBSave, true);
                SharePreferenceUtil.put(LoginActivity.this, Constants.Is_Logined, true);

                if (mCheckbox.isChecked()) {
                    //????????????
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, true);
                } else {
                    //??????????????????
                    SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.Current_Login_Remember_Password, false);
                }

                /**
                 * ???????????????????????????????????? ???????????????????????????socket??????--->Constants.KEY_RECEIVE_PORT
                 * ???????????????????????????????????? ????????????????????????(????????????????????????????????????????????????)----->Constants.KEY_RECEIVE_PORT_BY_SEARCH
                 */
                MainActivity.start(getContext(), CaseManageOfflineFragment.class);
                finish();
            } else {
                toast("????????????");
            }


        }


    }


    //??????????????????
    private void choseLoginType() {
        //?????????????????????
        new SelectDialog.Builder(getActivity())
                .setTitle("?????????")
                .setList("????????????", "????????????")
                .setSingleSelect()
                .setSelect(0)
                .setListener(new SelectDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, HashMap data) {
                        String string = data.toString();
                        int size = data.size();
                        int i = string.indexOf("=");
                        String value = string.substring(i + 1, string.length() - 1);
                        if (value.equals("????????????")) {  //????????????
                            mLoginType.setText("????????????");
                            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, true);
                            sendRequest(mBaseUrl);
                        } else if (value.equals("????????????")) {//????????????
                            mLoginType.setText("????????????");
                            SharePreferenceUtil.put(LoginActivity.this, SharePreferenceUtil.OnLine_Flag, false);
//                            setOfflineHistoryData();
                            setOfflineFirstName();

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
     * ?????????????????????
     *
     * @param platform ????????????
     * @param data     ??????????????????
     */
    @Override
    public void onSucceed(Platform platform, UmengLogin.LoginData data) {

//        GlideApp.with(this)
//                .load(data.getAvatar())
//                .circleCrop()
//                .into(mLogoView);

    }

    /**
     * ?????????????????????
     *
     * @param platform ????????????
     * @param t        ????????????
     */
    @Override
    public void onError(Platform platform, Throwable t) {
        toast("????????????????????????" + t.getMessage());
    }

    /**
     * {@link KeyboardWatcher.SoftKeyboardStateListener}
     */

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        // ??????????????????
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -mCommitView.getHeight());
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        // ??????????????????
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
        // ??????????????????
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0f);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        if (mTopLogoAnim.getTranslationY() == 0) {
            return;
        }

        // ??????????????????
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
        mBaseUrl = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");
        postDelayed(() -> {
            sendRequest(mBaseUrl);
        }, 500);
        String mType = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Type, "??????????????????");
        deviceID = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceID, "1");
        setDeviceTitleLogo();

        /**
         * ????????????
         *
         */
        //??????????????????,?????????????????????????????????????????????????????????

//        String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";
//        String strr = "AAC5 01 006A 22 EE 07 00000000000000005618B1F96D92837Ca1 f9432b11b93e8bb4ae34539b7472c20e FD 7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD";
//
//        int fd = str.indexOf("FD");
//        String substring = str.substring(fd);
//        LogUtils.e("========???????????????????????????~~~~====LoginActivity==fd===" + fd);
//        LogUtils.e("========???????????????????????????~~~~====LoginActivity==fd=substring==" + substring);
//        String currentCMD = CalculateUtils.getCMD(strr);
//        LogUtils.e("SocketManage??????==currentCMD===" + currentCMD);

    }

    /**
     * eventbus ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        setDeviceTitleLogo();
    }

    /**
     * ????????????logo?????????
     *
     * @return
     */
    @SuppressLint("ResourceType")
    private void setDeviceTitleLogo() {
        List queryBeanBySelected = DeviceDBUtils.getQueryBeanBySelected(LoginActivity.this, true);
        String mCurrentTypeMsg = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Type_Msg, "1????????????");
        String mCurrentTypeDes = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_Type, "???????????????");
        String mCurrentDeviceName = (String) SharePreferenceUtil.get(LoginActivity.this, SharePreferenceUtil.Current_DeviceName, "ENT");

        if (0 == queryBeanBySelected.size()) {//????????????????????????,?????????????????????,?????????????????????
            mDeviceType.setText("");
            mDeviceTitle.setText("???????????????!");
            mDeviceTitle.setTextColor(getResources().getColor(R.color.red));
            mUserListData.clear();
            mLogoView.setImageResource(R.drawable.icon_bg_default);
        } else {
            //????????????
            mDeviceType.setText("" + mCurrentDeviceName);
            //????????????
            mDeviceTitle.setText(mCurrentTypeMsg + "");
            mDeviceTitle.setTextColor(getResources().getColor(R.color.color_707070));
            //???????????????
            switch (mCurrentTypeDes) {
                case Constants.Type_V1_YiTiJi:
                    mLogoView.setImageResource(R.drawable.icon_yitiji);
                    break;
                case Constants.Type_EarNoseTable:
                    mLogoView.setImageResource(R.drawable.icon_erbihou);
                    break;
                case Constants.Type_FuKeTable:
                    mLogoView.setImageResource(R.drawable.icon_erbihou);
                case Constants.Type_MiNiaoTable:
                    mLogoView.setImageResource(R.drawable.icon_erbihou);
                    break;
            }

        }
    }


    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // ????????????????????????
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // ???????????????????????????
                .navigationBarColor(R.color.white);
    }

    private void showError() {
        // ???????????????
        new TipsDialog.Builder(this)
                .setIcon(TipsDialog.ICON_ERROR)
                .setMessage("??????")
                .show();

    }

    private void showComplete() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }

    private void showLoading(String string) {
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog.Builder(this);
            // ??????????????????????????????
            mWaitDialog.setMessage(string)
                    .create();
        } else {
            mWaitDialog.setMessage(string);
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
//            postDelayed(mWaitDialog::dismiss, 2000);
        }
    }

}