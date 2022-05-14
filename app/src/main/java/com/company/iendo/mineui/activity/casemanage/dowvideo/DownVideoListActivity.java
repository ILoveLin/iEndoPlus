package com.company.iendo.mineui.activity.casemanage.dowvideo;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DetailDownVideoBean;
import com.company.iendo.bean.OfflineVideoBean;
import com.company.iendo.bean.event.downevent.DownEndEvent;
import com.company.iendo.bean.event.downevent.DownLoadingEvent;
import com.company.iendo.green.db.DownVideoMsgDBUtils;
import com.company.iendo.green.db.TaskDBBean;
import com.company.iendo.green.db.TaskDBBeanUtils;
import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.mineui.activity.vlc.VideoActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.donkingliang.consecutivescroller.ConsecutiveScrollerLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 下载进度界面
 */
public final class DownVideoListActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, BaseAdapter.OnChildClickListener {
    private ArrayList<DetailDownVideoBean.DataDTO> mDataLest = new ArrayList<>();
    ;
    private StatusLayout mStatusLayout;
    private RecyclerView mRecyclerView;
    private DownList01Adapter mAdapter;
    private String commonFolderName;
    private TextView tv_click;
    private String currentItemCaseID;
    private String mDeviceCode;
    private String caseID;
    private RecyclerView mDBRecyclerView;
    private List<DownVideoMessage> mDBDataLest;
    private MMKV mmkv;
    private DownList02DBDataAdapter mDBAdapter;
    private DownloadContext mQueueController;
    private DownloadListener4WithSpeed mQueueListener;
    private ArrayList<DetailDownVideoBean.DataDTO> currentDownList;
    private ConsecutiveScrollerLayout mScrollerLayout;
    private TextView mDowningView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_down_statue_list;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mmkv = MMKV.defaultMMKV();
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        TitleBar mTitleBar = findViewById(R.id.titlebar);
        mScrollerLayout = findViewById(R.id.scrollerLayout);
        //第一个列表是下载进度的列表
        mRecyclerView = findViewById(R.id.rv_video_statue_list);
        mDowningView = findViewById(R.id.view_downing);

        mRecyclerView.setVisibility(View.VISIBLE);
        mDowningView.setVisibility(View.VISIBLE);

        //第二个列表是当前设备当下下载过的视频
        mDBRecyclerView = findViewById(R.id.rv_video_db_list);
//        mStatusLayout = findViewById(R.id.status_hint);

        TextView tv_click = findViewById(R.id.tv_click);

        int i = tv_click.getHeight();
        // 监听滑动
        mScrollerLayout.setOnVerticalScrollChangeListener(new ConsecutiveScrollerLayout.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollY, int oldScrollY, int scrollState) {
                if (scrollY > i){
                    mScrollerLayout.setStickyOffset(0); // 恢复吸顶偏移量
                } else {
                    // 通过设置吸顶便宜量，实现flSink滑动隐藏时的向上移动效果
                    mScrollerLayout.setStickyOffset(-scrollY / 2);
                }
            }
        });



        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownVideoMsgDBUtils.deleteAll(getApplicationContext());
                TaskDBBeanUtils.deleteAll(getApplicationContext());
            }
        });

        Intent intent = this.getIntent();
//        ArrayList<DetailDownVideoBean.DataDTO> mList = (ArrayList<DetailDownVideoBean.DataDTO>) intent.getSerializableExtra("mSelectedList");

//        mDataLest.addAll(mList);
        //查询当前病例所有的下载列表
        commonFolderName = intent.getStringExtra("commonFolderName");
        currentItemCaseID = intent.getStringExtra("currentItemCaseID");
        mDeviceCode = intent.getStringExtra("mDeviceCode");
        mDBDataLest = DownVideoMsgDBUtils.getQueryBeanByTow(DownVideoListActivity.this, mDeviceCode, currentItemCaseID);
        LogUtils.e("DownStatueActivity====下载任务==结束=init=mDBDataLest= " + mDBDataLest.size());
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
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

    }

    //获取当前正在下载的列表,并且设置adapter
    private void getCurrentDownListToSetAdapter() {
        LogUtils.e("DownStatueActivity====下载任务==结束==conmmoncode= " + mDeviceCode + "_" + currentItemCaseID);
        List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanByCommonCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID);
        mDataLest.clear();
        mDowningView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        if (mDBDownList.size() != 0) {
            LogUtils.e("DownStatueActivity====下载任务==结束==mDBDownList.size()= " + mDBDownList.size());
            for (int i = 0; i < mDBDownList.size(); i++) {
                TaskDBBean taskDBBean = mDBDownList.get(i);
                String taskString = taskDBBean.getTaskString();
                DetailDownVideoBean.DataDTO adapterBean = mGson.fromJson(taskString, DetailDownVideoBean.DataDTO.class);
                mDataLest.add(adapterBean);
            }
            mAdapter.setData(mDataLest);
        } else {
//            mAdapter.setData(mDataLest);
            mRecyclerView.setVisibility(View.GONE);
            mDowningView.setVisibility(View.GONE);

        }

    }

    private void initDBRecycleViewData() {
        mDBAdapter = new DownList02DBDataAdapter(DownVideoListActivity.this, (ArrayList<DownVideoMessage>) mDBDataLest);
        mDBAdapter.setOnItemClickListener(this);
        mDBAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

                DownVideoMessage item = mDBAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                //http://192.168.31.249:7001/4/2022-04-19-17-54-07.mp4
                LogUtils.e("当前播放URL" + item.toString());
                LogUtils.e("DownStatueActivity====onItemClick=mDBAdapter===" + item.getUrl());
                //LogUtils.e("当前播放URL" + mUrl);
                //intent.putExtra("mUrl","http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                intent.putExtra("mTitle", item.getTag());
                intent.putExtra("mUrl", item.getUrl());
                intent.putExtra("loginType", "offline");
                startActivity(intent);
                LogUtils.e("DownStatueActivity====onItemClick=mDBAdapter=position...==== " + position);
                LogUtils.e("DownStatueActivity====onItemClick=mDBAdapter=getTag...==== " + item.getTag());
            }
        });
        mDBRecyclerView.setAdapter(mDBAdapter);
        mDBRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));
        mDBDataLest = DownVideoMsgDBUtils.getQueryBeanByCode(DownVideoListActivity.this, mDeviceCode);  //当前设备下的下载视频数目
//        mDBDataLest = DownVideoMsgDBUtils.getQueryBeanByTow(DownVideoListActivity.this, mDeviceCode, currentItemCaseID);  //当前病例下的下载视频数目
        removeDuplicate();
        mDBAdapter.setData(mDBDataLest);

        LogUtils.e("DownloadListener==queue==true==DDD==标题不为空====已下载列表==: " + mDBDataLest.size());


    }

    /**
     * 多个病例下载可能会存入多条相同的数据,这里手动去重复
     */
    public  void removeDuplicate(){
        HashSet hashSet = new HashSet(mDBDataLest);
        mDBDataLest.clear();
        mDBDataLest.addAll(hashSet);
    }

    @Override
    protected void initData() {
        mAdapter = new DownList01Adapter(DownVideoListActivity.this, mDataLest);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnChildClickListener(R.id.iv_down_statue, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));
        mDataLest.clear();
        getCurrentDownListToSetAdapter();
        initDBRecycleViewData();


    }

    /**
     * eventbus 下载任务==结束,初始化一开始最大值进度条
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownEndEvent(DownEndEvent event) {
        LogUtils.e("DownStatueActivity====下载任务==结束=== " + event.getTag());
        String tag = event.getTag();
        String mDeviceCode = event.getDeviceCode();
        String currentItemCaseID = event.getCurrentItemCaseID();
        //删除下载队列任务
        List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID + "-" + tag);
        if (mDBDownList.size() != 0) {
            TaskDBBean taskDBBean = mDBDownList.get(0);
            //删除具体某天数据
            TaskDBBeanUtils.delete(getApplicationContext(), taskDBBean);
        }

        String localFolderName = Environment.getExternalStorageDirectory() + "/MyDownVideos/" + mDeviceCode + "_" + currentItemCaseID;

        getCurrentDownListToSetAdapter();
        addDataInGreenDao(event);
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{localFolderName + "/" + event.getRefreshLocalFileName()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        //刷新成功的回调方法
                        LogUtils.e("DownSelectedVideoActivity02======下载任务==结束==相册刷新成功==" + tag);

                    }
                });
//        mDBDataLest = DownVideoMsgDBUtils.getQueryBeanByTow(DownVideoListActivity.this, mDeviceCode, currentItemCaseID);
        mDBDataLest = DownVideoMsgDBUtils.getQueryBeanByCode(DownVideoListActivity.this, mDeviceCode);
        removeDuplicate();
        mDBAdapter.setData(mDBDataLest);

    }

    private void addDataInGreenDao(DownEndEvent event) {
        //1,判断是否下载过
        List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoListActivity.this,
                mDeviceCode, currentItemCaseID, event.getTag());
        //下载成功
        if (event.getStatue().equals(Constants.STATUE_COMPLETED)) {
            //数据库存在
            if (mList.size() > 0) {
                LogUtils.e("DownStatueActivity====下载任务==结束====数据库存在这条数据==存在了===");
                DownVideoMessage dbBean = mList.get(0);
                String localUrl = event.getLocalUrl();
                dbBean.setId(dbBean.getId());
                dbBean.setDeviceCode(mDeviceCode);
                dbBean.setSaveCaseID(currentItemCaseID);
                dbBean.setIsDown(true);
                dbBean.setMaxProcess(event.getTotalLength());
                dbBean.setTag(event.getTag());
                dbBean.setUrl(localUrl);
                DownVideoMsgDBUtils.insertOrReplaceInTx(DownVideoListActivity.this, dbBean);

            } else {
                DownVideoMessage downVideoMessage = new DownVideoMessage();
                String localUrl = event.getLocalUrl();
                downVideoMessage.setDeviceCode(mDeviceCode);
                downVideoMessage.setSaveCaseID(currentItemCaseID);
                downVideoMessage.setIsDown(true);
                downVideoMessage.setMaxProcess(event.getTotalLength());
                downVideoMessage.setTag(event.getTag());
                downVideoMessage.setUrl(localUrl);
                DownVideoMsgDBUtils.insertOrReplaceInTx(DownVideoListActivity.this, downVideoMessage);
            }

        } else if (event.getStatue().equals(Constants.STATUE_ERROR)) {//下载失败
            //数据库存在
            if (mList.size() > 0) {
                DownVideoMessage dbBean = mList.get(0);
                DownVideoMsgDBUtils.delete(DownVideoListActivity.this, dbBean);
            }
        }
    }


    /**
     * eventbus 下载任务==下载中..
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownProcessStatueEvent(DownLoadingEvent event) {
        LogUtils.e("DownStatueActivity====下载任务==下载中...==== " + event.getTag());
        String tag = event.getTag();
        if (null != mDataLest && mDataLest.size() != 0) {
            LogUtils.e("DownStatueActivity====下载任务==下载中...==mDataLest.size()== " + mDataLest.size());

            for (int i = 0; i < mDataLest.size(); i++) {
                DetailDownVideoBean.DataDTO mItemBean = mDataLest.get(i);
                String fileName = mItemBean.getFileName();
                //同一个数据,更新
                if (tag.equals(fileName)) {
                    DetailDownVideoBean.DataDTO bean = new DetailDownVideoBean.DataDTO();
                    bean.setProcessMax(event.getProcessMax());
                    bean.setFileName(event.getTag());
                    bean.setDownStatue(event.getStatue());
                    bean.setDownStatueDes(event.getStatueDes());
                    bean.setSpeed(event.getSpeed());
                    bean.setProcessOffset(event.getCurrentOffset());
                    bean.setProcessformatOffset(event.getFormatCurrentOffset());
                    bean.setAllUrl(event.getUrl());
                    mAdapter.setItem(i, bean);

                }
            }

        }
    }

    //下载进度的list
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {


    }

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initDownService() {


    }

    @Override
    protected void onPause() {
        super.onPause();
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