package com.company.iendo.mineui.activity.login;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.ui.adapter.GuideAdapter;
import com.hjq.base.BaseDialog;

import me.relex.circleindicator.CircleIndicator3;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/09/21
 * desc   : 应用引导页
 */
public final class GuideActivity extends AppActivity {

    private ViewPager2 mViewPager;
    private CircleIndicator3 mIndicatorView;
    private View mCompleteView;

    private GuideAdapter mAdapter;
    private Boolean isLogined;
    private Boolean userAgreementTag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_guide_pager);
        mIndicatorView = findViewById(R.id.cv_guide_indicator);
        mCompleteView = findViewById(R.id.btn_guide_complete);
        setOnClickListener(mCompleteView);
    }

    @Override
    protected void initData() {
        mAdapter = new GuideAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.registerOnPageChangeCallback(mCallback);
        mIndicatorView.setViewPager(mViewPager);

        userAgreementTag = (Boolean) SharePreferenceUtil.get(GuideActivity.this, Constants.Sp_UserAgreement_Tag, false);
        isLogined = (Boolean) SharePreferenceUtil.get(this, Constants.Is_Logined, false);


        if (!userAgreementTag) {
            showUserAgreementDialog();
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCompleteView) {
//            HomeActivity.start(getContext());
            if (!isLogined) {  //未登入,跳转登入界面
                Intent intent = new Intent();
                intent.setClass(GuideActivity.this, LoginActivity.class);
//                intent.setClass(GuideActivity.this, LoginActivity.class);
                startActivity(intent);
                SharePreferenceUtil.put(GuideActivity.this, Constants.SP_IS_FIRST_IN, false);
                SharePreferenceUtil.put(GuideActivity.this, Constants.Sp_UserAgreement_Tag, true);
            } else {
                SharePreferenceUtil.put(GuideActivity.this, Constants.SP_IS_FIRST_IN, false);   //false 不是第一次登入了
                SharePreferenceUtil.put(GuideActivity.this, Constants.Is_Logined, false);
                mMMKVInstace.encode(Constants.KEY_Login_Tag, false);//是否登入成功

                startActivity(new Intent(GuideActivity.this, MainActivity.class));
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.unregisterOnPageChangeCallback(mCallback);
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPager.getCurrentItem() != mAdapter.getCount() - 1 || positionOffsetPixels <= 0) {
                return;
            }

            mIndicatorView.setVisibility(View.VISIBLE);
            mCompleteView.setVisibility(View.INVISIBLE);
            mCompleteView.clearAnimation();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return;
            }

            boolean lastItem = mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
            mIndicatorView.setVisibility(lastItem ? View.INVISIBLE : View.VISIBLE);
            mCompleteView.setVisibility(lastItem ? View.VISIBLE : View.INVISIBLE);

            if (lastItem) {
                // 按钮呼吸动效
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(350);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(Animation.INFINITE);
                mCompleteView.startAnimation(animation);
            }



        }
    };


    /**
     * 用户协议dialog 不然上不了应用市场
     */
    private void showUserAgreementDialog() {
        SpannableString textSpanned1 = new SpannableString("在你使用CME Player之前，请你认真阅读并了解《iEndo用户协议》和《iEndo隐私权政策》,点击同意即表示你已阅读并且了解。");
        //设置颜色
        textSpanned1.setSpan(new ForegroundColorSpan(Color.BLUE),
                26, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSpanned1.setSpan(new ForegroundColorSpan(Color.BLUE),
                43, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置Hello World前三个字符有点击事件
//        SpannableStringBuilder textSpanned4 = new SpannableStringBuilder("Hello World");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("typeUrl", "1");
//                openActivity(SomeRequestActivity.class, bundle);
                Toast.makeText(GuideActivity.this, "用户协议", Toast.LENGTH_SHORT).show();

            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GuideActivity.this, "隐私权", Toast.LENGTH_SHORT).show();

//                Bundle bundle = new Bundle();
//                bundle.putString("typeUrl", "2");
//                openActivity(SomeRequestActivity.class, bundle);
            }
        };
        textSpanned1.setSpan(clickableSpan,
                26, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSpanned1.setSpan(clickableSpan2,
                43, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//注意：此时必须加这一句，不然点击事件不会生效
//        text4.setMovementMethod(LinkMovementMethod.getInstance());
//        text4.setText(textSpanned4);
        int screenWidth = ScreenSizeUtil.getScreenWidth(this);
        double v = screenWidth * 0.7;


        // 自定义对话框
        BaseDialog.Builder<BaseDialog.Builder<?>> mUserAgreementDialog = new BaseDialog.Builder<>(this);
        mUserAgreementDialog
                .setContentView(R.layout.dialog_useragreement)
                .setAnimStyle(BaseDialog.ANIM_IOS)
                .setCanceledOnTouchOutside(false)
                .setWidth((int) v)
                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        SharePreferenceUtil.put(GuideActivity.this, Constants.Sp_UserAgreement_Tag, true);
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.btn_dialog_custom_cancle, new  BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        ActivityManager.getInstance().finishAllActivities();

                    }
                })
                .show();

        //注意：此时必须加这一句，不然点击事件不会生效
        TextView viewById = mUserAgreementDialog.findViewById(R.id.tv_content);
        viewById.setMovementMethod(LinkMovementMethod.getInstance());


//
//        // 自定义对话框
//        BaseDialog.Builder<BaseDialog.Builder> builderBuilder = new BaseDialog.Builder<>(this);
//        builderBuilder.setContentView(R.layout.dialog_useragreement)
//                .setAnimStyle(BaseDialog.ANIM_IOS)
//                .setCanceledOnTouchOutside(false)
//                .setWidth((int)v )
//                .setText(R.id.tv_content, textSpanned1)
//                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener() {
//                    @Override
//                    public void onClick(BaseDialog dialog, View view) {
//                        SharePreferenceUtil.put(GuideActivity.this, Constants.Sp_UserAgreement_Tag, true);
//                        dialog.dismiss();
//                    }
//                })
//                .setOnClickListener(R.id.btn_dialog_custom_cancle, new BaseDialog.OnClickListener() {
//                    @Override
//                    public void onClick(BaseDialog dialog, View view) {
//                        Toast.makeText(GuideActivity.this, "Dialog 取消了", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
////                .addOnShowListener(dialog -> Toast.makeText(this, "Dialog 显示了", Toast.LENGTH_SHORT).show())
////                .addOnCancelListener(new BaseDialog.OnCancelListener() {
////                    @Override
////                    public void onCancel(BaseDialog dialog) {
////
////                    }
////                })
////                .addOnDismissListener(dialog -> Toast.makeText(this, "Dialog 销毁了", Toast.LENGTH_SHORT).show())
////                .setOnKeyListener((dialog, event) -> {
////                    Toast.makeText(this, "按键代码：" + event.getKeyCode(), Toast.LENGTH_SHORT).show();
////                    return false;
////                })
//                .show();
//        //注意：此时必须加这一句，不然点击事件不会生效
//        TextView viewById = builderBuilder.findViewById(R.id.tv_content);
//        viewById.setMovementMethod(LinkMovementMethod.getInstance());


    }


}