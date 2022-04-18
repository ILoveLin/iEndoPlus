package com.company.iendo.mineui.activity.vlc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.company.iendo.widget.vlc.ENDownloadView;
import com.company.iendo.widget.vlc.ENPlayView;
import com.company.iendo.widget.vlc.MyVlcVideoView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 视频界面
 */
public final class VideoActivity extends AppActivity implements StatusAction, SeekBar.OnSeekBarChangeListener {
    public static String path = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    //    public static final String path = "http://192.168.64.28:7001/ID/FilePath";
    private MyVlcVideoView mPlayer;
    private VlcVideoView mVLCView;
    private StatusLayout mStatusLayout;
    private RelativeLayout mRelativePlayerAll;
    private ENDownloadView mLoadingView;
    private ENPlayView mStartView;
    private ImageView mLockScreen;
    private String currentTime = "0";
    public boolean isFullscreen = false;
    private int onPauseTime = 0;
    /**
     * 是否播放的时候onPause了界面
     */
    private boolean isExitWhenPause = false;
    private TextView mTime;
    private TitleBar mTilteBar;
    private TextView mTimeAll;
    private AppCompatSeekBar mProgress;
    private RelativeLayout mBottomControl;
    private int progressData;
    private static final int Time = 104;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Time:
                    String string = CommonUtil.stringForTime(Integer.parseInt(currentTime));
                    String stringAll = CommonUtil.stringForTime(mVLCView.getDuration());
                    mTime.setText("" + string);
                    mTimeAll.setText(stringAll + "");
                    if (!isTouch) {
                        if (mVLCView.getDuration() != 0) {
                            double v = Double.parseDouble(currentTime);
                            double duration = (double) mVLCView.getDuration();
                            double v1 = v / (duration);
                            LogUtils.e("VideoActivity==currentTime==" + currentTime);
                            LogUtils.e("VideoActivity==getDuration==" + mVLCView.getDuration());
                            LogUtils.e("VideoActivity==v1==" + v1);//  0.4611   0.4906  0.4932  0.4946
                            int intData = getIntData(v1 + "");
                            mProgress.setProgress(intData);
                        } else {
                            mProgress.setProgress(1);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mStatusLayout = findViewById(R.id.status_hint);
        mRelativePlayerAll = findViewById(R.id.ff_player_all);
        mBottomControl = findViewById(R.id.relative_bottom_control);
        mPlayer = findViewById(R.id.player);
        mVLCView = mPlayer.findViewById(R.id.vlc_video_view);
//        mLockScreen = findViewById(R.id.lock_screen);
        mTime = findViewById(R.id.tv_time);
        mTimeAll = findViewById(R.id.tv_time_all);
        mProgress = findViewById(R.id.sb_player_view_progress);
        mLoadingView = findViewById(R.id.control_load_view);
        mStartView = findViewById(R.id.control_start_view);
        mTilteBar = findViewById(R.id.video_titlebar);
        mProgress.setOnSeekBarChangeListener(this);
        setOnClickListener(R.id.full_change, R.id.control_start_view);
        Intent intent = getIntent();
        path = intent.getStringExtra("mUrl");
        startLive(path);
    }

    private boolean lockType = true;   //点击界面,显示或者隐藏 控制面板的-标识

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.root_layout_vlc:       //控制界面的显示和隐藏
                if (lockType) {
                    lockType = false;
                    mBottomControl.setVisibility(View.INVISIBLE);
                } else {
                    lockType = true;
                    mBottomControl.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.control_start_view:       //重新开始链接直播
                startLive(path);
                break;
            case R.id.full_change:       //全屏
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
            mTilteBar.setVisibility(View.GONE);
        } else {//放大
            LogUtils.e("全屏设置==开始==" + "竖---屏");
            mTilteBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void initData() {
        setOnClickListener(R.id.control_start_view, R.id.root_layout_vlc);
        mVLCView.setMediaListenerEvent(new MediaListenerEvent() {
            @Override
            public void eventBuffing(int event, float buffing) {
                if (buffing < 100) {
                    mLoadingView.start();
                    mLoadingView.setVisibility(View.VISIBLE);
                } else if (buffing == 100) {
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
                //此处保持每次进来都是之前退出界面播放的时间段
                if (isExitWhenPause) {//播放的时候退出了,需要重新回到播放位置
                    if (null != mVLCView) {
                        mVLCView.seekTo(onPauseTime);
                    }
                    if (null != mProgress) {
                        mProgress.setProgress(progressData);
                    }
                }
            }

            @Override
            public void eventSystemEnd(String isStringed) {

            }

            @Override
            public void eventCurrentTime(String time) {
                currentTime = time;
                mHandler.sendEmptyMessageDelayed(Time, 1000);
            }


        });
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


    private void startLive(String path) {
        mVLCView.setPath("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
//        mVLCView.setPath(path);
        mVLCView.startPlay();
        if (null != mProgress) {
            mProgress.setProgress(0);
        }
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLive(path);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //直接调用stop 不然回ANR
        mLoadingView.release();
        boolean playing = mVLCView.isPlaying();
        progressData = mProgress.getProgress();
        if (playing) {//在播放
            onPauseTime = Integer.parseInt(currentTime);
            isExitWhenPause = true;
        }
        mVLCView.onStop();
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
        onPauseTime = 0;
        isExitWhenPause = false;
//        mVLCView.setMediaListenerEvent(null);
        mVLCView.onStop();
        mVLCView.onDestroy();
    }


    private int getIntData(String data) {
        int i = data.indexOf(".");
        String substring = data.substring(i + 1, i + 3);
        if ("00".equals(substring)) {
            return 1;
        } else if (substring.startsWith("0")) {
            String replace = substring.replace("0", "");
            int i1 = Integer.parseInt(replace);
            return i1;
        } else {
            return Integer.parseInt(substring);
        }
    }

    /**
     * 是否在拖动进度条,默认没有
     */
    private boolean isTouch = false;


    /**
     * 该方法拖动进度条进度改变的时候调用
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        LogUtils.e("VideoActivity==progress==" + progress);

    }

    /**
     * 该方法拖动进度条开始拖动的时候调用。
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouch = true;
    }

    /**
     * 该方法拖动进度条停止拖动的时候调用
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtils.e("VideoActivity==onStart==" + seekBar.getProgress());
        isTouch = false;
        float v = (float) (seekBar.getProgress() / 100f);  //获取当前拖动到的百分比
        float v1 = v * mVLCView.getDuration();//设置当前拖动到的时间
        int round = Math.round(v1);//取整
        mVLCView.seekTo(round); //设置
        // 设置选择的播放进度
        setProgress(seekBar.getProgress());
    }
}