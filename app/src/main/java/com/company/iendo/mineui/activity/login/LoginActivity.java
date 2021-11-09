package com.company.iendo.mineui.activity.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.bean.AddCaseBean;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.mineui.fragment.AFragment;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.activity.HomeActivity;
import com.company.iendo.ui.dialog.TipsDialog;
import com.company.iendo.ui.dialog.WaitDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.LogUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.company.iendo.R;
import com.company.iendo.aop.Log;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.http.api.LoginApi;
import com.company.iendo.http.glide.GlideApp;
import com.company.iendo.http.model.HttpData;
import com.company.iendo.manager.InputTextManager;
import com.company.iendo.other.KeyboardWatcher;
import com.company.iendo.ui.fragment.MineFragment;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengLogin;
import com.hjq.widget.view.PasswordEditText;
import com.hjq.widget.view.SubmitButton;
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
public final class LoginActivity extends AppActivity
        implements UmengLogin.OnLoginListener,
        KeyboardWatcher.SoftKeyboardStateListener,
        TextView.OnEditorActionListener {

    private static final String INTENT_KEY_IN_PHONE = "Admin";
    private static final String INTENT_KEY_IN_PASSWORD = "123";
    private TitleBar mTitleBar;
    private ImageButton username_right;
    private int mPhoneViewWidth;


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
//                    LogUtils.e("path=====录像--是否存在=====" + UserDBRememberBeanUtils.queryListIsExist((String) msg.obj));
                    username_right.setTag("close");
                    username_right.setImageResource(R.drawable.login_icon_down);
                    mPhoneView.setText(""+(String) msg.obj);
//                    if (UserDBRememberBeanUtils.queryListIsExist((String) msg.obj)) {
//                        UserDBRememberBean userDBRememberBean = UserDBRememberBeanUtils.queryListByName((String) msg.obj);
//                        username_right.setTag("close");
//                        username_right.setImageResource(R.drawable.login_icon_down);
//                        if ("Yes".equals(userDBRememberBean.getRemember())) {
//                            checkbox.setChecked(true);
//                            mPhoneView.setText("" + userDBRememberBean.getUsername());
//                            mPasswordView.setText("" + userDBRememberBean.getPassword());
//                        } else {
//                            mPhoneView.setText("" + userDBRememberBean.getUsername());
//                            mPasswordView.setText("");
//                            checkbox.setChecked(false);
//                        }
//                    }
                    break;
            }
        }
    };
    private WaitDialog.Builder mWaitDialog;
    private List<UserListBean.DataDTO> mUserListData;

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

    private ImageView mLogoView;

    private ViewGroup mBodyLayout;
    private EditText mPhoneView;
    private PasswordEditText mPasswordView;

    private SubmitButton mCommitView;


    /**
     * logo 缩放比例
     */
    private final float mLogoScale = 0.8f;
    /**
     * 动画时间
     */
    private final int mAnimTime = 300;

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
        mTitleBar = findViewById(R.id.mtitlebar);

        setOnClickListener(mCommitView);

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
        sendRequest();


    }


    /**
     * 获取列表数据
     */

    private void sendRequest() {
        showLoading();
        OkHttpUtils.get()
                .url(HttpConstant.UserManager_List)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                toast("" + mBean.getData().size());

                                mUserListData = mBean.getData();

                            } else {
                                showError();
                            }
                        } else {
                            showError();
                        }
                    }
                });


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


    @Override
    protected void initData() {
        postDelayed(() -> {
            KeyboardWatcher.with(LoginActivity.this)
                    .setListener(LoginActivity.this);
        }, 500);


        // 自动填充手机号和密码
//        mPhoneView.setText(getString(INTENT_KEY_IN_PHONE));
//        mPasswordView.setText(getString(INTENT_KEY_IN_PASSWORD));


        mPhoneView.setText("Admin");
        mPasswordView.setText("123");


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
                historyBuilder
                        .setList(getListData())
                        .setGravity(Gravity.CENTER)
                        .setAutoDismiss(true)
                        .setOutsideTouchable(false)
                        .setWidth(mPhoneViewWidth + 60)
                        .setXOffset(-30)
                        .setHeight(650)
                        .setAnimStyle(AnimAction.ANIM_SCALE)
                        .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
                                    toast("点击了：" + str);

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

    private  ArrayList<String>  getListData() {
        ArrayList<String> mList = new ArrayList<>();

        for (int i = 0; i < mUserListData.size(); i++) {
            mList.add(mUserListData.get(i).getUserName()+"");
        }
        return mList;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommitView) {
//            if (mPhoneView.getText().toString().length() != 11) {
//                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
//                mCommitView.showError(3000);
//                toast(R.string.common_phone_input_error);
//                return;
//            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            if (true) {
                mCommitView.showProgress();
                postDelayed(() -> {
                    mCommitView.showSucceed();
                    postDelayed(() -> {
                        MainActivity.start(getContext(), AFragment.class);
                        finish();
                    }, 1000);
                }, 2000);
                return;
            }

//            EasyHttp.post(this)
//                    .api(new LoginApi()
//                            .setPhone(mPhoneView.getText().toString())
//                            .setPassword(mPasswordView.getText().toString()))
//                    .request(new HttpCallback<HttpData<LoginApi.Bean>>(this) {
//
//                        @Override
//                        public void onStart(Call call) {
//                            mCommitView.showProgress();
//                        }
//
//                        @Override
//                        public void onEnd(Call call) {}
//
//                        @Override
//                        public void onSucceed(HttpData<LoginApi.Bean> data) {
//                            // 更新 Token
//                            EasyConfig.getInstance()
//                                    .addParam("token", data.getData().getToken());
//                            postDelayed(() -> {
//                                mCommitView.showSucceed();
//                                postDelayed(() -> {
//                                    // 跳转到首页
//                                    HomeActivity.start(getContext(), MineFragment.class);
//                                    finish();
//                                }, 1000);
//                            }, 1000);
//                        }
//
//                        @Override
//                        public void onFail(Exception e) {
//                            super.onFail(e);
//                            postDelayed(() -> {
//                                mCommitView.showError(3000);
//                            }, 1000);
//                        }
//                    });
            return;
        }


        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {

            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {
            }

            @Override
            public void onRightClick(View view) {
            }
        });

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
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", 1f, mLogoScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", 1f, mLogoScale);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", 0f, -mCommitView.getHeight());
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

        if (mLogoView.getTranslationY() == 0) {
            return;
        }

        // 执行放大动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", mLogoScale, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", mLogoScale, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", mLogoView.getTranslationY(), 0f);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
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
}