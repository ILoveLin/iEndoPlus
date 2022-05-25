package com.company.iendo.mineui.activity.casemanage;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/2 14:42
 * desc：报告预览界面
 */

public class ReportActivity extends AppActivity implements StatusAction {
    private static final int UDP_Receive = 135;
    private static final int UDP_Hand = 136;
    private static final int UDP_Report_Url = 137;
    private static final int UDP_Print_Report = 138;
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功
    private StatusLayout mStatusLayout;
    private ImageView mReport;
    private TitleBar mTitlebar;


    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);
        mTitlebar = findViewById(R.id.titlebar);
        mReport = findViewById(R.id.iv_report);

    }

    @Override
    protected void initData() {
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
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


    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initReceiveThread();
        //握手通讯
//        sendHandLinkMessage();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
