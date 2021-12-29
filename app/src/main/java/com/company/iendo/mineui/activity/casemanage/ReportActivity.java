package com.company.iendo.mineui.activity.casemanage;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.gyf.immersionbar.ImmersionBar;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/2 14:42
 * desc：报告界面
 */
public class ReportActivity extends AppActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        int a = bundle.getInt("A");
        String b = bundle.getString("B");
        toast("Socket通讯模块暂未开发" + a);

    }

    @Override
    protected void initData() {

    }
    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }
}
