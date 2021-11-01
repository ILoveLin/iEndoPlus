package com.company.iendo.mineui.fragment.casemanage;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.search.SearchActivity;
import com.company.iendo.ui.adapter.StatusAdapter;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
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
 * time：2021/10/29 13:55
 * desc：第3个tab-fragment
 */
public class CaseManageFragment extends TitleBarFragment<MainActivity> implements BaseAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusAdapter mAdapter;
    private TitleBar mTitleRight;

    public static CaseManageFragment newInstance() {
        return new CaseManageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_case_manage;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_b_refresh);
        mRecyclerView = findViewById(R.id.rv_b_recyclerview);
        mTitleRight = findViewById(R.id.case_manage_title_right);
    }

    @Override
    protected void initData() {
        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(analogData());

        responseListener();
    }

    private void responseListener() {

        mTitleRight.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                startActivity(SearchActivity.class);
            }
        });

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

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

}
