package com.company.iendo.mineui.fragment.setting;

import android.view.View;

import com.company.iendo.R;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.login.LoginActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.SharePreferenceUtil;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第3个tab-fragment
 */
public class SettingFragment extends TitleBarFragment<MainActivity> {

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_c;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.exit_bar);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_bar:
                SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
                startActivity(LoginActivity.class);
                ActivityManager.getInstance().finishActivity(MainActivity.class);
                break;

        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
