package com.company.iendo.mineui.offline.fragment;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.OfflineVideoBean;
import com.company.iendo.green.db.DownVideoMsgDBUtils;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.vlc.VideoActivity;
import com.company.iendo.mineui.offline.activity.DetailCaseOfflineActivity;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：离线模式的--视频fragment
 */
public class VideoOfflineFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private List<OfflineVideoBean> mDataLest = new ArrayList<>();
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
        CaseDBBean currentItemClickDBBean = CaseManageOfflineFragment.currentItemClickDBBean;

        String mDeviceCode = currentItemClickDBBean.getDeviceCaseID();//当前设备码
        String currentItemCaseID = currentItemClickDBBean.getOthers();//当前病例ID

//        0000000000000000546017FE6BC28949
//        1195
        List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByTow(getAttachActivity(), mDeviceCode, currentItemCaseID);

        if (mList != null) {
            if (mList.size() > 0) {
                showComplete();
                for (int i = 0; i < mList.size(); i++) {
                    DownVideoMessage downVideoMessage = mList.get(i);
                    if (downVideoMessage.getIsDown()) {
                        OfflineVideoBean bean = new OfflineVideoBean();
                        bean.setTitle(downVideoMessage.getTag());
                        bean.setUrl(downVideoMessage.getUrl());
                        bean.setMaxProcess(downVideoMessage.getMaxProcess());

                        mDataLest.add(bean);
                    }


                }
                DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + mDataLest.size() + ")");
            } else {
                DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + 0 + ")");
            }
        } else {
            showEmpty();
            DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + 0 + ")");
        }
//
//        if (videoList != null) {
//            if (videoList.size() > 0) {
//                showComplete();
//                for (int i = 0; i < videoList.size(); i++) {
//                    CaseVideoListBean bean = videoList.get(i);
//                    mDataLest.add(bean.getVideoPath());
//                }
//                DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + mDataLest.size() + ")");
//            }else {
//                DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + 0 + ")");
//            }
//        } else {
//            showEmpty();
//            DetailCaseOfflineActivity.mTabAdapter.setItem(2, "视频(" + 0 + ")");
//        }


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
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        OfflineVideoBean item = mAdapter.getItem(position);
//        http://192.168.31.249:7001/4/2022-04-19-17-54-07.mp4
//        LogUtils.e("当前播放URL" + mUrl);
//        intent.putExtra("mUrl","http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
        intent.putExtra("mTitle", item.getTitle());
        intent.putExtra("mUrl", item.getUrl());
        intent.putExtra("loginType", "offline");
        startActivity(intent);
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
