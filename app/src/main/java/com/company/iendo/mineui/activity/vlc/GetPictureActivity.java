package com.company.iendo.mineui.activity.vlc;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.MicVoiceBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.event.RefreshCaseMsgEvent;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.MicSocketBean;
import com.company.iendo.bean.socket.RecodeBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.bean.socket.params.DeviceParamsBean;
import com.company.iendo.bean.socket.params.Type01Bean;
import com.company.iendo.bean.socket.params.Type02Bean;
import com.company.iendo.mineui.fragment.casemanage.adapter.CaseManageAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.service.HandService;
import com.company.iendo.ui.dialog.DateDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.utils.SystemUtil;
import com.company.iendo.widget.StatusLayout;
import com.company.iendo.widget.vlc.ENDownloadView;
import com.company.iendo.widget.vlc.ENPlayView;
import com.company.iendo.widget.vlc.MyVlcVideoView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.SwitchButton;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.pedro.rtplibrary.rtmp.RtmpOnlyAudio;
import com.tencent.mmkv.MMKV;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android ?????????
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : ??????????????????
 * ????????????  1-2.5 ???????????????    ???????????????1--15
 */
public final class GetPictureActivity extends AppActivity implements StatusAction, OnRangeChangedListener,
        SwitchButton.OnCheckedChangeListener, ConnectCheckerRtmp, BaseAdapter.OnItemClickListener {
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
    //??????--//???????????? ????????????-????????????/Pictures/
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "CME");
    //vlc??????????????????
    private String directory = recordFile.getAbsolutePath();
    //vlc??????????????????
    private File shotFile = new File(Environment.getExternalStorageDirectory(), "CME");
    private boolean isFirstIn = true;
    private boolean lockType = true;   //????????????,?????????????????? ???????????????-??????
    private boolean isPlayering = false;   //??????????????????????????????
    private boolean mFlagRecord = false;
    private boolean mFlagMicOnLine = false;
    private TextView mRecordMsg;
    private RtmpOnlyAudio rtmpOnlyAudio;
    private AudioManager mAudiomanager;
    private int mCurrentVolume;
    private String itemID;
    private String mTypeText = "??????HD";
    private List<CaseManageListBean.DataDTO> mDataLest = new ArrayList<>();      //???????????????list??????

    private String currentUrl0, currentUrl1;  //0?????????,1?????????
    private RelativeLayout mTopControl;
    private RelativeLayout mBottomControl;
    private ImageView mImageBack;
    private String mCaseID; //????????????ID
    private static final int Lock = 100;
    private static final int Unlock = 101;
    private static final int Record = 102;
    private static final int Record_Request = 103;
    private static final int Time = 104;
    //  private static final int UDP_Cold = 108;      //????????????ID
    private static boolean UDP_RECODE_INIT_TAG = false; //?????????????????????,???????????????????????????
    private static boolean UDP_RECODE_STATUS_TAG = UDP_RECODE_INIT_TAG; //?????????????????????,????????????????????????,????????????????????????????????????????????????ui?????????bug
    private static boolean UDP_EQUALS_ID = false; //??????????????????id,?????????????????????id ????????????,?????????????????????????????????,???????????????,
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                0????????????????????? 1??????????????????2??????????????????3???????????????  4????????????  5?????????????????????
                case Record_Request://?????????????????????????????????,????????????????????????????????????????????????
                    if (UDP_RECODE_INIT_TAG) {//true ??????:???????????????,??????????????????
                        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "2");
                    } else {//???????????????,??????????????????
                        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "1");
                    }
                    break;
                case Time:
                    String string = CommonUtil.stringForTime(Integer.parseInt(currentTime));
                    mTime.setText("" + string);
                    break;
                case Lock: //??????
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_close_ic));
                    rootView.setOnTouchListener(null);
                    rootView.setLongClickable(false);  //?????????????????????--????????????
                    mTopControl.setVisibility(View.INVISIBLE);
                    mRightLiveTypeControl.setVisibility(View.INVISIBLE);
                    mBottomControl.setVisibility(View.INVISIBLE);
                    mMicStatueView.setVisibility(View.INVISIBLE);
                    break;
                case Unlock: //??????
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_open_ic));
                    rootView.setLongClickable(true);  //????????????--?????????
                    rootView.setOnTouchListener(onTouchVideoListener);
                    mTopControl.setVisibility(View.VISIBLE);
                    mRightLiveTypeControl.setVisibility(View.VISIBLE);
                    mBottomControl.setVisibility(View.VISIBLE);

                    break;
//                case Record:
////                    0????????????????????? 1??????????????????2??????????????????3???????????????  4????????????
//                    String tag = (String) msg.obj;
//                    if ("1".equals(tag) || "3".equals(tag)) {
//                        setTextColor(getResources().getColor(R.color.white), "?????????", true);
//                    } else if ("2".equals(tag) || "4".equals(tag)) {
//                        setTextColor(getResources().getColor(R.color.white), "??????", false);
//                    } else if ("5".equals(tag)) {
//                        toast("??????????????????");
//                    }
//                    LogUtils.e("??????====" + tag);
//                    LogUtils.e("??????====" + UDP_RECODE_INIT_TAG);
//                    switch ((String) msg.obj) {
//                        case "0":
//                            break;
//                        case "1":
//                            setTextColor(getResources().getColor(R.color.red), "?????????", true);
//                            break;
//                        case "2":
//                            setTextColor(getResources().getColor(R.color.white), "??????", false);
//                            break;
//                        case "3":
//                            setTextColor(getResources().getColor(R.color.red), "?????????", true);
//                            break;
//                        case "4":
//                            setTextColor(getResources().getColor(R.color.white), "??????", false);
//                            break;
//                    }
//                    break;
//                case Record_Start:
//                    mFlagMicOnLine = true;
//                    setTextColor(getResources().getColor(R.color.red), "?????????", false);
//                    break;
//                case Record_Stop:
//                    mFlagMicOnLine = false;
//                    setTextColor(getResources().getColor(R.color.white), "??????", true);
//                    toast("????????????");
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
    private boolean isFirstInitData;  //??????????????????????????????,????????????????????????,???????????????????????????????????????????????????bug
    private TextView mTitleName;
    private String mName;
    private String mCaseNo;
    private TextView m01LightBlack;
    private TextView m01LightAdd;
    private TextView m02LightBlack;
    private TextView m02LightAdd;
    private TextView m02SaturationBlack;
    private TextView m02SaturationAdd;
    private TextView m02DefinitionBlack;
    private TextView m02DefinitionAdd;
    private TextView m02ZoomBlack;
    private TextView m02ZoomAdd;
    private TextView m01LightDesc;
    private TextView m02LightDesc;
    private TextView m02SaturationDesc;
    private TextView m02DefinitionDesc;
    private TextView m02ZoomDesc;
    private TextView mCurrentCheckPatientInfo;
    private TextView mCurrentSocketStatue;
    private LinearLayout mLinearStatueView;
    private RelativeLayout mRelativeChangeAll;
    private TextView mTvLiftFinish;
    private LinearLayout mLinearTitleAll;
    private ImageView mChangeAnimImage;
    private DateDialog.Builder mDateDialog;
    private CaseManageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mRecycleEmpty;
    private TextView mChangeTitleData;
    private Button mBtnSureChange;
    private String itemCaseID;
    private CaseManageListBean.DataDTO itemBean;
    private TextView mMicStatueView;

    /**
     * eventbus ??????socket??????
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://??????
                LogUtils.e("????????????====" + HandService.UDP_HAND_GLOBAL_TAG);
                mCurrentSocketStatue.setTextColor(getResources().getColor(R.color.color_25A5FF));
                mCurrentSocketStatue.setText(Constants.SOCKET_STATUE_ONLINE);
                if (View.VISIBLE == mMicStatueView.getVisibility()) { //????????????????????????
                    mCurrentSocketStatue.setTextColor(getResources().getColor(R.color.color_25A5FF));
                    mCurrentSocketStatue.setText("???????????????????????????,??????????????????:?????????");
                } else {
                    mCurrentSocketStatue.setTextColor(getResources().getColor(R.color.color_25A5FF));
                    mCurrentSocketStatue.setText("???????????????????????????,??????????????????:?????????");
                }
                break;
            case Constants.UDP_CUSTOM_TOAST://??????
                toast("" + data);
                break;
            case Constants.UDP_F0://???????????????????????????ID,??????????????????,?????????????????????
                //?????????????????????ID
                if ("true".equals(data)) {//??????????????????????????????
                    UDP_EQUALS_ID = true;
                    //??????????????????ID
                } else {
                    UDP_EQUALS_ID = false;
                }
                String mServerCaseID = event.getIp();
                sendRequestToGetServerCaseInfo(mServerCaseID);
                break;
            case Constants.UDP_14://???????????????
                if (data.equals(mCaseID)) {//??????????????????ID??????????????????ID??????,???????????????
                    showExitDialog();
                }
                break;
            case Constants.UDP_F3:////???????????????:00?????????01??????,?????????
                if ("00".equals(data)) {
                    toast("????????????");
                } else {
                    toast("????????????");
                }
                break;
            case Constants.UDP_18://??????--->0????????????????????? 1??????????????????2??????????????????3???????????????  4????????????
                //????????????????????????????????????ID
                String mUpCaseID = event.getIp();
                if (mUpCaseID.equals(mCaseID)) {
                    String tag = (String) event.getData();
                    if ("1".equals(tag) || "3".equals(tag)) {
                        setTextColor(getResources().getColor(R.color.white), "?????????", true);
                    } else if ("2".equals(tag) || "4".equals(tag)) {
                        setTextColor(getResources().getColor(R.color.white), "??????", false);
                    } else if ("5".equals(tag)) {
                        toast("??????????????????");
                    }
                }
                break;
            case Constants.UDP_15://??????
                if (mCaseID.equals(event.getData())) {
                    sendRequest(mCaseID);
                }
                break;
            case Constants.UDP_F5://????????????????????????
                DeviceParamsBean deviceParamsBean = mGson.fromJson(event.getData(), DeviceParamsBean.class);
                //??????Tab
                setTypeTabData(deviceParamsBean);

                break;
            case Constants.UDP_F7://??????????????????,???????????????,????????????,???????????????????????????????????????,???????????????????????????????????????
                if (event.getTga()) {
                    requestCurrentPermission();
                }
                break;
            case Constants.UDP_42://????????????,????????????????????????,?????????????????????vioceID
                getVoiceIDRequest();
                break;
            case Constants.UDP_41://????????????
                String Operation = event.getIp();
                String url = event.getData();
                //Operation
                //0???????????????????????????
                //1: ????????????????????????????????????
                //2????????????????????????Nginx???
                //3?????????Nginx???????????????
                //5???????????????
                //6?????????????????????????????????????????????

                if (Operation.equals("2")) {
                    upLoadStreamToNginx(url);


                } else if (Operation.equals("3")) {
//                    mTvMicStatus.setTag("startStream");
//                    mTvMicStatus.setText("???????????????");
//                    MicSocketBean bean = new MicSocketBean();
//                    bean.setErrCode("0");
//                    bean.setOperation("1");
//                    bean.setVoiceID("");
//                    bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
//                    bean.setUrl("");
//                    sendSocketPointMicMessage(bean);

                } else if (Operation.equals("5")) {
                    stopMicSteam();
                    getVoiceIDRequest();

                } else if (Operation.equals("6")) {
//                    mTvMicStatus.setTag("stopStream");
//                    mTvMicStatus.setText("???????????????");
//                    MicSocketBean bean = new MicSocketBean();
//                    bean.setErrCode("0");
//                    bean.setOperation("6");
//                    bean.setVoiceID("");
//                    bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
//                    bean.setUrl("");
//                    sendSocketPointMicMessage(bean);

                }
                break;
        }

    }

    private void stopMicSteam() {
        rtmpOnlyAudio.stopStream();
        mMicStatueView.setVisibility(View.INVISIBLE);

    }

    /**
     * ??????????????????Nginx;
     *
     * @param url
     */
    private void upLoadStreamToNginx(String url) {
        LogUtils.e("Nginx????????????=" + url);
//        flase ?????????????????????
        if (HandService.UDP_HAND_GLOBAL_TAG) {//????????????
            if (!rtmpOnlyAudio.isStreaming()) {//false ????????????????????????
                if (rtmpOnlyAudio.prepareAudio()) {
                    //????????????,
                    if (HandService.UDP_HAND_GLOBAL_TAG) {
                        rtmpOnlyAudio.startStream(url);
                    } else {
                        LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
                    }

                } else {
                    toast("???????????????????????????");

                }
            } else {
                if (rtmpOnlyAudio.prepareAudio()) {
                    //????????????,
                    if (HandService.UDP_HAND_GLOBAL_TAG) {
                        rtmpOnlyAudio.startStream(url);
                    } else {
                        LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
                    }
                } else {
                    toast("???????????????????????????");

                }
            }


        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
        }


    }

    private boolean mTagCanVoice = false;

    /**
     * ?????????????????????VoiceID
     */
    public void getVoiceIDRequest() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_getVoiceID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("TAG", "RtmpOnlyAudio===onDisconnectRtmp==????????????===" + response);
                        if (!"".equals(response)) {
                            MicVoiceBean micVoiceBean = mGson.fromJson(response, MicVoiceBean.class);
                            if (micVoiceBean.getCode() == 0) {
                                //????????????????????????????????????voiceID
                                mMMKVInstace.encode(Constants.KET_MIC_CURRENT_VOICE_ID, micVoiceBean.getData().getVoiceStationID() + "");
                                String currentVoiceID = micVoiceBean.getData().getVoiceStationID() + "";
                                String voiceIDForMe = mMMKVInstace.decodeString(Constants.KET_MIC_VOICE_ID_FOR_ME, "ABC");
                                LogUtils.e("TAG" + "????????????:--->?????????????????????ID:" + voiceIDForMe);
                                LogUtils.e("TAG" + "????????????:--->http??????voiceID:" + currentVoiceID);
                                LogUtils.e("TAG" + "????????????:--------------------------:");

                                if ("255".equals(currentVoiceID) || voiceIDForMe.equals(currentVoiceID)) {//255????????????
                                    mTagCanVoice = true;
                                    toast("??????????????????");
                                    rootView.setLongClickable(true);  //????????????--?????????
                                    rootView.setOnTouchListener(onTouchVideoListener);
                                    mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    // ?????????????????????
                                    mCurrentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    int mMaxVolume = mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // ????????????????????????
                                    mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume - 2, 0);
                                } else {
                                    mTagCanVoice = false;
                                    toast("??????---????????????");
                                    mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    // ?????????????????????
                                    mCurrentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                    rootView.setOnTouchListener(null);
                                    rootView.setLongClickable(false);  //?????????????????????--????????????
                                }
                            } else {
                                toast(micVoiceBean.getMsg());
                            }
                        }

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int mMaxVolume = mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // ????????????????????????
        int currentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // ???????????????????????????
        switch (keyCode) {
            // ????????????
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // ??????????????????????????????????????????
                LogUtils.e("aaaa????????????" + mTagCanVoice);
                LogUtils.e("aaaa????????????" + currentVolume);
                if (mTagCanVoice) {
                    int setVolume = currentVolume + 1;
                    if (currentVolume == mMaxVolume) {
                        mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, 0);
                    } else {
                        mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);

                    }

                } else {
                    mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                }
                return true;
            // ????????????
            case KeyEvent.KEYCODE_VOLUME_UP:
                // ??????????????????????????????????????????
                LogUtils.e("aaaa????????????" + mTagCanVoice);
                LogUtils.e("aaaa????????????" + currentVolume);
                if (mTagCanVoice) {
                    int setVolume = currentVolume + 1;
                    if (currentVolume == mMaxVolume) {
                        mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, 0);
                    } else {
                        mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);

                    }
                } else {
                    mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                }

                return true;
        }
        mCurrentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        LogUtils.e("aaaa" + mCurrentVolume);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ?????????????????????,??????????????????????????????????????????
     */
    private void getMicPermission() {
        XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            //tag???????????????(?????????????????????)
                            if ("stopStream".equals(mTvMicStatus.getTag())) {
                                mTvMicStatus.setTag("startStream");
                                mTvMicStatus.setText("???????????????");
                                MicSocketBean bean = new MicSocketBean();
                                bean.setErrCode("0");
                                bean.setOperation("1");
                                bean.setVoiceID("");
                                bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                                bean.setUrl("");
                                sendSocketPointMicMessage(bean);
                            } else if ("startStream".equals(mTvMicStatus.getTag())) {
                                mTvMicStatus.setTag("stopStream");
                                mTvMicStatus.setText("???????????????");
                                MicSocketBean bean = new MicSocketBean();
                                bean.setErrCode("0");
                                //????????????????????????????????????voiceID??????,???????????????,??????????????????????????????????????????????????????????????????
                                bean.setOperation("6");
                                bean.setVoiceID("");
                                bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                                bean.setUrl("");
                                mMMKVInstace.encode(Constants.KET_MIC_VOICE_ID_FOR_ME, "default");
                                sendSocketPointMicMessage(bean);
                            }
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("???????????????????????????????????????????????????");
                            // ??????????????????????????????????????????????????????????????????
                            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            toast("???????????????????????????");
                        }
                    }
                });


    }


    /**
     * ???????????????????????????,??????????????????
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
                                mMMKVInstace.encode(Constants.KEY_UserMan, bean.isUserMan());//????????????(??????????????????????????????)
                                mMMKVInstace.encode(Constants.KEY_CanPsw, bean.isCanPsw());//????????????(??????????????????)
                                mMMKVInstace.encode(Constants.KEY_SnapVideoRecord, bean.isSnapVideoRecord());//????????????
                                mMMKVInstace.encode(Constants.KEY_CanNew, bean.isCanNew());  //????????????(????????????)
                                mMMKVInstace.encode(Constants.KEY_CanEdit, bean.isCanEdit());//????????????
                                mMMKVInstace.encode(Constants.KEY_CanDelete, bean.isCanDelete());//????????????
                                mMMKVInstace.encode(Constants.KEY_CanPrint, bean.isCanPrint()); //????????????
                                mMMKVInstace.encode(Constants.KEY_UnPrinted, bean.isUnPrinted()); //???????????????
                                mMMKVInstace.encode(Constants.KEY_OnlySelf, bean.isOnlySelf());//????????????
                                mMMKVInstace.encode(Constants.KEY_HospitalInfo, bean.isHospitalInfo());//????????????(??????????????????????????????)
                            }
                        }

                    }
                });
    }


    private void setTypeTabData(DeviceParamsBean deviceParamsBean) {
        isFirstInitData = true;
        DeviceParamsBean.Type01 type01 = deviceParamsBean.getType01(); //?????????
        DeviceParamsBean.Type02 type02 = deviceParamsBean.getType02(); //??????
        if (type02 != null) {//??????
            //????????????
            mRangeBar01Light.setProgress(Integer.parseInt(type02.getBrightness()));
            m01LightDesc.setText(Integer.parseInt(type02.getBrightness()) + "");
        }
        if (type01 != null) {//??????
            //??????
            mRangeBar02Light.setProgress(Integer.parseInt(type01.getBrightness()));
            m02LightDesc.setText(Integer.parseInt(type01.getBrightness()) + "");
            //?????????
            mRangeBar02Saturation.setProgress(Integer.parseInt(type01.getSaturation()));
            m02SaturationDesc.setText(Integer.parseInt(type01.getSaturation()) + "");

            //?????????
            mRangeBar02Definition.setProgress(Integer.parseInt(type01.getSharpness()));
            m02DefinitionDesc.setText(Integer.parseInt(type01.getSharpness()) + "");

            //????????????           //?????????1???2.5???,?????????0--15
            float rangeBarNeedSetData = CommonUtil.getRangeBarData(type01.getZoomrate());
            mRangeBar02Zoom.setProgress(rangeBarNeedSetData);
            m02ZoomDesc.setText(rangeBarNeedSetData + "");
            //:?????????????????????  0??????????????????1?????????????????????2?????????????????????3???????????????+???????????????

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
            //????????????-->8????????? 0?????????
            //????????????
            if ("8".equals(type01.getBloodenhance())) {
                mSwitchBlood.setChecked(false);
            } else {
                mSwitchBlood.setChecked(true);
            }

            isFirstInitData = false;

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
        //???????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        initLayoutView();
    }

    @Override
    protected void initData() {

        rootView.setLongClickable(true);  //????????????--?????????
        rootView.setOnTouchListener(onTouchVideoListener);
        //???????????????????????????
        mRangeBar01Light.setIndicatorTextDecimalFormat("0");
        mRangeBar02Light.setIndicatorTextDecimalFormat("0");
        mRangeBar02Saturation.setIndicatorTextDecimalFormat("0");
        mRangeBar02Definition.setIndicatorTextDecimalFormat("0");
        mRangeBar02Zoom.setIndicatorTextDecimalFormat("0.0");
        //rootView.setOnTouchListener(null);
        //rootView.setLongClickable(false);  //?????????????????????--????????????
        rtmpOnlyAudio = new RtmpOnlyAudio(this);

        //????????????
        mTitle.setText(mCurrentTypeDes + "(" + mSocketOrLiveIP + ")");

        //??????????????????ID,???????????????
        Bundle bundle = getIntent().getExtras();
        currentUrl0 = bundle.getString("currentUrl0");
        currentUrl1 = bundle.getString("currentUrl1");
        mCaseID = bundle.getString("ItemID");
        path = currentUrl0;
        startLive(path);
        //??????url??????
        mUrlMap = new HashMap<>();
        mUrlMap.put("??????HD", currentUrl0);
        mUrlMap.put("??????SD", currentUrl1);
        responseListener();

        //??????socket????????????????????????
        setSocketStatue(mCurrentSocketStatue);

        //????????????????????????,??????????????????,????????????????????????????????????????????????
        mRelativeChangeAll.setVisibility(View.INVISIBLE);
        showCloseChangeCaseAnim();

        //?????????????????????????????????
        mAdapter = new CaseManageAdapter(GetPictureActivity.this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(mDataLest);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

//        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(2, 30, true));
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        //?????????????????????
        if (!mChangeTitleData.getText().toString().trim().isEmpty()) {
            sendDataRequest(mChangeTitleData.getText().toString().trim());
        } else {
            sendDataRequest("2022-03-28");
        }
    }


    /**
     * ***************************************************************************????????????**************************************************************************
     */


    /**
     * ?????????????????????,??????????????????
     *
     * @param CMDCode ??????cmd
     * @param status  ?????????
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
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * ?????????????????????,??????????????????
     *
     * @param CMDCode ??????cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            HandBean handBean = new HandBean();
            handBean.setHelloPc("");
            handBean.setComeFrom("");
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);


        }

    }

    /**
     * ??????????????????
     * MicSocketBean bean
     * {"ErrCode":"0", "Operation":"1", "voiceID":"22", "StringParam":"MobileName",url:xxxx}
     * ErrCode ?????????????????????????????????
     * 0????????????
     * 1?????????????????????Nginx??????
     * 2???Nginx???????????????
     * 3??????Nginx??????????????????
     * <p>
     * Operation ????????????????????? ???????????????
     * 0???????????????????????????
     * 1: ????????????????????????????????????
     * 2????????????????????????Nginx???
     * 3?????????Nginx???????????????
     * 5???????????????
     * 6?????????????????????????????????????????????
     */
    public void sendSocketPointMicMessage(MicSocketBean bean) {
        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                Constants.UDP_41);
        if (("".equals(mSocketPort))) {
            toast("????????????????????????");
            return;
        }
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), this);
    }

    /**
     * ??????--->?????????????????????,??????????????????
     *
     * @param CMDCode ??????cmd
     */
    public void sendSocketPointShotMessage(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            ShotPictureBean bean = new ShotPictureBean();
            String spCaseID = mMMKVInstace.decodeString(Constants.KEY_CurrentCaseID);
            String hexID = CalculateUtils.numToHex16(Integer.parseInt(spCaseID));
            bean.setRecordid(hexID);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * ????????????-->?????????????????????
     * 02-?????????(type02)
     *
     * @param CMDCode ??????cmd
     */
    public void getSocketLightData(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            Type02Bean bean = new Type02Bean();
            Type02Bean.Type02 Type02 = new Type02Bean.Type02();
            Type02.setBrightness("");
            bean.setType02(Type02);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }


    /**
     * ????????????-->?????????????????????
     * 02-?????????(type02)
     *
     * @param CMDCode ??????cmd
     */
    public void sendSocketPointLight(String CMDCode, String data) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            Type02Bean bean = new Type02Bean();
            Type02Bean.Type02 typeBean = new Type02Bean.Type02();
            typeBean.setBrightness(data);
            bean.setType02(typeBean);

            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }

    /**
     * ????????????(????????????)-->???????????????
     * 01-?????????(type01)
     *
     * @param CMDCode ??????cmd
     */
    public void sendSocketPointVideoDevice(String CMDCode, Type01Bean bean) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(bean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }
            SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), GetPictureActivity.this);
        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);

        }

    }


    public void setProcessData(String type, RangeSeekBar mSeekBar, TextView mTvDesc, int maxData, int minData) {
        int progress = (int) mSeekBar.getLeftSeekBar().getProgress();
        //???
        if (type.equals("add")) {

            if (progress == maxData) {
                toast("?????????????????????");
                return;
            }
            //?????????????????????
            mSeekBar.setProgress(progress + 1);
            setProcessDataByAddOrBlack(mSeekBar, mTvDesc);
        } else {//???
            if (progress == minData) {
                toast("?????????????????????");
                return;
            }
            //?????????????????????
            mSeekBar.setProgress(progress - 1);
            //?????????????????????,???????????????bean?????????
            setProcessDataByAddOrBlack(mSeekBar, mTvDesc);
        }


    }

    /**
     * ????????????,???????????????,?????????????????????,??????????????????,
     *
     * @param mSeekBar
     * @param mTvDesc
     */
    private void setProcessDataByAddOrBlack(RangeSeekBar mSeekBar, TextView mTvDesc) {
        //?????????????????????,???????????????bean?????????
        if (View.VISIBLE == mDevicePart.getVisibility()) {
            //?????????????????????bean
            Type01Bean bean = new Type01Bean();
            Type01Bean.Type01 typeBean = new Type01Bean.Type01();

            float progressData = mSeekBar.getLeftSeekBar().getProgress();
            String round = (Math.round(progressData) + "").replace(".", "");
            typeBean.setBrightness(round);
            bean.setType01(typeBean);
            sendSocketPointVideoDevice(Constants.UDP_F6, bean);
            mTvDesc.setText((int) mSeekBar.getLeftSeekBar().getProgress() + "");
        } else {
            //????????????socket??????
            float progress2 = mSeekBar.getLeftSeekBar().getProgress();
            String round2 = (Math.round(progress2) + "").replace(".", "");
            sendSocketPointLight(Constants.UDP_F6, "" + round2);
            mTvDesc.setText((int) mSeekBar.getLeftSeekBar().getProgress() + "");

        }
    }

    private void showDataSelectedDialog() {
        // ?????????????????????
        mDateDialog = new DateDialog.Builder(getActivity());
        mDateDialog.setTitle("???????????????")
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .addOnDismissListener(new BaseDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(BaseDialog dialog) {
                        startDialogIconAnim(false, mChangeAnimImage);

                    }
                })
                .setListener(new DateDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, int year, int month, int day) {
                        // ???????????????????????????????????????????????????
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        // ???????????????????????????????????? 1
                        calendar.set(Calendar.MONTH, month - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        String mDate = new SimpleDateFormat("yyyy???MM???dd???").format(calendar.getTime());
                        String mChoiceDate = mDate.replace("???", "-").replace("???", "-").replace("???", "");
                        mChangeTitleData.setText(mChoiceDate + "");
                        sendDataRequest(mChoiceDate);
                        startDialogIconAnim(false, mChangeAnimImage);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        startDialogIconAnim(false, mChangeAnimImage);

                    }
                }).show();


    }

    /**
     * ??????????????????,????????????,????????????
     *
     * @param mChoiceDate
     */
    private void sendDataRequest(String mChoiceDate) {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_List)
                .addParams("datetime", mChoiceDate)
//                .addParams("EndoType", "4")  //???????????????3  ??????????????????
                .addParams("EndoType", endoType)  //???????????????3  ??????????????????
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendRequest(mChoiceDate);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            showComplete();
                            if ("" != response) {
                                mGson = GsonFactory.getSingletonGson();
                                CaseManageListBean mBean = mGson.fromJson(response, CaseManageListBean.class);
                                if (0 == mBean.getCode()) {  //??????
                                    if (mBean.getData().size() != 0) {
                                        mRecycleEmpty.setVisibility(View.INVISIBLE);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        mDataLest.clear();
                                        mDataLest.addAll(mBean.getData());
                                        //???????????????????????????,????????????
                                        for (int i = 0; i < mDataLest.size(); i++) {
                                            CaseManageListBean.DataDTO bean = mDataLest.get(i);
                                            String caseID = bean.getID() + "";
                                            //mCaseID
                                            if (caseID.equals(mCaseID)) {
                                                bean.setSelected(true);
                                            } else {
                                                bean.setSelected(false);
                                            }
                                        }
                                        mAdapter.setData(mDataLest);
                                    } else {
                                        mRecycleEmpty.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    showError(listener -> {
                                        sendRequest(mChoiceDate);
                                    });
                                }
                            } else {
                                showError(listener -> {
                                    sendRequest(mChoiceDate);
                                });
                            }
                        } catch (Exception e) {
                            toast("??????????????????");

                        }


                    }
                });
    }

    /**
     * ***************************************************************************????????????**************************************************************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title_name:        //????????????
                showStartChangeCaseAnim();
                break;
            case R.id.linear_title_all:        //??????????????????,?????????????????????
                startDialogIconAnim(true, mChangeAnimImage);
                showDataSelectedDialog();
                break;
            case R.id.btn_sure_change:              //????????????
                //???????????????????????????(?????????????????????)
                //???????????????CaseID????????????????????????mCaseID
                if (itemCaseID.equals(mCaseID)) {
                    return;
                }
                mCaseID = itemCaseID;
                isFirstIn = true;
//                isFirstInitData = true;
                //????????????
                sendRequest(mCaseID);
//                //????????????????????????
//                getSocketLightData(Constants.UDP_F5);
                //??????????????????ID
                sendSocketPointMessage(Constants.UDP_F0);
                //???????????????????????????,???????????????
                sendSocketPointRecodeStatusMessage(Constants.UDP_18, "0");
                showCloseChangeCaseAnim();
                //??????MMKV
                mMMKVInstace.encode(Constants.KEY_CurrentCaseID, mCaseID + "");
                //???????????????????????????????????????????????????????????????,??????????????????????????????????????????????????????
                EventBus.getDefault().post(new RefreshCaseMsgEvent(mCaseID));

                break;
            case R.id.tv_left:              //??????????????????,???????????????
                showCloseChangeCaseAnim();
                break;
            case R.id.linear_light_tab:        //????????????tab
                setLightTab();
                break;
            case R.id.tv_01_light_black:        //??????,?????? -
                setProcessData("black", mRangeBar01Light, m01LightDesc, 100, 0);
                break;
            case R.id.tv_01_light_add:        //??????,?????? +
                setProcessData("add", mRangeBar01Light, m01LightDesc, 100, 0);
                break;
            case R.id.linear_device_tab:        //???????????????tab
                setDeviceTab();
                break;
            case R.id.tv_02_light_black:        //??????,?????? -
                setProcessData("black", mRangeBar02Light, m02LightDesc, 63, 0);
                break;
            case R.id.tv_02_light_add:        //??????,?????? +
                setProcessData("add", mRangeBar02Light, m02LightDesc, 63, 0);
                break;
            case R.id.tv_02_saturation_black://?????????,?????? -
                setProcessData("black", mRangeBar02Saturation, m02SaturationDesc, 64, 0);
                break;
            case R.id.tv_02_saturation_add://?????????,?????? +
                setProcessData("add", mRangeBar02Saturation, m02SaturationDesc, 64, 0);
                break;
            case R.id.tv_02_definition_black://?????????,?????? -
                setProcessData("black", mRangeBar02Definition, m02DefinitionDesc, 31, 0);
                break;
            case R.id.tv_02_definition_add://?????????,?????? +
                setProcessData("add", mRangeBar02Definition, m02DefinitionDesc, 31, 0);
                break;
            case R.id.tv_02_zoom_black://????????????,?????? -
                //????????????           //?????????1???2.5???,?????????0--15

                float s = mRangeBar02Zoom.getLeftSeekBar().getProgress();//editText????????????
                if (s == 1) {
                    toast("?????????????????????");
                } else {
                    Type01Bean bean = new Type01Bean();
                    Type01Bean.Type01 typeBean = new Type01Bean.Type01();
                    float progress = ((mRangeBar02Zoom.getLeftSeekBar().getProgress()) * 10) - 10;
                    int round = Math.round(progress - 1); //?????????
                    typeBean.setZoomrate(round + "");
                    bean.setType01(typeBean);
                    sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                    float rangeBarNeedSetData = CommonUtil.getRangeBarData(round + "");
                    mRangeBar02Zoom.setProgress(rangeBarNeedSetData);
                    m02ZoomDesc.setText(rangeBarNeedSetData + "");
                }
                break;
            case R.id.tv_02_zoom_add://????????????,?????? +
                //????????????           //?????????1???2.5???,???????????????0--15
                float s2 = mRangeBar02Zoom.getLeftSeekBar().getProgress();//editText????????????
                if (2.5 == s2) {
                    toast("?????????????????????");
                } else {
                    Type01Bean bean = new Type01Bean();
                    Type01Bean.Type01 typeBean = new Type01Bean.Type01();
                    //???????????????????????????,?????????1-1.5 ?????????0-15socket?????????
                    float progress = ((mRangeBar02Zoom.getLeftSeekBar().getProgress()) * 10) - 10;
                    int round = Math.round(progress + 1); //?????????
                    typeBean.setZoomrate(round + "");
                    bean.setType01(typeBean);
                    sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                    float rangeBarNeedSetData = CommonUtil.getRangeBarData(round + "");
                    mRangeBar02Zoom.setProgress(rangeBarNeedSetData);
                    m02ZoomDesc.setText(rangeBarNeedSetData + "");

                }
                break;
            case R.id.video_back:               //???????????????????????????
                //?????????????????????????????????
                isFullscreen = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//??????????????????
                setVideoViewFull(R.drawable.nur_ic_fangda, "??????");
                break;
            case R.id.root_layout_vlc:          //????????????????????????
                changeControlStatus();
                break;
            case R.id.control_start_view:       //????????????????????????
                startLive(path);
                break;
            case R.id.linear_record:            //??????,????????????,socket?????????????????????
                //??????????????????????????????
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
//                        if ("??????".equals(mRecordMsg.getText())) {
//                            mFlagRecord = true;
//                            mHandler.sendEmptyMessage(Record_Start);
////                        vlcVideoView.getMediaPlayer().record(directory);
//                            LogUtils.e("path=====??????--??????:=====" + directory); //   /storage/emulated/0/1604026573438.mp4
//                            recordEvent.startRecord(mVLCView.getMediaPlayer(), directory, "cme.mp4");
//                        } else {
//                            vlcRecordOver();
//                        }
//                    } else {
//                        vlcRecordOver();
//                    }
//                } else {
//                    toast("????????????????????????????????????!");
//                }
                break;
            case R.id.linear_picture:           //??????,????????????,socket?????????????????????
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
                    LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
                }
//                if (isPlayering) {
//                    if (mVLCView.isPrepare()) {
//                        Media.VideoTrack videoTrack = mVLCView.getVideoTrack();
//                        if (videoTrack != null) {
//                            toast("????????????");
//                            //??????
//                            LogUtils.e("path=====????????????:=====" + shotFile.getAbsolutePath());
//                            File localFile = new File(shotFile.getAbsolutePath());
//                            if (!localFile.exists()) {
//                                localFile.mkdir();
//                            }
//                            recordEvent.takeSnapshot(mVLCView.getMediaPlayer(), shotFile.getAbsolutePath(), 0, 0);
//                            //???????????? ?????????????????????????????????
//                            MediaStore.Images.Media.insertImage(getContentResolver(), mVLCView.getBitmap(), "", "");
//                            //???????????????
//                            //recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
//                        }
//                    }
//                    //?????????????????? ??????Bitmap?????????
//                    //thumbnail.setImageBitmap(vlcVideoView.getBitmap());
//                    //Bitmap bitmap = vlcVideoView.getBitmap();
//                    //saveBitmap("", bitmap);
//                } else {
//                    toast("????????????????????????????????????!");
//                }
                break;
//            case R.id.linear_cold:              //??????
//                sendSocketPointMessage(Constants.UDP_F3);
//                break;
            case R.id.linear_mic:               //?????????,????????????
                getMicPermission();
                break;
            case R.id.lock_screen:  //??????
                if (mLockScreen.getTag().equals("lock")) {   //??????
                    mHandler.sendEmptyMessage(Unlock);
                    mLockScreen.setTag("unLock");
                } else {
                    mLockScreen.setTag("lock");     //??????
                    mHandler.sendEmptyMessage(Lock);
                }
                break;
            case R.id.full_change:
                isFullscreen = !isFullscreen;
                if (isFullscreen) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); //??????????????????
                    setVideoViewFull(R.drawable.nur_ic_fangxiao, "??????");

                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//??????????????????
                    setVideoViewFull(R.drawable.nur_ic_fangda, "??????");

                }
                break;
        }
    }


    private void responseListener() {
//        R.id.linear_mic, R.id.linear_cold,linear_title_all
        setOnClickListener(R.id.linear_light_tab, R.id.linear_device_tab, R.id.linear_record, R.id.linear_picture, R.id.full_change,
                R.id.lock_screen, R.id.root_layout_vlc, R.id.video_back, R.id.control_start_view, R.id.linear_mic,
                R.id.tv_01_light_black, R.id.tv_01_light_add, R.id.tv_02_light_black, R.id.tv_02_light_add,
                R.id.tv_02_saturation_black, R.id.tv_02_saturation_add, R.id.tv_02_definition_black, R.id.tv_02_definition_add,
                R.id.tv_02_zoom_black, R.id.tv_02_zoom_add, R.id.tv_title_name, R.id.linear_title_all, R.id.btn_sure_change, R.id.tv_left);
//

        //?????????????????????
        mRangeBar01Light.setOnRangeChangedListener(this);
        mRangeBar02Light.setOnRangeChangedListener(this);
        mRangeBar02Saturation.setOnRangeChangedListener(this);
        mRangeBar02Definition.setOnRangeChangedListener(this);
        mRangeBar02Zoom.setOnRangeChangedListener(this);

        //??????switch???????????????
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

            @SuppressLint("WrongConstant")
            @Override
            public void eventError(int event, boolean show) {
                isPlayering = false;
                mStartView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.INVISIBLE);
                //????????????,??????????????????,??????????????????
                if (View.VISIBLE == mTvMicStatus.getVisibility()) { //????????????????????????
                    stopMicSteam();
                }
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
//                    if (mFlagRecord) { //??????????????????????????????
//                        vlcRecordOver();
//                    }
                    if (mFlagMicOnLine) {//??????????????????????????????
//                        pusherStop("Common");
                    }

                }
            }

            @Override
            public void eventCurrentTime(String time) {
                currentTime = time;
                mHandler.sendEmptyMessageDelayed(Time, 1000);
            }

            @Override
            public void eventRecordCurrentTime(String time) {

            }


        });


        //??????????????????????????????
        mRightLiveTypeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopMicSteam();
                showSelectedUrlTypeDialog();
            }


        });

        //??????????????????????????????
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                if (rtmpOnlyAudio.isStreaming()) {
                    rtmpOnlyAudio.stopStream();
                    MicSocketBean bean = new MicSocketBean();
                    bean.setErrCode("0");
                    //????????????????????????????????????voiceID??????,???????????????,??????????????????????????????????????????????????????????????????
                    bean.setOperation("6");
                    bean.setVoiceID("");
                    bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                    bean.setUrl("");
                    mMMKVInstace.encode(Constants.KET_MIC_VOICE_ID_FOR_ME, "default");
                    sendSocketPointMicMessage(bean);
                }
                mTvMicStatus.setTag("stopStream");
                mTvMicStatus.setText("???????????????");


                mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                // ?????????????????????
                mCurrentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int mMaxVolume = mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // ????????????????????????
                mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume - 2, 0);
                //??????????????????
                rootView.setLongClickable(true);  //????????????--?????????
                rootView.setOnTouchListener(onTouchVideoListener);
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
     * ???????????????
     */
    private void showSelectedUrlTypeDialog() {
        if (mUrlMap.size() > 0) {
            new SelectDialog.Builder(this)
                    .setTitle("??????????????????")
                    .setList("??????HD", "??????SD")
                    // ??????????????????
                    .setSingleSelect()
                    // ??????????????????
                    .setSelect(0)
                    .setListener(new SelectDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                            String string = data.toString();
                            int size = data.size();
                            int i = string.indexOf("=");
                            String value = string.substring(i + 1, string.length() - 1);
//                            toast("????????????" + value);

                            String url = mUrlMap.get(value);
                            Drawable urlTypeSD = getResources().getDrawable(R.mipmap.icon_url_type_sd);
                            Drawable urlTypeHD = getResources().getDrawable(R.mipmap.icon_url_type_hd);
                            if ("??????HD".equals(value)) {
                                mRightLiveTypeControl.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeHD, null, null);
                            } else if ("??????SD".equals(value)) {
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
     * ???????????????,?????????????????????,??????????????????View???????????????
     */
    private void changeControlStatus() {

        if (mLockScreen.getTag().equals("lock")) {   //?????????-??????-??????,????????????,??????unlock
            if (lockType) { ////????????????,?????????????????? ???????????????-??????,????????????
                lockType = false;
                mLockScreen.setVisibility(View.INVISIBLE);
            } else {
                lockType = true;
                mLockScreen.setVisibility(View.VISIBLE);
            }
        } else {                                      //?????????-??????-??????,????????????
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
            setVideoViewFull(R.drawable.nur_ic_fangxiao, "??????");
        } else {
            setVideoViewFull(R.drawable.nur_ic_fangda, "??????");
        }
    }

    private void setVideoViewFull(int mID, String type) {
        if ("??????".equals(type)) { //??????
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitleBar.setVisibility(View.GONE);
            mLinearStatueView.setVisibility(View.GONE);
            mLinearBottom.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);//????????????
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);
            mImageBack.setVisibility(View.VISIBLE);
            mTitleName.setVisibility(View.GONE);

        } else {//??????
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitleBar.setVisibility(View.VISIBLE);
            mLinearStatueView.setVisibility(View.VISIBLE);
            mLinearBottom.setVisibility(View.VISIBLE);
            mTitleName.setVisibility(View.VISIBLE);
            mImageBack.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenSizeUtil.dp2px(GetPictureActivity.this, getResources().getDimension(R.dimen.dp_80)));//????????????
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);

        }
    }

    private void initLayoutView() {
        //?????????????????????????????????
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //???????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTitleBar = findViewById(R.id.titlebar_get);
        mStatusLayout = findViewById(R.id.picture_status_hint);
        mChangeFull = findViewById(R.id.full_change);
        mPictureDes = findViewById(R.id.case_picture); //??????
        mTvMicStatus = findViewById(R.id.tv_mic_status); //??????
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
        mMicStatueView = findViewById(R.id.tv_mic_statue);   //????????????????????????,???????????????
        mMicStatueView.setVisibility(View.INVISIBLE);

        //?????????????????????
        mRelativeChangeAll = findViewById(R.id.relative_change_anim);//????????????????????????
        mTvLiftFinish = findViewById(R.id.tv_left);//??????????????????
        mLinearTitleAll = findViewById(R.id.linear_title_all);//???????????????????????????
        mChangeAnimImage = findViewById(R.id.iv_change_case_tag_anim);//?????????????????????????????????
        mChangeTitleData = findViewById(R.id.tv_change_case_title);//???????????????
        mChangeTitleData.setText(DateUtil.getSystemDate());
        mRecyclerView = findViewById(R.id.rv_b_recyclerview);
        mBtnSureChange = findViewById(R.id.btn_sure_change);
        mRecycleEmpty = findViewById(R.id.tv_recycle_empty);

        mRecycleEmpty.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mMic = findViewById(R.id.linear_mic);
        mRecordMsg = findViewById(R.id.case_record);

        mLinearStatueView = findViewById(R.id.relative_statue);
        mCurrentCheckPatientInfo = findViewById(R.id.current_patient_info);
        mCurrentSocketStatue = findViewById(R.id.current_socket_statue);
        //??????
        mRangeBar01Light = findViewById(R.id.sb_01_range_light);
        m01LightBlack = findViewById(R.id.tv_01_light_black);
        m01LightAdd = findViewById(R.id.tv_01_light_add);
        m01LightDesc = findViewById(R.id.tv_01_light_desc);

        mRangeBar01Light.setStepsAutoBonding(false);
        //??????
        mRangeBar02Light = findViewById(R.id.sb_02_range_light);
        m02LightBlack = findViewById(R.id.tv_02_light_black);
        m02LightAdd = findViewById(R.id.tv_02_light_add);
        m02LightDesc = findViewById(R.id.tv_02_light_desc);

        //?????????
        mRangeBar02Saturation = findViewById(R.id.sb_02_range_saturation);
        m02SaturationBlack = findViewById(R.id.tv_02_saturation_black);
        m02SaturationAdd = findViewById(R.id.tv_02_saturation_add);
        m02SaturationDesc = findViewById(R.id.tv_02_saturation_desc);

        //?????????
        mRangeBar02Definition = findViewById(R.id.sb_02_range_definition);
        m02DefinitionBlack = findViewById(R.id.tv_02_definition_black);
        m02DefinitionAdd = findViewById(R.id.tv_02_definition_add);
        m02DefinitionDesc = findViewById(R.id.tv_02_definition_desc);

        //????????????
        mRangeBar02Zoom = findViewById(R.id.sb_02_range_zoom);
        m02ZoomBlack = findViewById(R.id.tv_02_zoom_black);
        m02ZoomAdd = findViewById(R.id.tv_02_zoom_add);
        m02ZoomDesc = findViewById(R.id.tv_02_zoom_desc);

        //????????????
        mSwitchHorizontal = findViewById(R.id.sb_find_switch_horizontal);
        //????????????
        mSwitchVertical = findViewById(R.id.sb_find_switch_vertical);
        //????????????
        mSwitchBlood = findViewById(R.id.sb_find_switch_blood);
        mLightLine = findViewById(R.id.view_light);
        mDeviceLine = findViewById(R.id.view_device);
        mLightTab = findViewById(R.id.linear_light_tab);
        mDeviceTab = findViewById(R.id.linear_device_tab);
        mLightPart = findViewById(R.id.linear_light_part);
        mDevicePart = findViewById(R.id.linear_device_part);
        //??????????????????tab
        setLightTab();
        mRelativeChangeAll.setVisibility(View.INVISIBLE);
        mImageBack.setVisibility(View.INVISIBLE);
        rootView = mPlayer.getRootView();                         //?????????????????????????????????
        onTouchVideoListener = mPlayer.getOnTouchVideoListener();
        mVLCView = mPlayer.findViewById(R.id.vlc_video_view);

        mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


    }


    /**
     * ??????????????????????????????????????????????????? ????????????????????????
     */
    private void showExitDialog() {
        // ??????????????????
        new BaseDialog.Builder<>(this)
                .setContentView(R.layout.dialog_custom_exit)
                .setAnimStyle(BaseDialog.ANIM_SCALE)
                //.setText(id, "????????????????????????")
                .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<View>() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        SocketRefreshEvent event1 = new SocketRefreshEvent();
                        event1.setTga(true);
                        event1.setData(Constants.UDP_CUSTOM14);//???????????????ID,???????????????ID?????????App???????????????ID ???????????????????????????
                        event1.setIp(Constants.UDP_CUSTOM14);
                        event1.setUdpCmd(Constants.UDP_CUSTOM14);
                        EventBus.getDefault().post(event1);
                        finish();
                    }
                })
                .setOnKeyListener((dialog, event) -> {
                    toast("???????????????" + event.getKeyCode());
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
                // ???????????????????????????
                .navigationBarColor(R.color.white);
    }

//    //????????????
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
        getVoiceIDRequest();
        //????????????
        sendRequest(mCaseID);
        //????????????????????????
        getSocketLightData(Constants.UDP_F5);
        //??????????????????ID
        sendSocketPointMessage(Constants.UDP_F0);
        //???????????????????????????,???????????????
        sendSocketPointRecodeStatusMessage(Constants.UDP_18, "0");
    }

    //????????????????????????
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
                            if (0 == mBean.getCode()) {  //??????
                                showComplete();
                                int imageCount = mBean.getData().getImagesCount();
                                CaseDetailBean.DataDTO data = mBean.getData();
                                mPictureDes.setText("??????(" + imageCount + ")");
                                mCaseNo = mBean.getData().getCaseNo();
                                mName = mBean.getData().getName();
                                mTitleName.setText(mName + "-" + mCaseNo);
                                mTitleName.setText(mName + "-" + mCaseNo);


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
        //????????????stop ?????????ANR
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
     * switch ???????????????
     *
     * @param button  ????????????
     * @param checked ????????????
     */
    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        if (isFirstInitData) {
            return;
        }
        Type01Bean bean = new Type01Bean();
        Type01Bean.Type01 typeBean = new Type01Bean.Type01();
        //?????????????????????  0??????????????????1?????????????????????2?????????????????????3???????????????+???????????????
        switch (button.getId()) {
            // ?????????????????????
            //???????????????????????????  ???0
            //???????????????         ???3
            //????????? ?????????       ???1
            //????????? ?????????       ???2
            case R.id.sb_find_switch_vertical:  //????????????
            case R.id.sb_find_switch_horizontal://????????????
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
            case R.id.sb_find_switch_blood://????????????   ?????????8  ?????????0
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
     * RangeBar ?????????
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
        //?????????????????????bean
        Type01Bean bean = new Type01Bean();
        Type01Bean.Type01 typeBean = new Type01Bean.Type01();


        switch (view.getId()) {
            case R.id.sb_01_range_light:  //??????,??????
//                type02Bean.setBrightness(round);
//                bean02.setType02(type02Bean);
//                sendSocketPointLight(Constants.UDP_F6, "" + bean02);
                sendSocketPointLight(Constants.UDP_F6, "" + round);
                m01LightDesc.setText((int) view.getLeftSeekBar().getProgress() + "");
                break;
            case R.id.sb_02_range_light://?????????,??????
                typeBean.setBrightness(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                m02LightDesc.setText((int) view.getLeftSeekBar().getProgress() + "");
                break;
            case R.id.sb_02_range_saturation://?????????,?????????
                typeBean.setSaturation(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                m02SaturationDesc.setText((int) view.getLeftSeekBar().getProgress() + "");
                break;
            case R.id.sb_02_range_definition://?????????,?????????
                typeBean.setSharpness(round);
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                m02DefinitionDesc.setText((int) view.getLeftSeekBar().getProgress() + "");

                break;
            case R.id.sb_02_range_zoom://?????????,????????????
                //?????????1???2.5???,?????????0--15
                float progress1 = ((view.getLeftSeekBar().getProgress()) * 10) - 10;
                int round2 = Math.round(progress1); //?????????
                String s = (int) view.getLeftSeekBar().getProgress() + ""; //editText????????????
                typeBean.setZoomrate(round2 + "");
                bean.setType01(typeBean);
                sendSocketPointVideoDevice(Constants.UDP_F6, bean);
                //????????????           //?????????1???2.5???,???????????????0--15
                float rangeBarNeedSetData = CommonUtil.getRangeBarData(round2 + "");//?????????
                m02ZoomDesc.setText(rangeBarNeedSetData + "");
                break;

        }
    }

    //??????????????????????????????????????????
    private void sendRequestToGetServerCaseInfo(String mCaseID) {
        if ("0".equals(mCaseID)) {
            mCurrentCheckPatientInfo.setText("???");
            return;
        }
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
                .addParams("ID", mCaseID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            CaseDetailBean mBean = mGson.fromJson(response, CaseDetailBean.class);
                            CaseDetailBean.DataDTO data = mBean.getData();
                            if (0 == mBean.getCode()) {  //??????
                                String longSeeCase = MMKV.defaultMMKV().decodeString(Constants.KEY_CurrentLongSeeCaseID);
                                if (longSeeCase.equals("0")) {
                                    mCurrentCheckPatientInfo.setText("???");
                                } else {
                                    mCurrentCheckPatientInfo.setText(data.getCaseNo() + " | " + data.getName() + " |" + data.getSex());
                                }

                            } else {

                            }
                        } else {

                        }
                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void showCloseChangeCaseAnim() {
        mRelativeChangeAll.setBackgroundResource(R.color.white);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleX", 1f, 0.01f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator).with(animator2);
        animSet.setDuration(450);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRelativeChangeAll.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * ????????????????????????
     */
    private void showStartChangeCaseAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleX", 1f, 0.01f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleY", 0.01f, 1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mRelativeChangeAll, "scaleX", 0.01f, 1f);
        AnimatorSet animSet2 = new AnimatorSet();
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator).with(animator2);
        animSet.setDuration(50);//100
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRelativeChangeAll.setVisibility(View.VISIBLE);
                animSet2.play(animator3).with(animator4);
                animSet2.setDuration(450);//300
                animSet2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animSet2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRelativeChangeAll.setBackgroundResource(R.color.gray);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    /**
     * rtmp?????? ??????
     */
    @Override
    public void onConnectionSuccessRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onConnectionSuccessRtmp==");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast("?????????????????? ");
//                toast("??????????????????");
//                setMicStatus("startStream", "?????????..");
                /**
                 * ??????,?????????????????????,????????????????????????,???????????????????????????,????????????
                 */

                if (isPlayering) {
                    MicSocketBean bean = new MicSocketBean();
                    bean.setErrCode("0");
                    bean.setOperation("3");
                    bean.setVoiceID("");
                    bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                    bean.setUrl("");
                    sendSocketPointMicMessage(bean);
                    //?????????????????????voiceID
                    getVoiceIDRequest();
                    mMicStatueView.setVisibility(View.VISIBLE);

                } else {
                    toast("?????????????????????!");
                    startLive(path);
                    postDelayed(() -> {
                        MicSocketBean bean = new MicSocketBean();
                        bean.setErrCode("0");
                        bean.setOperation("3");
                        bean.setVoiceID("");
                        bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                        bean.setUrl("");
                        sendSocketPointMicMessage(bean);
                        mMicStatueView.setVisibility(View.VISIBLE);
                        //?????????????????????voiceID
                        getVoiceIDRequest();
                    }, 1000);

                }


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
                toast("??????????????????: " + reason);
//                mTvMicStatus.setText("??????:?????????");
                rtmpOnlyAudio.stopStream();
                stopMicSteam();
//                sendSocketPointMicMessage("0");

                MicSocketBean bean = new MicSocketBean();
                bean.setErrCode("1");
                bean.setOperation("3");
                bean.setVoiceID("");
                bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                bean.setUrl("");
                sendSocketPointMicMessage(bean);
                mMicStatueView.setVisibility(View.INVISIBLE);


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
                toast("?????????????????? ");
//                setMicStatus("stopStream", "????????????");


            }
        });
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("TAG", "RtmpOnlyAudio=====onDisconnectRtmp");
//                if (!isOnPauseExit) {
//                    startSendToast("?????????????????????");
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
//                toast("?????????????????? ");
                MicSocketBean bean = new MicSocketBean();
                bean.setErrCode("1");
                bean.setOperation("3");
                bean.setVoiceID("");
                bean.setStringParam(SystemUtil.getDeviceBrand() + "_" + SystemUtil.getSystemModel() + "_" + mLoginUserName);
                bean.setUrl("");
                sendSocketPointMicMessage(bean);
                mMicStatueView.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        Log.e("TAG", "RtmpOnlyAudio===onAuthSuccessRtmp==");


    }

    /**
     * ?????????????????????????????????
     *
     * @param recyclerView RecyclerView ??????
     * @param itemView     ????????????????????????
     * @param position     ????????????????????????
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        itemBean = mAdapter.getItem(position);
        itemCaseID = itemBean.getID() + "";
        for (int i = 0; i < mDataLest.size(); i++) {
            CaseManageListBean.DataDTO bean = mDataLest.get(i);
            String caseID = bean.getID() + "";
            //mCaseID
            if (caseID.equals(itemCaseID)) {
                bean.setSelected(true);
            } else {
                bean.setSelected(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}