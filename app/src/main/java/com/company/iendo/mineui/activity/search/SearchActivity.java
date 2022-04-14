package com.company.iendo.mineui.activity.search;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.SearchListBean;
import com.company.iendo.bean.event.RefreshItemIdEvent;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.mineui.activity.search.adapter.SearchAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 13:46
 * desc：搜索界面
 */
public class SearchActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {
    private List<SearchListBean.DataDTO> mDataLest = new ArrayList<>();
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private StatusLayout mStatusLayout;
    private HashMap parmasMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_search;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_search_refresh);
        mRecyclerView = findViewById(R.id.rv_search_recyclerview);
        mStatusLayout = findViewById(R.id.status_hint);

        Intent intent = getIntent();
        parmasMap = (HashMap) intent.getSerializableExtra("parmasMap");

        String checkDateStart = (String) parmasMap.get("CheckDateStart");
        String CheckDateEnd = (String) parmasMap.get("CheckDateEnd");
        String Married = (String) parmasMap.get("Married");
        // Iterator entrySet 获取key and value
        Iterator<Map.Entry<Integer, Integer>> it = parmasMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            LogUtils.e(entry.getKey() + ":" + entry.getValue());
            // it.remove(); 删除元素
        }
    }

    @Override
    protected void initData() {
        mAdapter = new SearchAdapter(SearchActivity.this);
        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(this, 1, R.drawable.shape_divideritem_decoration));
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
                .url(mBaseUrl + HttpConstant.CaseManager_Search)
                .params(parmasMap)
                .addParams("EndoType", endoType)////目前默认是3  耳鼻喉治疗台
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("=TAG=sendRequest=onError==" + e.toString());
                        showError(listener -> {
                            sendRequest(systemDate);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e("=TAG=sendRequest=onResponse==" + response);
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
                                toast(mBean.getMsg() + "");
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

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_back:
//                ActivityManager.getInstance().finishActivity(SearchActivity.class);
//                break;
//        }
//    }


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
        LogUtils.e("======GetPictureActivity=====搜索结果界面跳转====item==" + item.toString());
        mMMKVInstace.encode(Constants.KEY_CurrentCaseID, item.getID() + "");
        Intent intent = new Intent(getActivity(), DetailCaseActivity.class);
//        ((MainActivity) getActivity()).setCurrentItemID(item.getID() + "");
        RefreshItemIdEvent refreshItemIdEvent = new RefreshItemIdEvent(true);
        refreshItemIdEvent.setId(item.getID()+"");
        EventBus.getDefault().post(refreshItemIdEvent);
        LogUtils.e("itemID==" + item.getID() + "");
        intent.putExtra("Name", item.getName() + "");
        intent.putExtra("itemID", item.getID() + "");
        intent.putExtra("itemUserName", item.getUserName() + "");
        startActivity(intent);
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
