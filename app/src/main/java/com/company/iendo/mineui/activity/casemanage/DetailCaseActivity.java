package com.company.iendo.mineui.activity.casemanage;

import android.view.View;

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
import com.company.iendo.ui.adapter.TabAdapter;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_detail;
    }

    @Override
    protected void initView() {
        mTabView = findViewById(R.id.rv_detail_tab);
        mViewPager = findViewById(R.id.vp_detail_pager);
        mTitlebar = findViewById(R.id.titlebar);

        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DetailFragment.newInstance(), "详情");
        mPagerAdapter.addFragment(PictureFragment.newInstance(), "图片");
        mPagerAdapter.addFragment(VideoFragment.newInstance(), "视频");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                if (mTitlebar.getRightTitle().equals("编辑")) {
                    mOnEditStatusListener.onEditStatus(true);
                    mTitlebar.setRightTitle("编辑中...");
                    mTitlebar.setRightTitleColor(getResources().getColor(R.color.red));
                } else {
                    mTitlebar.setRightTitle("编辑");
                    mTitlebar.setRightTitleColor(getResources().getColor(R.color.black));
                    mOnEditStatusListener.onEditStatus(false);
                }
            }
        });


        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);


    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
    }

    @Override
    protected void initData() {
        String currentItemID = MainActivity.getCurrentItemID();
        toast("id~======" + currentItemID);
        mTabAdapter.addItem("详情");
        mTabAdapter.addItem("图片");
        mTabAdapter.addItem("视频");
        mTabAdapter.setOnTabListener(this);

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
        void onEditStatus(boolean status);
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
