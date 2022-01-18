package com.company.iendo.mineui.activity.setting;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.widget.view.SwitchButton;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/1/18 13:50
 * desc：设备参数界面
 */
public class DeviceParamsActivity extends AppActivity implements StatusAction, OnRangeChangedListener, SwitchButton.OnCheckedChangeListener, TextWatcher, View.OnFocusChangeListener {
    private StatusLayout mStatusLayout;
    private RangeSeekBar mBalance;
    private RangeSeekBar mPressure;
    private RangeSeekBar mFlow;
    private TitleBar mTitleBar;
    private SwitchButton mSwitchBlood;
    private SwitchButton mSwitchLight;
    private String mCurrentDeviceType;
    private String mCurrentDeviceCode;
    private TextView mDeviceType;
    private TextView mDeviceCode;
    private EditText mEditBalance;
    private EditText mEditPressure;
    private EditText mEditFlow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_params;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.titlebar);
        mDeviceType = findViewById(R.id.tv_device_type);
        mDeviceCode = findViewById(R.id.tv_device_code_num);

        mEditBalance = findViewById(R.id.edit_light);
        mEditPressure = findViewById(R.id.edit_pressure);
        mEditFlow = findViewById(R.id.edit_flow);

        mSwitchLight = findViewById(R.id.sb_find_switch_light);
        mSwitchBlood = findViewById(R.id.sb_find_switch_blood);

        mBalance = findViewById(R.id.sb_single_balance);
        mPressure = findViewById(R.id.sb_single_pressure);
        mFlow = findViewById(R.id.sb_single_flow);

        responseListener();
    }

    private void responseListener() {
        mBalance.setOnRangeChangedListener(this);
        mPressure.setOnRangeChangedListener(this);
        mFlow.setOnRangeChangedListener(this);

        mSwitchLight.setOnCheckedChangeListener(this);
        mSwitchBlood.setOnCheckedChangeListener(this);

        mEditBalance.addTextChangedListener(this);
        mEditPressure.addTextChangedListener(this);
        mEditFlow.addTextChangedListener(this);

        mEditBalance.setOnFocusChangeListener(this);
        mEditPressure.setOnFocusChangeListener(this);
        mEditFlow.setOnFocusChangeListener(this);

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                setInitData();

            }
        });
    }

    //重置数据
    private void setInitData() {

    }


    @Override
    protected void initData() {
        mCurrentDeviceType = (String) SharePreferenceUtil.get(DeviceParamsActivity.this, SharePreferenceUtil.Current_Type, "");
        mCurrentDeviceCode = (String) SharePreferenceUtil.get(DeviceParamsActivity.this, SharePreferenceUtil.Current_DeviceCode, "");
        mBalance.setIndicatorTextDecimalFormat("0");
        mPressure.setIndicatorTextDecimalFormat("0");
        mFlow.setIndicatorTextDecimalFormat("0");
        mDeviceType.setText("" + mCurrentDeviceType);
        mDeviceCode.setText("" + mCurrentDeviceCode);
        mBalance.setIndicatorTextDecimalFormat("0");

        mBalance.setProgress(12);
        mPressure.setProgress(50);
        mFlow.setProgress(66);

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
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

    }

    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
        float progress = view.getLeftSeekBar().getProgress();
        String round = (Math.round(progress) + "").replace(".", "");


        switch (view.getId()) {
            case R.id.sb_single_balance:
                LogUtils.e("progress==balance==" + round);
                toast("白平衡===" + round);
                mEditBalance.setText("" + round);
                break;
            case R.id.sb_single_pressure:
                LogUtils.e("progress==pressure==" + round);
                toast("实时压力===" + round);
                mEditPressure.setText("" + round);
                break;
            case R.id.sb_single_flow:
                LogUtils.e("progress==flow==" + round);
                toast("流量===" + round);
                mEditFlow.setText("" + round);
                break;
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        switch (button.getId()) {
            case R.id.sb_find_switch_light:
                LogUtils.e("progress==SwitchButton-----Light==" + checked);
                toast("Light==" + checked);
                break;
            case R.id.sb_find_switch_blood:
                LogUtils.e("progress==SwitchButton-----Blood==" + checked);
                toast("Blood==" + checked);
                break;

        }
    }

    /**
     * 数值0-100的限制监听
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Pattern p = Pattern.compile("^(100|[1-9]\\d|\\d)$");//处理0~100正则
        Matcher m = p.matcher(s.toString());
        if (m.find() || ("").equals(s.toString())) {
        } else {
            toast("请输入正确的数值!");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 焦点的监听
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edit_light:
                if (!hasFocus) {   //失去焦点
                    mBalance.setProgress(Integer.parseInt(mEditBalance.getText().toString().trim()));
                }
                break;
            case R.id.edit_pressure:
                if (!hasFocus) {   //失去焦点
                    mPressure.setProgress(Integer.parseInt(mEditPressure.getText().toString().trim()));
                }
                break;
            case R.id.edit_flow:
                if (!hasFocus) {   //失去焦点
                    mFlow.setProgress(Integer.parseInt(mEditFlow.getText().toString().trim()));
                }
                break;
        }

    }
}
