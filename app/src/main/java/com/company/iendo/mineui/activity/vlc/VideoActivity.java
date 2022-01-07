package com.company.iendo.mineui.activity.vlc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.widget.StatusLayout;
import com.company.iendo.widget.vlc.ENDownloadView;
import com.company.iendo.widget.vlc.ENPlayView;
import com.company.iendo.widget.vlc.MyVlcVideoView;
import com.gyf.immersionbar.ImmersionBar;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 视频界面
 */
public final class VideoActivity extends AppActivity implements StatusAction {
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
            }
        }
    };
    private TextView mTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mRelativePlayerAll = findViewById(R.id.ff_player_all);
        mPlayer = findViewById(R.id.player);
        mVLCView = mPlayer.findViewById(R.id.vlc_video_view);
//        mLockScreen = findViewById(R.id.lock_screen);
        mTime = findViewById(R.id.tv_time);
        mLoadingView = findViewById(R.id.control_load_view);
        mStartView = findViewById(R.id.control_start_view);
        Intent intent = getIntent();
        path = intent.getStringExtra("mUrl");
        startLive(path);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.control_start_view:       //重新开始链接直播
                startLive(path);
                break;
        }
    }

    @Override
    protected void initData() {
        setOnClickListener(R.id.control_start_view);
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


    @Override
    protected void onResume() {
        super.onResume();
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
//        mVLCView.setMediaListenerEvent(null);
        mVLCView.onStop();
        mVLCView.onDestroy();
    }


}