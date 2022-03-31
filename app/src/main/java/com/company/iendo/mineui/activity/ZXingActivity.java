package com.company.iendo.mineui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.ZXBean;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.device.DeviceActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.db.DBManager;
import com.company.iendo.widget.StatusLayout;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 二维码扫描界面
 */
public final class ZXingActivity extends AppActivity implements QRCodeView.Delegate {
    private static final String TAG = ZXingActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private Boolean isFirstIn = true;
    private ZXingView mZXingView;
    private ImageView zx_back;
    private TextView choose_picture;
    private TextView close_light;
    private TextView text111;
    private TextView open_light;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zxing;
    }

    @Override
    protected void initView() {
        mZXingView = findViewById(R.id.zxing_view);
        open_light = findViewById(R.id.open_light);
        text111 = findViewById(R.id.text111);
        close_light = findViewById(R.id.close_light);
        zx_back = findViewById(R.id.zx_back);
//        choose_picture = findViewById(R.id.choose_picture);

        setOnClickListener(R.id.open_light, R.id.close_light, R.id.zx_back);
        mZXingView.setDelegate(this);

    }

    @Override
    protected void initData() {

    }


    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zx_back:  //退出
                finish();
                break;
            case R.id.open_light:  //打开闪光灯
                mZXingView.openFlashlight();
                break;
            case R.id.close_light: //关闭闪光灯
                mZXingView.closeFlashlight();
                break;
//            case R.id.choose_picture://打开相册
//                Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
//                        .cameraFileDir(null)
//                        .maxChooseCount(1)
//                        .selectedPhotos(null)
//                        .pauseOnScroll(false)
//                        .build();
//                startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Log.i(TAG, "result:" + result);
        setTitle("扫描结果为：" + result);
        Log.e("扫描成功====结果为：", "result===" + result);
        Log.e("扫描成功====结果为：", "result===" + result);
//        toast(result);
        //取消震动
//        vibrate();
        if (null == result) {
            toast("扫描失败!");

        } else {
            try {
                text111.setText("" + result);
                if (!"".equals(result) && isFirstIn) {
                    isFirstIn = false;
                    /**
                     *{ "deviceID" : "abcdefg123456789", "deviceType" : 9, "endoType" : 4,
                     *  "httpPort" : 7001, "ip" : "192.168.64.19", "makemsg" : "GDT", "micport" : 0,
                     *  "password" : "root", "port" : 7788, "socketPort" : 7006, "title" : "GDT-LIVE",
                     *  "type" : 1, "username" : "root" }
                     */
                    if (isGoodJson(result)) {  //
                        Log.e("扫描结果为：", "result===" + result);
                        getJsonData(result);
                    } else {//暂时认定为自定义url链接
                        toast("自定义url链接");
//                        getCustomUrl(result);
                    }
                }
                mZXingView.startSpot(); // 开始识别
                finish();
            } catch (Exception e) {
            }
        }

    }

    @SuppressLint("NewApi")
    private void getJsonData(String result) {
//        try {
        Gson gson = new Gson();
        Type type = new TypeToken<ZXBean>() {
        }.getType();
        ZXBean mBean = gson.fromJson(result, type);
        LogUtils.e("Zingx==========" + mBean.toString());
//            VideoDBBean01 videoDBBean = new VideoDBBean01();

        DeviceDBBean deviceDBBean = new DeviceDBBean();

        String deviceTypeString = getDeviceTypeNum(mBean.getDeviceType());
        LogUtils.e("Zingx=====deviceTypeString=====" + deviceTypeString);

        deviceDBBean.setDeviceID("" + mBean.getDeviceID());//
        deviceDBBean.setDeviceCode("" + mBean.getDeviceID());//
        deviceDBBean.setEndoType("" + mBean.getEndoType());//PC端 对应的endotype  比如我们这边耳鼻喉治疗台是3   这个对应是8
        deviceDBBean.setType_num("" +mBean.getDeviceType());//PC端 对呀的设备类型比如07 对应一代一体机    此处typeNum是数字表示:07
        deviceDBBean.setType("" + deviceTypeString);//PC端 对呀的设备类型比如07 对应一代一体机    此处type是中文表示:一代一体机
        deviceDBBean.setDeviceName("" + mBean.getTitle());//设备名字
        deviceDBBean.setHttpPort("" + mBean.getHttpPort());//
        deviceDBBean.setIp("" + mBean.getIp());//
        deviceDBBean.setMsg("" + mBean.getMakemsg());//
        deviceDBBean.setTitle("" + mBean.getTitle());//
        deviceDBBean.setMicPort("" + mBean.getMicport());//
        deviceDBBean.setPassword("" + mBean.getPassword());//
        deviceDBBean.setLivePort("" + mBean.getPort());//
        deviceDBBean.setSocketPort("" + mBean.getSocketPort());//
        deviceDBBean.setTitle("" + mBean.getTitle());//
        deviceDBBean.setUsername("" + mBean.getUsername());//
        deviceDBBean.setMSelected(false);//

//
        LogUtils.e("========当前设备的备注信息~~~~====ZXingActivity==deviceDBBean===" + deviceDBBean.toString());


        DeviceDBUtils.insertOrReplace(ZXingActivity.this, deviceDBBean);

        EventBus.getDefault().post(new RefreshEvent("refresh"));
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(ZXingActivity.this);
        LogUtils.e("========当前设备的备注信息~~~~====ZXingActivity==deviceDBBeans===" + deviceDBBeans.size());

    }

    /**
     * 根据设备名-中文获取对应数字
     * 一代一体机=07
     *
     * @param str
     * @return
     */
    public String getDeviceTypeNum(int str) {
        if (Constants.Type_07 == str) {
            return Constants.Type_V1_YiTiJi;
        } else if (Constants.Type_08 == str) {
            return Constants.Type_EarNoseTable;
        } else if (Constants.Type_09 == str) {
            return Constants.Type_MiNiaoTable;
        } else if (Constants.Type_0A == str) {
            return Constants.Type_FuKeTable;
        }


        return Constants.Type_V1_YiTiJi + "";
    }

    public static boolean isGoodJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        } catch (JsonParseException e) {
            return false;
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        toast("打开相机出错");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
            mZXingView.decodeQRCode(picturePath);
            /*
            没有用到 QRCodeView 时可以调用 QRCodeDecoder 的 syncDecodeQRCode 方法

            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
            .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(TestScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TestScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZXingView.onDestroy(); // 销毁二维码扫描控件

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstIn = true;
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

}