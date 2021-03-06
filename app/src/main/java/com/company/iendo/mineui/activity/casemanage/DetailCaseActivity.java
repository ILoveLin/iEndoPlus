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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.ReportExistBean;
import com.company.iendo.bean.UserReloBean;
import com.company.iendo.bean.event.RefreshCaseMsgEvent;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.model.LocalDialogCaseModelBean;
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
import com.company.iendo.ui.dialog.CaseModelDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SocketUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.widget.layout.NestedViewPager;
import com.tencent.mmkv.MMKV;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import okhttp3.Call;

/**
 * company???????????????????????????????????????
 * author??? LoveLin
 * time???2021/11/4 13:58
 * desc?????????????????????---???????????????
 */
public class DetailCaseActivity extends AppActivity implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener {
    private NestedViewPager mViewPager;
    private RecyclerView mTabView;
    public static TabAdapter mTabAdapter;
    public static TitleBar mTitlebar;
    private TextView mDelete;
    public static TextView mPicture, mCaseDown;
    private Boolean mFatherExit;   //??????Activity ???????????????????????????,??????????????????????????????fragment???????????????
    private String currentItemID;
    private Boolean FLAG_PICTURE_EXIST = false;  //???????????????????????????????????????
    private String FLAG_PICTURE_URL = "";  //???????????????????????????????????????,??????????????????,??????url????????????
    private RelativeLayout mReportAll;
    private TitleBar mReportBar;
    private AppCompatImageView mReportImageView;
    private static boolean UDP_EQUALS_ID = false; //??????????????????id,?????????????????????id ????????????,?????????????????????????????????,???????????????,
    private boolean isPrinted;   //true,    ?????????????????????,true??????????????????,????????????
    private String mCreatedByWho;
    private String itemUserName;
    private String currentCaseName;
    private String videosCounts;
    private String imageCounts;
    private MessageDialog.Builder existBuilder;
    private TextView mCaseDownVideo;
    private TextView mCurrentCheckPatientInfo;
    private TextView mCurrentSocketStatue;
    public static AppCompatTextView mModelView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_case_detail;
    }

    //????????????????????????
    private void sendImageRequest(String mCaseID) {
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
                            if (0 == mBean.getCode()) {  //??????
                                imageCounts = mBean.getData().getImagesCount() + "";
                                videosCounts = mBean.getData().getVideosCount() + "";
                                DetailCaseActivity.mTabAdapter.setItem(1, "??????(" + imageCounts + ")");
                                DetailCaseActivity.mTabAdapter.setItem(2, "??????(" + videosCounts + ")");

                            } else {
                                toast("????????????");
                            }
                        } else {
                            toast("????????????");

                        }
                    }
                });
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTabView = findViewById(R.id.rv_detail_tab);
        mViewPager = findViewById(R.id.vp_detail_pager);
        //??????view
        mReportAll = findViewById(R.id.relative_anim);
        mReportBar = findViewById(R.id.anim_titlebar);
        mReportImageView = findViewById(R.id.iv_anim_report);
        mTitlebar = findViewById(R.id.titlebar);
        mPicture = findViewById(R.id.case_picture);
        mCaseDown = findViewById(R.id.case_down);
        mCaseDownVideo = findViewById(R.id.case_down_video);
        mDelete = findViewById(R.id.case_delete);
        mModelView = findViewById(R.id.tv_ui_model_confirm);
        //????????????
        mCurrentCheckPatientInfo = findViewById(R.id.current_patient_info);
        mCurrentSocketStatue = findViewById(R.id.current_socket_statue);
        mFatherExit = false;
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DetailFragment.newInstance(), "??????");
        mPagerAdapter.addFragment(PictureFragment.newInstance(), "??????");
        mPagerAdapter.addFragment(VideoFragment.newInstance(), "??????");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        responseListener();


        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);
        //??????????????????
        sendRequest2getModelDialogData();

    }


    private void responseListener() {
        //?????????????????????
        mMMKVInstace.encode(Constants.KEY_Picture_Downing, false);
        sendGetEditStatueRequest();
        setOnClickListener(R.id.linear_get_picture, R.id.linear_get_report, R.id.linear_delete, R.id.linear_down, R.id.linear_down_video);
        mTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                boolean downStatue = mMMKVInstace.decodeBool(Constants.KEY_Picture_Downing, false);
                LogUtils.e("?????????????????????(downStatue),downStatue()==" + downStatue);
                //?????????????????????,????????????????????????
                if (mCaseDown.getText().equals("?????????..") || downStatue) {
                    // ???????????????
                    existBuilder = new MessageDialog.Builder(getActivity());
                    existBuilder.setTitle("????????????")
                            .setMessage("??????????????????????????????,?????????????????????????????????????????????")
                            .setConfirm("??????")
                            // ?????? null ???????????????????????????
                            .setCancel("????????????")
                            .setCanceledOnTouchOutside(false)
                            // ???????????????????????????????????????
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
                    //???????????????????????????????????????
                    if (null != mOnEditStatusListener) {
                        mOnEditStatusListener.onEditStatus(true, true, mModelView);
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


                //???????????????????????????
                //?????????????????????
                boolean KEY_CanEdit = mMMKVInstace.decodeBool(Constants.KEY_CanEdit);
                //?????????????????????,???????????????,??????
                boolean KEY_UnPrinted = mMMKVInstace.decodeBool(Constants.KEY_UnPrinted);
                //????????????????????????,????????????,??????
                boolean KEY_OnlySelf = mMMKVInstace.decodeBool(Constants.KEY_OnlySelf);
                //????????????????????????
                boolean KEY_Printed = isPrinted;
                //????????????????????????????????????
                boolean caseIsSelf = itemUserName.equalsIgnoreCase(mLoginUserName) ? true : false;
                //????????????
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
                    //????????????
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
                    LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
                }
            }
        });

        /**
         * ????????????
         */
        mModelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????????
                if (View.VISIBLE == mModelView.getVisibility()) {
                    //????????????
                    if (null == mTitleList || null == mBeanHashMap || null == mStringHashMap || null == items) {
                        toast("???????????????...???????????????!");
                        return;
                    }
                    new CaseModelDialog.Builder(getActivity(), mTitleList, mBeanHashMap, mStringHashMap, items).setBackgroundDimEnabled(true)
                            .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                            .addOnDismissListener(new BaseDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(BaseDialog dialog) {

                                }
                            })
                            .setListener(new CaseModelDialog.OnListener<String>() {


                                @Override
                                public void onConfirm(LocalDialogCaseModelBean mBean) {
                                    if (null != mBean) {
                                        mOnEditStatusListener.onClickModel(mBean);
                                    } else {
                                        toast("mBean.toString()???null");

                                    }

                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            })
                            .show();

                }


            }
        });

    }

    private void clickEidtListener() {
        if (mTitlebar.getRightTitle().equals("??????")) {
            if (null != mOnEditStatusListener) {
                mOnEditStatusListener.onEditStatus(true, false, mModelView);
            }
            mTitlebar.setRightTitle("??????");
            mTitlebar.setRightTitleColor(getResources().getColor(R.color.red));
        } else {
            mTitlebar.setRightTitle("??????");
            mTitlebar.setRightTitleColor(getResources().getColor(R.color.black));
            if (null != mOnEditStatusListener) {
                mOnEditStatusListener.onEditStatus(false, false, mModelView);
            }
        }
    }


    /**
     * ??????????????????,
     * ??????,???????????????(?????????????????????)  Printed??????
     * ??????,????????????(????????????????????????)  UserName??????
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
     * ????????????????????????
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
     * ????????????????????????
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

        //????????????
//                if (!"".equals(FLAG_PICTURE_URL)){
        //194/Report/??????022021091403.bmp
        String path = "http://" + mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + FLAG_PICTURE_URL;
        Glide.with(DetailCaseActivity.this)
                .load(path)
                .placeholder(R.drawable.ic_bg_splash_des) //????????? ???????????????????????????????????????gif
                .error(R.mipmap.bg_splash_des)
                .signature(new ObjectKey(System.currentTimeMillis()))//???????????????
                .into(mReportImageView);
    }
    /**
     * ***************************************************************************????????????**************************************************************************
     */


    /**
     * eventbus ??????socket??????
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://??????
                mCurrentSocketStatue = findViewById(R.id.current_socket_statue);
                mCurrentSocketStatue.setTextColor(getResources().getColor(R.color.color_25A5FF));
                mCurrentSocketStatue.setText(Constants.SOCKET_STATUE_ONLINE);
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

            case Constants.UDP_CUSTOM_DOWN_OVER://?????????????????????
                if ("true".equals(data)) {//????????????,??????:?????????  ?????????????????????:????????????,???????????????:?????????..
                    mCaseDown.setText("?????????");
                    if (null != existBuilder) {
                        existBuilder.dismiss();
                    }
                } else {
                    mCaseDown.setText("?????????..");
                }
                break;
            case Constants.UDP_CUSTOM_TOAST://??????
                toast("" + data);
                break;
            case Constants.UDP_CUSTOM_FINISH://???????????????,??????????????????
                if (mCaseDown.getText().equals("?????????..")) {
                    //????????????????????????,????????????,?????????????????????
                    return;
                }
                postDelayed(() -> {
                    finish();
                }, 100);
                break;
            case Constants.UDP_CUSTOM14://???????????????---->?????????????????????,?????????????????????,???????????????????????????????????????????????????????????????
                if (Constants.UDP_CUSTOM14.equals(data)) {//??????????????????????????????
                    finish();
                }
                break;
            case Constants.UDP_14://???????????????
                if (data.equals(currentItemID)) {//??????????????????ID??????????????????ID??????,???????????????
                    showExitDialog();
                }
                break;
            case Constants.UDP_F1://????????????
                if ("".equals(data)) {
                    toast("????????????");
                } else {
                    String path = "http://" + mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
//                    String path = mSocketOrLiveIP + ":" + mBaseUrlPort + "/" + data;
                    Glide.with(DetailCaseActivity.this)
                            .load(path)
                            .placeholder(R.drawable.ic_bg_splash_des) //????????? ???????????????????????????????????????gif
                            .error(R.mipmap.bg_splash_des)
                            .signature(new ObjectKey(System.currentTimeMillis()))//???????????????
                            .into(mReportImageView);
                }
                break;
            case Constants.UDP_F2://????????????
                if ("00".equals(data)) {
                    toast("??????????????????");
                } else {
                    toast("??????????????????");
                }
                break;
            case Constants.UDP_F7://??????????????????,???????????????,????????????,???????????????????????????????????????,???????????????????????????????????????
                if (event.getTga()) {
                    requestCurrentPermission();
                }
                break;
        }

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
                        finish();
                    }
                })
                .setOnKeyListener((dialog, event) -> {
                    toast("???????????????" + event.getKeyCode());
                    return false;
                })
                .show();
    }

    /**
     * ?????????????????????,??????????????????
     *
     * @param CMDCode ??????cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (HandService.UDP_HAND_GLOBAL_TAG) {
            ShotPictureBean shotPictureBean = new ShotPictureBean();
            String spCaseID = mMMKVInstace.decodeString(Constants.KEY_CurrentCaseID);
            String s = CalculateUtils.hex10To16Result4(Integer.parseInt(spCaseID));
            shotPictureBean.setRecordid(s);
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(shotPictureBean), mCurrentTypeNum + "", mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("????????????????????????");
                return;
            }

            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), DetailCaseActivity.this);
//            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);

        } else {
            LogUtils.e(Constants.HAVE_HAND_FAIL_OFFLINE);
        }

    }


    /**
     * ***************************************************************************????????????**************************************************************************
     */

    /**
     * ???????????? ????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void RefreshCaseMsgEvent(RefreshCaseMsgEvent event) {
        currentItemID = event.getCaseID();
        sendImageRequest(currentItemID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //??????????????????ID
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

        mTabAdapter.addItem("??????");
        mTabAdapter.addItem("??????");
        mTabAdapter.addItem("??????");
        mTabAdapter.setOnTabListener(this);
        //??????socket????????????????????????
        setSocketStatue(mCurrentSocketStatue);
        sendRequest();


    }


    /**
     * ???????????????????????????????????????
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
                            if (0 == mBean.getCode()) {  //??????
                                if (mBean.getData().isExists()) {
                                    FLAG_PICTURE_EXIST = true;
                                    FLAG_PICTURE_URL = mBean.getData().getUrl();
                                } else {
                                    FLAG_PICTURE_EXIST = false;
                                    FLAG_PICTURE_URL = "";

                                }
                            } else {
                                toast("??????????????????????????????");
                            }
                        } else {
                            toast("??????????????????????????????");

                        }
                    }
                });


    }


    @Override
    public void onClick(View view) {
        if (null != mOnEditStatusListener) {
            switch (view.getId()) {
                case R.id.linear_get_picture://????????????
                    mOnEditStatusListener.onGetPicture();
//                    currentUrl01 = "rtsp://" + username + ":" + password + "@" + ip + ":" + port + "/session0.mpg";  //??????
                    String currentUrl0 = "rtsp://" + mUsername + ":" + mPassword + "@" + mSocketOrLiveIP + ":" + mLivePort + "/session0.mpg";  //??????
                    String currentUrl1 = "rtsp://" + mUsername + ":" + mPassword + "@" + mSocketOrLiveIP + ":" + mLivePort + "/session1.mpg";  //??????
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
                case R.id.linear_get_report://????????????
                    Intent intent = new Intent(this, PictureChoseActivity.class);
                    if (FLAG_PICTURE_EXIST) {  //??????????????????
                        //?????????????????????????????????,????????????????????????
                        showSingleDialog(intent);
                    } else {
                        boolean b = mMMKVInstace.decodeBool(Constants.KEY_CanPrint);
                        if (b) {
                            mOnEditStatusListener.onGetReport();
                            startActivity(intent);
                        } else {
                            toast("?????????????????????????????????");
                        }
                    }
                    break;
                case R.id.linear_delete://??????
                    mOnEditStatusListener.onDelete();
                    break;
                case R.id.linear_down://?????????????????????
                    mOnEditStatusListener.onDown(true, true);
                    break;
                case R.id.linear_down_video://????????????
                    mOnEditStatusListener.onDownVideo();
                    break;
            }
        }
    }

    private void showSingleDialog(Intent intent) {
        // ???????????????
        new SelectDialog.Builder(this)
                .setTitle("??????????????????????????????")
                .setList("????????????", "????????????")
                // ??????????????????
                .setSingleSelect()
                // ??????????????????
                .setSelect(0)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        String substring = data.toString().substring(1, 2);
                        String str = data.get(Integer.parseInt(substring));
                        if ("????????????".equals(str)) {  //????????????????????????,????????????????????????,????????????CanPrint???????????????????????????????????????
                            boolean b = mMMKVInstace.decodeBool(Constants.KEY_CanPrint);
                            if (b) {
                                mOnEditStatusListener.onGetReport();
                                startActivity(intent);
                            } else {
                                toast("?????????????????????????????????");
                            }
                        } else { //??????????????????
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
     * activity ??? fragment????????????= activity??????fragment??????UI??????
     * <p>
     * ??????????????????
     *
     * @return
     * @return
     */

    private OnEditStatusListener mOnEditStatusListener;


    public void setOnEditStatusListener(OnEditStatusListener mOnEditStatusListener) {
        this.mOnEditStatusListener = mOnEditStatusListener;

    }

    public interface OnEditStatusListener {
        //activity?????????????????????,?????????????????????DetailFragment????????????
        void onEditStatus(boolean status, boolean isFatherExit, AppCompatTextView mModelConfirm);

        void onClickModel(LocalDialogCaseModelBean mBean);

        //??????????????????
        void onDown(boolean userInfo, boolean userPicture);

        //????????????
        void onDelete();

        //????????????
        void onGetReport();

        //????????????
        void onGetPicture();

        void onDownVideo();

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


    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // ???????????????????????????
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
        mCaseDown = null;
        mModelView = null;
        mPicture=null;
        mTitlebar=null;
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.setAdapter(null);
        mTabAdapter.setOnTabListener(null);
        mTabAdapter=null;
        EventBus.getDefault().unregister(this);

    }
}
