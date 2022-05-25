package com.company.iendo.mineui.activity.login;

import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.service.HandService;
import com.company.iendo.service.ReceiveSocketService;
import com.company.iendo.utils.SharePreferenceUtil;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.DaemonEnv;

import static java.lang.Thread.sleep;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/28 15:05
 * desc：
 */
public class SplashActivity extends AppActivity {
    private Boolean isFirstIn;
    private ImageView ivSplash;
    private Boolean isLogined;
    private Gson mGson = new Gson();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        ivSplash = findViewById(R.id.iv_splash_login);
//        ImageView tv_011_text = findViewById(R.id.tv_011_text);
        //是否第一次进入app
        isFirstIn = (Boolean) SharePreferenceUtil.get(this, Constants.SP_IS_FIRST_IN, true);
        //是否登入
        isLogined = (Boolean) SharePreferenceUtil.get(this, Constants.Is_Logined, false);
        // 从浅到深,从百分之10到百分之百
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(1200);// 设置动画时间
//        ivSplash.setAnimation(aa);// 给image设置动画
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                initData();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean workStatue = HandService.getWorkStatue();
        if (!workStatue){
            initHandService();
        }




    }
    /**
     * 开启保活握手服务
     */
    private void initHandService() {

        //初始化
        DaemonEnv.initialize(this, HandService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //是否 任务完成, 不再需要服务运行?
        HandService.sShouldStopService = false;
        //开启服务
        DaemonEnv.startServiceMayBind(HandService.class);

    }
    @Override
    protected void initData() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                switchGoing();
            }
        },1500);
    }


    //判断进入那个activity
    private void switchGoing() {
//            Intent intent = new Intent();
//            if (!isLogined) {  //登入成功 ,false==未登录
//                intent.setClass(SplashActivity.this, LoginAnimatorActivity.class);
//            } else {   //已经登陆
//                intent.setClass(SplashActivity.this, MainActivity.class);
//            }
//            startActivity(intent);
//            finish();
        if (isFirstIn) {
            SharePreferenceUtil.put(SplashActivity.this, Constants.SP_IS_FIRST_IN, true);
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        } else {  //不是第一次进App,判断是否登陆过
            Intent intent = new Intent();
            if (!isLogined) {  //登入成功 ,false==未登录
                intent.setClass(SplashActivity.this, LoginActivity.class);
            } else {   //已经登陆
                intent.setClass(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();

        }

    }
}
