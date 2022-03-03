package com.company.iendo.mineui.activity.casemanage;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.PictureChoseBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.adapter.ChosePictureAdapter;
import com.company.iendo.other.GridSpaceDecoration;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 图片选择界面--生成报告前需要选择图片
 */
public final class PictureChoseActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {

    private StatusLayout mStatusLayout;
    private TitleBar mTitlebar;
    private RecyclerView mRecyclerView;
    private ArrayList<PictureChoseBean> mPathList;
    private ArrayList<PictureChoseBean> mDataLest = new ArrayList<>();
    private ChosePictureAdapter mAdapter;
    private TextView mToLookActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chose_picture;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitlebar = findViewById(R.id.titlebar);
        mRecyclerView = findViewById(R.id.rv_image_recyclerview);
        mToLookActivity = findViewById(R.id.tv_go_look);

    }

    @Override
    protected void initData() {

        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new ChosePictureAdapter(getActivity(), MainActivity.getCurrentItemID(), mBaseUrl);

        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceDecoration(30));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(mDataLest);
        sendRequest(MainActivity.getCurrentItemID());

        responseListener();
    }

    private void responseListener() {
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {

            }
        });

        mToLookActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断选中了多少张图片,不能超过9张

                ArrayList<PictureChoseBean> mList = new ArrayList<>();
                mList.clear();
                //点击之前先判断当前有几个选中的图片,超过9张提示不能在选择
                List<PictureChoseBean> data = mAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    PictureChoseBean pictureChoseBean = data.get(i);
                    if (pictureChoseBean.isSelected()) {
                        mList.add(pictureChoseBean);
                    }
                }
                LogUtils.e("图片" + "mList.size()===" + mList.size());////原图路径

                if (mList.size() > 2) {
                    toast("最多不超过2张!");
                    return;
                } else {
                    //去报告预览界面
                    Intent intent = new Intent(PictureChoseActivity.this, ReportActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

        if (null != mDataLest) {
            PictureChoseBean bean = mDataLest.get(position);
            String newID = bean.getItemID();
            for (int i = 0; i < mDataLest.size(); i++) {
                PictureChoseBean oldBean = mDataLest.get(i);
                String oldID = oldBean.getItemID();
                if (newID.equals(oldID)) {
                    if (oldBean.isSelected()) {
                        oldBean.setSelected(false);
                    } else {
                        oldBean.setSelected(true);

                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();


    }

    /**
     * 获取当前用户的图片
     *
     * @param currentItemID
     */
    private void sendRequest(String currentItemID) {
        showLoading();
        LogUtils.e("currentItemID" + currentItemID);
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CasePictures)
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
                        mPathList = new ArrayList<>();

                        showComplete();
                        if ("" != response) {
                            DetailPictureBean mBean = mGson.fromJson(response, DetailPictureBean.class);
                            List<DetailPictureBean.DataDTO> data = mBean.getData();
                            LogUtils.e("图片" + "response===" + response);////原图路径

                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                if (mBean.getData().size() != 0) {

                                    //添加跳转大图界面的前提是,把图片url 添加到集合之中
                                    for (int i = 0; i < mBean.getData().size(); i++) {
                                        String imageName = mBean.getData().get(i).getImagePath();
                                        String url = mBaseUrl + "/" + MainActivity.getCurrentItemID() + "/" + imageName;
                                        LogUtils.e("图片fragment===" + imageName);
                                        LogUtils.e("图片fragment===" + url);
                                        PictureChoseBean bean = new PictureChoseBean();
                                        bean.setUrl(url);
                                        bean.setSelected(false);
                                        bean.setItemID(url);
                                        mPathList.add(bean);
                                    }
                                    mDataLest.clear();
                                    mDataLest.addAll(mPathList);
                                    LogUtils.e("图片" + "");////原图路径
                                    mAdapter.setData(mDataLest);

                                } else {
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