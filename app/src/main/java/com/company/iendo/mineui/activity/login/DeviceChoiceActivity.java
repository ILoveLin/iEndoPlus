package com.company.iendo.mineui.activity.login;

import android.view.View;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.hjq.shape.view.ShapeTextView;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 设备选择界面
 */
public final class DeviceChoiceActivity extends AppActivity {

    private ShapeTextView mStvDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.device_choice_activity;
    }

    @Override
    protected void initView() {
        mStvDevice = findViewById(R.id.stv_device);
        setOnClickListener(R.id.stv_device);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stv_device:
                startActivity(LoginActivity.class);
                break;
        }
    }
}