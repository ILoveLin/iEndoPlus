package com.company.iendo.ui.activity;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 可进行拷贝的副本
 */
public final class CopyActivity extends AppActivity implements StatusAction {

    private StatusLayout mStatusLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.copy_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
    }

    @Override
    protected void initData() {
        RangeSeekBar sb_single6 = findViewById(R.id.sb_single6);
        RangeSeekBar sb_range_5 = findViewById(R.id.sb_single5);
        sb_single6.setIndicatorTextDecimalFormat("0");
        sb_range_5.setIndicatorTextDecimalFormat("0");
        sb_range_5.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //leftValue is left seekbar value, rightValue is right seekbar value
                double round = Math.round(leftValue);
                toast("round==="+round);

            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //stop tracking touch
                float progress = view.getLeftSeekBar().getProgress();
                double round = Math.round(progress);
                LogUtils.e("progress==leftValue=="+round);
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

}