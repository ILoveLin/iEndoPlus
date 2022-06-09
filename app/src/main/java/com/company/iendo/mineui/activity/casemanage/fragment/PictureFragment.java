package com.company.iendo.mineui.activity.casemanage.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.adapter.PictureAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.GridSpaceDecoration;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.activity.ImagePreviewActivity;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
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
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class PictureFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private List<DetailPictureBean.DataDTO> mDataLest = new ArrayList<>();
    private PictureAdapter mAdapter;
    private String mBaseUrl;
    private ArrayList<String> mPathList;
    private String currentItemCaseID;
    private Boolean firstInitAdapter = true;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override

    protected int getLayoutId() {
        return R.layout.fragment_detail_picture;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

//        mRefreshLayout = findViewById(R.id.rl_pic_refresh);
        mRecyclerView = findViewById(R.id.rv_pic_list);
        mStatusLayout = findViewById(R.id.pic_hint);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new PictureAdapter(getActivity(), MainActivity.getCurrentItemID(), mBaseUrl, "", firstInitAdapter);

        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceDecoration(30));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setData(mDataLest);
        currentItemCaseID = MainActivity.getCurrentItemID();

        sendRequest(currentItemCaseID);


    }

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_17://编辑图片
                if (data.equals(currentItemCaseID)) {//当前病例一样,才更新
                    String changePicID = event.getIp();
                    for (int i = 0; i < mDataLest.size(); i++) {
                        DetailPictureBean.DataDTO dataDTO = mDataLest.get(i);
                        String id = dataDTO.getID();
                        if (changePicID.equals(id)) {
                            mAdapter.setItem(i, dataDTO);
                        }
                    }
//                    mAdapter.setCurrentChangeImageID(changePicID);
                }
                break;
            case Constants.UDP_15://截图
                if (currentItemCaseID.equals(event.getData())) {
                    sendRequest(currentItemCaseID);
                }

            case Constants.UDP_16://删除图片
                sendRequest(currentItemCaseID);
                break;
        }
    }

    /**
     * 获取当前用户的图片
     *
     * @param currentItemID
     */
    private void sendRequest(String currentItemID) {
        firstInitAdapter = true;
        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CasePictures)
                .addParams("ID", currentItemID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendRequest(currentItemID);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mPathList = new ArrayList<>();

                        showComplete();
                        if ("" != response) {
                            DetailPictureBean mBean = mGson.fromJson(response, DetailPictureBean.class);
                            List<DetailPictureBean.DataDTO> data = mBean.getData();

                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                if (mBean.getData().size() != 0) {
                                    mDataLest.clear();
                                    mDataLest.addAll(mBean.getData());
                                    mAdapter.setData(mDataLest);


                                    //添加跳转大图界面的前提是,把图片url 添加到集合之中
                                    for (int i = 0; i < mBean.getData().size(); i++) {
                                        String imageName = mBean.getData().get(i).getImagePath();
                                        String id1 = mBean.getData().get(i).getID();
                                        String url = mBaseUrl + "/" + MainActivity.getCurrentItemID() + "/" + imageName;
                                        mPathList.add(url);

                                    }
                                } else {
                                    showEmpty();
                                }

                            } else {
                                showError(listener -> {
                                    sendRequest(currentItemID);
                                });
                            }
                        } else {
                            showError(listener -> {
                                sendRequest(currentItemID);
                            });
                        }
                    }
                });

    }

    @Override
    protected void initData() {

    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        DetailPictureBean.DataDTO item = mAdapter.getItem(position);
        ImagePreviewActivity.start(getAttachActivity(), mPathList, position);
//        ImagePreviewActivity.start(getAttachActivity(), mPathList, mPathList.size() - 1);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //    /**
//     * {@link OnRefreshLoadMoreListener}
//     */
//
//    @Override
//    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mAdapter.clearData();
//            mAdapter.setData(mDataLest);
//            mRefreshLayout.finishRefresh();
//        }, 1000);
//    }
//
//    @Override
//    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mRefreshLayout.finishLoadMore();
//            mAdapter.setLastPage(true);
//            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
//        }, 1000);
//    }


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
