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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.PictureChoseBean;
import com.company.iendo.bean.ReportExistBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.DetailFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.PictureFragment;
import com.company.iendo.mineui.activity.casemanage.fragment.VideoFragment;
import com.company.iendo.mineui.activity.vlc.GetPictureActivity;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.adapter.TabAdapter;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private TabAdapter mTabAdapter;
    private TitleBar mTitlebar;
    private TextView mDown;
    private TextView mDelete;
    private TextView mPicture;
    private Boolean mFatherExit;   //父类Activity 是否主动退出的标识,主动退出需要请求保存fragment的更新数据
    private String currentItemID;
    private Boolean FLAG_PICTURE_EXIST = false;  //查询服务端是否已经生成报告
    private String FLAG_PICTURE_URL = "";  //查询服务端是否已经生成报告,存在的情况下,会把url赋值给它
    private RelativeLayout mReportAll;
    private TitleBar mReportBar;
    private AppCompatImageView mReportImageView;
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_detail;
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
        mDelete = findViewById(R.id.case_delete);
        mDown = findViewById(R.id.case_down);
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
        setOnClickListener(R.id.linear_get_picture, R.id.linear_get_report, R.id.linear_delete, R.id.case_down, R.id.linear_down);
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                //退出界面的时候必须保存数据
                if (null != mOnEditStatusListener) {
                    mOnEditStatusListener.onEditStatus(true, true);
                }
                postDelayed(() -> {
                    finish();
                }, 300);
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
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
                LogUtils.e("Socket回调==DetailCaseActivity==握手==onRightClick==" + UDP_HAND_TAG);

                if (UDP_HAND_TAG) {
                    sendSocketPointMessage(Constants.UDP_F2);
                } else {
                    toast("暂未建立连接!");
                    sendHandLinkMessage();
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
        LogUtils.e("历史报告地址=="+path);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==PictureChoseActivity==event.getData()==" + event.getData());
        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(event.getData());//随机数之后到data结尾的String
        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
        String currentCMD = CalculateUtils.getCMD(event.getData());
        LogUtils.e("Socket回调==PictureChoseActivity==随机数之后到data的Str==mRun2End4==" + mRun2End4);
        LogUtils.e("Socket回调==PictureChoseActivity==发送方设备类型==deviceType==" + deviceType);
        LogUtils.e("Socket回调==PictureChoseActivity==获取发送方设备Code==deviceOnlyCode==" + deviceOnlyCode);
        LogUtils.e("Socket回调==PictureChoseActivity==当前UDP命令==currentCMD==" + currentCMD);
        LogUtils.e("Socket回调==PictureChoseActivity==当前UDP命令==event.getUdpCmd()==" + event.getUdpCmd());
        LogUtils.e("Socket回调==PictureChoseActivity==SocketRefreshEvent==event.toString()==" + event.toString());

        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://握手
                UDP_HAND_TAG = true;
                break;
            case Constants.UDP_F1://预览报告
                if ("".equals(data)) {
                    toast("暂无报告");
                } else {
                    String path = "http://" + mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
//                    String path = mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
                    LogUtils.e("Socket回调==PictureChoseActivity==当前UDP命令==path==" + path);
                    Glide.with(DetailCaseActivity.this)
                            .load(path)
                            .placeholder(R.drawable.ic_bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                            .error(R.mipmap.bg_splash_des)
                            .signature(new ObjectKey(System.currentTimeMillis()))//不适用缓存
                            .into(mReportImageView);
                }
                break;
            case Constants.UDP_F2://打印报告
                LogUtils.e("Socket回调==DetailCaseActivity==当前UDP命令==打印报告=="+data );
                if ("00".equals(data)) {
                    toast("报告打印成功!");
                } else {
                    toast("报告打印失败!");
                }
                break;
        }

    }

    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (UDP_HAND_TAG) {
            ShotPictureBean shotPictureBean = new ShotPictureBean();
            String spCaseID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Chose_CaseID, "");
            String s = CalculateUtils.hex10To16Result4(Integer.parseInt(spCaseID));
            shotPictureBean.setRecordid(s);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(shotPictureBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空!");
                return;
            }
            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);
        } else {
            sendHandLinkMessage();
            toast("请先建立握手链接!");
        }

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        HandBean handBean = new HandBean();
        handBean.setHelloPc("HelloPc");
        handBean.setComeFrom("Android");
        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_HAND);
        if (("".equals(mSocketPort))) {
            toast("通讯端口不能为空!");
            return;
        }
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort),this);
//        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
    }

    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume===DetailCaseActivity===开始建立握手链接!");
        sendHandLinkMessage();
    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
    }

    @Override
    protected void initData() {
        currentItemID = getIntent().getStringExtra("itemID");
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
                        LogUtils.e("图片" + "response===" + response);////原图路径

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
                    String currentUrl = "rtsp://" + mUsername + ":" + mPassword + "@" + mSocketOrLiveIP + ":" + mLivePort + "/session0.mpg";  //高清
                    Intent intent1 = new Intent(this, GetPictureActivity.class);
                    LogUtils.e("======GetPictureActivity=====currentUrl====currentUrl==" + currentUrl);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("ItemID", currentItemID);
                    bundle1.putString("currentUrl", currentUrl);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    break;
                case R.id.linear_get_report://获取报告
                    Intent intent = new Intent(this, PictureChoseActivity.class);

                    if (FLAG_PICTURE_EXIST) {  //存在历史报告
                        //弹出对话框显示历史报告,还是获取新的报告
                        showSingleDialog(intent);
                    } else {
                        mOnEditStatusListener.onGetReport();
                        startActivity(intent);
                    }

                    break;
                case R.id.linear_delete://删除
                    mOnEditStatusListener.onDelete();

                    break;
                case R.id.linear_down://下载
                    mOnEditStatusListener.onDown(true, true);
                    break;
            }
        }
    }

    private void showSingleDialog(Intent intent) {
        // 单选对话框
        new SelectDialog.Builder(this)
                .setTitle("请选择")
                .setList("获取报告", "历史报告")
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(0)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        String substring = data.toString().substring(1, 2);
                        LogUtils.e("确定了=" + substring);
                        String str = data.get(Integer.parseInt(substring));
                        if ("获取报告".equals(str)) {  //进入图片选择界面,选择获取新的报告
                            mOnEditStatusListener.onGetReport();
                            startActivity(intent);
                        } else { //获取历史报告
                            showStartReportAnim();
                        }


                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
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
