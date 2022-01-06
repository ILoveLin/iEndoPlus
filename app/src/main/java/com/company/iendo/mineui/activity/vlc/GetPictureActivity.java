package com.company.iendo.mineui.activity.vlc;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.FileUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.ScreenSizeUtil;
import com.company.iendo.widget.vlc.ENDownloadView;
import com.company.iendo.widget.vlc.ENPlayView;
import com.company.iendo.widget.vlc.MyVlcVideoView;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.pedro.rtplibrary.rtmp.RtmpOnlyAudio;
import com.vlc.lib.RecordEvent;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.videolan.libvlc.Media;

import java.io.File;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 获取图片界面
 */
public final class GetPictureActivity extends AppActivity implements StatusAction, ConnectCheckerRtmp {
    public static final String path = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    public boolean isFullscreen = false;
    private StatusLayout mStatusLayout;
    private TextView mChangeFull;
    private LinearLayout mLinearBottom;
    private TitleBar mTitle;
    private RelativeLayout mRelativePlayerAll;
    private MyVlcVideoView mPlayer;
    private RelativeLayout rootView;
    private TextView mTime;
    private VlcVideoView mVLCView;
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
    private static final int Lock = 100;
    private static final int Unlock = 101;
    private static final int Record_Start = 102;
    private static final int Record_Stop = 103;
    private static final int Time = 104;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Time:
                    String string = CommonUtil.stringForTime(Integer.parseInt(currentTime));
                    mTime.setText("" + string);
                    break;
                case Lock: //锁屏
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_close_ic));
                    rootView.setOnTouchListener(null);
                    rootView.setLongClickable(false);  //手势不需要需要--不能触摸
                    mTopControl.setVisibility(View.INVISIBLE);
                    mBottomControl.setVisibility(View.INVISIBLE);
                    break;
                case Unlock: //解锁
                    mLockScreen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_open_ic));
                    rootView.setLongClickable(true);  //手势需要--能触摸
                    rootView.setOnTouchListener(onTouchVideoListener);
                    mTopControl.setVisibility(View.VISIBLE);
                    mBottomControl.setVisibility(View.VISIBLE);
                    break;
                case Record_Start:
                    mFlagMicOnLine = true;
                    setTextColor(getResources().getColor(R.color.red), "录像中", false);
                    break;
                case Record_Stop:
                    mFlagMicOnLine = false;
                    setTextColor(getResources().getColor(R.color.white), "录像", true);
                    toast("录像成功");
                    break;
            }
        }
    };
    private TextView mRecordMsg;
    private RtmpOnlyAudio rtmpOnlyAudio;


    public void setTextColor(int color, String message, boolean isStarting) {
        mRecordMsg.setText(message);
        mRecordMsg.setTextColor(color);
//        this.isStarting = isStarting;
    }

    private RelativeLayout mTopControl;
    private RelativeLayout mBottomControl;
    private ImageView mImageBack;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_picture;
    }

    @Override
    protected void initView() {
        initLayoutView();
    }

    @Override
    protected void initData() {
        rootView.setLongClickable(true);  //手势需要--能触摸
        rootView.setOnTouchListener(onTouchVideoListener);
        //rootView.setOnTouchListener(null);
        //rootView.setLongClickable(false);  //手势不需要需要--不能触摸
        rtmpOnlyAudio = new RtmpOnlyAudio(this);
        LogUtils.e("pusherStart====111===" + rtmpOnlyAudio.isStreaming());    //true   断开的时候
        LogUtils.e("pusherStart====222===" + rtmpOnlyAudio.prepareAudio());   //true

//        推流音频代码
//        if (!rtmpOnlyAudio.isStreaming()) {
//            if (rtmpOnlyAudio.prepareAudio()) {
//                if (CommonUtil.isFastClick()) {
//                    if ("一体机".equals(mTitleData.substring(0, 3))) {
//                        if (isPlayering) {
//                            pusherStart();
//                        } else {
//                            startSendToast("只有在直播开启的时候,才能使用语音功能!");
//                        }
//                    } else {
//                        startSendToast("当前直播没有语音功能!");
//                    }
//                }
//            } else {
//                startSendToast("Error preparing stream, This device cant do it");
//            }
//        } else {
//            if (CommonUtil.isFastClick()) {
//                pusherStop("Common");
//            }
//        }
        responseListener();

    }

    private void responseListener() {
        setOnClickListener(R.id.linear_record, R.id.linear_picture, R.id.linear_cold, R.id.linear_mic, R.id.full_change, R.id.lock_screen, R.id.root_layout_vlc, R.id.video_back, R.id.control_start_view);
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
                    if (mFlagRecord) { //如果在录像，断开录像
                        vlcRecordOver();
                    }
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_back:               //全屏的时候退出界面
                finishThisActivity();
                break;
            case R.id.root_layout_vlc:          //点击控制播放界面
                changeControlStatus();
                break;
            case R.id.control_start_view:       //重新开始链接直播
                startLive(path);
                break;
            case R.id.linear_record:            //录像
                if (isPlayering) {
                    if (mVLCView.isPrepare()) {
                        if ("录像".equals(mRecordMsg.getText())) {
                            mFlagRecord = true;
                            mHandler.sendEmptyMessage(Record_Start);
//                        vlcVideoView.getMediaPlayer().record(directory);
                            LogUtils.e("path=====录像--开始:=====" + directory); //   /storage/emulated/0/1604026573438.mp4
                            recordEvent.startRecord(mVLCView.getMediaPlayer(), directory, "cme.mp4");
                        } else {
                            vlcRecordOver();
                        }
                    } else {
                        vlcRecordOver();
                    }
                } else {
                    toast("只有在播放的时候才能录像!");
                }
                break;
            case R.id.linear_picture:           //截图
                if (isPlayering) {
                    if (mVLCView.isPrepare()) {
                        Media.VideoTrack videoTrack = mVLCView.getVideoTrack();
                        if (videoTrack != null) {
                            toast("截图成功");
                            //原图
                            LogUtils.e("path=====截图地址:=====" + shotFile.getAbsolutePath());
                            File localFile = new File(shotFile.getAbsolutePath());
                            if (!localFile.exists()) {
                                localFile.mkdir();
                            }
                            recordEvent.takeSnapshot(mVLCView.getMediaPlayer(), shotFile.getAbsolutePath(), 0, 0);
                            //插入相册 解决了华为截图显示问题
                            MediaStore.Images.Media.insertImage(getContentResolver(), mVLCView.getBitmap(), "", "");
                            //原图的一半
                            //recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
                        }
                    }
                    //这个就是截图 保存Bitmap就行了
                    //thumbnail.setImageBitmap(vlcVideoView.getBitmap());
                    //Bitmap bitmap = vlcVideoView.getBitmap();
                    //saveBitmap("", bitmap);
                } else {
                    toast("只有在播放的时候才能截图!");
                }
                break;
            case R.id.linear_cold:              //冻结
                toast("暂未开发");
                break;
            case R.id.linear_mic:               //麦克风

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

    private void finishThisActivity() {
        if (mFlagRecord) {
            vlcRecordOver();
        }
        mVLCView.setAddSlave(null);
        mVLCView.onDestroy();
        finish();
    }

    /**
     * 不管是锁屏,还是解锁状态下,点击改变控制View的状态显示
     */
    private void changeControlStatus() {
        if (mLockScreen.getTag().equals("lock")) {   //当前为-锁屏-状态,需要解锁
            if (lockType) {
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
                mBottomControl.setVisibility(View.INVISIBLE);
                mLockScreen.setVisibility(View.INVISIBLE);
            } else {
                lockType = true;
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
            LogUtils.e("全屏设置==开始==" + "横---屏");
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitle.setVisibility(View.GONE);
            mLinearBottom.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);//工具类哦
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);
            mImageBack.setVisibility(View.VISIBLE);
        } else {//放大
            LogUtils.e("全屏设置==开始==" + "竖---屏");
            Drawable record_end = getResources().getDrawable(mID);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
            mTitle.setVisibility(View.VISIBLE);
            mLinearBottom.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenSizeUtil.dp2px(GetPictureActivity.this, getResources().getDimension(R.dimen.dp_70)));//工具类哦
            mRelativePlayerAll.setLayoutParams(layoutParams);
            mPlayer.setLayoutParams(layoutParams);
            mImageBack.setVisibility(View.INVISIBLE);
        }
    }

    private void initLayoutView() {
        //设置沉浸式观影模式体验
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //永远不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mStatusLayout = findViewById(R.id.picture_status_hint);
        mChangeFull = findViewById(R.id.full_change);
        mTime = findViewById(R.id.tv_time);
        mLinearBottom = findViewById(R.id.linear_bottom);
        mTopControl = findViewById(R.id.relative_top_control);
        mBottomControl = findViewById(R.id.relative_bottom_control);
        mRelativePlayerAll = findViewById(R.id.ff_player_all);
        mPlayer = findViewById(R.id.player);
        mLockScreen = findViewById(R.id.lock_screen);
        mLoadingView = findViewById(R.id.control_load_view);
        mStartView = findViewById(R.id.control_start_view);
        mImageBack = findViewById(R.id.video_back);
//        LinearLayout mRecord = findViewById(R.id.linear_record);
//        LinearLayout mShot = findViewById(R.id.linear_picture);
//        LinearLayout mCold = findViewById(R.id.linear_cold);
//        LinearLayout mMic = findViewById(R.id.linear_mic);
        mRecordMsg = findViewById(R.id.case_record);


        mImageBack.setVisibility(View.INVISIBLE);
        rootView = mPlayer.getRootView();                         //点击控制锁显示和隐藏的
        onTouchVideoListener = mPlayer.getOnTouchVideoListener();
        mVLCView = mPlayer.findViewById(R.id.vlc_video_view);
        mTitle = findViewById(R.id.titlebar);

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

    //结束录像
    private void vlcRecordOver() {
        mFlagRecord = false;
        mHandler.sendEmptyMessage(Record_Stop);
        mVLCView.getMediaPlayer().record(null);
//        FileUtil.scanFile(VlcPlayerActivity.this, directory);
        FileUtil.RefreshAlbum(directory, true, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstIn = true;
        startLive(path);
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
        isFirstIn = false;
//        mVLCView.setMediaListenerEvent(null);
        mVLCView.onStop();
        mVLCView.onDestroy();
    }


    /**
     * rtmp推流 音频
     */
    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp(String reason) {

    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }
}