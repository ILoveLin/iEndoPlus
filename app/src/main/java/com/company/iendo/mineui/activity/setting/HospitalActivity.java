package com.company.iendo.mineui.activity.setting;

import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.HospitalBean;
import com.company.iendo.bean.HospitalUpdateBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.http.glide.GlideApp;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.service.HandService;
import com.company.iendo.ui.activity.ImageCropActivity;
import com.company.iendo.ui.activity.ImagePreviewActivity;
import com.company.iendo.ui.activity.ImageSelectActivity;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.PictureFileUtil;
import com.company.iendo.utils.RegexUtils;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/13 9:19
 * desc：医院信息
 */
public class HospitalActivity extends AppActivity implements StatusAction {
    private TitleBar mTitlebar;
    private StatusLayout mStatusLayout;
    /**
     * 头像地址
     */
    private Uri mAvatarUrl;
    private AppCompatImageView mAvatarView;
    private ClearEditText mNumber;
    private ClearEditText mTitle_01;
    private ClearEditText mTitle_02;
    private ClearEditText mAddress;
    private ClearEditText mPhone;
    private ArrayList<ClearEditText> clearEditList;
    private String mID;
    private String szPostCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hospital;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTitlebar = findViewById(R.id.hospital_bar);
        mStatusLayout = findViewById(R.id.status_hint);
        mAvatarView = findViewById(R.id.avatar_view);
//        mAvatarView = findViewById(R.id.avatar_view);
        mTitle_01 = findViewById(R.id.title_01);
        mTitle_02 = findViewById(R.id.title_02);
        mAddress = findViewById(R.id.address);
        mPhone = findViewById(R.id.phone);
        mNumber = findViewById(R.id.number);
        clearEditList = new ArrayList<>();
        clearEditList.add(mTitle_02);
        clearEditList.add(mAddress);
        clearEditList.add(mPhone);
        clearEditList.add(mNumber);
        clearEditList.add(mTitle_01);
        setOnClickListener(R.id.avatar_view, R.id.relative_top);
    }

    @Override
    protected void initData() {

        setEditStatus(false);

        responseListener();

    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        switch (event.getUdpCmd()) {
            case Constants.UDP_40://刷新医院信息
                sendRequest();
                break;
        }

    }



    /**
     * 采图--->发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointRefreshData(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            HandBean handBean = new HandBean();
            handBean.setHelloPc("");
            handBean.setComeFrom("");
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), HospitalActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);
        }

    }

    private void responseListener() {
        sendRequest();
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

                if (mMMKVInstace.decodeBool(Constants.KEY_HospitalInfo)) {
                    if (mTitlebar.getRightTitle().equals("编辑")) {
                        setEditStatus(true);
                        mTitlebar.setRightTitle("保存");
                        mTitlebar.setRightTitleColor(getResources().getColor(R.color.red));
                    } else {
                        mTitlebar.setRightTitle("编辑");
                        mTitlebar.setRightTitleColor(getResources().getColor(R.color.black));
                        setEditStatus(false);
                        sendUpdateRequest();
                    }
                } else {
                    toast(Constants.HAVE_NO_PERMISSION);
                }

            }
        });
    }

    //修改信息
    private void sendUpdateRequest() {
        boolean postCode = RegexUtils.checkPostcode(mNumber.getText().toString().trim());
        if (postCode) {
            showLoading();
            OkHttpUtils.post()
                    .url(mBaseUrl + HttpConstant.CaseManager_CaseUpdateHospitalInfo)
                    .addParams("ID", mID)//内部ID
                    .addParams("UserName", mLoginUserName)//操作员用户名
                    .addParams("EndoType", endoType)//EndoType
                    .addParams("UserID", mUserID)//UserID
                    .addParams("szHospital", mTitle_01.getText().toString().trim())
                    .addParams("szSlave", mTitle_02.getText().toString().trim())
                    .addParams("szAddress", mAddress.getText().toString().trim())
                    .addParams("szTelephone", mPhone.getText().toString().trim())
                    .addParams("szPostCode", mNumber.getText().toString().trim())
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
                            showComplete();
                            HospitalUpdateBean mBean = mGson.fromJson(response, HospitalUpdateBean.class);
                            if ("" != response && 0 == mBean.getCode()) {  //成功
                                toast("修改成功!");
                                sendSocketPointRefreshData(Constants.UDP_40);

                            } else {
                                showError(listener -> {
                                    sendRequest();
                                });
                            }

                        }
                    });
        } else {
            toast("邮编格式错误!");
            mNumber.setText("" + szPostCode);
        }
    }

    private void sendRequest() {
        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseHospitalInfo)
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
                        showComplete();
                        HospitalBean mBean = mGson.fromJson(response, HospitalBean.class);
                        if ("" != response && 0 == mBean.getCode()) {  //成功
                            mID = mBean.getData().getID();
                            refreshData(mBean.getData());

                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }

                    }
                });


    }

    //设置数据
    private void refreshData(HospitalBean.DataDTO data) {
        mTitle_01.setText("" + data.getSzHospital());
        mTitle_02.setText("" + data.getSzSlave());
        mAddress.setText("" + data.getSzAddress());
        mPhone.setText("" + data.getSzTelephone());
        mNumber.setText("" + data.getSzPostCode());
        szPostCode = data.getSzPostCode();
        //http://ip:port/DefaultLogo.jpg
        String szIconPath = mBaseUrl+ "/" + data.getSzIconPath();

        // 显示圆角的 ImageView
        GlideApp.with(this)
                .load(szIconPath)
                .placeholder(R.mipmap.bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                .error(R.mipmap.bg_splash_des)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_5))))
                .into(mAvatarView);

    }

    private void setEditStatus(boolean status) {
        if (status) {//设置可编辑状态
            for (int i = 0; i < clearEditList.size(); i++) {
                clearEditList.get(i).setFocusableInTouchMode(true);
                clearEditList.get(i).setFocusable(true);
                clearEditList.get(i).requestFocus();
            }
        } else { //设置不可编辑状态
            for (int i = 0; i < clearEditList.size(); i++) {
                clearEditList.get(i).setFocusable(false);
                clearEditList.get(i).setFocusableInTouchMode(false);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_top:
                ImageSelectActivity.start(this, data -> {
                    // 裁剪头像
                    cropImageFile(new File(data.get(0)));
                });
                break;
            case R.id.avatar_view:

                if (mAvatarUrl != null) {
                    // 查看头像
                    ImagePreviewActivity.start(getActivity(), mAvatarUrl.toString());
                } else {
                    // 选择头像
                    ImageSelectActivity.start(this, data -> {
                        // 裁剪头像
                        cropImageFile(new File(data.get(0)));
                    });
                }
                break;
        }
    }

    /**
     * 裁剪图片
     */
    private void cropImageFile(File sourceFile) {
        ImageCropActivity.start(this, sourceFile, 1, 1, new ImageCropActivity.OnCropListener() {

            @Override
            public void onSucceed(Uri fileUri, String fileName) {
                File outputFile;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    outputFile = new FileContentResolver(getActivity(), fileUri, fileName);
                } else {
                    try {
                        outputFile = new File(new URI(fileUri.toString()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        outputFile = new File(fileUri.toString());
                    }
                }
                updateCropImage(outputFile, true);
            }

            @Override
            public void onError(String details) {
                // 没有的话就不裁剪，直接上传原图片
                // 但是这种情况极其少见，可以忽略不计
                updateCropImage(sourceFile, false);
            }
        });
    }

    /**
     * 上传裁剪后的图片
     */
    private void updateCropImage(File file, boolean deleteFile) {
        if (true) {
            if (file instanceof FileContentResolver) {
                mAvatarUrl = ((FileContentResolver) file).getContentUri();
            } else {
                mAvatarUrl = Uri.fromFile(file);
            }

            // 显示圆角的 ImageView
            GlideApp.with(this)
                    .load(mAvatarUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_5))))
                    .into(mAvatarView);
        }


        String realPathFromURI = PictureFileUtil.getRealPathFromURI(this, mAvatarUrl);
        File file1 = new File(realPathFromURI);
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseUpdateHospitalLogo)
                .addFile("logo", file1.getName(), file1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
