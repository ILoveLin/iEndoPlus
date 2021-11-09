package com.company.iendo.mineui.activity.casemanage.fragment;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.widget.StatusLayout;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class DetailFragment extends TitleBarFragment<MainActivity> implements StatusAction, DetailCaseActivity.OnEditStatusListener {

    private AppCompatTextView mTV;
    private StatusLayout mStatusLayout;
    private ClearEditText mEdit;
    private Boolean mEditStatus= false;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_message;
    }

    @Override
    protected void initView() {
        sendRequest(MainActivity.getCurrentItemID());
        mTV = findViewById(R.id.detail_text);
        mEdit = findViewById(R.id.clearedit);
        mStatusLayout = findViewById(R.id.detail_hint);
        setEditStatus();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DetailCaseActivity mActivity = (DetailCaseActivity) getActivity();
        mActivity.setOnEditStatusListener(this);
    }

    private void sendRequest(String currentItemID) {
        OkHttpUtils.get()
                .url(HttpConstant.CaseManager_CaseInfo)
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
                            CaseDetailBean mBean = mGson.fromJson(response, CaseDetailBean.class);
                            toast(mBean.getMsg());
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                setLayoutData(mBean);

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

    private void setLayoutData(CaseDetailBean mBean) {
        CaseDetailBean.DataDTO data = mBean.getData();
        mTV.setText("全部的数据:" + data.toString());

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
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }


    /**
     * EditText 和  弹窗是否可以用的标识
     *
     * @param status
     */
    @Override
    public void onEditStatus(boolean status) {
        this.mEditStatus = status;
        toast(status);
        setEditStatus();
    }

    private void setEditStatus() {
        if (mEditStatus) {
            //设置可编辑状态
            mEdit.setFocusableInTouchMode(true);
            mEdit.setFocusable(true);
            mEdit.requestFocus();
        } else {
            //设置不可编辑状态
            mEdit.setFocusable(false);
            mEdit.setFocusableInTouchMode(false);
        }
    }
}
