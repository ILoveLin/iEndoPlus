package com.company.iendo.mineui.fragment.casemanage;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.activity.search.SearchActivity;
import com.company.iendo.mineui.activity.search.SearchSelectedActivity;
import com.company.iendo.mineui.fragment.casemanage.adapter.CaseManageAdapter;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.DateDialog;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：病例列表
 */
public class CaseManageFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private CaseManageAdapter mAdapter;
    private DateDialog.Builder mDateDialog;
    private String mChoiceDate;
    private StatusLayout mStatusLayout;
    private List<CaseManageListBean.DataDTO> mDataLest = new ArrayList<>();
    private String mBaseUrl;
    private TextView mTitle;
    private String currentChoseDate;
    private String endoType;

    public static CaseManageFragment newInstance() {
        return new CaseManageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_case_manage;
    }

    @Override
    protected void initView() {
        endoType = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_EndoType, "3");
        mRefreshLayout = findViewById(R.id.rl_b_refresh);
        mRecyclerView = findViewById(R.id.rv_b_recyclerview);
        mTitle = findViewById(R.id.tv_title);
        mStatusLayout = findViewById(R.id.b_hint);
        mTitle.setText(DateUtil.getSystemDate());
        currentChoseDate = mTitle.getText().toString().trim();
        setOnClickListener(R.id.ib_right, R.id.ib_left, R.id.tv_title);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title:
                //选择事件请求列表
                showDateDialog();
                break;
            case R.id.ib_left:
                //跳转病例添加界面
                startActivity(AddCaseActivity.class);

                break;
            case R.id.ib_right:

                startActivity(SearchSelectedActivity.class);
//                startActivity(SearchActivity.class);
                break;
        }
    }


    @Override
    protected void initData() {
        mAdapter = new CaseManageAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mDataLest);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));


    }

    @Override
    public void onResume() {
        super.onResume();
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "192.168.132.102");
        LogUtils.e("currentChoseDate=====" + mTitle.getText().toString().trim());
        if (!mTitle.getText().toString().trim().isEmpty()) {
            sendRequest(mTitle.getText().toString().trim());
        } else {
            sendRequest(currentChoseDate);

        }
//        showDateDialog();
    }

    //选择日期
    private void showDateDialog() {
        // 日期选择对话框
        mDateDialog = new DateDialog.Builder(getActivity());
        mDateDialog.setTitle("请选择日期")
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(new DateDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, int year, int month, int day) {
                        // 如果不指定时分秒则默认为现在的时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        // 月份从零开始，所以需要减 1
                        calendar.set(Calendar.MONTH, month - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        String mDate = new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime());
                        String mChoiceDate = mDate.replace("年", "-").replace("月", "-").replace("日", "");

                        LogUtils.e("TTTTT" + mChoiceDate);
//                        toast("时间：" + mChoiceDate);
                        mTitle.setText(mChoiceDate + "");
                        sendRequest(mChoiceDate);

                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();


    }

    private void sendRequest(String mChoiceDate) {
        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_List)
                .addParams("datetime", mChoiceDate)
//                .addParams("EndoType", "4")  //目前默认是3  耳鼻喉治疗台
                .addParams("EndoType", endoType)  //目前默认是3  耳鼻喉治疗台
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("=病例列表=hy=onError==" + e.toString());
                        showError(listener -> {
                            sendRequest(mChoiceDate);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            showComplete();
                            if ("" != response) {
                                mGson = GsonFactory.getSingletonGson();
                                CaseManageListBean mBean = mGson.fromJson(response, CaseManageListBean.class);
                                LogUtils.e("=病例列表=hy=response==response===" + response);
                                LogUtils.e("=病例列表=hy=response==mBean===" + mBean.toString());
//                                for (int i = 0; i < mBean.getData().size(); i++) {
//                                    LogUtils.e("=病例列表=hy=time==" + mBean.getData().get(i).getID());
//                                }
                                if (0 == mBean.getCode()) {  //成功
                                    if (mBean.getEmpty()) {
                                        showEmpty();

                                    }else {
                                        mDataLest.clear();
                                        mDataLest.addAll(mBean.getData());
                                        mAdapter.setData(mDataLest);
                                    }
//                                    if (mBean.getData().isEmpty()){
//                                        showEmpty();
//                                    }else {
//                                        mDataLest.clear();
//                                        mDataLest.addAll(mBean.getData());
//                                        mAdapter.setData(mDataLest);
//                                    }
//                                    if (mBean.getData().size() != 0) {
//                                        LogUtils.e("=病例列表=hy= 0 0 0 0 0 0==" );
//
//                                        mDataLest.clear();
//                                        mDataLest.addAll(mBean.getData());
//                                        mAdapter.setData(mDataLest);
//                                    } else {
//                                        LogUtils.e("=病例列表=hy= 111111111==" );
//                                        showEmpty();
//                                    }
                                } else {
                                    LogUtils.e("=病例列表=hy= 22222222==" );

                                    showError(listener -> {
                                        sendRequest(mChoiceDate);
                                    });
                                }
                            } else {
                                showError(listener -> {
                                    sendRequest(mChoiceDate);
                                });
                            }
                        } catch (Exception e) {
                            LogUtils.e("=TAG=hy=Exception==size===" + e.toString());

                        }


                    }
                });


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
        CaseManageListBean.DataDTO item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), DetailCaseActivity.class);
        ((MainActivity) getActivity()).setCurrentItemID(item.getID() + "");
        LogUtils.e("item.getID()" + item.getID() + "");

        startActivity(intent);
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
//            mAdapter.clearData();
//            mAdapter.setData(analogData());
//            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
//            mAdapter.addData(mRequestListData);
            mRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(true);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
//
//            mAdapter.addData(analogData());
//            mRefreshLayout.finishLoadMore();
//
//            mAdapter.setLastPage(mAdapter.getCount() >= 100);
//            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏

        return !super.isStatusBarEnabled();
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
