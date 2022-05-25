package com.company.iendo.mineui.activity;

import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.bean.UserListBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.mineui.activity.usermanage.UserListAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.InputAddUserDialog;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.dialog.SelectUserDialog;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.ClearEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.mmkv.MMKV;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/18 14:39
 * desc：
 */
public class UserListActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, BaseAdapter.OnChildClickListener {
    private List<UserListBean.DataDTO> mDataLest = new ArrayList<>();
    private List<UserListBean.DataDTO> mUserListData;
    private StatusLayout mStatusLayout;
    private SmartRefreshLayout mSmartRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private UserListAdapter mAdapter;
    private String mLoginRole;
    private String mLoginUserID;
    private String mLoginUserName;
    private TitleBar mTitleBar;
    //  //角色权限:0-管理员 1-操作员 2-查询员
    private int mAddCurrentCode = 0;//管理员
    private String mAddCurrentString = "管理员";//管理员

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_user_add;
//    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_list;
    }
    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.userlist_titlebar);
        mSmartRefreshLayout = findViewById(R.id.rl_userlist_refresh);
        mRecyclerView = findViewById(R.id.rv_userlist_recyclerview);

    }

    @Override
    protected void initData() {
        mLoginRole = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_Role, "");
        mLoginUserID = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_UserID, "1");
        mLoginUserName = mMMKVInstace.decodeString(Constants.KEY_CurrentLoginUserName);

        mAdapter = new UserListAdapter(this, mLoginUserName);
        mAdapter.setData(mDataLest);
        mAdapter.setOnItemClickListener(this);
        responseListener();
        mRecyclerView.setAdapter(mAdapter);
        sendRequest();
//        响应删除,修改权限,修改密码等事件
    }

    private void responseListener() {
        mAdapter.setOnChildClickListener(R.id.linear_relo, this);
        mAdapter.setOnChildClickListener(R.id.linear_change, this);
        mAdapter.setOnChildClickListener(R.id.linear_delete, this);

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                showAddUserDialog();
            }
        });
    }

    /**
     * 添加用户
     */
    private void showAddUserDialog() {

        InputAddUserDialog.Builder addUserBuilder = new InputAddUserDialog.Builder(this);
        addUserBuilder.setTitle("添加用户")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new InputAddUserDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String userName, String passwrod, String relo) {
                        //添加用户请求
                        if ("".equals(userName)) {
                            toast("用户名不能为空");
                        } else if ("".equals(mAddCurrentCode)) {
                            toast("用户名不能为空");
                        } else {
                            //  //角色权限:0-管理员 1-操作员 2-查询员
                            sendAddUserRequest(userName, passwrod, mAddCurrentCode);

                        }
                    }

                }).show();

        ClearEditText reloView = addUserBuilder.getRelo();
        reloView.setFocusable(false);
        reloView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 角色 0-超级管理员 1-管理员 2-操作员 3-查询员 4-自定义
                new SelectUserDialog.Builder(UserListActivity.this)
                        .setTitle("请选择用户角色")
                        .setList("管理员", "操作员", "查询员")
                        .setBackgroundDimEnabled(false)
                        .setSingleSelect()
                        .setCancel("取消")
                        .setConfirm("确定")
                        .setListener(new SelectUserDialog.OnListener() {
                            @Override
                            public void onSelected(BaseDialog dialog, HashMap data) {
                                String string = data.toString();
                                int i = string.indexOf("=");
                                String substringName = string.substring(i + 1, data.toString().length() - 1);
                                mAddCurrentCode = Integer.parseInt(string.substring(1, 2));
                                mAddCurrentString = substringName;
                                reloView.setText("" + mAddCurrentString);
                            }
                        }).show();
            }
        });

    }

    /**
     * 添加用户
     *
     * @param userName
     * @param passwrod
     * @param mAddCurrentCode
     */
    private void sendAddUserRequest(String userName, String passwrod, int mAddCurrentCode) {
        String UserID = (String) SharePreferenceUtil.get(UserListActivity.this, SharePreferenceUtil.Current_Login_UserID, "");
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_AddUser)
                .addParams("CurrentRelo", mLoginRole)    //当前用户权限
                .addParams("CreateRelo", mAddCurrentCode + "")     //新用户的权限
                .addParams("UserName", userName)    //新用户的名字
                .addParams("Password",MD5ChangeUtil.Md5_32(passwrod) )    //新用户的密码
                .addParams("Des", mAddCurrentString)    //新用户的描述
                .addParams("CanSUE", "1")    //新用户是否激活1激活，0是未激活
                .addParams("UserID", UserID)    //用户ID
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
                            toast("添加成功");
                            if (mBean.getCode().equals("0")) {
//                                toast(mBean.getMsg() + "");
                                sendRequest();

                            }
                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });
    }

    private void sendRequest() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_List)
                .addParams("type","manager")
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
                            showComplete();
                            UserListBean mBean = mGson.fromJson(response, UserListBean.class);
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
//        toast(mAdapter.getItem(position).getUserName());
    }

//    @Override
//    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mAdapter.clearData();
//            mAdapter.setData(mDataLest);
//            mSmartRefreshLayout.finishRefresh();
//        }, 1000);
//    }
//
//    @Override
//    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mSmartRefreshLayout.finishLoadMore();
//            mAdapter.setLastPage(true);
//            mSmartRefreshLayout.setNoMoreData(mAdapter.isLastPage());
//        }, 1000);
//    }

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        switch (childView.getId()) {
            case R.id.linear_delete://删除
                showDeleteDialog(mAdapter.getItem(position));
                break;
            case R.id.linear_change://修改密码
                MMKV mmkv = MMKV.defaultMMKV();
                if (mmkv.decodeBool(Constants.KEY_CanPsw)) {
                    showChangePasswordDialog(mAdapter.getItem(position));
                } else {
                    toast("暂无权限");
                }
                break;
            case R.id.linear_relo://修改权限
                showChangeReloDialog(mAdapter.getItem(position));
                break;
        }
    }

    /**
     * 修改权限对话框
     *
     * @param item
     */
    private void showChangeReloDialog(UserListBean.DataDTO item) {
        // 角色 0-超级管理员 1-管理员 2-操作员 3-查询员 4-自定义
        new SelectDialog.Builder(this)
                .setTitle("修改权限")
                .setList("管理员", "操作员", "查询员")//0 1 2
                .setSingleSelect()
                .setSelect(0)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        sendChangeReloRequest(item, data.toString().substring(1, 2));
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();

    }

    /**
     * 修改用户权限
     *
     * @param item
     * @param substring
     */
    private void sendChangeReloRequest(UserListBean.DataDTO item, String substring) {
        int i = Integer.parseInt(substring);
        showLoading();
        String userID = item.getUserID();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_ChangeRelo)
                .addParams("CurrentUserID", mLoginUserID)//当前登入的用户ID == 1
                .addParams("ChangeUserID", userID)//需要被修改权限的用户ID
                .addParams("UserName", mLoginUserName)//当前用户名字
                .addParams("Relo", i + "")//需要被修改的用户权限等级
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
                            toast("修改成功");
                            if (mBean.getCode().equals("0")) {
                                sendRequest();
                            }
                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });

    }

    /**
     * 修改密码对话框
     *
     * @param item
     */
    private void showChangePasswordDialog(UserListBean.DataDTO item) {
        new InputDialog.Builder(this)
                .setTitle("提示")
                .setHint("请输入新密码")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String password) {
                        sendChangePasswordRequest(item, MD5ChangeUtil.Md5_32(password));
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();

    }

    /**
     * 发送其他人修改密码请求
     *
     * @param item
     * @param password
     */
    private void sendChangePasswordRequest(UserListBean.DataDTO item, String password) {
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_ChangeElsePassword)
                .addParams("userID", mLoginUserID)//自己的ID
                .addParams("changedUserID", item.getUserID())//被修改用户ID
                .addParams("userRelo", mLoginRole)//自己的权限
                .addParams("changedUserRelo", item.getRole() + "")//被修改用户的权限
                .addParams("changedPassword", password)//新密码
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
                            if (mBean.getCode().equals("0")) {
                                toast("修改成功");
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });

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
                        sendDeleteRequest(item);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
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
                .url(mBaseUrl + HttpConstant.UserManager_Delete)
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
                            if (mBean.getCode().equals("0")) {
                                toast("删除成功");

                                mAdapter.removeItem(item);
                                mAdapter.notifyDataSetChanged();
                            }

                            if (item.getUserName().equals("Admin") && "0".equals(mLoginRole)) {
                                toast("超级管理员不能被删除");

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
