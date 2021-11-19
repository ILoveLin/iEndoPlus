package com.company.iendo.mineui.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.mineui.activity.usermanage.UserListAdapter;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
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
 * time：2021/11/18 14:39
 * desc：
 */
public class UserListActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, OnRefreshLoadMoreListener, BaseAdapter.OnChildClickListener {
    private List<UserListBean.DataDTO> mDataLest = new ArrayList<>();
    private List<UserListBean.DataDTO> mUserListData;
    private StatusLayout mStatusLayout;
    private SmartRefreshLayout mSmartRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private UserListAdapter mAdapter;
    private String mLoginRole;
    private String mLoginUserID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_list;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mSmartRefreshLayout = findViewById(R.id.rl_userlist_refresh);
        mRecyclerView = findViewById(R.id.rv_userlist_recyclerview);

    }

    @Override
    protected void initData() {
        mLoginRole = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_Role, "");
        mLoginUserID = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_UserID, "1");

        mAdapter = new UserListAdapter(this);
        mAdapter.setData(mDataLest);
        mAdapter.setOnItemClickListener(this);
        responseListener();
        mRecyclerView.setAdapter(mAdapter);
        sendRequest();
        //响应删除,修改权限,修改密码等事件
    }

    private void responseListener() {
        mAdapter.setOnChildClickListener(R.id.tv_delete, this);
        mAdapter.setOnChildClickListener(R.id.tv_change_password, this);
        mAdapter.setOnChildClickListener(R.id.tv_change_relo, this);


    }

    private void sendRequest() {

        showLoading();
        OkHttpUtils.get()
                .url(HttpConstant.UserManager_List)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(new StatusLayout.OnRetryListener() {
                            @Override
                            public void onRetry(StatusLayout layout) {
                                toast("请求错误");
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
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
                                showError(listener -> {
                                    sendRequest();
                                });
                            }

                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });


    }


    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

        toast(mAdapter.getItem(position).getUserName());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(mDataLest);
            mSmartRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mSmartRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(true);
            mSmartRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        switch (childView.getId()) {
            case R.id.tv_delete://删除
                showDeleteDialog(mAdapter.getItem(position));
                break;
            case R.id.tv_change_password://修改密码

                break;
            case R.id.tv_change_relo://修改权限

                break;
        }
    }

    /**
     * 删除对话框
     *
     * @param item
     */
    private void showDeleteDialog(UserListBean.DataDTO item) {
        new MessageDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除用户吗?")
                .setConfirm("确定")
                .setCancel("取消")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        toast("onConfirm");
                        sendDeleteRequest(item);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("onCancel");

                    }
                }).show();

    }

    /**
     * 删除请求
     *
     * @param item
     */
    private void sendDeleteRequest(UserListBean.DataDTO item) {
        showLoading();
        OkHttpUtils.post()
                .url(HttpConstant.UserManager_Delete)
                .addParams("DeleteUserID", item.getUserID())//被删除用户的ID
                .addParams("CurrentUserID", mLoginUserID)//当前用户ID
                .addParams("CurrentRelo", mLoginRole + "")//当前用户权限
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(new StatusLayout.OnRetryListener() {
                            @Override
                            public void onRetry(StatusLayout layout) {
                                toast("请求错误");
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        showComplete();
                        if ("" != response) {
                            UserDeletedBean mBean = mGson.fromJson(response, UserDeletedBean.class);
                            toast(mBean.getMsg() + "");
                            LogUtils.e("删除用户====");
                            if (mBean.getCode().equals("0")) {
                                mAdapter.removeItem(item);
                                mAdapter.notifyDataSetChanged();
                            }

//                            if (0 == ) {  //成功
//                                if (mBean.getData().size() != 0) {
//                                    showComplete();
//                                    mDataLest.clear();
//                                    mDataLest.addAll(mBean.getData());
//                                    mAdapter.setData(mDataLest);
//                                } else {
//                                    showEmpty();
//                                }
//                            } else {
//                                showError(listener -> {
//                                    sendRequest();
//                                });
//                            }

                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });


    }
}
