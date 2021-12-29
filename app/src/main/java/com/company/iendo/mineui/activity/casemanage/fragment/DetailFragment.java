package com.company.iendo.mineui.activity.casemanage.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.DeleteBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

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

    private Boolean mEditStatus = false;
    private DetailCaseActivity mActivity;
    private CaseDetailBean mBean;
    private String mBaseUrl;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_message;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.detail_hint);

        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        setEditStatus();
        sendRequest(MainActivity.getCurrentItemID());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //activity和fragment 通信回调
        mActivity = (DetailCaseActivity) getActivity();
        mActivity.setOnEditStatusListener(this);
    }

    private void sendRequest(String currentItemID) {
        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
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
                            mBean = mGson.fromJson(response, CaseDetailBean.class);
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

    /**
     * 设置数据
     * @param mBean
     */
    private void setLayoutData(CaseDetailBean mBean) {
        CaseDetailBean.DataDTO data = mBean.getData();

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

    private void setEditStatus() {
//        if (mEditStatus) {
//            //设置可编辑状态
//            mEdit.setFocusableInTouchMode(true);
//            mEdit.setFocusable(true);
//            mEdit.requestFocus();
//        } else {
//            //设置不可编辑状态
//            mEdit.setFocusable(false);
//            mEdit.setFocusableInTouchMode(false);
//        }
    }

    /**
     * EditText 和  弹窗是否可以用的标识
     *
     * @param status
     */
    @Override
    public void onEditStatus(boolean status) {
        this.mEditStatus = status;
        setEditStatus();
    }

    @Override
    public void onDown(boolean userInfo, boolean userPicture) {

        new SelectDialog.Builder(getActivity())
                .setTitle("信息下载")
                .setSelect(0, 1)
                .setList("用户信息", "图片信息")
                .setListener(new SelectDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, HashMap data) {
                        String string = data.toString();
                        LogUtils.e("下载===" + data.toString());
                        int size = data.size();
                        LogUtils.e("下载===size=" + size);
                        if (size == 2) {//下载用户信息和图片信息
//                            Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());
//                            String path = "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath();
//                            https://images.csdn.net/20150817/1.jpg
                            File toLocalFile = new File(Environment.getExternalStorageDirectory() +
                                    "/MyData/Images/" + MainActivity.getCurrentItemID());

                            //创建本地的/MyData/Images/mID文件夹  再把图片下载到这个文件夹下

                            sendGetPictureRequest();

                        } else {//筛选下载哪种信息
                            int i = string.indexOf("=");
                            String value = string.substring(i + 1, string.length() - 1);
                            LogUtils.e("下载===value=" + value);
                            if (value.equals("用户信息")) {  //下载用户信息

                            } else if (value.equals("图片信息")) {//下载图片信息

                            }

                        }


                    }
                }).show();

    }

    private void sendGetPictureRequest() {

//      Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());
//      String path = "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath();
//      https://images.csdn.net/20150817/1.jpg
        File toLocalFile = new File(Environment.getExternalStorageDirectory() +
                "/MyData/Images/" + MainActivity.getCurrentItemID());

        //创建本地的/MyData/Images/mID文件夹  再把图片下载到这个文件夹下
        String url = "http://images.csdn.net/20150817/1.jpg";
        if (!toLocalFile.exists()) {
            toLocalFile.mkdir();
        }


        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(toLocalFile.getAbsolutePath(), "1.jpg") {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("下载图片==onError==" + e);
                        //下载失败
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtils.e("下载图片==onResponse==" + response.toString());
                        //刷新相册
                        try {
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), toLocalFile.getAbsolutePath() + "/1.jpg", "", "");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    @Override
    public void onDelete() {
        new MessageDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("确认删除该用户吗?")
                .setConfirm("确定")
                .setCancel("取消")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        sendDeleteRequest();
                    }
                }).show();
    }


    @Override
    public void onGetReport() {
        toast("获取报告");

    }

    @Override
    public void onGetPicture() {
        toast("图像采集");

    }

    //删除用户请求
    private void sendDeleteRequest() {
        LogUtils.e("删除用户==params=" + mBean.getData().getID() + "");

        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.CaseManager_DeleteCase)
                .addParams("ID", mBean.getData().getID() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendDeleteRequest();
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e("删除用户===" + response);
                        if ("" != response) {
                            DeleteBean mBean = mGson.fromJson(response, DeleteBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                mActivity.finish();

                            } else {
                                showError(listener -> {
                                    sendDeleteRequest();
                                });
                            }
                        } else {
                            showError(listener -> {
                                sendDeleteRequest();
                            });
                        }
                    }
                });
    }

}
