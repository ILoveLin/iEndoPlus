package com.company.iendo.mineui.offline.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.mineui.offline.fragment.DetailOfflineFragment;
import com.company.iendo.mineui.offline.fragment.PictureOfflineFragment;
import com.company.iendo.mineui.offline.fragment.VideoOfflineFragment;
import com.company.iendo.ui.adapter.TabAdapter;
import com.company.iendo.utils.LogUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.widget.layout.NestedViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 13:58
 * desc：详情界面---主架构界面  离线界面
 */
public class DetailCaseOfflineActivity extends AppActivity implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener {
    private NestedViewPager mViewPager;
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;
    private TitleBar mTitlebar;
    private Boolean mFatherExit;   //父类Activity 是否主动退出的标识,主动退出需要请求保存fragment的更新数据
    private String currentItemID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_case_detail;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTabView = findViewById(R.id.rv_detail_tab);
        mViewPager = findViewById(R.id.vp_detail_pager);
        //报告view
        mTitlebar = findViewById(R.id.titlebar);
        mFatherExit = false;
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DetailOfflineFragment.newInstance(), "详情");
        mPagerAdapter.addFragment(PictureOfflineFragment.newInstance(), "图片");
        mPagerAdapter.addFragment(VideoOfflineFragment.newInstance(), "视频");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        responseListener();


        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);


    }

    private void responseListener() {
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


    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();

    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume===DetailCaseActivity===开始建立握手链接!");
    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
    }

    @Override
    protected void initData() {
        currentItemID = getIntent().getStringExtra("itemID");
        mTabAdapter.addItem("详情");
        mTabAdapter.addItem("图片");
        mTabAdapter.addItem("视频");
        mTabAdapter.setOnTabListener(this);


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
        EventBus.getDefault().unregister(this);

    }
}
