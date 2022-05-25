package com.company.iendo.mineui.activity.usermanage;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.bean.UserManagerListBean;
import com.company.iendo.bean.event.RefreshItemIdEvent;
import com.company.iendo.bean.event.RefreshUserListEvent;
import com.company.iendo.mineui.activity.usermanage.UserListAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 可进行拷贝的副本
 */
public final class UserManagerListActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {
    private List<UserManagerListBean.DataDTO> mDataLest = new ArrayList<>();
    private StatusLayout mStatusLayout;
    private WrapRecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;
    private String mLoginUserID;
    private UserManagerListAdapter mAdapter;
    private String mLoginRole;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_manager_list;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.userlist_titlebar);
        mSmartRefreshLayout = findViewById(R.id.rl_userlist_refresh);
        mRecyclerView = findViewById(R.id.rv_userlist_recyclerview);

    }

    @Override
    protected void initData() {

        mLoginRole = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_Role, "");
        mLoginUserID = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_UserID, "1");
        mLoginUserName = mMMKVInstace.decodeString(Constants.KEY_CurrentLoginUserName);
        mAdapter = new UserManagerListAdapter(this, mLoginUserName);
        mAdapter.setData(mDataLest);
        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UserManagerListActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        sendRequest();
        responListener();
    }

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void RefreshItemIdEvent(RefreshUserListEvent event) {
        sendRequest();
    }

    private void responListener() {

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                startActivity(AddUserActivity.class);
            }
        });


    }

    private void sendRequest() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_List)
                .addParams("type", "manager")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(new StatusLayout.OnRetryListener() {
                            @Override
                            public void onRetry(StatusLayout layout) {
                                toast("请求错误");
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            showComplete();
                            UserManagerListBean mBean = mGson.fromJson(response, UserManagerListBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                if (mBean.getData().size() != 0) {
                                    mDataLest.clear();
                                    mDataLest.addAll(mBean.getData());
                                    mAdapter.setData(mDataLest);
                                } else {
                                    showEmpty();
                                }
                            } else {
                                showError(listener -> {
                                    sendRequest();
                                });
                            }

                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });
    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

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