package com.company.iendo.mineui.activity.casemanage;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.ReportExistBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.casemanage.fragment.DetailFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.PictureFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.VideoFragment;
import com.company.iendo.mineui.activity.vlc.GetPictureActivity;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.service.HandService;
import com.company.iendo.ui.adapter.TabAdapter;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.SocketUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.widget.layout.NestedViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 13:58
 * desc：详情界面---主架构界面
 */
public class DetailCaseActivity extends AppActivity implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener {
    private NestedViewPager mViewPager;
    private RecyclerView mTabView;
    public static TabAdapter mTabAdapter;
    public static TitleBar mTitlebar;
    private TextView mDelete;
    public static TextView mPicture, mCaseDown;
    private Boolean mFatherExit;   //父类Activity 是否主动退出的标识,主动退出需要请求保存fragment的更新数据
    private String currentItemID;
    private Boolean FLAG_PICTURE_EXIST = false;  //查询服务端是否已经生成报告
    private String FLAG_PICTURE_URL = "";  //查询服务端是否已经生成报告,存在的情况下,会把url赋值给它
    private RelativeLayout mReportAll;
    private TitleBar mReportBar;
    private AppCompatImageView mReportImageView;
    private static boolean UDP_EQUALS_ID = false; //获取当前操作id,和进入该界面的id 是否相等,相等才可以进行各种操作,默认不相等,
    private boolean isPrinted;   //true,    是否已经打印过,true表示打印过了,不能编辑
    private String mCreatedByWho;
    private String itemUserName;
    private String currentCaseName;
    private String videosCounts;
    private String imageCounts;
    private MessageDialog.Builder existBuilder;
    private TextView mCaseDownVideo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_detail;
    }

    //获取病例图片数目
    private void sendImageRequest(String mCaseID) {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
                .addParams("ID", mCaseID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toast("请求错误" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            CaseDetailBean mBean = mGson.fromJson(response, CaseDetailBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                imageCounts = mBean.getData().getImagesCount() + "";
                                videosCounts = mBean.getData().getVideosCount() + "";
                                DetailCaseActivity.mTabAdapter.setItem(1, "图片(" + imageCounts + ")");
                                DetailCaseActivity.mTabAdapter.setItem(2, "视频(" + videosCounts + ")");

                            } else {
                                toast("请求错误");
                            }
                        } else {
                            toast("请求错误");

                        }
                    }
                });
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTabView = findViewById(R.id.rv_detail_tab);
        mViewPager = findViewById(R.id.vp_detail_pager);
        //报告view
        mReportAll = findViewById(R.id.relative_anim);
        mReportBar = findViewById(R.id.anim_titlebar);
        mReportImageView = findViewById(R.id.iv_anim_report);
        mTitlebar = findViewById(R.id.titlebar);
        mPicture = findViewById(R.id.case_picture);
        mCaseDown = findViewById(R.id.case_down);
        mCaseDownVideo = findViewById(R.id.case_down_video);
        mDelete = findViewById(R.id.case_delete);
        mFatherExit = false;
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DetailFragment.newInstance(), "详情");
        mPagerAdapter.addFragment(PictureFragment.newInstance(), "图片");
        mPagerAdapter.addFragment(VideoFragment.newInstance(), "视频");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        responseListener();


        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);


    }


    private void responseListener() {
        sendGetEditStatueRequest();
        setOnClickListener(R.id.linear_get_picture, R.id.linear_get_report, R.id.linear_delete, R.id.linear_down, R.id.linear_down_video);
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                //如果下载病历中,退出界面提示用户
                if (mCaseDown.getText().equals("下载中..")) {
                    // 消息对话框
                    existBuilder = new MessageDialog.Builder(getActivity());
                    existBuilder.setTitle("是否返回")
                            .setMessage("当前正在下载病历信息,返回可能导致下载的病例图片不全")
                            .setConfirm("取消")
                            // 设置 null 表示不显示取消按钮
                            .setCancel("立即返回")
                            .setCanceledOnTouchOutside(false)
                            // 设置点击按钮后不关闭对话框
                            //.setAutoDismiss(false)
                            .setListener(new MessageDialog.OnListener() {

                                @Override
                                public void onConfirm(BaseDialog dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            })
                            .show();
                }

                if (mMMKVInstace.decodeBool(Constants.KEY_CanEdit)) {
                    //退出界面的时候必须保存数据
                    if (null != mOnEditStatusListener) {
                        mOnEditStatusListener.onEditStatus(true, true);
//                        mOnEditStatusListener.onEditStatus(true, true);
                    }


                } else {
                    postDelayed(() -> {
                        finish();
                    }, 100);
                }


            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {


                //勾选了几个判断几个
                //是否能编辑遍历
                boolean KEY_CanEdit = mMMKVInstace.decodeBool(Constants.KEY_CanEdit);
                //仅限未打印病例,未打印病历,权限
                boolean KEY_UnPrinted = mMMKVInstace.decodeBool(Constants.KEY_UnPrinted);
                //仅限本人创建病例,本人病历,权限
                boolean KEY_OnlySelf = mMMKVInstace.decodeBool(Constants.KEY_OnlySelf);
                //当前病例是否打印
                boolean KEY_Printed = isPrinted;
                //当前病例是否该账号创建的
                boolean caseIsSelf = itemUserName.equalsIgnoreCase(mLoginUserName) ? true : false;
                //控制变量
                boolean canOpeartion = false;
                if (KEY_CanEdit) {
                    canOpeartion = true;
                    if (KEY_OnlySelf) {
                        if (KEY_UnPrinted) {
                            canOpeartion = !KEY_Printed && caseIsSelf;
                        } else {
                            canOpeartion = caseIsSelf;
                        }
                    } else {
                        if (KEY_UnPrinted) {
                            canOpeartion = !caseIsSelf;
                        }
                    }
                }
                if (canOpeartion) {
                    //能被编辑
                    clickEidtListener();
                } else {
                    toast(Constants.HAVE_NO_PERMISSION);

                }

            }


        });


        mReportBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                showCloseReportAnim();

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                if (HandService.UDP_HAND_GLOBAL_TAG) {
                    sendSocketPointMessage(Constants.UDP_F2);
                } else {
                    toast(Constants.HAVE_HAND_FAIL_OFFLINE);
                }
            }
        });
    }

    private void clickEidtListener() {
        if (mTitlebar.getRightTitle().equals("编辑")) {
            if (null != mOnEditStatusListener) {
                mOnEditStatusListener.onEditStatus(true, false);
            }
            mTitlebar.setRightTitle("保存");
            mTitlebar.setRightTitleColor(getResources().getColor(R.color.red));
        } else {
            mTitlebar.setRightTitle("编辑");
            mTitlebar.setRightTitleColor(getResources().getColor(R.color.black));
            if (null != mOnEditStatusListener) {
                mOnEditStatusListener.onEditStatus(false, false);
            }
        }
    }


    /**
     * 获取病例详情,
     * 获取,未打印病历(是否已经打印过)  Printed字段
     * 获取,本人病历(表示谁创建的病例)  UserName字段
     */
    private void sendGetEditStatueRequest() {

        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
                .addParams("ID", currentItemID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            CaseDetailBean mBean = mGson.fromJson(response, CaseDetailBean.class);
                            mCreatedByWho = mBean.getData().getUserName();

                            isPrinted = mBean.getData().isPrinted();
                        } else {

                        }
                    }
                });

    }

    /**
     * 关闭获取报告动画
     */
    private void showCloseReportAnim() {
        mReportAll.setBackgroundResource(R.color.white);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mReportAll, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mReportAll, "scaleX", 1f, 0.01f);
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
                mReportAll.setVisibility(View.INVISIBLE);
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
     * 开启获取报告动画
     */
    private void showStartReportAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mReportAll, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mReportAll, "scaleX", 1f, 0.01f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mReportAll, "scaleY", 0.01f, 1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mReportAll, "scaleX", 0.01f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        AnimatorSet animSet2 = new AnimatorSet();
        animSet.play(animator).with(animator2);
        animSet.setDuration(50);//100
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mReportAll.setVisibility(View.VISIBLE);
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
                mReportAll.setBackgroundResource(R.color.gray);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //加载图片
//                if (!"".equals(FLAG_PICTURE_URL)){
        //194/Report/电脑022021091403.bmp
        String path = "http://" + mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + FLAG_PICTURE_URL;
        Glide.with(DetailCaseActivity.this)
                .load(path)
                .placeholder(R.drawable.ic_bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                .error(R.mipmap.bg_splash_des)
                .signature(new ObjectKey(System.currentTimeMillis()))//不适用缓存
                .into(mReportImageView);
    }
    /**
     * ***************************************************************************通讯模块**************************************************************************
     */


    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_CUSTOM_DOWN_OVER://图片下载的提示
                if ("true".equals(data)) {//下载完毕,显示:已下载  未下载的话显示:下载病历,下载中显示:下载中..
                    mCaseDown.setText("已下载");
                    if (null != existBuilder) {
                        existBuilder.dismiss();
                    }
                } else {
                    mCaseDown.setText("下载中..");
                }
                break;
            case Constants.UDP_CUSTOM_TOAST://吐司
                toast("" + data);
                break;
            case Constants.UDP_CUSTOM_FINISH://自定义信息,结束当前界面
                if (mCaseDown.getText().equals("下载中..")) {
                    //此处如果正在下载,直接返回,停留在这个界面
                    return;
                }
                postDelayed(() -> {
                    finish();
                }, 100);
                break;
            case Constants.UDP_F0://获取当前病例
                if ("true".equals(data)) {//当前病例相同才能操作
                    UDP_EQUALS_ID = true;
                    //获取当前病例ID
                } else {
                    UDP_EQUALS_ID = false;
                }
                break;
            case Constants.UDP_CUSTOM14://自定义命令---->在图像采集界面,接受到删除病例,需要退到病例列表界面而不是回退病例详情界面
                if (Constants.UDP_CUSTOM14.equals(data)) {//当前病例相同才能操作
                    finish();
                }
                break;
            case Constants.UDP_14://删除病例了
                if (data.equals(currentItemID)) {//被删除的病例ID和当前的病例ID相同,退出该界面
                    showExitDialog();
                }
                break;
            case Constants.UDP_F1://预览报告
                if ("".equals(data)) {
                    toast("暂无报告");
                } else {
                    String path = "http://" + mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
//                    String path = mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
                    Glide.with(DetailCaseActivity.this)
                            .load(path)
                            .placeholder(R.drawable.ic_bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                            .error(R.mipmap.bg_splash_des)
                            .signature(new ObjectKey(System.currentTimeMillis()))//不适用缓存
                            .into(mReportImageView);
                }
                break;
            case Constants.UDP_F2://打印报告
                if ("00".equals(data)) {
                    toast("报告打印成功");
                } else {
                    toast("报告打印失败");
                }
                break;
            case Constants.UDP_F7://权限通知变动,在病例列表,病例详情,和图像采集三个界面相互监听,发现了请求后台更新本地权限
                if (event.getTga()) {
                    requestCurrentPermission();
                }
                break;
        }

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
                        finish();
                    }
                })
                .setOnKeyListener((dialog, event) -> {
                    toast("按键代码：" + event.getKeyCode());
                    return false;
                })
                .show();
    }

    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            ShotPictureBean shotPictureBean = new ShotPictureBean();
            String spCaseID = mMMKVInstace.decodeString(Constants.KEY_CurrentCaseID);
            String s = CalculateUtils.hex10To16Result4(Integer.parseInt(spCaseID));
            shotPictureBean.setRecordid(s);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(shotPictureBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空");
                return;
            }

            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), DetailCaseActivity.this);
//            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);

        } else {
            toast(Constants.HAVE_HAND_FAIL_OFFLINE);
        }

    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
        //获取当前病例ID
        sendSocketPointMessage(Constants.UDP_F0);
        sendImageRequest(currentItemID);
    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
    }

    @Override
    protected void initData() {
        currentItemID = getIntent().getStringExtra("itemID");
        currentCaseName = getIntent().getStringExtra("Name");
        itemUserName = getIntent().getStringExtra("itemUserName");
        mTitlebar.setTitle(currentCaseName + "");

        mTabAdapter.addItem("详情");
        mTabAdapter.addItem("图片");
        mTabAdapter.addItem("视频");
        mTabAdapter.setOnTabListener(this);
        sendRequest();


    }

    /**
     * 查询服务端是否已经生成报告
     */
    private void sendRequest() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_Report_Exists)
                .addParams("ID", currentItemID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            ReportExistBean mBean = mGson.fromJson(response, ReportExistBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                if (mBean.getData().isExists()) {
                                    FLAG_PICTURE_EXIST = true;
                                    FLAG_PICTURE_URL = mBean.getData().getUrl();
                                } else {
                                    FLAG_PICTURE_EXIST = false;
                                    FLAG_PICTURE_URL = "";

                                }
                            } else {
                                toast("报告是否存在获取失败");
                            }
                        } else {
                            toast("报告是否存在获取失败");

                        }
                    }
                });


    }


    @Override
    public void onClick(View view) {
        if (null != mOnEditStatusListener) {
            switch (view.getId()) {
                case R.id.linear_get_picture://图像采集
                    mOnEditStatusListener.onGetPicture();
//                    currentUrl01 = "rtsp://" + username + ":" + password + "@" + ip + ":" + port + "/session0.mpg";  //高清
                    String currentUrl0 = "rtsp://" + mUsername + ":" + mPassword + "@" + mSocketOrLiveIP + ":" + mLivePort + "/session0.mpg";  //高清
                    String currentUrl1 = "rtsp://" + mUsername + ":" + mPassword + "@" + mSocketOrLiveIP + ":" + mLivePort + "/session1.mpg";  //标清
                    Intent intent1 = new Intent(this, GetPictureActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("ItemID", currentItemID);
                    bundle1.putString("currentUrl0", currentUrl0);
                    bundle1.putString("currentUrl1", currentUrl1);
//                    bundle1.putString("currentUrl0", "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
//                    bundle1.putString("currentUrl1", "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    break;
                case R.id.linear_get_report://获取报告
                    Intent intent = new Intent(this, PictureChoseActivity.class);

                    if (FLAG_PICTURE_EXIST) {  //存在历史报告
                        //弹出对话框显示历史报告,还是获取新的报告
                        showSingleDialog(intent);
                    } else {
                        boolean b = mMMKVInstace.decodeBool(Constants.KEY_CanPrint);
                        if (b) {
                            mOnEditStatusListener.onGetReport();
                            startActivity(intent);
                        } else {
                            toast("当前账号无权限修改病历");
                        }
                    }
                    break;
                case R.id.linear_delete://删除
                    mOnEditStatusListener.onDelete();
                    break;
                case R.id.linear_down://下载图片和病例
                    mOnEditStatusListener.onDown(true, true);
                    break;
                case R.id.linear_down_video://下载视频
                    mOnEditStatusListener.onDownVideo();
                    break;
            }
        }
    }

    private void showSingleDialog(Intent intent) {
        // 单选对话框
        new SelectDialog.Builder(this)
                .setTitle("当前病例已经生产报告")
                .setList("预览报告", "重新生成")
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(0)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        String substring = data.toString().substring(1, 2);
                        String str = data.get(Integer.parseInt(substring));
                        if ("获取报告".equals(str)) {  //进入图片选择界面,选择获取新的报告,需要获取CanPrint字段判断是否有能打印的权限
                            boolean b = mMMKVInstace.decodeBool(Constants.KEY_CanPrint);
                            if (b) {
                                mOnEditStatusListener.onGetReport();
                                startActivity(intent);
                            } else {
                                toast("当前账号无权限修改病历");
                            }
                        } else { //获取历史报告
                            showStartReportAnim();
                        }


                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
    }


    /**
     * activity 和 fragment数据通信= activity通知fragment刷新UI状态
     * <p>
     * 状态回调监听
     *
     * @return
     * @return
     */

    private OnEditStatusListener mOnEditStatusListener;


    public void setOnEditStatusListener(OnEditStatusListener mOnEditStatusListener) {
        this.mOnEditStatusListener = mOnEditStatusListener;

    }

    public interface OnEditStatusListener {
        //activity制作发送的提示,具体操作全部在DetailFragment里面实现
        void onEditStatus(boolean status, boolean isFatherExit);

        //下载用户数据
        void onDown(boolean userInfo, boolean userPicture);

        //删除病例
        void onDelete();

        //获取报告
        void onGetReport();

        //图像采集
        void onGetPicture();

        void onDownVideo();

    }


    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }


    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
        EventBus.getDefault().unregister(this);

    }
}
