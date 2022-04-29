package com.company.iendo.mineui.activity.casemanage;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DetailDownVideoBean;
import com.company.iendo.bean.downvideo.DownEndEvent;
import com.company.iendo.bean.downvideo.DownProcessStatueEvent;
import com.company.iendo.bean.downvideo.DownStartEvent;
import com.company.iendo.green.db.DownVideoMsgDBUtils;
import com.company.iendo.green.db.TaskDBBean;
import com.company.iendo.green.db.TaskDBBeanUtils;
import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.service.DownVideoService02;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.MyItemDecoration;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.WrapRecyclerView;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 下载视频选择界面
 * <p>
 * <p>
 * 下载成功之后,再次进入该界面为什么 前面两个显示未下载  需要更近下
 */
public final class DownVideoSelectedActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, BaseAdapter.OnChildClickListener {

    private StatusLayout mStatusLayout;
    private WrapRecyclerView mRecyclerView;
    private DownVideoAdapter mAdapter;
    private ArrayList<DetailDownVideoBean.DataDTO> mDataLest = new ArrayList<>();
    private String currentItemCaseID;
    private String mDeviceCode;
    private String caseID;
    private DownloadContext mQueueController;//队列控制器
    private DownloadListener4WithSpeed mQueueListener;//队列控制器,的监听
    private List<DownVideoMessage> mDBList;
    private String localFolderName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_down_video_selected;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mStatusLayout = findViewById(R.id.status_hint);
        mRecyclerView = findViewById(R.id.rv_video_list);

        //开启下载任务
        //初始化
        DaemonEnv.initialize(this, DownVideoService02.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        //是否 任务完成, 不再需要服务运行?
        DownVideoService02.sShouldStopService = true;
        //开启服务
        DaemonEnv.startServiceMayBind(DownVideoService02.class);

    }

    @Override
    protected void initData() {
        mAdapter = new DownVideoAdapter(DownVideoSelectedActivity.this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnChildClickListener(R.id.checkbox_down, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(getActivity(), 1, R.drawable.shape_divideritem_decoration));
        mAdapter.addData(mDataLest);
        mDeviceCode = getIntent().getStringExtra("mDeviceCode");
        currentItemCaseID = getIntent().getStringExtra("currentItemCaseID");
        caseID = getIntent().getStringExtra("CaseID");
        mDBList = DownVideoMsgDBUtils.getQueryBeanByCode(DownVideoSelectedActivity.this, mDeviceCode);
        localFolderName = Environment.getExternalStorageDirectory() + "/MyDownVideos/" + mDeviceCode + "_" + currentItemCaseID;

        LogUtils.e("DownloadListener===下载任务的path====选择界面==mDeviceCode==="+mDeviceCode);
        LogUtils.e("DownloadListener===下载任务的path====选择界面==currentItemCaseID=="+currentItemCaseID);

        setOnClickListener(R.id.btn_look_downed, R.id.btn_start_down);
//        mBaseUrl=http://192.168.132.102:7001
//        String mUrl = mBaseUrl + "/" + item.getRecordID() + "/" + item.getFilePath();
        sendRequest(currentItemCaseID);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_look_downed: //查看下载列表

                Intent intent = new Intent(DownVideoSelectedActivity.this, DownVideoListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("commonFolderName", localFolderName);//所有视频存入的视频文件夹
                bundle.putString("currentItemCaseID", currentItemCaseID);
                bundle.putString("mDeviceCode", mDeviceCode);
                intent.putExtras(bundle);
                startActivity(intent);


                break;
            case R.id.btn_start_down:  //确认下载
                ConfirmDownVideos();
                break;

        }
    }

    private void ConfirmDownVideos() {
        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
//                .permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
//                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            for (int i = 0; i < mAdapter.getData().size(); i++) {
                                DetailDownVideoBean.DataDTO dataDTO = mAdapter.getData().get(i);
                                boolean selected = dataDTO.isSelected();
                                if (selected) {
                                    LogUtils.e("DownloadListener===下载任务的path====选择界面=确认下载=mDeviceCode==="+mDeviceCode);
                                    LogUtils.e("DownloadListener===下载任务的path====选择界面=确认下载=currentItemCaseID=="+currentItemCaseID);
                                    DownVideoService02 downVideoService02 = new DownVideoService02();
                                    downVideoService02.startDownVideoThread(dataDTO, DownVideoSelectedActivity.this, localFolderName, mDeviceCode, currentItemCaseID);
                                }
                            }
                        }
                    }


                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(DownVideoSelectedActivity.this, permissions);
                        } else {
                        }
                    }
                });
    }

    private void addDataInGreenDao(DownEndEvent event) {
        //1,判断是否下载过
        List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoSelectedActivity.this,
                mDeviceCode, currentItemCaseID, event.getTag());
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
            DownVideoMsgDBUtils.insertOrReplaceInTx(DownVideoSelectedActivity.this, dbBean);
            List<DownVideoMessage> queryBeanByTag = DownVideoMsgDBUtils.getQueryBeanByTag(DownVideoSelectedActivity.this, event.getTag());
            DownVideoMessage downVideoMessage = queryBeanByTag.get(0);
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====数据库====存在=更新===size==" + queryBeanByTag.size());
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====数据库====存在=更新===downVideoMessage==" + downVideoMessage.toString());

        } else {
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====数据库没有下载过====不存在=====");

            DownVideoMessage downVideoMessage = new DownVideoMessage();
            String localUrl = event.getLocalUrl();
            downVideoMessage.setDeviceCode(mDeviceCode);
            downVideoMessage.setSaveCaseID(currentItemCaseID);
            downVideoMessage.setIsDown(true);
            downVideoMessage.setMaxProcess(event.getTotalLength());
            downVideoMessage.setTag(event.getTag());
            downVideoMessage.setUrl(localUrl);
            DownVideoMsgDBUtils.insertOrReplaceInTx(DownVideoSelectedActivity.this, downVideoMessage);


            List<DownVideoMessage> queryBeanByTag = DownVideoMsgDBUtils.getQueryBeanByTag(DownVideoSelectedActivity.this, event.getTag());
            DownVideoMessage downVideoMessage11 = queryBeanByTag.get(0);
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====数据库====不存在===size==" + queryBeanByTag.size());
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====数据库====不存在===downVideoMessage11==" + downVideoMessage11.toString());
        }


    }

    /**
     * eventbus 下载任务==结束
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownEndEvent(DownEndEvent event) {
        String tag = event.getTag();
        LogUtils.e("DownSelectedVideoActivity02====下载任务==结束...==== ");
        /**
         * 删除下载的队列记录
         */
        List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID + "-" + tag);
        if (mDBDownList.size() != 0) {

            TaskDBBean taskDBBean = mDBDownList.get(0);
            //删除具体某天数据
            TaskDBBeanUtils.delete(getApplicationContext(), taskDBBean);
        }
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{localFolderName + "/" + event.getRefreshLocalFileName()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        //刷新成功的回调方法
                        LogUtils.e("DownSelectedVideoActivity02======下载任务==结束==相册刷新成功==" + tag);

                    }
                });


        //下载成功
        if (event.getStatue().equals(Constants.STATUE_COMPLETED)) {
            //添加到已下载列表的数据库当中去
            addDataInGreenDao(event);
        } else if (event.getStatue().equals(Constants.STATUE_ERROR)) {//下载失败
            //1,判断是否下载过
            List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoSelectedActivity.this,
                    mDeviceCode, currentItemCaseID, event.getTag());
            LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====下载失败====下载失败===size==" + mList.size());
            //数据库存在,就删除当前数据
            if (mList.size() > 0) {
                LogUtils.e("DownStatueActivity====下载任务==结束=数据库数据====下载失败====下载失败===size==" + mList.size());

                DownVideoMessage dbBean = mList.get(0);
                DownVideoMsgDBUtils.delete(DownVideoSelectedActivity.this, dbBean);
            }
        }

        /**
         * 更新item
         */
        if (mAdapter.getData().size() != 0) {
            List<DetailDownVideoBean.DataDTO> adapterList = mAdapter.getData();
            for (int i = 0; i < adapterList.size(); i++) {
                DetailDownVideoBean.DataDTO bean = adapterList.get(i);
                String itemName = bean.getFileName();
                if (itemName.equals(tag)) {

                    //查询数据库获取当前这条视频数据是否被下载过
                    //1,判断是否下载过
                    List<DownVideoMessage> mDBList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoSelectedActivity.this,
                            mDeviceCode, currentItemCaseID, bean.getFilePath());
                    //存在
                    if (mDBList.size() != 0) {
                        bean.setDowned(true);
                    } else {
                        //不存在
                        bean.setDowned(false);
                    }
                    mAdapter.setItem(i, bean);
                }

            }


        }

        LogUtils.e("DownSelectedVideoActivity02====下载任务==结束...==== " + mDataLest.size());
    }

    /**
     * eventbus 下载任务==开始
     * 每个任务开始的时候只会走一次回调!!!!
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownStartEvent(DownStartEvent event) {
        LogUtils.e("DownStatueActivity====下载任务==开始...==== " + event.getTag());
        DetailDownVideoBean.DataDTO bean = new DetailDownVideoBean.DataDTO();
        bean.setProcessMax(bean.getProcessMax());
        bean.setFileName(event.getTag());
        bean.setDownStatue(event.getStatue());
//        localFolderName = Environment.getExternalStorageDirectory() + "/MyDownVideos/" + mDeviceCode + "_" + currentItemCaseID;
        //先查询当前是否有下载任务
        //每次新建一个任务的时候,添加到数据库当中去

        List<TaskDBBean> queryBeanByCommonCode1 = TaskDBBeanUtils.getQueryBeanByCommonCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID);

        LogUtils.e("DownStatueActivity====下载任务==开始...=队列表中000000==DB.size=== " + queryBeanByCommonCode1.size());


        /**
         * 添加之前先判断队列数据库中是否存在
         */
        String tag = event.getTag();

        List<TaskDBBean> queryBeanBySingleCode = TaskDBBeanUtils.getQueryBeanBySingleCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID + "-" + tag);
        if (queryBeanBySingleCode.size() != 0) {
            LogUtils.e("DownStatueActivity====下载任务==开始...=队列表中存在===不添加=== " + tag);

        } else {
            LogUtils.e("DownStatueActivity====下载任务==开始...=队列表中不存在===添加=== " + tag);
            TaskDBBean taskDBBean = new TaskDBBean();
            taskDBBean.setCommonCode(mDeviceCode + "_" + currentItemCaseID);
            taskDBBean.setSingleCode(mDeviceCode + "_" + currentItemCaseID + "-" + tag);
            taskDBBean.setTaskString(mGson.toJson(bean));
            TaskDBBeanUtils.insertOrReplaceInTx(getApplicationContext(), taskDBBean);
        }


        List<TaskDBBean> queryBeanByCommonCode = TaskDBBeanUtils.getQueryBeanByCommonCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID);

        LogUtils.e("DownStatueActivity====下载任务==开始...=队列表中000000==DB.size=== " + queryBeanByCommonCode.size());


    }


    /**
     * eventbus 下载任务==下载中
     * <p>
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownProcessStatueEvent(DownProcessStatueEvent event) {
        LogUtils.e("====下载任务==下载中...==刷新这个界面== ");

        if (mAdapter.getData().size() != 0) {
            List<DetailDownVideoBean.DataDTO> adapterList = mAdapter.getData();
            for (int i = 0; i < adapterList.size(); i++) {
                DetailDownVideoBean.DataDTO bean = adapterList.get(i);
                LogUtils.e("====下载任务==下载中...==getFileName== " + bean.getFileName());
                LogUtils.e("====下载任务==下载中...==getTag== " + event.getTag());

                if (bean.getFileName().equals(event.getTag())) {
                    //设置下载进度数据
                    String speed = event.getSpeed();
                    String formatCurrentOffset = event.getFormatCurrentOffset();
                    long currentOffset = event.getCurrentOffset();
                    String mUrl = event.getUrl();
                    bean.setSpeed(speed);
                    bean.setProcessMax(event.getProcessMax());
                    bean.setProcessformatOffset(formatCurrentOffset);
                    bean.setProcessOffset(currentOffset);
                    bean.setAllUrl(mUrl);
                    bean.setDownStatue(Constants.STATUE_DOWNING);
                    bean.setDownStatueDes("下载中");
                    mAdapter.setItem(i, bean);
                }
            }


        }


    }



    /**
     * 获取当前用户的视频数据
     *
     * @param currentItemID
     */
    private void sendRequest(String currentItemID) {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseVideos)
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
                            DetailDownVideoBean mBean = mGson.fromJson(response, DetailDownVideoBean.class);
                            LogUtils.e("视频界面=== response==mCaseID=" + currentItemID);
                            LogUtils.e("视频界面=== response==currentItemCaseID=" + currentItemCaseID);
                            LogUtils.e("视频界面=== response==mDeviceCode=" + mDeviceCode);
                            LogUtils.e("视频界面=== response===" + mBaseUrl + HttpConstant.CaseManager_CaseVideos);
                            LogUtils.e("视频界面=== response===" + response);
                            LogUtils.e("视频界面=== size===" + mBean.getData().size());
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();

                                if (mBean.getData().size() != 0) {
                                    mDataLest.clear();

                                    for (int i = 0; i < mBean.getData().size(); i++) {
                                        DetailDownVideoBean.DataDTO bean = mBean.getData().get(i);

                                        //查询数据库获取当前这条视频数据是否被下载过
                                        //1,判断是否下载过
                                        List<DownVideoMessage> mDBList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoSelectedActivity.this,
                                                mDeviceCode, currentItemCaseID, bean.getFilePath());
                                        //存在
                                        if (mDBList.size() != 0) {
                                            bean.setDowned(true);
                                        } else {
                                            //不存在
                                            bean.setDowned(false);
                                        }

                                        //判断是否下载中
                                        List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID + "-" + bean.getFilePath());
                                        if (mDBDownList.size() != 0) {
                                            bean.setDownStatue(Constants.STATUE_DOWNING);
                                            bean.setDownStatueDes(Constants.STATUE_DOWNING_DES);
                                        }
                                        String mUrl = mBaseUrl + "/" + bean.getRecordID() + "/" + bean.getFilePath();
                                        LogUtils.e("视频界面=== response==mUrl=" + mUrl);
                                        bean.setAllUrl(mUrl);
                                        bean.setFileName(bean.getFilePath());
                                        bean.setLocalFolderName(localFolderName);
                                        mDataLest.add(bean);


                                    }
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
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    protected void onResume() {
        super.onResume();

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


    //item点击事件
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {


    }

    //子View的点击事件
    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        DetailDownVideoBean.DataDTO itemBean = mAdapter.getItem(position);
        List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByThree(DownVideoSelectedActivity.this, mDeviceCode, currentItemCaseID, itemBean.getFilePath());
        if (mList.size() > 0) {

        } else {
        }
        if (itemBean.isSelected()) {
            itemBean.setSelected(false);
        } else {
            itemBean.setSelected(true);
        }
        mAdapter.setItem(position, itemBean);

        LogUtils.e("onChildClick===" + itemBean.toString());

    }


}