package com.company.iendo.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.company.iendo.bean.DetailDownVideoBean;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.event.downevent.DownEndEvent;
import com.company.iendo.bean.event.downevent.DownLoadingEvent;
import com.company.iendo.bean.event.downevent.DownStartEvent;
import com.company.iendo.green.db.DownVideoMsgDBUtils;
import com.company.iendo.green.db.TaskDBBean;
import com.company.iendo.green.db.TaskDBBeanUtils;
import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.mineui.activity.casemanage.dowvideo.DownVideoListActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.FileUtil;
import com.company.iendo.utils.LogUtils;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.tencent.mmkv.MMKV;
import com.xdandroid.hellodaemon.AbsWorkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;


/**
 * 保活的Service通讯服务
 * <p>
 * 视频下载服务
 */

public class DownVideoService extends AbsWorkService {
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static boolean isFirstIn = false;
    public static Disposable sDisposable10s;
    private DownloadContext.Builder mDownBuilder;
    private MMKV mmkv;
    private static DownloadContext mDownContext;
    private static DownVideoService mDownVideoService;


    public static void stopService() {

        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable10s != null) sDisposable10s.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
        LogUtils.e("保活服务开启HandService==stopService------ + stopService... stopService = ");


    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }


    @Override
    public void startWork(Intent intent, int flags, int startId) {
//        Observable  类似于  button   被观察者
//        Observer    类似于  OnClickListener    是观察者

//        两者通过setOnClickListener来实现绑定关系,当Button有被click的动作，就会通知OnClickListener进行onClick里面定义的do something操作。

//        subscribe   类似于  setOnClickListener


//        subscribeOn是来设定我们的被观察者的操作是在哪个线程中进行   Schedulers.io()

//        observeOn是来设定我们的观察者的操作是在哪个线程执行   AndroidSchedulers.mainThread()
        LogUtils.e("保活服务开启HandService==----AAA--" + Thread.currentThread().getName());


    }


    private Context mContext = null;


    public void startDownVideoThread(DetailDownVideoBean.DataDTO DataDTOBean, Context mContext, String commonFolderName,
                                     String mDeviceCode, String currentItemCaseID) {
        this.mContext = mContext;
        DownloadListener4WithSpeed listener04 = new DownloadListener4WithSpeed() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                //队列只有一个的时候,只会发一次
                DownStartEvent event = new DownStartEvent();
                event.setTag((String) task.getTag());
                event.setStatue(Constants.STATUE_START);
                event.setDownStatueDes(Constants.STATUE_START_DES);
                event.setContentLength(task.getInfo().getTotalLength());
                event.setCurrentContentLength(task.getInfo().getTotalLength());
                EventBus.getDefault().postSticky(event);
                LogUtils.e("DownloadListener==taskStart: " + task.getTag());
            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                super.fetchStart(task, blockIndex, contentLength);
                LogUtils.e("DownloadListener==fetchStart==task.getTag==: " + task.getTag()); //02
//                LogUtils.e("DownloadListener==fetchStart==task.getParentFile==: " + task.getParentFile());// /storage/emulated/0/CME_01
//                LogUtils.e("DownloadListener==fetchStart==task.getFile==: " + task.getFile());///storage/emulated/0/CME_01/222.mp4
//                LogUtils.e("DownloadListener==fetchStart==task.getFilename==: " + task.getFilename());//222.mp4
//                LogUtils.e("DownloadListener==fetchStart==contentLength==: " + contentLength);
//
//                //http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4
//                LogUtils.e("DownloadListener==fetchStart==task.getUrl==: " + task.getUrl());
////                LogUtils.e("DownloadListener==fetchStart: " + contentLength);
//                LogUtils.e("DownloadListener==fetchStart==contentLength== long -String KB/MB==: " + FileUtil.formatFileSizeMethod(contentLength));//字符串转换 long -String KB/MB
//


            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                LogUtils.e("DownloadListener==connectStart: " + task.getTag());

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                LogUtils.e("DownloadListener==connectEnd:==responseCode " + responseCode);


            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {

                LogUtils.e("DownloadListener==infoReady==task.getEtag(): " + task.getTag());
                LogUtils.e("DownloadListener==infoReady==info.getTotalLength(): " + info.getTotalLength());
                LogUtils.e("DownloadListener==infoReady==info.getTaskSpeed(): " + model.getTaskSpeed());

            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {
                LogUtils.e("DownloadListener==progressBlock==task.toString(): " + task.toString());
                LogUtils.e("DownloadListener==progressBlock==task.getReadBufferSize(): " + task.getReadBufferSize());
                LogUtils.e("DownloadListener==progressBlock==task.getFlushBufferSize(): " + task.getFlushBufferSize());
                LogUtils.e("DownloadListener==progressBlock==task.getRedirectLocation(): " + task.getRedirectLocation());
                LogUtils.e("DownloadListener==progressBlock==task.getTag(): " + task.getTag());
                LogUtils.e("DownloadListener==progressBlock==task.getFilename(): " + task.getFilename());
                LogUtils.e("DownloadListener==progressBlock==blockSpeed: " + blockSpeed.speed());
                LogUtils.e("DownloadListener==progressBlock==getTotalLength: " + task.getInfo().getTotalLength());
                LogUtils.e("DownloadListener==progressBlock==getTotalOffset: " + task.getInfo().getTotalOffset());
                LogUtils.e("DownloadListener==progressBlock==getInstantSpeedDurationMillis: " + blockSpeed.getInstantSpeedDurationMillis());
                LogUtils.e("DownloadListener==progressBlock==currentBlockOffset: " + currentBlockOffset);

                String formatOffsetLength = FileUtil.formatFileSizeMethod(currentBlockOffset);
                DownLoadingEvent event = new DownLoadingEvent();
                event.setProcessMax(task.getInfo().getTotalLength());
                event.setTag((String) task.getTag());
                event.setStatue(Constants.STATUE_DOWNING);
                event.setStatueDes(Constants.STATUE_DOWNING_DES);
                event.setSpeed(blockSpeed.speed());
                event.setCurrentOffset(currentBlockOffset);
                event.setFormatCurrentOffset(formatOffsetLength);
                event.setUrl(task.getUrl());
                EventBus.getDefault().postSticky(event);


            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                LogUtils.e("DownloadListener==progress==currentOffset: " + currentOffset);
                LogUtils.e("DownloadListener==progress==task.getTag: " + task.getTag());
                LogUtils.e("DownloadListener==progress==task.getFilename: " + task.getFilename());
                LogUtils.e("DownloadListener==progress==taskSpeed: " + taskSpeed.speed());
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {
                LogUtils.e("DownloadListener==blockEnd==blockIndex: " + blockIndex);
                LogUtils.e("DownloadListener==blockEnd==blockSpeed: " + blockSpeed.speed());
                LogUtils.e("DownloadListener==blockEnd==blockIndex==getContentLength=: " + info.getContentLength());  //下载成功的前提下,这个就是总偏移量
                LogUtils.e("DownloadListener==blockEnd==blockIndex==getCurrentOffset=: " + info.getCurrentOffset());
                LogUtils.e("DownloadListener==blockEnd==blockIndex==getStartOffset=: " + info.getStartOffset());
                LogUtils.e("DownloadListener==blockEnd==blockIndex==getRangeLeft=: " + info.getRangeLeft());
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                LogUtils.e("DownloadListener==taskEnd=1=cause.name(): " + cause.name());
                LogUtils.e("DownloadListener==taskEnd=1=task.getTag(): " + task.getTag());
                LogUtils.e("DownloadListener==taskEnd=1=task.getFilename(): " + task.getFilename());
                LogUtils.e("DownloadListener==taskEnd=1=task.getParentFile(): " + task.getParentFile().getAbsolutePath());//commonFolderName
                LogUtils.e("DownloadListener==taskEnd=1=task.taskSpeed(): " + taskSpeed.speed());

                LogUtils.e("DownloadListener==taskEnd=1=task.getFlushBufferSize(): " + task.getFlushBufferSize());
                LogUtils.e("DownloadListener==taskEnd=1=task.getReadBufferSize(): " + task.getReadBufferSize());
                LogUtils.e("DownloadListener==taskEnd=1=task.getTotalLength(): " + task.getInfo().getTotalLength());
                LogUtils.e("DownloadListener==taskEnd=1=task.getTotalOffset(): " + task.getInfo().getTotalOffset());

                LogUtils.e("DownloadListener==taskEnd=1=taskSpeed: " + taskSpeed.speed());

                DownEndEvent event = new DownEndEvent();
                String tag = (String) task.getTag();
                event.setTag(tag);

                //SAME_TASK_BUSY   这个状态表示当前正在下载
                if (cause.name().equals("COMPLETED")) {//CANCELED
                    event.setStatue(Constants.STATUE_COMPLETED);
                    event.setDownStatueDes(Constants.STATUE_COMPLETED_DES);
                    event.setRefreshLocalVideoFolder(task.getParentFile().getAbsolutePath());
                    event.setRefreshLocalFileName(task.getFilename());
                    event.setLocalUrl(task.getFile().getAbsolutePath());
                    long totalLength = task.getInfo().getTotalLength();
                    event.setTotalLength(totalLength);
                    event.setSpeed(taskSpeed.speed());
                    event.setTotalOffsetLength(task.getInfo().getTotalOffset());
                    event.setDeviceCode(mDeviceCode);
                    event.setCurrentItemCaseID(currentItemCaseID);
                    EventBus.getDefault().postSticky(event);

                    /**
                     * 不管成功还是失败,都需要删除当前下载记录
                     */
                    List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(mContext, mDeviceCode + "_" + currentItemCaseID + "-" + (String) task.getTag());
                    if (mDBDownList.size() != 0) {
                        TaskDBBean taskDBBean = mDBDownList.get(0);
                        //删除具体某天数据
                        TaskDBBeanUtils.delete(mContext, taskDBBean);
                    }

                } else if (cause.name().equals("CANCELED")) {
                    event.setStatue(Constants.STATUE_CANCELED);
                    event.setDownStatueDes(Constants.STATUE_CANCELED_DES);
                    event.setRefreshLocalVideoFolder(task.getParentFile().getAbsolutePath());
                    event.setRefreshLocalFileName(task.getFilename());
                    event.setLocalUrl(task.getFile().getAbsolutePath());
                    long totalLength = task.getInfo().getTotalLength();
                    event.setTotalLength(totalLength);
                    event.setSpeed(taskSpeed.speed());
                    event.setDeviceCode(mDeviceCode);
                    event.setCurrentItemCaseID(currentItemCaseID);
                    event.setTotalOffsetLength(task.getInfo().getTotalOffset());
                    EventBus.getDefault().postSticky(event);
                    /**
                     * 不管成功还是失败,都需要删除当前下载记录
                     */
                    List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(mContext, mDeviceCode + "_" + currentItemCaseID + "-" + (String) task.getTag());
                    if (mDBDownList.size() != 0) {
                        TaskDBBean taskDBBean = mDBDownList.get(0);
                        //删除具体某天数据
                        TaskDBBeanUtils.delete(mContext, taskDBBean);
                        LogUtils.e("DownloadListener==taskEnd=2=cause.name(): " + cause.name());
                    }
                    LogUtils.e("DownloadListener==taskEnd=3=cause.name(): " + cause.name());

                } else if (cause.name().equals("ERROR")) {
                    /**
                     * 不管成功还是失败,都需要删除当前下载记录
                     */
                    List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(mContext, mDeviceCode + "_" + currentItemCaseID + "-" + (String) task.getTag());
                    if (mDBDownList.size() != 0) {
                        TaskDBBean taskDBBean = mDBDownList.get(0);
                        //删除具体某天数据
                        LogUtils.e("DownloadListener==taskEnd=2=cause.name(): " + cause.name());
                        //删除具体某天数据
                        TaskDBBeanUtils.delete(mContext, taskDBBean);
                    }
                    LogUtils.e("DownloadListener==taskEnd=3=cause.name(): " + cause.name());
                    event.setStatue(Constants.STATUE_ERROR);
                    event.setDownStatueDes(Constants.STATUE_ERROR_DES);
                    long totalLength = task.getInfo().getTotalLength();
                    event.setTotalLength(totalLength);
                    event.setDownStatueDes("下载错误");
                    event.setDeviceCode(mDeviceCode);
                    event.setCurrentItemCaseID(currentItemCaseID);
                    EventBus.getDefault().postSticky(event);


                } else if (cause.name().equals("SAME_TASK_BUSY")) {
                    //正在下载不能发消息 不能就删除了记录
                }
            }
        };


        DownloadContext.Builder mDownBuilderQueue = new DownloadContext.QueueSet()
                .setParentPathFile(new File(commonFolderName))
                .setMinIntervalMillisCallbackProcess(150)
                .commit();
        mDownBuilderQueue.setListener(new DownloadContextListener() {
            @Override
            public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause,
                                int remainCount) {
                LogUtils.e("DownloadListener==queue==taskEnd==taskSpeed==task.getTag==: " + task.getTag());
                LogUtils.e("DownloadListener==queue==taskEnd==taskSpeed==cause.name()==: " + cause.name());
                LogUtils.e("DownloadListener==queue==taskEnd==taskSpeed==Exception==: " + realCause);
                LogUtils.e("DownloadListener==queue==taskEnd==taskSpeed==cause==: " + cause.toString());
                LogUtils.e("DownloadListener==queue==taskEnd==taskSpeed==remainCount==: " + remainCount);

            }

            @Override
            public void queueEnd(@NonNull DownloadContext context) {

                /**
                 * 不管成功还是失败,都需要删除全部正在下载的视频条目记录
                 */
                for (int i = 0; i < context.getTasks().length; i++) {
                    DownloadTask task = context.getTasks()[i];
                    List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(mContext, mDeviceCode + "_" + currentItemCaseID + "-" + (String) task.getTag());
                    if (mDBDownList.size() != 0) {
                        TaskDBBean taskDBBean = mDBDownList.get(0);
                        //删除具体某天数据
                        TaskDBBeanUtils.delete(mContext, taskDBBean);
                    }
                }

            }
        });

        LogUtils.e("DownloadListener===下载任务的path==" + DataDTOBean.getAllUrl());
        ///storage/emulated/0/MyDownVideos/null_null
        LogUtils.e("DownloadListener下载任务的本地文件路径==" + DataDTOBean.getLocalFolderName());
        LogUtils.e("DownloadListener下载任务的path==" + DataDTOBean.getFileName());
        //同一个任务在运行的时候,会显示taskEnd回调===cause.name()==SAME_TASK_BUSY
        DownloadTask task = new DownloadTask.Builder(DataDTOBean.getAllUrl(), DataDTOBean.getLocalFolderName(), DataDTOBean.getFileName())
                .setConnectionCount(1)
//                    .setPassIfAlreadyCompleted(false)
//                .setPassIfAlreadyCompleted(true)////存在不会下载,直接显示完成
                .setPassIfAlreadyCompleted(false)//会一直下载不管有没有缓存,
                .setMinIntervalMillisCallbackProcess(1000)
                .build();

        task.setTag(DataDTOBean.getFileName());//2022-04-22-08-41-51.mp4
        //添加到队列中
        mDownBuilderQueue.bindSetTask(task);
        mDownContext = mDownBuilderQueue.build();
        mDownContext.startOnSerial(listener04);//单个执行
//            mDownContext.startOnParallel(listener);  //同时进行

    }


    /**
     * eventbus 下载任务==结束,添加数据库刷新相册,避免选择界面或者下载界面 关闭了,不能及时添加到数据库导致下载成功了下次进来显示未下载的bug
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void DownEndEvent(DownEndEvent event) {
        LogUtils.e("DownloadListener==服务==监听==下载任务==结束=== " + event.getTag());
        String tag = event.getTag();
        String mDeviceCode = event.getDeviceCode();
        String currentItemCaseID = event.getCurrentItemCaseID();
        List<TaskDBBean> mDBDownList = TaskDBBeanUtils.getQueryBeanBySingleCode(getApplicationContext(), mDeviceCode + "_" + currentItemCaseID + "-" + tag);
        //下载接收,不管失败还是错误,都需要删除下载队列任务
        if (mDBDownList.size() != 0) {
            TaskDBBean taskDBBean = mDBDownList.get(0);
            //删除具体某天数据
            TaskDBBeanUtils.delete(getApplicationContext(), taskDBBean);
        }
        //存入数据库
        addDataInGreenDao(event, mDeviceCode, currentItemCaseID);

        //刷新相册
        // /storage/emulated/0/MyDownVideos/0000000000000000546017FE6BC28949_1195/720220424151314404.mp4
        //localFolderName==/storage/emulated/0/MyDownVideos/0000000000000000546017FE6BC28949_1195
        //event.getRefreshLocalFileName()  ==720220424151314404.mp4
        String  localFolderName = Environment.getExternalStorageDirectory() + "/MyDownVideos/" + mDeviceCode + "_" + currentItemCaseID;
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{localFolderName + "/" + event.getRefreshLocalFileName()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        //刷新成功的回调方法
                        LogUtils.e("DownSelectedVideoActivity02======下载任务==结束==相册刷新成功==" + tag);

                    }
                });

    }

    private void addDataInGreenDao(DownEndEvent event, String mDeviceCode, String currentItemCaseID) {
        //1,判断是否下载过
        List<DownVideoMessage> mList = DownVideoMsgDBUtils.getQueryBeanByThree(mContext,
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
                DownVideoMsgDBUtils.insertOrReplaceInTx(mContext, dbBean);

            } else {
                DownVideoMessage downVideoMessage = new DownVideoMessage();
                String localUrl = event.getLocalUrl();
                downVideoMessage.setDeviceCode(mDeviceCode);
                downVideoMessage.setSaveCaseID(currentItemCaseID);
                downVideoMessage.setIsDown(true);
                downVideoMessage.setMaxProcess(event.getTotalLength());
                downVideoMessage.setTag(event.getTag());
                downVideoMessage.setUrl(localUrl);
                DownVideoMsgDBUtils.insertOrReplaceInTx(mContext, downVideoMessage);
            }

        } else if (event.getStatue().equals(Constants.STATUE_ERROR)) {//下载失败
            //数据库存在
            if (mList.size() > 0) {
                DownVideoMessage dbBean = mList.get(0);
                DownVideoMsgDBUtils.delete(mContext, dbBean);
            }
        }
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        LogUtils.e("保活服务开启HandService=====关闭了====stopWork。");
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        //若还没有取消订阅, 就说明任务仍在运行.
        boolean b1 = sDisposable10s != null && !sDisposable10s.isDisposed();

        return b1;
//        return sDisposable != null && !sDisposable.isDisposed();
    }


    public static Boolean getWorkStatue() {
        boolean b1 = sDisposable10s != null && !sDisposable10s.isDisposed();
        LogUtils.e("保活服务开启HandService=====getWorkStatue====b1==" + b1);

        return b1;
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        LogUtils.e("保活服务HandService=====保存数据到磁盘===onServiceKilled。");
    }

    /**
     * 将获取到的int型ip转成string类型
     */
    private static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("保活服务HandService====onCreate===onCreate。");

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        isFirstIn = false;
        LogUtils.e("保活服务HandService====保存数据到磁盘===onDestroy。");
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }


}
