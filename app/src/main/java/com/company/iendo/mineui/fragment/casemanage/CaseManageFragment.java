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
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.ZXBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
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
import com.company.iendo.utils.SocketUtils;
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
import com.umeng.commonsdk.debug.E;
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

import io.reactivex.internal.operators.flowable.FlowableOnErrorReturn;
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
    private String imageCounts;

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
        setOnClickListener(R.id.ib_right, R.id.ib_left, R.id.tv_title, R.id.iv_tag_anim, R.id.iv_tag_anim);
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
            case R.id.iv_tag_anim:
            case R.id.tv_title:
                mTitle.setTag("close");
                startRotationAnim("open");   //打开dialog
                //选择事件请求列表
                showDateDialog();
                break;
            case R.id.ib_left:
                //跳转病例添加界面
                if (mMMKVInstace.decodeBool(Constants.KEY_CanNew)) {
                    startActivity(AddCaseActivity.class);
                } else {
                    toast(Constants.HAVE_NO_PERMISSION);
                }
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
                            toast("数据解析错误");

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
        mMMKVInstace.encode(Constants.KEY_CurrentCaseID, item.getID() + "");
        Intent intent = new Intent(getActivity(), DetailCaseActivity.class);
        ((MainActivity) getActivity()).setCurrentItemID(item.getID() + "");
        LogUtils.e("itemID==" + item.getID() + "");
        intent.putExtra("Name", item.getName() + "");
        intent.putExtra("itemID", item.getID() + "");
        intent.putExtra("itemUserName", item.getUserName() + "");
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

        sendHandLinkMessage();


    }

    /**
     * 发送握手消息
     *
     * @param
     */
    public void sendHandLinkMessage() {
        HandBean handBean = new HandBean();
        handBean.setHelloPc("");
        handBean.setComeFrom("");

        byte[] sendByteData = CalculateUtils.getSendByteData(getAttachActivity(), mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_HAND);

        if (("".equals(mSocketPort))) {
//            toast("通讯端口不能为空");
            return;
        }
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==detailCaseActivity==sendByteData=i=" + sendByteData);
        LogUtils.e("SocketUtils==HandService===发送消息==点对点==detailCaseActivity==mSocketPort===i=" + mSocketPort);
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), getAttachActivity());
//        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
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
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_CUSTOM_TOAST://吐司
                toast("" + data);
                break;
            case Constants.UDP_12://新增病例,刷新界面数据
            case Constants.UDP_13://更新病例,刷新界面数据
                if (!mTitle.getText().toString().trim().isEmpty()) {
                    sendRequest(mTitle.getText().toString().trim());
                } else {
                    sendRequest(currentChoseDate);
                }
                break;

            case Constants.UDP_F7://权限通知变动,在病例列表,病例详情,和图像采集三个界面相互监听,发现了请求后台更新本地权限
                if (event.getTga()) {
                    requestCurrentPermission();
                }
                break;
        }

    }

    /**
     * 上位机权限变动通知,更新本地权限
     */
    private void requestCurrentPermission() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_getCurrentRelo)
                .addParams("UserID", mUserID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {


                    }

                    @Override
                    public void onResponse(String response, int id) {
//
                        LogUtils.e("登录===" + response);
                        if (!"".equals(response)) {
                            UserReloBean mBean = mGson.fromJson(response, UserReloBean.class);
                            if (0 == mBean.getCode()) {
                                UserReloBean.DataDTO bean = mBean.getData();
                                mMMKVInstace.encode(Constants.KEY_UserMan, bean.isUserMan());//用户管理(用户管理界面能不能进)
                                mMMKVInstace.encode(Constants.KEY_CanPsw, bean.isCanPsw());//设置口令(修改别人密码)
                                mMMKVInstace.encode(Constants.KEY_SnapVideoRecord, bean.isSnapVideoRecord());//拍照录像
                                mMMKVInstace.encode(Constants.KEY_CanNew, bean.isCanNew());  //登记病人(新增病人)
                                mMMKVInstace.encode(Constants.KEY_CanEdit, bean.isCanEdit());//修改病历
                                mMMKVInstace.encode(Constants.KEY_CanDelete, bean.isCanDelete());//删除病历
                                mMMKVInstace.encode(Constants.KEY_CanPrint, bean.isCanPrint()); //打印病历
                                mMMKVInstace.encode(Constants.KEY_UnPrinted, bean.isUnPrinted()); //未打印病历
                                mMMKVInstace.encode(Constants.KEY_OnlySelf, bean.isOnlySelf());//本人病历
                                mMMKVInstace.encode(Constants.KEY_HospitalInfo, bean.isHospitalInfo());//医院信息(不能进入医院信息界面)
                            }
                        }

                    }
                });
    }


}
