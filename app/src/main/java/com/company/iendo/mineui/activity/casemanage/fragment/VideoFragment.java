package com.company.iendo.mineui.activity.casemanage.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.DetailVideoBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.adapter.VideoAdapter;
import com.company.iendo.mineui.activity.vlc.VideoActivity;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
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
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class VideoFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener{
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private List<DetailVideoBean.DataDTO> mDataLest = new ArrayList<>();
    private VideoAdapter mAdapter;
    private String mBaseUrl;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_viedo;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.rv_video_list);
        mStatusLayout = findViewById(R.id.video_hint);

        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new VideoAdapter(getActivity(), MainActivity.getCurrentItemID());

        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));
        mAdapter.setData(mDataLest);
        sendRequest(MainActivity.getCurrentItemID());
    }

    /**
     * 获取当前用户的视频数据
     *
     * @param currentItemID
     */
    private void sendRequest(String currentItemID) {
        OkHttpUtils.get()
                .url(mBaseUrl+HttpConstant.CaseManager_CaseVideos)
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
                        if ("" != response) {
                            DetailVideoBean mBean = mGson.fromJson(response, DetailVideoBean.class);
                            List<DetailVideoBean.DataDTO> data = mBean.getData();
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                if (mBean.getData().size()!=0){
                                    mDataLest.clear();
                                    mDataLest.addAll(mBean.getData());
                                    mAdapter.setData(mDataLest);
                                }else{
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
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        DetailVideoBean.DataDTO item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), VideoActivity.class);
//        http://192.168.64.28:7001/ID/FilePath
//        mBaseUrl=http://192.168.132.102:7001
        String mUrl = mBaseUrl + "/" + item.getRecordID() + "/" + item.getFilePath();

        LogUtils.e("当前播放URL"+item.toString());
        LogUtils.e("当前播放URL"+mUrl);
//        intent.putExtra("mUrl","http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
        intent.putExtra("mUrl",mUrl);
        startActivity(intent);
    }



    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }
}
