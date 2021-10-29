package com.company.iendo.mineui.fragment;

import com.company.iendo.R;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.mineui.activity.MainActivity;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第3个tab-fragment
 */
public class BFragment extends TitleBarFragment<MainActivity> {

    public static BFragment newInstance(){
        return new BFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_b;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
