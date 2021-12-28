package com.company.iendo.mineui.activity.search;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.SearchListBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.search.adapter.SearchAdapter;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.RecycleViewDivider;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 13:46
 * desc：搜索界面
 */
public class SearchActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private List<SearchListBean.DataDTO> mDataLest = new ArrayList<>();
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private StatusLayout mStatusLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_search;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_search_refresh);
        mRecyclerView = findViewById(R.id.rv_search_recyclerview);
        mStatusLayout = findViewById(R.id.status_hint);
        setOnClickListener(R.id.tv_back);
    }

    @Override
    protected void initData() {
        mAdapter = new SearchAdapter(SearchActivity.this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, 1, R.drawable.shape_divideritem_decoration));
        mAdapter.setData(mDataLest);
    }

    @Override
    public void onResume() {
        super.onResume();
        sendRequest(DateUtil.getSystemDate());
    }

    private void sendRequest(String systemDate) {
        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl+HttpConstant.CaseManager_Search)
                .addParams("CheckDateStart", "2021-11-03")
                .addParams("EndoType", "3")////目前默认是3  耳鼻喉治疗台
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("=TAG=hy=onError==" + e.toString());
                        showError(listener -> {
                            sendRequest(systemDate);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            SearchListBean mBean = mGson.fromJson(response, SearchListBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                if (mBean.getData().size() != 0) {
                                    showComplete();
                                    mDataLest.clear();
                                    mDataLest.addAll(mBean.getData());
                                    mAdapter.setData(mDataLest);
                                } else {
                                    showEmpty();
                                }
                            } else {
                                showError(listener -> {
                                    sendRequest(systemDate);
                                });
                            }
                        } else {
                            showError(listener -> {
                                sendRequest(systemDate);
                            });
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                ActivityManager.getInstance().finishActivity(SearchActivity.class);
                break;
        }
    }


    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        SearchListBean.DataDTO item = mAdapter.getItem(position);
        toast(item.getCheckDate());
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(mDataLest);
            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(true);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }


    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }
}
