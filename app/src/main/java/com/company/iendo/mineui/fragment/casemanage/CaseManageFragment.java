package com.company.iendo.mineui.fragment.casemanage;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.ZXBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.http.glide.GlideRequest;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.activity.search.SearchActivity;
import com.company.iendo.mineui.activity.search.SearchSelectedActivity;
import com.company.iendo.mineui.fragment.casemanage.adapter.CaseManageAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.DateDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.GridSpaceItemDecoration;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.JsonCallback;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：病例列表 --此处开启监听线程,上位机删除或者更新,接受的到数据从新请求列表刷新数据
 * , OnRefreshLoadMoreListener
 */
public class CaseManageFragment extends TitleBarFragment<MainActivity> implements StatusAction, BaseAdapter.OnItemClickListener {
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
    private ImageView mAnim;

    public static CaseManageFragment newInstance() {
        return new CaseManageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_case_manage;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        endoType = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_EndoType, "3");
        mRefreshLayout = findViewById(R.id.rl_b_refresh);
        mRecyclerView = findViewById(R.id.rv_b_recyclerview);
        mTitle = findViewById(R.id.tv_title);
        mAnim = findViewById(R.id.iv_tag_anim);
        mStatusLayout = findViewById(R.id.b_hint);
        mTitle.setText(DateUtil.getSystemDate());
        currentChoseDate = mTitle.getText().toString().trim();
        setOnClickListener(R.id.ib_right, R.id.ib_left, R.id.tv_title, R.id.iv_tag_anim);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnim, "rotation", 0f, 180f);
        animator.setDuration(100);
        animator.start();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_tag_anim:
//                //选择事件请求列表
//                showDateDialog();
//                break;
            case R.id.tv_title:
                mTitle.setTag("close");
                startRotationAnim("open");   //打开dialog
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

    public void startRotationAnim(String type) {
        if ("close".equals(type)) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mAnim, "rotation", 0f, 180f);
            animator.setDuration(300);
            animator.start();

        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mAnim, "rotation", 180f, 360f);
            animator.setDuration(300);
            animator.start();
        }
    }

    @Override
    protected void initData() {
        mAdapter = new CaseManageAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mDataLest);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

//        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(2, 30, true));
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

//        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));


    }


    //选择日期
    private void showDateDialog() {
        // 日期选择对话框
        mDateDialog = new DateDialog.Builder(getActivity());
        mDateDialog.setTitle("请选择日期")
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .addOnDismissListener(new BaseDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(BaseDialog dialog) {
                        startRotationAnim("close");

                    }
                })
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
                        startRotationAnim("close");


                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        startRotationAnim("close");

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
                                LogUtils.e("=病例列表=hy=response==response===" + response);
                                CaseManageListBean mBean = mGson.fromJson(response, CaseManageListBean.class);
                                LogUtils.e("=病例列表=hy=response==response===" + mBean.toString());
                                LogUtils.e("=病例列表=hy=response==getEmpty===" + mBean.isIsEmpty());
                                LogUtils.e("=病例列表=hy=response==getCode()===" + mBean.getCode());
                                LogUtils.e("=病例列表=hy=response==getData===" + mBean.getData());

                                if (0 == mBean.getCode()) {  //成功
                                    if (mBean.getData().size() != 0) {
                                        mDataLest.clear();
                                        mDataLest.addAll(mBean.getData());
                                        mAdapter.setData(mDataLest);
                                    } else {
                                        showEmpty();
                                    }
                                } else {
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
                            toast("数据解析错误!");

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
        LogUtils.e("======GetPictureActivity=====Handler接受====item==" + item.toString());

        SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_Chose_CaseID, item.getID() + "");
        Intent intent = new Intent(getActivity(), DetailCaseActivity.class);
        ((MainActivity) getActivity()).setCurrentItemID(item.getID() + "");
        LogUtils.e("itemID==" + item.getID() + "");
        intent.putExtra("itemID", item.getID() + "");
        startActivity(intent);
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
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==DetailFragment==event.getData()==" + event.getData());
//        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(event.getData());//随机数之后到data结尾的String
//        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
//        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
//        String currentCMD = CalculateUtils.getCMD(event.getData());
//        LogUtils.e("Socket回调==DetailFragment==随机数之后到data的Str==mRun2End4==" + mRun2End4);
//        LogUtils.e("Socket回调==DetailFragment==发送方设备类型==deviceType==" + deviceType);
//        LogUtils.e("Socket回调==DetailFragment==获取发送方设备Code==deviceOnlyCode==" + deviceOnlyCode);
//        LogUtils.e("Socket回调==DetailFragment==当前UDP命令==currentCMD==" + currentCMD);
//        LogUtils.e("Socket回调==DetailFragment==当前UDP命令==event.getUdpCmd()==" + event.getUdpCmd());
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_12://新增病例,刷新界面数据
            case Constants.UDP_13://更新病例,刷新界面数据
                if (!mTitle.getText().toString().trim().isEmpty()) {
                    sendRequest(mTitle.getText().toString().trim());
                } else {
                    sendRequest(currentChoseDate);
                }
                break;
        }

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
////            mAdapter.clearData();
////            mAdapter.setData(analogData());
////            mRefreshLayout.finishRefresh();
//        }, 1000);
//    }

//    @Override
//    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
////            mAdapter.addData(mRequestListData);
//            mRefreshLayout.finishLoadMore();
//            mAdapter.setLastPage(true);
//            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
////
////            mAdapter.addData(analogData());
////            mRefreshLayout.finishLoadMore();
////
////            mAdapter.setLastPage(mAdapter.getCount() >= 100);
////            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
//        }, 1000);
//    }

}
