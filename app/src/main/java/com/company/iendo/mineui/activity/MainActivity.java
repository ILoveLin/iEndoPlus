package com.company.iendo.mineui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.app.AppFragment;
import com.company.iendo.bean.event.RefreshItemIdEvent;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.fragment.casemanage.CaseManageFragment;
import com.company.iendo.mineui.fragment.setting.SettingFragment;
import com.company.iendo.mineui.offline.fragment.CaseManageOfflineFragment;
import com.company.iendo.mineui.offline.fragment.SettingOfflineFragment;
import com.company.iendo.other.DoubleClickHelper;
import com.company.iendo.ui.adapter.NavigationAdapter;
import com.company.iendo.ui.fragment.HomeFragment;
import com.company.iendo.utils.SharePreferenceUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/28 14:51
 * desc：主界面
 */
public class MainActivity extends AppActivity implements NavigationAdapter.OnNavigationListener {
    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";

    private ViewPager mViewPager;
    private RecyclerView mNavigationView;

    private NavigationAdapter mNavigationAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    private static String mCurrentItemID;
    private Boolean mOnLineFlag;

    public static void start(Context context) {
        start(context, HomeFragment.class);
    }

    public static void start(Context context, Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mOnLineFlag = (Boolean) SharePreferenceUtil.get(MainActivity.this, SharePreferenceUtil.OnLine_Flag, true);
        mViewPager = findViewById(R.id.vp_home_pager);
        mNavigationView = findViewById(R.id.rv_home_navigation);
        mNavigationAdapter = new NavigationAdapter(this);
//        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_index01),
//                ContextCompat.getDrawable(this, R.drawable.home_home_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_index02),
                ContextCompat.getDrawable(this, R.drawable.home_found_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_index03),
                ContextCompat.getDrawable(this, R.drawable.home_message_selector)));
//        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_me),
//                ContextCompat.getDrawable(this, R.drawable.home_me_selector)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);


    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        if (mOnLineFlag) {
            mPagerAdapter.addFragment(CaseManageFragment.newInstance());
            mPagerAdapter.addFragment(SettingFragment.newInstance());
        } else {
            mPagerAdapter.addFragment(CaseManageOfflineFragment.newInstance());
            mPagerAdapter.addFragment(SettingOfflineFragment.newInstance());
        }
//        mPagerAdapter.addFragment(AFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);
        onNewIntent(getIntent());
//        requestPermission();
    }

    private void requestPermission() {
        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
//                .permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
//                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
//                            showToast("获取存储权限成功");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            toast("获取存储权限失败");
                        }
                    }
                });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
//            case 2:
//            case 3:
                mViewPager.setCurrentItem(fragmentIndex);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
//            case 2:
                mViewPager.setCurrentItem(position);
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }
        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false);
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void RefreshItemIdEvent(RefreshItemIdEvent event) {
        setCurrentItemID(event.getId() + "");
    }


    public static String getCurrentItemID() {
        return mCurrentItemID;
    }

    public void setCurrentItemID(String mCurrentItemID) {
        this.mCurrentItemID = mCurrentItemID;
    }



    public  Context getContext(){
        Context applicationContext = getApplicationContext();
        return  applicationContext;
    }

}
