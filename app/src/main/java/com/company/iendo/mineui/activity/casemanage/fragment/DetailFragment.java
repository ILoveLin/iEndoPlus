package com.company.iendo.mineui.activity.casemanage.fragment;

import androidx.appcompat.widget.AppCompatTextView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.widget.StatusLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class DetailFragment extends TitleBarFragment<MainActivity> implements StatusAction {

    private AppCompatTextView mTV;
    private StatusLayout mStatusLayout;

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
        mStatusLayout = findViewById(R.id.detail_hint);
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
}
