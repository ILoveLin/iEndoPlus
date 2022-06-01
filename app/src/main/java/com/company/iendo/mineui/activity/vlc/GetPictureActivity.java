package com.company.iendo.mineui.activity.vlc;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.MicRequestBean;
import com.company.iendo.bean.socket.RecodeBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.bean.socket.params.DeviceParamsBean;
import com.company.iendo.bean.socket.params.Type02Bean;
import com.company.iendo.bean.socket.params.Type01Bean;
import com.company.iendo.other.Constants;

import com.company.iendo.other.HttpConstant;
import com.company.iendo.service.HandService;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.utils.SystemUtil;
import com.company.iendo.widget.vlc.ENDownloadView;
import com.company.iendo.widget.vlc.ENPlayView;
import com.company.iendo.widget.vlc.MyVlcVideoView;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.ClearEditText;
import com.hjq.widget.view.SwitchButton;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.pedro.rtplibrary.rtmp.RtmpOnlyAudio;
import com.vlc.lib.RecordEvent;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 获取图片界面
 * 放大倍数  1-2.5 显示的数字    传的的值是1--15
 */
public final class GetPictureActivity extends AppActivity implements StatusAction, OnRangeChangedListener,
        SwitchButton.OnCheckedChangeListener, ConnectCheckerRtmp {
    public static String path = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    public static String path1 = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
    public boolean isFullscreen = false;
    private StatusLayout mStatusLayout;
    private TextView mChangeFull;
    private LinearLayout mLinearBottom;
    private TitleBar mTitleBar;
    private RelativeLayout mRelativePlayerAll;
    private MyVlcVideoView mPlayer;
    private VlcVideoView mVLCView;
    private RelativeLayout rootView;
    private TextView mTime;
    private View.OnTouchListener onTouchVideoListener;
    private ENDownloadView mLoadingView;
    private ENPlayView mStartView;
    private ImageView mLockScreen;
    private String currentTime = "0";
    private RecordEvent recordEvent = new RecordEvent();
    //录像--//老徐手机 录像地址-内部存储/Pictures/
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "CME");
    //vlc录像文件地址
    private String directory = recordFile.getAbsolutePath();
    //vlc截图文件地址
    private File shotFile = new File(Environment.getExternalStorageDirectory(), "CME");
    private boolean isFirstIn = true;
    private boolean lockType = true;   //点击界面,显示或者隐藏 控制面板的-标识
    private boolean isPlayering = false;   //视频是否播放的标识符
    private boolean mFlagRecord = false;
    private boolean mFlagMicOnLine = false;
    private TextView mRecordMsg;
    private RtmpOnlyAudio rtmpOnlyAudio;
    private String itemID;
    private String mTypeText = "高清HD";

    private String currentUrl0, currentUrl1;  //0是高清,1是标清
    private RelativeLayout mTopControl;
    private RelativeLayout mBottomControl;
    private ImageView mImageBack;
    private String mCaseID; //当前病例ID
    private static final int Lock = 100;
    private static final int Unlock = 101;
    private static final int Record = 102;
    private static final int Record_Request = 103;
    private static final int Time = 104;
    //  private static final int UDP_Cold = 108;      //获取病例ID
    private static boolean UDP_RECODE_INIT_TAG = false; //首次进入该界面,查询是否录像的标识
    private static boolean UDP_RECODE_STATUS_TAG = UDP_RECODE_INIT_TAG; //每次请求的时候,需要再次请求状态,不然容易在未接受到消息的时候出现ui错乱的bug
    private static boolean UDP_EQUALS_ID = false; //获取当前操作id,和进入该界面的id 是否相等,相等才可以进行各种操作,默认不相等,
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                0：查询录像状态 1：开始录像，2：停止录像，3：正在录像  4：未录像  5：采集卡未安装
                case Record_Request://每次请求录像操作的时候,都需要获取上位机状态再做后续操作
                    if (UDP_RECODE_INIT_TAG) {//true 表示:因为录像中,所以直接停止
                        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "2");
                    } else {//因为未录像,所以开始录像
                        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "1");
                    }
                    break;
                case Time:
                    String string = CommonUtil.stringForTime(Integer.parseInt(currentTime));
                    mTime.setText("" + string);
                    break;
                case Lock: //锁屏
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_close_ic));
                    rootView.setOnTouchListener(null);
                    rootView.setLongClickable(false);  //手势不需要需要--不能触摸
                    mTopControl.setVisibility(View.INVISIBLE);
                    mRightLiveTypeControl.setVisibility(View.INVISIBLE);
                    mBottomControl.setVisibility(View.INVISIBLE);
                    break;
                case Unlock: //解锁
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_open_ic));
                    rootView.setLongClickable(true);  //手势需要--能触摸
                    rootView.setOnTouchListener(onTouchVideoListener);
                    mTopControl.setVisibility(View.VISIBLE);
                    mRightLiveTypeControl.setVisibility(View.VISIBLE);
                    mBottomControl.setVisibility(View.VISIBLE);
                    break;
//                case Record:
////                    0：查询录像状态 1：开始录像，2：停止录像，3：正在录像  4：未录像
//                    String tag = (String) msg.obj;
//                    if ("1".equals(tag) || "3".equals(tag)) {
//                        setTextColor(getResources().getColor(R.color.white), "录像中", true);
//                    } else if ("2".equals(tag) || "4".equals(tag)) {
//                        setTextColor(getResources().getColor(R.color.white), "录像", false);
//                    } else if ("5".equals(tag)) {
//                        toast("采集卡未安装");
//                    }
//                    LogUtils.e("录像====" + tag);
//                    LogUtils.e("录像====" + UDP_RECODE_INIT_TAG);
//                    switch ((String) msg.obj) {
//                        case "0":
//                            break;
//                        case "1":
//                            setTextColor(getResources().getColor(R.color.red), "录像中", true);
//                            break;
//                        case "2":
//                            setTextColor(getResources().getColor(R.color.white), "录像", false);
//                            break;
//                        case "3":
//                            setTextColor(getResources().getColor(R.color.red), "录像中", true);
//                            break;
//                        case "4":
//                            setTextColor(getResources().getColor(R.color.white), "录像", false);
//                            break;
//                    }
//                    break;
//                case Record_Start:
//                    mFlagMicOnLine = true;
//                    setTextColor(getResources().getColor(R.color.red), "录像中", false);
//                    break;
//                case Record_Stop:
//                    mFlagMicOnLine = false;
//                    setTextColor(getResources().getColor(R.color.white), "录像", true);
//                    toast("录像成功");
//                    break;
            }
        }
    };
    private TextView mPictureDes;
    private RangeSeekBar mRangeBar01Light, mRangeBar02Light, mRangeBar02Saturation, mRangeBar02Definition, mRangeBar02Zoom;
    private SwitchButton mSwitchBlood, mSwitchVertical, mSwitchHorizontal;
    private TextView mLightLine, mDeviceLine;
    private LinearLayout mLightTab, mDeviceTab, mLightPart, mDevicePart;
    private TextView mRightLiveTypeControl;
    private HashMap<String, String> mUrlMap;
    private TextView mTitle;
    private LinearLayout mMic;
    private String micUrl;
    private TextView mTvMicStatus;
    private boolean isFirstInitData;  //第一次初始化设备参数,避免初始化的时候,设置参数进而回调发送消息设置参数的bug
    private TextView mTitleName;
    private String mName;
    private String mCaseNo;

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_CUSTOM_TOAST://吐司
                toast("" + data);
                break;
            case Constants.UDP_F0://获取当前病例
                if ("true".equals(data)) {//当前病例相同才能操作
                    UDP_EQUALS_ID = true;
                    //获取当前病例ID
                } else {
                    UDP_EQUALS_ID = false;
                }
                break;
            case Constants.UDP_14://删除病例了
                if (data.equals(mCaseID)) {//被删除的病例ID和当前的病例ID相同,退出该界面
                    showExitDialog();
                }
                break;
            case Constants.UDP_F3:////冻结与解冻:00冻结，01解冻,未调试
                if ("00".equals(data)) {
                    toast("冻结成功");
                } else {
                    toast("解冻成功");
                }
                break;
            case Constants.UDP_18://录像--->0：查询录像状态 1：开始录像，2：停止录像，3：正在录像  4：未录像
                //获取当前上位机操作的病例ID
                String mUpCaseID = event.getIp();
                if (mUpCaseID.equals(mCaseID)) {
                    String tag = (String) event.getData();
                    if ("1".equals(tag) || "3".equals(tag)) {
                        setTextColor(getResources().getColor(R.color.white), "录像中", true);
                    } else if ("2".equals(tag) || "4".equals(tag)) {
                        setTextColor(getResources().getColor(R.color.white), "录像", false);
                    } else if ("5".equals(tag)) {
                        toast("采集卡未安装");
                    }
                }
                break;
            case Constants.UDP_15://截图
                if (mCaseID.equals(event.getData())) {
                    sendRequest(mCaseID);
                }
                break;
            case Constants.UDP_F5://获取当前设备参数
                DeviceParamsBean deviceParamsBean = mGson.fromJson(event.getData(), DeviceParamsBean.class);
                //设置Tab
                setTypeTabData(deviceParamsBean);

                break;
            case Constants.UDP_F7://权限通知变动,在病例列表,病例详情,和图像采集三个界面相互监听,发现了请求后台更新本地权限
                if (event.getTga()) {
                    requestCurrentPermission();
                }
                break;
            case Constants.UDP_F4://语音接入
                //url
                micUrl = event.getData();
                String onLine = event.getIp();//(0：离线 1:上线)
                Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==micUrl===" + micUrl);

                if ("1".equals(onLine)) {
                    if (!"".equals(micUrl) && rtmpOnlyAudio.prepareAudio()) {
                        //获取当前UI tag
                        String tag = (String) mTvMicStatus.getTag();
                        //此时是关闭状态,请求推流的回调
                        if ("stopStream".equals(tag)) {
                            setMicStatus("startStream", "状态:已连接");
                            rtmpOnlyAudio.startStream(micUrl);
                            toast("开始推流");
                            Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==开始推流===");
//                            setMicStatus("startStream", "通话中..");
                            //此时是开启状态,请求关闭推流的回调
                        } else if ("startStream".equals(tag)) {
                            rtmpOnlyAudio.stopStream();
                            toast("关闭推流");
                            setMicStatus("stopStream", "语音通话");
                            Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==关闭推流===");

//                            setMicStatus("stopStream", "语音通话");
                        }
                    }
                } else {
                    toast("设备语音服务器未在线，连接失败");
                }
                break;
        }

    }

    /**
     * 获取麦克风权限
     */
    private void getMicPermission() {
        XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            //flase 表示未开启推流
                            if (HandService.UDP_HAND_GLOBAL_TAG) {//握手成功
                                if (!rtmpOnlyAudio.isStreaming()) {//false 表示还未开启推流
                                    if (rtmpOnlyAudio.prepareAudio()) {
                                        //握手成功,
                                        if (HandService.UDP_HAND_GLOBAL_TAG) {
                                            //tag为关闭状态(默认是关闭状态)点击的时候如果关闭状态就开启推流
                                            if ("stopStream".equals(mTvMicStatus.getTag())) {
                                                sendSocketPointMicMessage("1");
                                                mTvMicStatus.setText("状态:连接中");
                                            } else if ("startStream".equals(mTvMicStatus.getTag())) {
                                                mTvMicStatus.setText("语音通话");
                                                sendSocketPointMicMessage("0");
                                            }
                                        } else {
                                            toast(Constants.HAVE_HAND_FAIL_OFFLINE);
                                        }


                                    } else {
                                        toast("未获取到麦克风权限");

                                    }
                                } else {
                                    if (rtmpOnlyAudio.prepareAudio()) {
                                        //握手成功,
                                        if (HandService.UDP_HAND_GLOBAL_TAG) {
                                            //tag为关闭状态(默认是关闭状态)点击的时候如果关闭状态就开启推流
                                            if ("stopStream".equals(mTvMicStatus.getTag())) {
                                                sendSocketPointMicMessage("1");
                                                mTvMicStatus.setText("状态:连接中");
                                            } else if ("startStream".equals(mTvMicStatus.getTag())) {
                                                mTvMicStatus.setText("语音通话");
                                                sendSocketPointMicMessage("0");
                                            }
                                        } else {
                                            toast(Constants.HAVE_HAND_FAIL_OFFLINE);
                                        }


                                    } else {
                                        toast("未获取到麦克风权限");

                                    }
                                }


                            } else {
                                toast(Constants.HAVE_HAND_FAIL_OFFLINE);
                            }

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            toast("获取麦克风权限失败");
                        }
                    }
                });


    }

    private void setMicStatus(String tag, String des) {
        mTvMicStatus.setTag(tag);
        mTvMicStatus.setText(des);
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


    private void setTypeTabData(DeviceParamsBean deviceParamsBean) {
        isFirstInitData = true;
        DeviceParamsBean.Type01 type01 = deviceParamsBean.getType01(); //摄像机
        DeviceParamsBean.Type02 type02 = deviceParamsBean.getType02(); //光源
        if (type02 != null) {
            mRangeBar01Light.setProgress(Integer.parseInt(type02.getBrightness()));
        }
        if (type01 != null) {
            //光源
            mRangeBar02Light.setProgress(Integer.parseInt(type01.getBrightness()));

            //饱和度
            mRangeBar02Saturation.setProgress(Integer.parseInt(type01.getSaturation()));

            //清晰度
            mRangeBar02Definition.setProgress(Integer.parseInt(type01.getSharpness()));

            //放大倍数           //显示是1到2.5倍,传值是0--15
            float rangeBarNeedSetData = CommonUtil.getRangeBarNeedSetData(type01.getZoomrate());
            mRangeBar02Zoom.setProgress(rangeBarNeedSetData);

            //:影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）

            switch (type01.getReversal()) {
                case "0":
                    mSwitchHorizontal.setChecked(false);
                    mSwitchVertical.setChecked(false);
                    break;
                case "1":
                    mSwitchHorizontal.setChecked(true);
                    mSwitchVertical.setChecked(false);
                    break;
                case "2":
                    mSwitchHorizontal.setChecked(false);
                    mSwitchVertical.setChecked(true);
                    break;
                case "3":
                    mSwitchHorizontal.setChecked(true);
                    mSwitchVertical.setChecked(true);
                    break;

            }
            //血管增强-->8是关闭 0是打开
            //血管增强
            if ("8".equals(type01.getBloodenhance())) {
                mSwitchBlood.setChecked(false);
            } else {
                mSwitchBlood.setChecked(true);
            }

            isFirstInitData = false;

        }


    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_light_tab:        //点击光源tab
                setLightTab();
                break;
            case R.id.linear_device_tab:        //点击摄像机tab
                setDeviceTab();
                break;
            case R.id.video_back:               //全屏的时候退出界面
                //此处应该是缩小播放界面
                isFullscreen = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏动态转换
                setVideoViewFull(R.drawable.nur_ic_fangda, "竖屏");
                break;
            case R.id.root_layout_vlc:          //点击控制播放界面
                changeControlStatus();
                break;
            case R.id.control_start_view:       //重新开始链接直播
                startLive(path);
                break;
            case R.id.linear_record:            //录像,本地不做,socket通讯机子做操作
                //有权限才去做录像操作
                if (mMMKVInstace.decodeBool(Constants.KEY_SnapVideoRecord)) {
                    sendSocketPointRecodeStatusMessage(Constants.UDP_18, "0");
                    if (UDP_EQUALS_ID) {
                        mHandler.sendEmptyMessageDelayed(Record_Request, 200);
                    } else {
                        toast(Constants.UDP_CASE_ID_DIFFERENT);
                    }
                } else {
                    toast(Constants.HAVE_NO_PERMISSION);
                }


//                if (isPlayering) {
//                    if (mVLCView.isPrepare()) {
//                        if ("录像".equals(mRecordMsg.getText())) {
//                            mFlagRecord = true;
//                            mHandler.sendEmptyMessage(Record_Start);
////                        vlcVideoView.getMediaPlayer().record(directory);
//                            LogUtils.e("path=====录像--开始:=====" + directory); //   /storage/emulated/0/1604026573438.mp4
//                            recordEvent.startRecord(mVLCView.getMediaPlayer(), directory, "cme.mp4");
//                        } else {
//                            vlcRecordOver();
//                        }
//                    } else {
//                        vlcRecordOver();
//                    }
//                } else {
//                    toast("只有在播放的时候才能录像!");
//                }
                break;
            case R.id.linear_picture:           //截图,本地不做,socket通讯机子做操作
                if (HandService.UDP_HAND_GLOBAL_TAG) {
                    if (UDP_EQUALS_ID) {
                        if (mMMKVInstace.decodeBool(Constants.KEY_SnapVideoRecord)) {
                            sendSocketPointShotMessage(Constants.UDP_15);
                        } else {
                            toast(Constants.HAVE_NO_PERMISSION);
                        }
                    } else {
                        toast(Constants.UDP_CASE_ID_DIFFERENT);
                    }
                } else {
                    toast(Constants.HAVE_HAND_FAIL_OFFLINE);
                }
//                if (isPlayering) {
//                    if (mVLCView.isPrepare()) {
//                        Media.VideoTrack videoTrack = mVLCView.getVideoTrack();
//                        if (videoTrack != null) {
//                            toast("截图成功");
//                            //原图
//                            LogUtils.e("path=====截图地址:=====" + shotFile.getAbsolutePath());
//                            File localFile = new File(shotFile.getAbsolutePath());
//                            if (!localFile.exists()) {
//                                localFile.mkdir();
//                            }
//                            recordEvent.takeSnapshot(mVLCView.getMediaPlayer(), shotFile.getAbsolutePath(), 0, 0);
//                            //插入相册 解决了华为截图显示问题
//                            MediaStore.Images.Media.insertImage(getContentResolver(), mVLCView.getBitmap(), "", "");
//                            //原图的一半
//                            //recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
//                        }
//                    }
//                    //这个就是截图 保存Bitmap就行了
//                    //thumbnail.setImageBitmap(vlcVideoView.getBitmap());
//                    //Bitmap bitmap = vlcVideoView.getBitmap();
//                    //saveBitmap("", bitmap);
//                } else {
//                    toast("只有在播放的时候才能截图!");
//                }
                break;
//            case R.id.linear_cold:              //冻结
//                sendSocketPointMessage(Constants.UDP_F3);
//                break;
            case R.id.linear_mic:               //麦克风
                getMicPermission();
                break;
            case R.id.lock_screen:  //锁屏
                if (mLockScreen.getTag().equals("lock")) {   //解锁
                    mHandler.sendEmptyMessage(Unlock);
                    mLockScreen.setTag("unLock");
                } else {
                    mLockScreen.setTag("lock");     //锁屏
                    mHandler.sendEmptyMessage(Lock);
                }
                break;
            case R.id.full_change:
                isFullscreen = !isFullscreen;
                if (isFullscreen) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); //横屏动态转换
                    setVideoViewFull(R.drawable.nur_ic_fangxiao, "横屏");

                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏动态转换
                    setVideoViewFull(R.drawable.nur_ic_fangda, "竖屏");

                }
                break;
        }
    }

    private void setDeviceTab() {
        mLightLine.setVisibility(View.INVISIBLE);
        mDeviceLine.setVisibility(View.VISIBLE);
        mLightPart.setVisibility(View.INVISIBLE);
        mDevicePart.setVisibility(View.VISIBLE);
        mLightTab.setBackgroundResource(R.drawable.shape_bg_getpicture_light_nor);
        mDeviceTab.setBackgroundResource(R.drawable.shape_bg_getpicture_device_pre);
    }

    private void setLightTab() {
        mLightLine.setVisibility(View.VISIBLE);
        mDeviceLine.setVisibility(View.INVISIBLE);
        mLightPart.setVisibility(View.VISIBLE);
        mDevicePart.setVisibility(View.INVISIBLE);
        mLightTab.setBackgroundResource(R.drawable.shape_bg_getpicture_light_pre);
        mDeviceTab.setBackgroundResource(R.drawable.shape_bg_getpicture_device_nor);
    }

    public void setTextColor(int color, String message, Boolean recodeStatus) {
        UDP_RECODE_INIT_TAG = recodeStatus;
        mRecordMsg.setText(message);
        mRecordMsg.setTextColor(color);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_picture;
    }

    @Override
    protected void initView() {
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        initLayoutView();
    }

    @Override
    protected void initData() {

        rootView.setLongClickable(true);  //手势需要--能触摸
        rootView.setOnTouchListener(onTouchVideoListener);
        //设置拖动条数字格式
        mRangeBar01Light.setIndicatorTextDecimalFormat("0");
        mRangeBar02Light.setIndicatorTextDecimalFormat("0");
        mRangeBar02Saturation.setIndicatorTextDecimalFormat("0");
        mRangeBar02Definition.setIndicatorTextDecimalFormat("0");
        mRangeBar02Zoom.setIndicatorTextDecimalFormat("0.0");
        //rootView.setOnTouchListener(null);
        //rootView.setLongClickable(false);  //手势不需要需要--不能触摸
        rtmpOnlyAudio = new RtmpOnlyAudio(this);

        //设置标题
        mTitle.setText(mCurrentTypeDes + "(" + mSocketOrLiveIP + ")");

        //获取当前病例ID,和播放地址
        Bundle bundle = getIntent().getExtras();
        currentUrl0 = bundle.getString("currentUrl0");
        currentUrl1 = bundle.getString("currentUrl1");
        mCaseID = bundle.getString("ItemID");
        path = currentUrl0;
        startLive(path);
        //存储url地址
        mUrlMap = new HashMap<>();
        mUrlMap.put("高清HD", currentUrl0);
        mUrlMap.put("标清SD", currentUrl1);
        responseListener();


    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */


    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     * @param status  状态值
     */
    public void sendSocketPointRecodeStatusMessage(String CMDCode, String status) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            RecodeBean bean = new RecodeBean();
            bean.setQrycode(status);
            if (!("".equals(mCaseID))) {
                String id = CalculateUtils.numToHex16(Integer.parseInt(mCaseID));
                bean.setRecordid(id);
            } else {
                bean.setRecordid("");
            }
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
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
            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);


        }

    }

    /**
     * 发送语音链接
     *
     * @param onLineStatus 开关语音标识:(0：离线 1:上线)
     */
    public void sendSocketPointMicMessage(String onLineStatus) {
        MicRequestBean micRequestBean = new MicRequestBean();
        //手机厂商_手机型号_UserName     HUAWEI_SCMR-W09_Admin
        micRequestBean.setTitle(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
        micRequestBean.setCalltype("rtmp");
        micRequestBean.setServicemode("1");
        micRequestBean.setOnline(onLineStatus);
        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(micRequestBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_F4);

        if (("".equals(mSocketPort))) {
            toast("通讯端口不能为空");
            return;
        }
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), this);
    }

    /**
     * 采图--->发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointShotMessage(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            ShotPictureBean bean = new ShotPictureBean();
            String spCaseID = mMMKVInstace.decodeString(Constants.KEY_CurrentCaseID);
            String hexID = CalculateUtils.numToHex16(Integer.parseInt(spCaseID));
            bean.setRecordid(hexID);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * 获取参数-->获取冷光源参数
     * 02-冷光源(type02)
     *
     * @param CMDCode 命令cmd
     */
    public void getSocketLightData(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            Type02Bean bean = new Type02Bean();
            Type02Bean.Type02 Type02 = new Type02Bean.Type02();
            Type02.setBrightness("");
            bean.setType02(Type02);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }


    /**
     * 参数上传-->发送冷光源参数
     * 02-冷光源(type02)
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointLight(String CMDCode, String data) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            Type02Bean bean = new Type02Bean();
            Type02Bean.Type02 typeBean = new Type02Bean.Type02();
            typeBean.setBrightness(data);
            bean.setType02(typeBean);

            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * 参数上传(设置参数)-->发送摄像机
     * 01-冷光源(type01)
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointVideoDevice(String CMDCode, Type01Bean bean) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    private void responseListener() {
//        R.id.linear_mic, R.id.linear_cold,
        setOnClickListener(R.id.linear_light_tab, R.id.linear_device_tab, R.id.linear_record, R.id.linear_picture,
                R.id.full_change, R.id.lock_screen, R.id.root_layout_vlc, R.id.video_back, R.id.control_start_view, R.id.linear_mic);
        //设置拖动条监听
        mRangeBar01Light.setOnRangeChangedListener(this);
        mRangeBar02Light.setOnRangeChangedListener(this);
        mRangeBar02Saturation.setOnRangeChangedListener(this);
        mRangeBar02Definition.setOnRangeChangedListener(this);
        mRangeBar02Zoom.setOnRangeChangedListener(this);

        //设置switch的按钮监听
        mSwitchBlood.setOnCheckedChangeListener(this);
        mSwitchVertical.setOnCheckedChangeListener(this);
        mSwitchHorizontal.setOnCheckedChangeListener(this);



        mVLCView.setMediaListenerEvent(new MediaListenerEvent() {
            @Override
            public void eventBuffing(int event, float buffing) {
                if (buffing < 100) {
                    mLoadingView.start();
                    mLoadingView.setVisibility(View.VISIBLE);
                } else if (buffing == 100) {
                    isPlayering = true;
                    mLoadingView.release();
                    mLoadingView.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void eventStop(boolean isPlayError) {
                mLoadingView.setVisibility(View.INVISIBLE);
                mStartView.setVisibility(View.VISIBLE);
            }

            @Override
            public void eventError(int event, boolean show) {
                isPlayering = false;
                mStartView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void eventPlayInit(boolean openClose) {
                mStartView.setVisibility(View.INVISIBLE);
//                error_text.setVisibility(View.INVISIBLE);
            }

            @Override
            public void eventPlay(boolean isPlaying) {

            }

            @Override
            public void eventSystemEnd(String isStringed) {
                isPlayering = false;
                if ("EndReached".equals(isStringed)) {
//                    if (mFlagRecord) { //如果在录像，断开录像
//                        vlcRecordOver();
//                    }
                    if (mFlagMicOnLine) {//如果在连麦，断开连麦
//                        pusherStop("Common");
                    }

                }
            }

            @Override
            public void eventCurrentTime(String time) {
                currentTime = time;
                mHandler.sendEmptyMessageDelayed(Time, 1000);
            }


        });


        //切换清晰度的点击事件
        mRightLiveTypeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedUrlTypeDialog();
            }


        });

        //退出界面断开语音通话
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                if (rtmpOnlyAudio.isStreaming()) {
                    rtmpOnlyAudio.stopStream();
                }
                setMicStatus("stopStream", "语音通话");
                finish();
            }

            @Override
            public void onTitleClick(View view) {
            }

            @Override
            public void onRightClick(View view) {

            }
        });


    }

    /**
     * 切换清晰度
     */
    private void showSelectedUrlTypeDialog() {
        if (mUrlMap.size() > 0) {
            new SelectDialog.Builder(this)
                    .setTitle("请选择清晰度")
                    .setList("高清HD", "标清SD")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(0)
                    .setListener(new SelectDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                            String string = data.toString();
                            int size = data.size();
                            int i = string.indexOf("=");
                            String value = string.substring(i + 1, string.length() - 1);
//                            toast("确定了：" + value);

                            String url = mUrlMap.get(value);
                            Drawable urlTypeSD = getResources().getDrawable(R.mipmap.icon_url_type_sd);
                            Drawable urlTypeHD = getResources().getDrawable(R.mipmap.icon_url_type_hd);
                            if ("高清HD".equals(value)) {
                                mRightLiveTypeControl.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeHD, null, null);
                            } else if ("标清SD".equals(value)) {
                                mRightLiveTypeControl.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeSD, null, null);
                            }

                            startLive(url);
                            mRightLiveTypeControl.setText(value + "");
//                            mTypeText=value;

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                        }
                    })
                    .show();


        }


    }

    private void finishThisActivity() {
//        if (mFlagRecord) {
//            vlcRecordOver();
//        }
        mVLCView.setAddSlave(null);
        mVLCView.onDestroy();

        finish();
    }

    /**
     * 不管是锁屏,还是解锁状态下,点击改变控制View的状态显示
     */
    private void changeControlStatus() {
        if (mLockScreen.getTag().equals("lock")) {   //当前为-锁屏-状态,需要解锁,默认unlock
            if (lockType) { ////点击界面,显示或者隐藏 控制面板的-标识,默认显示
                lockType = false;
                mLockScreen.setVisibility(View.INVISIBLE);
            } else {
                lockType = true;
                mLockScreen.setVisibility(View.VISIBLE);
            }
        } else {                                      //当前为-解锁-状态,需要锁屏
            if (lockType) {
                lockType = false;
                mTopControl.setVisibility(View.INVISIBLE);
                mRightLiveTypeControl.setVisibility(View.INVISIBLE);
                mBottomControl.setVisibility(View.INVISIBLE);
                mLockScreen.setVisibility(View.INVISIBLE);
            } else {
                lockType = true;
                mRightLiveTypeControl.setVisibility(View.VISIBLE);
                mLockScreen.setVisibility(View.VISIBLE);
                mTopControl.setVisibility(View.VISIBLE);
                mBottomControl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//
            setVideoViewFull(R.drawable.nur_ic_fangxiao, "横屏");
        } else {
            setVideoViewFull(R.drawable.nur_ic_fangda, "竖屏");
        }
    }

    private void setVideoViewFull(int mID, String type) {
        if ("横屏".equals(type)) { //放小
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitleBar.setVisibility(View.GONE);
            mLinearBottom.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);//工具类哦
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);
            mImageBack.setVisibility(View.VISIBLE);
            mTitleName.setVisibility(View.GONE);

        } else {//放大
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitleBar.setVisibility(View.VISIBLE);
            mLinearBottom.setVisibility(View.VISIBLE);
            mTitleName.setVisibility(View.VISIBLE);
            mImageBack.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenSizeUtil.dp2px(GetPictureActivity.this, getResources().getDimension(R.dimen.dp_80)));//工具类哦
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);

        }
    }

    private void initLayoutView() {
        //设置沉浸式观影模式体验
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTitleBar = findViewById(R.id.titlebar_get);
        mStatusLayout = findViewById(R.id.picture_status_hint);
        mChangeFull = findViewById(R.id.full_change);
        mPictureDes = findViewById(R.id.case_picture); //采图
        mTvMicStatus = findViewById(R.id.tv_mic_status); //语音
        mTime = findViewById(R.id.tv_time);
        mLinearBottom = findViewById(R.id.linear_bottom);
        mTitleName = findViewById(R.id.tv_title_name);
        mTopControl = findViewById(R.id.relative_top_control);
        mBottomControl = findViewById(R.id.relative_bottom_control);
        mRelativePlayerAll = findViewById(R.id.ff_player_all);
        mPlayer = findViewById(R.id.player);
        mLockScreen = findViewById(R.id.lock_screen);
        mTitle = findViewById(R.id.tv_title);
        mLoadingView = findViewById(R.id.control_load_view);
        mStartView = findViewById(R.id.control_start_view);
        mImageBack = findViewById(R.id.video_back);
        mRightLiveTypeControl = findViewById(R.id.change_live_type);
//        LinearLayout mRecord = findViewById(R.id.linear_record);
//        LinearLayout mShot = findViewById(R.id.linear_picture);
//        LinearLayout mCold = findViewById(R.id.linear_cold);
        mMic = findViewById(R.id.linear_mic);
        mRecordMsg = findViewById(R.id.case_record);
        //亮度
        mRangeBar01Light = findViewById(R.id.sb_01_range_light);

        mRangeBar01Light.setStepsAutoBonding(false);
        //亮度
        mRangeBar02Light = findViewById(R.id.sb_02_range_light);
        //饱和度
        mRangeBar02Saturation = findViewById(R.id.sb_02_range_saturation);
        //清晰度
        mRangeBar02Definition = findViewById(R.id.sb_02_range_definition);
        //放大倍数
        mRangeBar02Zoom = findViewById(R.id.sb_02_range_zoom);
        //水平翻转
        mSwitchHorizontal = findViewById(R.id.sb_find_switch_horizontal);
        //垂直翻转
        mSwitchVertical = findViewById(R.id.sb_find_switch_vertical);
        //血管增强
        mSwitchBlood = findViewById(R.id.sb_find_switch_blood);
        mLightLine = findViewById(R.id.view_light);
        mDeviceLine = findViewById(R.id.view_device);
        mLightTab = findViewById(R.id.linear_light_tab);
        mDeviceTab = findViewById(R.id.linear_device_tab);
        mLightPart = findViewById(R.id.linear_light_part);
        mDevicePart = findViewById(R.id.linear_device_part);
        //默认选中光源tab
        setLightTab();
        mImageBack.setVisibility(View.INVISIBLE);
        rootView = mPlayer.getRootView();                         //点击控制锁显示和隐藏的
        onTouchVideoListener = mPlayer.getOnTouchVideoListener();
        mVLCView = mPlayer.findViewById(R.id.vlc_video_view);

    }


    /**
     * 当前用户被其他设备或者上位机删除了 同步更新退出界面
     */
    private void showExitDialog() {
        // 自定义对话框
        new BaseDialog.Builder<>(this)
                .setContentView(R.layout.dialog_custom_exit)
                .setAnimStyle(BaseDialog.ANIM_SCALE)
                //.setText(id, "我是预设置的文本")
                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        SocketRefreshEvent event1 = new SocketRefreshEvent();
                        event1.setTga(true);
                        event1.setData(Constants.UDP_CUSTOM14);//只回调病例ID,回调的病例ID和当前App操作的病例ID 不同的时候不作处理
                        event1.setIp(Constants.UDP_CUSTOM14);
                        event1.setUdpCmd(Constants.UDP_CUSTOM14);
                        EventBus.getDefault().post(event1);
                        finish();
                    }
                })
                .setOnKeyListener((dialog, event) -> {
                    toast("按键代码：" + event.getKeyCode());
                    return false;
                })
                .show();
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

//    //结束录像
//    private void vlcRecordOver() {
//        mFlagRecord = false;
//        mHandler.sendEmptyMessage(Record_Stop);
//        mVLCView.getMediaPlayer().record(null);
////        FileUtil.scanFile(VlcPlayerActivity.this, directory);
//        FileUtil.RefreshAlbum(directory, true, this);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstIn = true;
        startLive(path);
        //握手通讯
        sendRequest(mCaseID);
        //获取当前设备参数
        getSocketLightData(Constants.UDP_F5);
        //获取当前病例ID
        sendSocketPointMessage(Constants.UDP_F0);
        //实时获取当前上位机,录像的状态
        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "0");
    }

    //获取病例图片数目
    private void sendRequest(String mCaseID) {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
                .addParams("ID", mCaseID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendRequest(mCaseID);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            CaseDetailBean mBean = mGson.fromJson(response, CaseDetailBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                int imageCount = mBean.getData().getImagesCount();
                                CaseDetailBean.DataDTO data = mBean.getData();
                                mPictureDes.setText("采图(" + imageCount + ")");
                                mCaseNo = mBean.getData().getCaseNo();
                                mName = mBean.getData().getName();
                                mTitleName.setText(mName+"-"+mCaseNo);


                            } else {
                                showError(listener -> {
                                    sendRequest(mCaseID);
                                });
                            }
                        } else {
                            showError(listener -> {
                                sendRequest(mCaseID);
                            });
                        }
                    }
                });
    }

    private void startLive(String path) {
        mVLCView.setPath(path);
        mVLCView.startPlay();
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //直接调用stop 不然回ANR
        mVLCView.onStop();
        mLoadingView.release();
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoadingView.release();
        mStartView.pause();
        mVLCView.setAddSlave(null);
        mVLCView.onStop();
        mLoadingView.release();
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isFirstIn = false;
//        mVLCView.setMediaListenerEvent(null);
        mVLCView.onStop();
        mVLCView.onDestroy();


    }


    /**
     * switch 按钮的监听
     *
     * @param button  切换按钮
     * @param checked 是否选中
     */
    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        if (isFirstInitData) {
            return;
        }
        Type01Bean bean = new Type01Bean();
        Type01Bean.Type01 typeBean = new Type01Bean.Type01();
        //影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）
        switch (button.getId()) {
            // 先判断两者状态
            //水平和垂直都是关闭  发0
            //两者都是开         发3
            //水平开 垂直关       发1
            //水平关 垂直开       发2
            case R.id.sb_find_switch_vertical:  //水平翻转
            case R.id.sb_find_switch_horizontal://垂直翻转
                boolean mFlagHorizontal = mSwitchHorizontal.isChecked();
                boolean mFlagVertical = mSwitchVertical.isChecked();

                if (mFlagHorizontal && mFlagVertical) {
                    typeBean.setReversal("3");

                } else if (!mFlagHorizontal && !mFlagVertical) {
                    typeBean.setReversal("0");

                } else if (mFlagHorizontal && !mFlagVertical) {
                    typeBean.setReversal("1");

                } else if (!mFlagHorizontal && mFlagVertical) {
                    typeBean.setReversal("2");

                }
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                break;
            case R.id.sb_find_switch_blood://血管增强   关闭是8  打开是0
                if (checked) {
                    typeBean.setBloodenhance("0");
                } else {
                    typeBean.setBloodenhance("8");
                }
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                break;

        }
    }

    /**
     * RangeBar 监听器
     *
     * @param view
     * @param leftValue
     * @param rightValue
     * @param isFromUser
     */
    @Override
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

    }


    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
        if (isFirstInitData) {
            return;
        }
        float progress = view.getLeftSeekBar().getProgress();
        String round = (Math.round(progress) + "").replace(".", "");
        //创建摄像机数据bean
        Type01Bean bean = new Type01Bean();
        Type01Bean.Type01 typeBean = new Type01Bean.Type01();

        switch (view.getId()) {
            case R.id.sb_01_range_light:  //冷光源,亮度
                sendSocketPointLight(Constants.UDP_F6, "" + round);
                break;
            case R.id.sb_02_range_light://摄像机,亮度
                typeBean.setBrightness(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);

                break;
            case R.id.sb_02_range_saturation://摄像机,饱和度
                typeBean.setSaturation(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                break;
            case R.id.sb_02_range_definition://摄像机,清晰度
                typeBean.setSharpness(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                break;
            case R.id.sb_02_range_zoom://摄像机,放大倍数
                //显示是1到2.5倍,传值是0--15
                float progress1 = ((view.getLeftSeekBar().getProgress()) * 10) - 10;
                int round2 = Math.round(progress1); //传的值
                String s = view.getLeftSeekBar().getProgress() + ""; //editText设置的值
                typeBean.setZoomrate(round2 + "");
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                break;

        }
    }



    /**
     * rtmp推流 音频
     */
    @Override
    public void onConnectionSuccessRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onConnectionSuccessRtmp==");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast("语音链接成功");
//                setMicStatus("startStream", "通话中..");

            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(String reason) {
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Log.e("TAG", "RtmpOnlyAudio===onConnectionFailedRtmp==" + reason);

                Log.e("TAG", "RtmpOnlyAudio=====" + reason);
                toast("语音链接失败: " + reason);
                mTvMicStatus.setText("状态:未连接");
                rtmpOnlyAudio.stopStream();
                sendSocketPointMicMessage("0");


            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==");
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==");
                toast("语音断开链接 ");
                setMicStatus("stopStream", "语音通话");
//                sendSocketPointMicMessage("0");
            }
        });
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("TAG", "RtmpOnlyAudio=====onDisconnectRtmp");
//                if (!isOnPauseExit) {
//                    startSendToast("断开麦克风链接");
//                }
//            }
//        });
    }

    @Override
    public void onAuthErrorRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onAuthErrorRtmp==");
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Log.e("TAG", "RtmpOnlyAudio===onConnectionFailedRtmp==");
                toast("语音断开链接 ");

            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onAuthSuccessRtmp==");

    }

}