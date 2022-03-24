package com.company.iendo.mineui.offline.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.green.db.downcase.CaseImageListBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.other.GridSpaceDecoration;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：离线模式的--图片fragment
 */
public class PictureOfflineFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private List<String> mDataLest = new ArrayList<>();
    private PictureOfflineAdapter mAdapter;
    private String mBaseUrl;
    private ArrayList<String> mPathList;
    private String currentItemCaseID;
    private Boolean firstInitAdapter = true;
    private List<CaseImageListBean> imageList;

    public static PictureOfflineFragment newInstance() {
        return new PictureOfflineFragment();
    }

    @Override

    protected int getLayoutId() {
        return R.layout.fragment_offline_detail_picture;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

//        mRefreshLayout = findViewById(R.id.rl_pic_refresh);
        mRecyclerView = findViewById(R.id.rv_pic_list);
        mStatusLayout = findViewById(R.id.pic_hint);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new PictureOfflineAdapter(getAttachActivity());

        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceDecoration(30));
        mRecyclerView.setAdapter(mAdapter);

        imageList = CaseManageOfflineFragment.currentItemClickDBBean.getImageList();
        if (imageList != null) {
            if (imageList.size() > 0) {
                showComplete();
                for (int i = 0; i < imageList.size(); i++) {
                    CaseImageListBean bean = imageList.get(i);
                    mDataLest.add(bean.getImagePath());
                }
            }
        }else {
            showEmpty();
        }

        mAdapter.setData(mDataLest);
        currentItemCaseID = MainActivity.getCurrentItemID();


    }

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {

    }


    @Override
    protected void initData() {

    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
//        ImagePreviewActivity.start(getAttachActivity(), imageList, imageList.size() - 1);


    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
