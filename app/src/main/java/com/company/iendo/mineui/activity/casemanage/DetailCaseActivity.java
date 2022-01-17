package com.company.iendo.mineui.activity.casemanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.DetailFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.PictureFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.VideoFragment;
import com.company.iendo.mineui.activity.vlc.GetPictureActivity;
import com.company.iendo.ui.adapter.TabAdapter;
import com.company.iendo.utils.LogUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.widget.layout.NestedViewPager;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 13:58
 * desc：详情界面---主架构界面
 */
public class DetailCaseActivity extends AppActivity implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener {
    private NestedViewPager mViewPager;
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;
    private TitleBar mTitlebar;
    private TextView mDown;
    private TextView mDelete;
    private TextView mReport;
    private TextView mPicture;
    private Boolean mFatherExit;   //父类Activity 是否主动退出的标识,主动退出需要请求保存fragment的更新数据

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_detail;
    }

    @Override
    protected void initView() {
        mTabView = findViewById(R.id.rv_detail_tab);
        mViewPager = findViewById(R.id.vp_detail_pager);
        mTitlebar = findViewById(R.id.titlebar);
        mPicture = findViewById(R.id.case_picture);
        mReport = findViewById(R.id.case_report);
        mDelete = findViewById(R.id.case_delete);
        mDown = findViewById(R.id.case_down);
        mFatherExit = false;
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DetailFragment.newInstance(), "详情");
        mPagerAdapter.addFragment(PictureFragment.newInstance(), "图片");
        mPagerAdapter.addFragment(VideoFragment.newInstance(), "视频");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        setOnClickListener(R.id.linear_get_picture, R.id.case_report, R.id.case_delete, R.id.case_down, R.id.linear_down);
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                //退出界面的时候必须保存数据
                if (null != mOnEditStatusListener) {
                    mOnEditStatusListener.onEditStatus(true, true);
                }
                postDelayed(() -> {
                    finish();
                }, 300);
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                if (mTitlebar.getRightTitle().equals("编辑")) {
                    if (null != mOnEditStatusListener) {
                        mOnEditStatusListener.onEditStatus(true, false);
                    }
                    mTitlebar.setRightTitle("保存");
                    mTitlebar.setRightTitleColor(getResources().getColor(R.color.red));
                } else {
                    mTitlebar.setRightTitle("编辑");
                    mTitlebar.setRightTitleColor(getResources().getColor(R.color.black));
                    if (null != mOnEditStatusListener) {
                        mOnEditStatusListener.onEditStatus(false, false);
                    }
                }
            }
        });


        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
    }

    @Override
    protected void initData() {
        String currentItemID = MainActivity.getCurrentItemID();
        mTabAdapter.addItem("详情");
        mTabAdapter.addItem("图片");
        mTabAdapter.addItem("视频");
        mTabAdapter.setOnTabListener(this);

    }


    @Override
    public void onClick(View view) {
        if (null != mOnEditStatusListener) {
            switch (view.getId()) {
                case R.id.linear_get_picture://图像采集
                    mOnEditStatusListener.onGetPicture();
                    startActivity(GetPictureActivity.class);
                    break;
                case R.id.case_report://获取报告
                    mOnEditStatusListener.onGetReport();
                    Intent intent = new Intent(this, ReportActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("A", 1);
                    bundle.putString("B", "B");
                    intent.putExtras(bundle);
                    startActivity(intent);

                    break;
                case R.id.case_delete://删除
                    mOnEditStatusListener.onDelete();

                    break;
                case R.id.linear_down://下载
                    mOnEditStatusListener.onDown(true, true);
                    break;
            }
        }
    }

    /**
     * activity 和 fragment数据通信= activity通知fragment刷新UI状态
     * <p>
     * 状态回调监听
     *
     * @return
     * @return
     */

    private OnEditStatusListener mOnEditStatusListener;


    public void setOnEditStatusListener(OnEditStatusListener mOnEditStatusListener) {
        this.mOnEditStatusListener = mOnEditStatusListener;

    }

    public interface OnEditStatusListener {
        //activity制作发送的提示,具体操作全部在DetailFragment里面实现
        void onEditStatus(boolean status, boolean isFatherExit);

        //下载用户数据
        void onDown(boolean userInfo, boolean userPicture);

        //删除病例
        void onDelete();

        //获取报告
        void onGetReport();

        //图像采集
        void onGetPicture();

    }


    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }


    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }
}
