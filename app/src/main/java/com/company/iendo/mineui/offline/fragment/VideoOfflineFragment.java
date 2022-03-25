package com.company.iendo.mineui.offline.fragment;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.green.db.downcase.CaseVideoListBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：离线模式的--视频fragment
 */
public class VideoOfflineFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener {
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private List<String> mDataLest = new ArrayList<>();
    private VideoOfflineAdapter mAdapter;

    public static VideoOfflineFragment newInstance() {
        return new VideoOfflineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_offline_detail_viedo;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.rv_video_list);
        mStatusLayout = findViewById(R.id.video_hint);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new VideoOfflineAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));
        List<CaseVideoListBean> videoList = CaseManageOfflineFragment.currentItemClickDBBean.getVideoList();
        LogUtils.e("用户表====videoList====" + CaseManageOfflineFragment.currentItemClickDBBean);

        if (videoList != null) {
            if (videoList.size() > 0) {
                showComplete();
                for (int i = 0; i < videoList.size(); i++) {
                    CaseVideoListBean bean = videoList.get(i);
                    mDataLest.add(bean.getVideoPath());
                }
            }
        } else {
            showEmpty();
        }


        mAdapter.setData(mDataLest);
    }


    @Override
    protected void initData() {
        if (mDataLest.size() == 0) {
            showEmpty();
        } else {
            showComplete();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

    }


    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
