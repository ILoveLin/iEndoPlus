package com.company.iendo.mineui.activity.login;

import android.view.View;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 15:59
 * desc：设备添加界面
 */
public class DeviceActivity extends AppActivity {

    private TitleBar mDeviceBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device;
    }

    @Override
    protected void initView() {
        mDeviceBar = findViewById(R.id.device_bar);
    }

    @Override
    protected void initData() {
        mDeviceBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                toast("添加设备");
            }
        });
    }
}
