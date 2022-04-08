package com.company.iendo.mineui.activity.casemanage;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.PictureChoseBean;
import com.company.iendo.bean.ReportSelectedImageBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.bean.socket.getpicture.ShotPictureBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.fragment.adapter.ChosePictureAdapter;
import com.company.iendo.other.Constants;
import com.company.iendo.other.GridSpaceDecoration;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
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
 * desc   : 图片选择界面--生成报告前需要选择图片
 */
public final class PictureChoseActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {

    private StatusLayout mStatusLayout;
    private TitleBar mTitlebar;
    private RecyclerView mRecyclerView;
    private ArrayList<PictureChoseBean> mPathList;
    private ArrayList<PictureChoseBean> mDataLest = new ArrayList<>();
    private ChosePictureAdapter mAdapter;
    private TextView mToLookActivity;
    private String currentItemID;
    private RelativeLayout mAnimRelative;
    private TitleBar mAnimTitlebar;
    private AppCompatImageView mAnimReport;
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功
    private String oldIDS = "";
    private RelativeLayout mRelativeAll;
    private TextView mImageEmpty;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chose_picture;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);
        mTitlebar = findViewById(R.id.titlebar);
        mRecyclerView = findViewById(R.id.rv_image_recyclerview);
        mToLookActivity = findViewById(R.id.tv_go_look);
        mAnimRelative = findViewById(R.id.relative_anim);
        mRelativeAll = findViewById(R.id.relative_all);
        mAnimTitlebar = findViewById(R.id.anim_titlebar);
        mAnimReport = findViewById(R.id.iv_anim_report);
        mImageEmpty = findViewById(R.id.tv_image_empty);

    }

    @Override
    protected void initData() {
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "111");
        mAdapter = new ChosePictureAdapter(getActivity(), MainActivity.getCurrentItemID(), mBaseUrl);
        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceDecoration(30));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(mDataLest);
        currentItemID = MainActivity.getCurrentItemID();
        sendRequest(currentItemID);
        responseListener();
    }

    private void responseListener() {
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
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
        mAnimTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                showCloseReportAnim();
                sendRequest(currentItemID);

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                if (UDP_HAND_TAG) {
                    sendSocketPointMessage(Constants.UDP_F2);
                } else {
                    toast("暂未建立连接");
                    sendHandLinkMessage();
                }
            }
        });

        mToLookActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断选中了多少张图片,不能超过9张
                ArrayList<PictureChoseBean> mList = new ArrayList<>();
                mList.clear();
                //点击之前先判断当前有几个选中的图片,超过9张提示不能在选择
                List<PictureChoseBean> data = mAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    PictureChoseBean pictureChoseBean = data.get(i);
                    if (pictureChoseBean.isNewSelected()) {
                        mList.add(pictureChoseBean);
                    }
                }
                LogUtils.e("图片" + "mList.size()===" + mList.size());////原图路径
                //获取请求参数oldImageIDs和newImageIDs
                String oldImageIDs = "";
                String newImageIDs = "";
                for (int i = 0; i < mList.size(); i++) {
                    PictureChoseBean bean = mList.get(i);
//                    if (bean.isOldSelected()) {
//                        if ("".equals(oldImageIDs)) {
//                            oldImageIDs = bean.getPictureID() + ",";
//                        } else {
//                            oldImageIDs = oldImageIDs + bean.getPictureID() + ",";
//                        }
//                    }
                    if (bean.isNewSelected()) {
                        if ("".equals(newImageIDs)) {
                            newImageIDs = bean.getPictureID() + ",";
                        } else {
                            newImageIDs = newImageIDs + bean.getPictureID() + ",";
                        }
                    }
                }
                LogUtils.e("图片" + "id==========oldImageIDs==" + getIDs(oldIDS));////原图路径
                LogUtils.e("图片" + "id==========newImageIDs==" + getIDs(newImageIDs));////原图路径

                if (mList.size() > 9) {
                    toast("最多不超过9张");
                    return;
                } else {
                    sendReportRequest(getIDs(oldIDS), getIDs(newImageIDs));

                }
            }
        });
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

        if (null != mDataLest) {
            PictureChoseBean bean = mDataLest.get(position);
            String newID = bean.getItemID();
            for (int i = 0; i < mDataLest.size(); i++) {
                PictureChoseBean oldBean = mDataLest.get(i);
                String oldID = oldBean.getItemID();
                if (newID.equals(oldID)) {
                    if (oldBean.isNewSelected()) {
                        oldBean.setNewSelected(false);
                    } else {
                        oldBean.setNewSelected(true);
                    }
                    mAdapter.setItem(position, oldBean);

                }
            }
        }

    }


    /**
     * 获取当前用户的图片
     *
     * @param currentItemID
     */
    private void sendRequest(String currentItemID) {
        showLoading();
        LogUtils.e("currentItemID" + currentItemID);
        LogUtils.e("currentItemID" + mBaseUrl + HttpConstant.CaseManager_Report);

        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CasePictures)
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
                        oldIDS="";
                        mPathList = new ArrayList<>();
                        showComplete();
                        mImageEmpty.setVisibility(View.INVISIBLE);
                        if ("" != response) {
                            DetailPictureBean mBean = mGson.fromJson(response, DetailPictureBean.class);
                            List<DetailPictureBean.DataDTO> data = mBean.getData();
                            LogUtils.e("图片" + "response===" + response);////原图路径

                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                if (mBean.getData().size() != 0) {
                                    //添加跳转大图界面的前提是,把图片url 添加到集合之中
                                    for (int i = 0; i < mBean.getData().size(); i++) {
                                        DetailPictureBean.DataDTO dataDTO = mBean.getData().get(i);
                                        String imageName = dataDTO.getImagePath();
                                        String url = mBaseUrl + "/" + MainActivity.getCurrentItemID() + "/" + imageName;
                                        LogUtils.e("图片fragment===" + imageName);
                                        LogUtils.e("图片fragment===" + url);
                                        PictureChoseBean bean = new PictureChoseBean();
                                        bean.setUrl(url);
                                        if (dataDTO.isSelected()) {
                                            //获取原来就选中的id,拼接成字符串
                                            if ("".equals(oldIDS)) {
                                                oldIDS = dataDTO.getID() + ",";
                                            } else {
                                                oldIDS = oldIDS + dataDTO.getID() + ",";
                                            }
                                            bean.setOldSelected(dataDTO.isSelected());
                                            bean.setNewSelected(true);
                                        } else {
                                            bean.setOldSelected(dataDTO.isSelected());
                                            bean.setNewSelected(false);
                                        }
                                        bean.setItemID(url);
                                        bean.setPictureID(dataDTO.getID());
                                        mPathList.add(bean);


                                    }
                                    mDataLest.clear();
                                    mDataLest.addAll(mPathList);
                                    LogUtils.e("图片" + "");////原图路径
                                    mAdapter.setData(mDataLest);

                                } else {
                                    mImageEmpty.setVisibility(View.VISIBLE);

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

    /**
     * 获取打印报告图片
     *
     * @param oldImageIDs 请求之后本身选中的图片id  :230,220,245
     * @param newImageIDs 自己新选的图片id       :230,220,245
     */
    private void sendReportRequest(String oldImageIDs, String newImageIDs) {
        LogUtils.e("currentItemID" + currentItemID);
        LogUtils.e("currentItemID" + mBaseUrl + HttpConstant.CaseManager_Report);
        OkHttpUtils.post()
//                .url("192.168.132.102:7001/report/selectImages")
                .url(mBaseUrl + HttpConstant.CaseManager_Report)
                .addParams("CaseID", currentItemID)
                .addParams("oldImageIDs", oldImageIDs)
                .addParams("newImageIDs", newImageIDs)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("图片" + "response==Exception=" + e);////原图路径
                        showError(listener -> {
                            sendReportRequest(oldImageIDs, newImageIDs);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mPathList = new ArrayList<>();
                        LogUtils.e("图片" + "response===" + response);////原图路径
                        if ("" != response) {
                            ReportSelectedImageBean mBean = mGson.fromJson(response, ReportSelectedImageBean.class);
                            int code = mBean.getCode();
                            LogUtils.e("图片" + "response===" + response);////原图路径
                            if (0 == code) {
                                //成功,之后才开启动画显示报告预览
                                if(UDP_HAND_TAG){
                                    showStartReportAnim();
                                }else {
                                    toast("请先建立握手连接");
                                }
                            } else {
                                toast("请求失败");
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


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==PictureChoseActivity==event.getData()==" + event.getData());
        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
        String currentCMD = CalculateUtils.getCMD(event.getData());
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
                    Glide.with(PictureChoseActivity.this)
                            .load(path)
                            .placeholder(R.drawable.ic_bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                            .error(R.drawable.ic_bg_splash_des)
                            .signature(new ObjectKey(System.currentTimeMillis()))//不使用缓存
                            .into(mAnimReport);
                }
                break;
            case Constants.UDP_F2://打印报告
                if ("00".equals(data)) {
                    toast("报告打印成功");
                } else {
                    toast("报告打印失败");
                }
                break;
        }

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        HandBean handBean = new HandBean();
        handBean.setHelloPc("");
        handBean.setComeFrom("");
        byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_HAND);
        if (("".equals(mSocketPort))) {
            toast("通讯端口不能为空");
            return;
        }
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort),this);
//        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
    }


    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (UDP_HAND_TAG) {
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

            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), PictureChoseActivity.this);
        } else {
            sendHandLinkMessage();
            toast("请先建立握手链接");
        }

    }

    /**
     * ***************************************************************************通讯模块**************************************************************************
     */
    /**
     * 关闭获取报告动画
     */
    private void showCloseReportAnim() {
        mAnimRelative.setBackgroundResource(R.color.white);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimRelative, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mAnimRelative, "scaleX", 1f, 0.01f);
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
                mAnimRelative.setVisibility(View.INVISIBLE);
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimRelative, "scaleY", 1f, 0.01f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mAnimRelative, "scaleX", 1f, 0.01f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mAnimRelative, "scaleY", 0.01f, 1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mAnimRelative, "scaleX", 0.01f, 1f);
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
                mAnimRelative.setVisibility(View.VISIBLE);
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
                mAnimRelative.setBackgroundResource(R.color.gray);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //发起预览报告的请求
        sendSocketPointMessage(Constants.UDP_F1);

    }

    public String getIDs(String str) {
        if ("".equals(str)) {
            return str;
        } else {
            String substring = str.substring(0, str.length() - 1);
            return substring;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume===PictureChoseActivity===开始建立握手链接!");
        sendHandLinkMessage();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

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