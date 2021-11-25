package com.company.iendo.mineui.activity.casemanage;

import android.view.View;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.AddCaseBean;
import com.company.iendo.bean.AddCaseNoBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 添加病例
 */
public final class AddCaseActivity extends AppActivity implements StatusAction {
    private StatusLayout mStatusLayout;
    private TitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_case;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.titlebar);
        ClearEditText mCaseNumber = findViewById(R.id.case03_case_number);
        ClearEditText mCaseName = findViewById(R.id.case03_name);

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                sendRequest();
            }
        });
    }

    /**
     * 添加病例
     */
    private void sendRequest() {
        showLoading();
        OkHttpUtils.post()
                .url(HttpConstant.CaseManager_AddCase)
                .addParams("Name", "张大仙")
                .addParams("UserName", "Admin")
                .addParams("EndoType", "3")  //目前默认是3  耳鼻喉治疗台
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendRequest();
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            AddCaseBean mBean = mGson.fromJson(response, AddCaseBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                toast("" + mBean.getMsg());
                                ActivityManager.getInstance().finishActivity(AddCaseActivity.class);

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
    protected void initData() {


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

}