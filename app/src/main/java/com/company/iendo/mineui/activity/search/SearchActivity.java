package com.company.iendo.mineui.activity.search;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.ui.adapter.StatusAdapter;
import com.company.iendo.widget.RecycleViewDivider;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 13:46
 * desc：搜索界面
 */
public class SearchActivity extends AppActivity implements BaseAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_search;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_search_refresh);
        mRecyclerView = findViewById(R.id.rv_search_recyclerview);
        setOnClickListener(R.id.tv_back);
    }

    @Override
    protected void initData() {
        mAdapter = new StatusAdapter(SearchActivity.this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, 1, R.drawable.shape_divideritem_decoration));
        mAdapter.setData(analogData());
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
     * 模拟数据
     */
    private List<String> analogData() {
        List<String> data = new ArrayList<>();
        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 15; i++) {
            data.add("我是第" + i + "条目");
        }
        return data;
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
        toast(mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(analogData());
            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.addData(analogData());
            mRefreshLayout.finishLoadMore();

            mAdapter.setLastPage(mAdapter.getCount() >= 100);
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
}
