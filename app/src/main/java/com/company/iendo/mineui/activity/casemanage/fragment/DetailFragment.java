package com.company.iendo.mineui.activity.casemanage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.AddCaseBean;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.DeleteBean;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.DialogItemBean;
import com.company.iendo.bean.ListDialogDateBean;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.green.db.CaseDBUtils;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.green.db.downcase.CaseImageListBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.mineui.socket.SocketManage;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.MenuDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.LinesEditView;
import com.company.iendo.widget.StatusLayout;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class DetailFragment extends TitleBarFragment<MainActivity> implements StatusAction, DetailCaseActivity.OnEditStatusListener {

    private AppCompatTextView mTV;
    private StatusLayout mStatusLayout;
    private Boolean mFirstIn = true;    //第一次进入界面---->解决  首次进来 tosat 提示
    private Boolean mEditStatus = false;    //编辑状态为true,不可编辑状态为flase
    private Boolean isFatherExit = false;   //父类Activity 是否主动退出的标识,主动退出需要请求保存fragment的更新数据
    private DetailCaseActivity mActivity;
    private CaseDetailBean mBean;
    private String mBaseUrl;
    private LinesEditView et_01_i_tell_you, et_01_bad_tell;
    private TextView tv_01_age_type;
    private boolean mFragClickable = false;  //dialog数据请求错误,相对于dialog不允许弹窗,不然会闪退
    private HashMap<String, String> mParamsMap;
    private HashMap mDialogItemMap;
    private ClearEditText et_01_check_num, et_01_name, et_01_sex_type, et_01_age, et_01_jop, et_01_fee, et_01_get_check_doctor;
    //            et_01_i_tell_you, et_01_bad_tell;
    private LinesEditView etlines_02_mirror_see, etlines_02_mirror_result, etlines_02_live_check, etlines_02_cytology, etlines_02_test,
            etlines_02_pathology, etlines_02_advice;
    private ClearEditText et_02_mirror_see, et_02_mirror_result, et_02_live_check, et_02_cytology, et_02_test, et_02_pathology,
            et_02_advice, et_02_check_doctor;
    private ClearEditText et_03_door_num, et_03_protection_num, et_03_section, et_03_device, et_03_case_num, et_03_in_hospital_num,
            et_03_case_area_num, et_03_case_bed_num, et_03_native_place, et_03_ming_zu, et_03_is_married, et_03_tel, et_03_address,
            et_03_my_id_num, et_03_case_history, et_03_family_case_history;
    private ArrayList<ClearEditText> mEditList;
    private ArrayList<ClearEditText> mNotFocusableEditList;   //解决编辑状态点击两次弹窗Bug
    private String mDeviceID;  //当前设备id
    private String mUserID;    //当前用户id
    private String currentItemCaseID;
    private ArrayList<String> ageList;
    private ArrayList<String> mNameList;
    private HashMap<String, String> mPathMap;     //例如imageName=001.jpg  url=http://192.168.64.56:7001/1_3/001.jpg
    private ArrayList<LinesEditView> linesEditViewList;
    private ClearEditText lines_edit_01_i_tell_you;
    private ClearEditText lines_edit_01_i_bad_tell;
    private CaseDetailBean.DataDTO mDataBean;
    private String mUserName;
    private static final int UDP_Hand = 126;   //握手
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UDP_Hand:
                    if ((Boolean) msg.obj) {
                        UDP_HAND_TAG = true;
                    } else {
                        UDP_HAND_TAG = false;
                        sendHandLinkMessage();
                    }
                    break;

            }
        }
    };

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_message;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.detail_hint);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "192.168.132.102");
        mDeviceID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_DeviceID, "");
        mUserID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserID, "");
        mUserName = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserName, "Admin");

        currentItemCaseID = MainActivity.getCurrentItemID();
        initLayoutViewDate();
        setEditStatus();
        responseListener();


        sendRequest(currentItemCaseID);
    }

    private void responseListener() {


        //年纪类别的List数据本地写:岁,月,天,
        setOnClickListener(R.id.et_01_sex_type, R.id.tv_01_age_type, R.id.et_01_jop, R.id.et_01_get_check_doctor,
                R.id.et_02_mirror_see, R.id.et_02_mirror_result, R.id.et_02_live_check, R.id.et_02_cytology, R.id.et_02_test, R.id.et_02_pathology,
                R.id.et_02_advice, R.id.et_02_check_doctor, R.id.et_03_section, R.id.et_03_device, R.id.et_03_ming_zu, R.id.et_03_is_married);


        lines_edit_01_i_tell_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(et_01_i_tell_you, "11");

            }
        });
        lines_edit_01_i_bad_tell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(et_01_bad_tell, "12");

            }
        });

        //02-layout

        et_02_mirror_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_mirror_see, "13");

            }
        });
        et_02_mirror_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_mirror_result, "14");

            }
        });
        et_02_live_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_live_check, "15");

            }
        });
        et_02_cytology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_cytology, "16");

            }
        });
        et_02_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_test, "17");

            }
        });
        et_02_pathology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_pathology, "18");

            }
        });
        et_02_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_advice, "19");

            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //activity和fragment 通信回调
        mActivity = (DetailCaseActivity) getActivity();
        mActivity.setOnEditStatusListener(this);
    }


    private void sendRequest(String currentItemID) {

        //获取图片并且把用户名添加到集合当中,以备下载需要
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CasePictures)
                .addParams("ID", currentItemID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("图片" + "初始化图片路径保存失败===" + e);////原图路径

                        showError(listener -> {
                            sendRequest(currentItemID);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mPathMap = new HashMap<>();
                        if ("" != response) {
                            DetailPictureBean mBean = mGson.fromJson(response, DetailPictureBean.class);
                            List<DetailPictureBean.DataDTO> data = mBean.getData();
                            LogUtils.e("图片" + "response===" + response);////原图路径

                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                if (mBean.getData().size() != 0) {
                                    for (int i = 0; i < mBean.getData().size(); i++) {
                                        String imageName = mBean.getData().get(i).getImagePath();
                                        String url = mBaseUrl + "/" + MainActivity.getCurrentItemID() + "/" + imageName;
                                        LogUtils.e("详情界面---获取到当前图片name===" + imageName);
                                        LogUtils.e("详情界面---获取到当前图片path===" + url);
                                        //例如imageName=001.jpg  url=http://192.168.64.56:7001/3/001.jpg
                                        mPathMap.put(imageName, url);

                                    }
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


        showLoading();
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseInfo)
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
                            mBean = mGson.fromJson(response, CaseDetailBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                setLayoutData(mBean);

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
     * 设置数据
     *
     * @param mBean
     */
    private void setLayoutData(CaseDetailBean mBean) {
        mDataBean = mBean.getData();
        LogUtils.e("病例详情界面数据====" + mDataBean);
        et_01_check_num.setText(mDataBean.getCaseNo());     //检查号也叫病例编号
        et_01_name.setText(mDataBean.getName());
        et_03_is_married.setText("" + mDataBean.getMarried());
        et_01_sex_type.setText("" + mDataBean.getSex());
        et_03_tel.setText("" + mDataBean.getTel());
        et_03_address.setText("" + mDataBean.getAddress());
        //        String PatientNo = et_01_check_num.getText().toString().trim();       //病人编号---检查号???
        et_03_my_id_num.setText("" + mDataBean.getCardID());
        et_03_case_history.setText("" + mDataBean.getMedHistory());
        et_03_family_case_history.setText("" + mDataBean.getFamilyHistory());
        et_03_ming_zu.setText("" + mDataBean.getRace());
        et_01_jop.setText("" + mDataBean.getOccupatior());
        et_03_protection_num.setText("" + mDataBean.getInsuranceID());
        et_03_native_place.setText("" + mDataBean.getNativePlace());
        //        String IsInHospital = et_03_in_hospital_num.getText().toString().trim();       //是否还在医院住院  ???
//        String LastCheckUserID = et_03_tel.getText().toString().trim();       //最后一个来查房的医生  ???
//        String DOB = et_.getText().toString().trim();       //生日                                  ???
        et_01_age.setText("" + mDataBean.getPatientAge());
        tv_01_age_type.setText("" + mDataBean.getAgeUnit());
        //        String ReturnVisit = et_03_tel.getText().toString().trim();       //初复诊 （0-初诊 1-复诊）  ???
        et_03_case_bed_num.setText("" + mDataBean.getBedID());
        et_03_case_area_num.setText("" + mDataBean.getWardID());
        et_03_case_num.setText("" + mDataBean.getCaseID());
        //        String SubmitDoctor = et_03_tel.getText().toString().trim();       //申请医生        ???
        et_03_section.setText("" + mDataBean.getDepartment());
        et_03_device.setText("" + mDataBean.getDevice());
        et_01_fee.setText("" + mDataBean.getFee());
        //        String FeeType = et_03_tel.getText().toString().trim();       //收费类型         ???
        et_01_i_tell_you.setContentText("" + mDataBean.getChiefComplaint());
        etlines_02_test.setContentText("" + mDataBean.getTest());
        etlines_02_advice.setContentText("" + mDataBean.getAdvice());
        et_03_in_hospital_num.setText("" + mDataBean.getInpatientID());
        et_03_door_num.setText("" + mDataBean.getOutpatientID());
        etlines_02_live_check.setContentText("" + mDataBean.getBiopsy());
        etlines_02_cytology.setContentText("" + mDataBean.getCtology());
        etlines_02_pathology.setContentText("" + mDataBean.getPathology());
        etlines_02_live_check.setContentText("" + mDataBean.getExaminingPhysician());
        et_01_bad_tell.setContentText("" + mDataBean.getClinicalDiagnosis());
        etlines_02_mirror_see.setContentText("" + mDataBean.getCheckContent());
        etlines_02_mirror_result.setContentText("" + mDataBean.getCheckDiagnosis());

    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    private void setEditStatus() {
        if (null != mEditList && !mEditList.isEmpty()) {
            for (int i = 0; i < mEditList.size(); i++) {
                if (mEditStatus) {
                    //设置可编辑状态
                    mEditList.get(i).setFocusableInTouchMode(true);
                    mEditList.get(i).setFocusable(true);
                    mEditList.get(i).requestFocus();
                    //android:focusable="false"
                    //谈对话框的不能获取焦点
                } else {
                    //设置不可编辑状态
                    mEditList.get(i).setFocusable(false);
                    mEditList.get(i).setFocusableInTouchMode(false);

                }
            }


            if (mEditStatus) {
                for (int i = 0; i < mNotFocusableEditList.size(); i++) {
                    mNotFocusableEditList.get(i).setFocusableInTouchMode(false);
                    mNotFocusableEditList.get(i).setFocusable(false);
                }
            }
        }
        if (!mEditStatus) {//切换到了不可编辑模式,发送请求
            if (mFirstIn) {  //解决  首次进来 tosat 提示
                mFirstIn = false;
            } else {
                checkDataAndRequest();

            }
        }
        if (isFatherExit) {//父类界面主动退出,保存当前数据
            checkDataAndRequest();
        }

    }

    /**
     * EditText 和  弹窗是否可以用的标识
     *
     * @param status
     */
    @Override
    public void onEditStatus(boolean status, boolean isFatherExit) {
        this.mEditStatus = status;
        this.isFatherExit = isFatherExit;
        setEditStatus();
    }

    @Override
    public void onDown(boolean userInfo, boolean userPicture) {
        new SelectDialog.Builder(getActivity())
                .setTitle("信息下载")
                .setSelect(0, 1)
                .setMinSelect(2)
                .setList("用户信息", "图片信息")
                .setListener(new SelectDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, HashMap data) {
                        String string = data.toString();
                        LogUtils.e("下载===" + data.toString());
                        int size = data.size();
                        LogUtils.e("下载===size=" + size);
                        if (size == 2) {//下载用户信息和图片信息
//                            https://images.csdn.net/20150817/1.jpg
                            //下载图片
                            startDownPicture();

                        } else {//筛选下载哪种信息
                            int i = string.indexOf("=");
                            String value = string.substring(i + 1, string.length() - 1);
                            LogUtils.e("下载===value=" + value);
                            if (value.equals("用户信息")) {  //下载用户信息

                            } else if (value.equals("图片信息")) {//下载图片信息

                            }

                        }


                    }
                }).show();

    }

    private void startDownPicture() {

//      Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());
//      String path = "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath();
//      https://images.csdn.net/20150817/1.jpg

        /**
         * 本地文件夹命名规则:文件夹（设备ID-病例ID）
         */
        //创建本地的/MyData/Images/mID文件夹  再把图片下载到这个文件夹下  文件夹（设备ID-病例ID）
        String dirName = Environment.getExternalStorageDirectory() + "/MyDownImages/" + mDeviceID + "_" + currentItemCaseID;
        LogUtils.e("文件下载=====文件夹名字===" + dirName);
        File toLocalFile = new File(dirName);
        if (!toLocalFile.exists()) {
            toLocalFile.mkdir();
        }
        LogUtils.e("详情界面---mPathMap===" + mPathMap.isEmpty());
        if (null != mPathMap && !mPathMap.isEmpty()) {   //说明有图片
            for (String key : mPathMap.keySet()) {
                LogUtils.e("文件下载=====entry.getKey()===" + key);
                LogUtils.e("文件下载=====entry.getValue()===" + mPathMap.get(key));
                int size = mPathMap.keySet().size();
                sendPictureRequest(toLocalFile, mPathMap.get(key), key, false);
            }

        }
        //下载病例和图片信息---到本地sd卡里面
        downLocalCaseData(toLocalFile);

    }

    /**
     * 下载病例和图片信息---到本地sd卡里面
     * 创建一个CaseDBBean,直接存信息和图片信息即可
     */
    private void downLocalCaseData(File toLocalFile) {

        CaseDBBean caseDBBean = new CaseDBBean();
        caseDBBean.setDeviceCaseID(mDeviceID + "");  //用户表和设备表进行绑定, //用户表和设备表进行绑定, //用户表和设备表进行绑定
        caseDBBean.setOccupatior(mDataBean.getOccupatior() + "");// 职业
        caseDBBean.setNativePlace(mDataBean.getAddress() + "");    //籍贯
        caseDBBean.setFee(mDataBean.getFee() + "");    //收费
        caseDBBean.setChiefComplaint(mDataBean.getChiefComplaint() + "");  //主诉
        //图片路径集合--文件夹（设备ID-病例ID）
//      /storage/emulated/0/MyDownImages/2_3/004.jpg
//      LogUtils.e("下载图片==更新相册图片==" + toLocalFile.getAbsolutePath() + "/" + pictureName);       本地存的路径
//     toLocalFile.getAbsolutePath()==/storage/emulated/0/MyDownImages/2_3         pictureName==004.jpg
        ArrayList<CaseImageListBean> caseImageList = new ArrayList<CaseImageListBean>();
        if (null != mPathMap && !mPathMap.isEmpty()) {
            for (String key : mPathMap.keySet()) {
                LogUtils.e("文件下载===存储图片==entry.getKey()===" + key);
                LogUtils.e("文件下载===存储图片==entry.getValue()===" + mPathMap.get(key));
                int size = mPathMap.keySet().size();
//                sendPictureRequest(toLocalFile, mPathMap.get(key), key, false);
                CaseImageListBean imageBean = new CaseImageListBean();
                imageBean.setImagePath(toLocalFile.getAbsolutePath() + "/" + key);  //存入本地存储路径
                caseImageList.add(imageBean);
            }
            caseDBBean.setImageList(caseImageList);    //图片路径集合--文件夹（设备ID-病例ID）
        }
        caseDBBean.setBiopsy(mDataBean.getBiopsy() + "");    //活检
        caseDBBean.setPathology(mDataBean.getPathology() + "");    //病理学
        caseDBBean.setFeeType(mDataBean.getFeeType() + "");    //收费类型
        caseDBBean.setMedHistory(mDataBean.getMedHistory() + "");    // 医疗病史
        caseDBBean.setLastCheckUserID(mDataBean.getLastCheckUserID() + "");    // 最后一个来查房的医生
        caseDBBean.setAgeUnit(mDataBean.getAgeUnit() + "");    // 年龄单位
        caseDBBean.setAdvice(mDataBean.getAdvice() + "");    // 建议
        caseDBBean.setUserName(mDataBean.getUserName() + "");    // 操作员用户名
        caseDBBean.setRecord_date(mDataBean.getRecord_date() + "");    // 创建时间
        caseDBBean.setImagesCount(mDataBean.getImageCount() + "");    // 图片数量
//        caseDBBean.setVideosCount(mDataBean.getVid() + "");    // 视频数量
        caseDBBean.setSubmitDoctor(mDataBean.getSubmitDoctor() + "");    // 申请医生
        caseDBBean.setRace(mDataBean.getRace() + "");    // 民族种族
        caseDBBean.setRecordType(mDataBean.getRecordType() + "");    // 病例类型
        caseDBBean.setUpdate_time(mDataBean.getUpdate_time() + "");    // 更新时间
        caseDBBean.setPatientAge(mDataBean.getPatientAge() + "");    // 患者年龄
        caseDBBean.setCardID(mDataBean.getCardID() + "");    // 身份证号
        caseDBBean.setTel(mDataBean.getTel() + "");    // 电话
        caseDBBean.setCheck_date(mDataBean.getCheck_date() + "");    // 检查时间
        caseDBBean.setPatientNo(mDataBean.getPatientNo() + "");    // 病人编号
        caseDBBean.setInpatientID(mDataBean.getInpatientID() + "");    // 住院号
        caseDBBean.setBedID(mDataBean.getBedID() + "");    // 病床号
        caseDBBean.setCheckContent(mDataBean.getCheckContent() + "");    // 检查内容（镜检所见）
        caseDBBean.setReturnVisit(mDataBean.isReturnVisit() + "");    // 初复诊 (0-初诊 1-复诊)
        caseDBBean.setCaseNo(mDataBean.getCaseNo() + "");    // 病例编号
        caseDBBean.setCtology(mDataBean.getCtology() + "");    // 细胞学
        caseDBBean.setDOB(mDataBean.getDOB() + "");    // 生日
        caseDBBean.setExaminingPhysician(mDataBean.getExaminingPhysician() + "");    // 检查医生
        caseDBBean.setCheckDiagnosis(mDataBean.getCheckDiagnosis() + "");    // 镜检诊断
        caseDBBean.setSex(mDataBean.getSex() + "");    // 性别
        caseDBBean.setEndoType(mDataBean.getEndoType() + "");    // 工作站类型
        caseDBBean.setDevice(mDataBean.getDevice() + "");    // 设备
        caseDBBean.setIsInHospital(mDataBean.isIsInHospital() + "");    // 是否还在医院住院
        caseDBBean.setMarried(mDataBean.getMarried() + "");    // 婚否
        caseDBBean.setFamilyHistory(mDataBean.getFamilyHistory() + "");    // 家族病史
        caseDBBean.setTest(mDataBean.getTest() + "");    // 试验
        caseDBBean.setClinicalDiagnosis(mDataBean.getClinicalDiagnosis() + "");    // 临床诊断
        caseDBBean.setDepartment(mDataBean.getDepartment() + "");    // 科室
        caseDBBean.setWardID(mDataBean.getWardID() + "");    // 病区号
        caseDBBean.setCaseID(mDataBean.getCaseID() + "");    // 病例号
        caseDBBean.setName(mDataBean.getName() + "");    // 姓名
        caseDBBean.setAddress(mDataBean.getAddress() + "");    // 住址
        caseDBBean.setInsuranceID(mDataBean.getInsuranceID() + "");    // 社保卡号
        CaseDBUtils.insertOrReplaceInTx(getActivity(), caseDBBean);


    }

    private void sendPictureRequest(File toLocalFile, String path, String pictureName, Boolean lastPicture) {
//        String url = "http://images.csdn.net/20150817/1.jpg";
        if (!toLocalFile.exists()) {
            toLocalFile.mkdir();
        }
        OkHttpUtils.get()
                .url(path)
                .build()
                .execute(new FileCallBack(toLocalFile.getAbsolutePath(), pictureName) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("下载图片==onError==" + e);
                        //下载失败
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtils.e("下载图片==onResponse==" + response.toString());
                        LogUtils.e("下载图片==更新相册图片==" + toLocalFile.getAbsolutePath() + "/" + pictureName);
                        //=====/storage/emulated/0/MyDownImages/2_3/004.jpg
                        //刷新相册
                        try {
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), toLocalFile.getAbsolutePath() + "/" + pictureName, pictureName, "");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onDelete() {
        new MessageDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("确认删除该用户吗?")
                .setConfirm("确定")
                .setCancel("取消")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        sendDeleteRequest();
                    }
                }).show();
    }


    @Override
    public void onGetReport() {


    }

    @Override
    public void onGetPicture() {

    }

    //删除用户请求
    private void sendDeleteRequest() {
        LogUtils.e("删除用户==params=" + mBean.getData().getID() + "");
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.CaseManager_DeleteCase)
                .addParams("ID", mBean.getData().getID() + "")
                .addParams("UserName", mUserName)
                .addParams("EndoType", endoType)
                .addParams("UserID", mUserID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError(listener -> {
                            sendDeleteRequest();
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e("删除用户===" + response);
                        if ("" != response) {
                            DeleteBean mBean = mGson.fromJson(response, DeleteBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                sendSocketPointMessage(Constants.UDP_14);
                                mActivity.finish();

                            } else {
                                showError(listener -> {
                                    sendDeleteRequest();
                                });
                            }
                        } else {
                            showError(listener -> {
                                sendDeleteRequest();
                            });
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_01_sex_type:  //性别
                showMenuDialog(et_01_sex_type, "100");
                break;
            case R.id.tv_01_age_type:  //年龄类别  --本地写数据List
                showMenuElseDialog();
                break;
            case R.id.et_01_jop:        //职业
                showMenuDialog(et_01_jop, "5");
                break;
            case R.id.et_01_get_check_doctor://送检医生
                showMenuDialog(et_01_get_check_doctor, "8");
                break;
            case R.id.et_01_i_tell_you:    //主诉--带字数限制的
//                showITellyouMenuDialog(et_01_i_tell_you, "11");
                break;
            case R.id.et_01_bad_tell:     //临床诊断--带字数限制的
//                showITellyouMenuDialog(et_01_bad_tell, "12");
                break;
//            case R.id.et_02_mirror_see:   //镜检所见
//                showMenuDialog(etlines_02_mirror_see, "13");
//                break;
//            case R.id.et_02_mirror_result://镜检诊断
//                showMenuDialog(etlines_02_mirror_result, "14");
//                break;
//            case R.id.et_02_live_check://活检
//                showMenuDialog(etlines_02_live_check, "15");
//                break;
//            case R.id.et_02_cytology://细胞学
//                showMenuDialog(etlines_02_cytology, "16");
//                break;
//            case R.id.et_02_test://试验
//                showMenuDialog(etlines_02_test, "17");
//                break;
//            case R.id.et_02_pathology://病理学
//                showMenuDialog(etlines_02_pathology, "18");
//                break;
//            case R.id.et_02_advice://建议
//                showMenuDialog(etlines_02_advice, "19");
//                break;
            case R.id.et_02_check_doctor://检查医生
                showMenuDialog(et_02_check_doctor, "20");
                break;
            case R.id.et_03_section: //科室
                showMenuDialog(et_03_section, "9");
                break;
            case R.id.et_03_device://设备
                showMenuDialog(et_03_device, "10");
                break;
            case R.id.et_03_ming_zu://民族
                showMenuDialog(et_03_ming_zu, "23");
                break;
            case R.id.et_03_is_married://婚否
                showMenuDialog(et_03_is_married, "101");
                break;

        }
    }

    private void showMenuElseDialog() {
        // 底部选择框
        if (mFragClickable && mEditStatus) {//有dialog数据,集合不为空,可编辑状态
            new MenuDialog.Builder(getActivity())
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(ageList)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String string) {
                            tv_01_age_type.setText("" + ageList.get(position));
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                        }
                    })
                    .show();
        }

    }

    private void showITellyouMenuDialog(LinesEditView mEdit, String key) {
        if (mFragClickable && null != mDialogItemMap && mEditStatus) {//有dialog数据,集合不为空,可编辑状态
            ArrayList<DialogItemBean> mDataList = (ArrayList<DialogItemBean>) mDialogItemMap.get(key);
            ArrayList<String> stringList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                stringList.add(mDataList.get(i).getDictItem());
            }
            // 底部选择框
            new MenuDialog.Builder(getActivity())
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(stringList)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String string) {
                            String s = stringList.get(position);
                            mEdit.setContentText(mEdit.getContentText() + "" + s);

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();
        }
    }

    private void showMenuDialog(ClearEditText mEdit, String key) {
        if (mFragClickable && null != mDialogItemMap && mEditStatus) {//有dialog数据,集合不为空,可编辑状态
            ArrayList<DialogItemBean> mDataList = (ArrayList<DialogItemBean>) mDialogItemMap.get(key);
            ArrayList<String> stringList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                stringList.add(mDataList.get(i).getDictItem());
            }
            // 底部选择框
            new MenuDialog.Builder(getActivity())
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(stringList)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String data) {
                            String s = stringList.get(position);
                            LogUtils.e("MenuDialog====位置：" + position + "，文本：" + data);
                            LogUtils.e("MenuDialog===s==" + s); //{0=HD3}
                            mEdit.setText("" + s);

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                        }
                    })
                    .show();
        }

    }


    /**
     * 获取需要Dialog选择数据的集合
     */
    private void sendListDictsRequest() {
        //获取Dialog item的数据
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseDialogDate)
                .addParams("EndoType", endoType)
//                .addParams("EndoType", "4")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("edit====" + e);
                        mFragClickable = false;
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.e("edit==onResponse==" + response);
                        ListDialogDateBean mBean = mGson.fromJson(response, ListDialogDateBean.class);
                        //1,按'DictName'进行分组
                        //2,按照分组中找出 'ParentId=0' 的项,作为每个分组的类别
                        List<ListDialogDateBean.DataDTO.ListDictsDTO> listDicts = mBean.getData().getListDicts();
                        //创建一个map  key是 DictName,value是list
                        mDialogItemMap = new HashMap<String, ArrayList<DialogItemBean>>();
                        for (int i = 0; i < listDicts.size(); i++) {
                            //获取每条数据的dictname
                            String currentDictName = listDicts.get(i).getDictName();  //100
                            //再次遍历这个集合,和currentDictName  相同的bean全部存入集合中
                            ArrayList<DialogItemBean> itemBeanList = new ArrayList<>();
                            for (int j = 0; j < listDicts.size(); j++) {
                                ListDialogDateBean.DataDTO.ListDictsDTO listDictsDTO = listDicts.get(j);
                                String dictName = listDictsDTO.getDictName();
                                if (currentDictName.equals(dictName)) {
                                    DialogItemBean itemBean = new DialogItemBean();
                                    itemBean.setID(listDictsDTO.getID());
                                    itemBean.setParentId(listDictsDTO.getParentId());
                                    itemBean.setDictItem(listDictsDTO.getDictItem());
                                    itemBean.setEndoType(listDictsDTO.getEndoType());
                                    itemBeanList.add(itemBean);
                                }
                            }
                            if (!itemBeanList.isEmpty()) {
                                boolean currentDictName1 = mDialogItemMap.containsKey(currentDictName);
                                if (!currentDictName1) {
                                    mDialogItemMap.put(currentDictName, itemBeanList);
                                }
                            }
                            mFragClickable = true;

                        }
//
//                        Iterator<Map.Entry<String, ArrayList<DialogItemBean>>> entries = mDialogItemMap.entrySet().iterator();
//                        while (entries.hasNext()) {
//                            Map.Entry<String, ArrayList<DialogItemBean>> entry = entries.next();
//                            String key = entry.getKey();
//                            ArrayList<DialogItemBean> value = entry.getValue();
//                            LogUtils.e("对话框数据====key====" + key);
//
//                            for (int i = 0; i < value.size(); i++) {
//                                DialogItemBean bean = value.get(i);
//                                LogUtils.e("对话框数据====value====" + bean.getDictItem());
//                            }
//                        }
                    }
                });

    }


    private void initLayoutViewDate() {

        ageList = new ArrayList<>();
        ageList.add("岁");
        ageList.add("月");
        ageList.add("天");
        /**
         * 获取基本信息id
         */
        //检查号
        et_01_check_num = findViewById(R.id.et_01_check_num);
        //姓名
        et_01_name = findViewById(R.id.et_01_name);
        //性别
        et_01_sex_type = findViewById(R.id.et_01_sex_type);
        //年龄
        et_01_age = findViewById(R.id.et_01_age);
        //年龄类别-弹窗选择
        tv_01_age_type = findViewById(R.id.tv_01_age_type);
        //职业
        et_01_jop = findViewById(R.id.et_01_jop);
        //职业
        et_01_fee = findViewById(R.id.et_01_fee);
        //送检医生
        et_01_get_check_doctor = findViewById(R.id.et_01_get_check_doctor);
        //主诉
        et_01_i_tell_you = findViewById(R.id.et_01_i_tell_you);
        //临床诊断
        et_01_bad_tell = findViewById(R.id.et_01_bad_tell);

        //主诉---多行显示的edit
        lines_edit_01_i_tell_you = et_01_i_tell_you.getContentEdit();
        //临床诊断---多行显示的edit
        lines_edit_01_i_bad_tell = et_01_bad_tell.getContentEdit();
        /**
         *获取镜信息id
         */
        //镜检所见
        etlines_02_mirror_see = findViewById(R.id.et_02_mirror_see);
        //镜检诊断
        etlines_02_mirror_result = findViewById(R.id.et_02_mirror_result);
        //活检
        etlines_02_live_check = findViewById(R.id.et_02_live_check);
        //细胞学
        etlines_02_cytology = findViewById(R.id.et_02_cytology);
        //试验
        etlines_02_test = findViewById(R.id.et_02_test);
        //病理学
        etlines_02_pathology = findViewById(R.id.et_02_pathology);
        //建议
        etlines_02_advice = findViewById(R.id.et_02_advice);
        //检查医生
        et_02_check_doctor = findViewById(R.id.et_02_check_doctor);

        et_02_mirror_see = etlines_02_mirror_see.getContentEdit();
        et_02_mirror_result = etlines_02_mirror_result.getContentEdit();
        et_02_live_check = etlines_02_live_check.getContentEdit();
        et_02_cytology = etlines_02_cytology.getContentEdit();
        et_02_test = etlines_02_test.getContentEdit();
        et_02_pathology = etlines_02_pathology.getContentEdit();
        et_02_advice = etlines_02_advice.getContentEdit();


        /**
         * 获取其他信息id
         */
        //门诊号
        et_03_door_num = findViewById(R.id.et_03_door_num);
        //医保号
        et_03_protection_num = findViewById(R.id.et_03_protection_num);
        //科室
        et_03_section = findViewById(R.id.et_03_section);
        //设备
        et_03_device = findViewById(R.id.et_03_device);
        //病例号
        et_03_case_num = findViewById(R.id.et_03_case_num);
        //住院号
        et_03_in_hospital_num = findViewById(R.id.et_03_in_hospital_num);
        //病区号
        et_03_case_area_num = findViewById(R.id.et_03_case_area_num);
        //病床号
        et_03_case_bed_num = findViewById(R.id.et_03_case_bed_num);
        //籍贯
        et_03_native_place = findViewById(R.id.et_03_native_place);
        //民族
        et_03_ming_zu = findViewById(R.id.et_03_ming_zu);
        //婚否
        et_03_is_married = findViewById(R.id.et_03_is_married);
        //电话
        et_03_tel = findViewById(R.id.et_03_tel);
        //住址
        et_03_address = findViewById(R.id.et_03_address);
        //身份证
        et_03_my_id_num = findViewById(R.id.et_03_my_id_num);
        //病史
        et_03_case_history = findViewById(R.id.et_03_case_history);
        //家族病史
        et_03_family_case_history = findViewById(R.id.et_03_family_case_history);

        mEditList = new ArrayList<>();
        mNotFocusableEditList = new ArrayList<>();   //不能获取焦点的edit

        mEditList.add(et_01_sex_type);
        mEditList.add(et_01_age);//et_01_age
        mEditList.add(et_01_jop);
        mEditList.add(et_01_fee);
        mEditList.add(et_01_get_check_doctor);

//        mEditList.add(et_01_i_tell_you);
//        mEditList.add(et_01_bad_tell);
        mEditList.add(et_02_mirror_see);
        mEditList.add(et_02_mirror_result);
        mEditList.add(et_02_live_check);
        mEditList.add(et_02_cytology);
        mEditList.add(et_02_test);
        mEditList.add(et_02_pathology);
        mEditList.add(et_02_advice);


        mEditList.add(et_02_check_doctor);
        mEditList.add(et_03_door_num);
        mEditList.add(et_03_protection_num);
        mEditList.add(et_03_section);
        mEditList.add(et_03_device);
        mEditList.add(et_03_case_num);
        mEditList.add(et_03_in_hospital_num);
        mEditList.add(et_03_case_area_num);
        mEditList.add(et_03_case_bed_num);
        mEditList.add(et_03_native_place);
        mEditList.add(et_03_ming_zu);
        mEditList.add(et_03_is_married);
        mEditList.add(et_03_tel);
        mEditList.add(et_03_address);
        mEditList.add(et_03_my_id_num);
        mEditList.add(et_03_case_history);
        mEditList.add(et_03_family_case_history);
        mEditList.add(et_01_check_num);
        mEditList.add(et_01_name);

        mNotFocusableEditList.add(lines_edit_01_i_tell_you);
        mNotFocusableEditList.add(lines_edit_01_i_bad_tell);
        mNotFocusableEditList.add(et_01_sex_type);
        mNotFocusableEditList.add(et_01_jop);
        mNotFocusableEditList.add(et_01_get_check_doctor);

        mNotFocusableEditList.add(et_02_mirror_see);
        mNotFocusableEditList.add(et_02_mirror_result);
        mNotFocusableEditList.add(et_02_live_check);
        mNotFocusableEditList.add(et_02_cytology);
        mNotFocusableEditList.add(et_02_pathology);
        mNotFocusableEditList.add(et_02_test);
        mNotFocusableEditList.add(et_02_advice);

        mNotFocusableEditList.add(et_02_check_doctor);
        mNotFocusableEditList.add(et_03_section);
        mNotFocusableEditList.add(et_03_device);
        mNotFocusableEditList.add(et_03_ming_zu);
        mNotFocusableEditList.add(et_03_is_married);
        mNotFocusableEditList.add(et_03_section);
        mNotFocusableEditList.add(et_03_section);
        mNotFocusableEditList.add(et_03_section);
        mNotFocusableEditList.add(et_03_section);

        linesEditViewList = new ArrayList<>();
        linesEditViewList.add(et_01_i_tell_you);
        linesEditViewList.add(et_01_bad_tell);


    }


    /**
     * 切换到不可编辑状态下-->就去修改数据
     */


    private void checkDataAndRequest() {
        String Name = et_01_name.getText().toString().trim();
        if (!Name.isEmpty()) {
            getElseCanSelected();
        } else {
            toast("用户名不能为空!");
        }
    }


    /**
     * 获取其他可选参数
     */
    private void getElseCanSelected() {
        mParamsMap = new HashMap<>();
        String Married = et_03_is_married.getText().toString().trim();       //婚否 （已婚，未婚）
        if (!"其他".equals(Married)) {
            mParamsMap.put("Married", Married);
        }
        String Sex = et_01_sex_type.getText().toString().trim();       //性别 （男，女）
        if (!"性别".equals(Married)) {
            mParamsMap.put("Sex", Sex);
        }

        String Tel = et_03_tel.getText().toString().trim();       //电话
        String Address = et_03_address.getText().toString().trim();       //住址
//        String PatientNo = et_01_check_num.getText().toString().trim();       //病人编号---检查号???
        String CardID = et_03_my_id_num.getText().toString().trim();       //身份证号
        String MedHistory = et_03_case_history.getText().toString().trim();       //医疗病史
        String FamilyHistory = et_03_family_case_history.getText().toString().trim();       //家族病史
        String Race = et_03_ming_zu.getText().toString().trim();       //民族种族
        if (!"民族".equals(Race)) {
            mParamsMap.put("Race", Race);
        }
        String Occupatior = et_01_jop.getText().toString().trim();       //职业
        if (!"职业".equals(Occupatior)) {
            mParamsMap.put("Occupatior", Occupatior);
        }
        String InsuranceID = et_03_protection_num.getText().toString().trim();       //社保卡ID
        String NativePlace = et_03_native_place.getText().toString().trim();       //籍贯
//        String IsInHospital = et_03_in_hospital_num.getText().toString().trim();       //是否还在医院住院  ???
//        String LastCheckUserID = et_03_tel.getText().toString().trim();       //最后一个来查房的医生  ???
//        String DOB = et_.getText().toString().trim();       //生日                                  ???
        String PatientAge = et_01_age.getText().toString().trim();       //患者年龄
        String AgeUnit = tv_01_age_type.getText().toString().trim();       //年龄单位 （岁，月，天）
//        String ReturnVisit = et_03_tel.getText().toString().trim();       //初复诊 （0-初诊 1-复诊）  ???
        String BedID = et_03_case_bed_num.getText().toString().trim();       //病床号
        String WardID = et_03_case_area_num.getText().toString().trim();       //病区号
        String CaseID = et_03_case_num.getText().toString().trim();       //病历号
//        String SubmitDoctor = et_03_tel.getText().toString().trim();       //申请医生        ???
        String Department = et_03_section.getText().toString().trim();       //科室

        if (!"科室".equals(Department)) {
            mParamsMap.put("Department", Department);
        }
        String Device = et_03_device.getText().toString().trim();       //设备

        if (!"设备".equals(Device)) {
            mParamsMap.put("Device", Device);
        }
        String Fee = et_01_fee.getText().toString().trim();       //收费
//        String FeeType = et_03_tel.getText().toString().trim();       //收费类型         ???
        String ChiefComplaint = et_01_i_tell_you.getContentText().toString().trim();       //主诉
        String Test = etlines_02_test.getContentText().toString().trim();       //试验
        String Advice = etlines_02_advice.getContentText().toString().trim();       //建议
        String InpatientID = et_03_in_hospital_num.getText().toString().trim();       //住院号
        String OutpatientID = et_03_door_num.getText().toString().trim();       //门诊号
        String Biopsy = etlines_02_live_check.getContentText().toString().trim();       //活检
        String Ctology = etlines_02_cytology.getContentText().toString().trim();       //细胞学
        String Pathology = etlines_02_pathology.getContentText().toString().trim();       //病理学
        String ExaminingPhysician = etlines_02_live_check.getContentText().toString().trim();       //检查医生
        String ClinicalDiagnosis = et_01_bad_tell.getContentText().toString().trim();       //临床诊断
        String CheckContent = etlines_02_mirror_see.getContentText().toString().trim();       //检查内容（镜检所见）
        String CheckDiagnosis = etlines_02_mirror_result.getContentText().toString().trim();       //镜检诊断


        //添加三个必须添加的参数
        String UserName = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserName, "Admin");
        String EndoType = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_EndoType, "3");
        mParamsMap.put("ID", MainActivity.getCurrentItemID());
        mParamsMap.put("Name", et_01_name.getText().toString().trim());
        mParamsMap.put("CaseNo", et_01_check_num.getText().toString().trim());
        mParamsMap.put("UserName", UserName);
        mParamsMap.put("EndoType", EndoType);
        mParamsMap.put("UserID", mUserID);
        mParamsMap.put("Tel", Tel);
        mParamsMap.put("Address", Address);
        mParamsMap.put("CardID", CardID);
        mParamsMap.put("MedHistory", MedHistory);
        mParamsMap.put("FamilyHistory", FamilyHistory);
        mParamsMap.put("Race", Race);
        mParamsMap.put("InsuranceID", InsuranceID);
        mParamsMap.put("NativePlace", NativePlace);
        mParamsMap.put("PatientAge", PatientAge);
        mParamsMap.put("AgeUnit", AgeUnit);
        mParamsMap.put("BedID", BedID);
        mParamsMap.put("WardID", WardID);
        mParamsMap.put("CaseID", CaseID);
        mParamsMap.put("Department", Department);
        mParamsMap.put("Fee", Fee);
        mParamsMap.put("ChiefComplaint", ChiefComplaint);
        mParamsMap.put("Test", Test);
        mParamsMap.put("Advice", Advice);
        mParamsMap.put("InpatientID", InpatientID);
        mParamsMap.put("OutpatientID", OutpatientID);
        mParamsMap.put("Biopsy", Biopsy);
        mParamsMap.put("Ctology", Ctology);
        mParamsMap.put("Pathology", Pathology);
        mParamsMap.put("ExaminingPhysician", ExaminingPhysician);
        mParamsMap.put("ClinicalDiagnosis", ClinicalDiagnosis);
        mParamsMap.put("CheckContent", CheckContent);
        mParamsMap.put("CheckDiagnosis", CheckDiagnosis);
        sendUpdateRequest();


    }

    /**
     * 发送更新病例请求
     *
     * @param
     */
    private void sendUpdateRequest() {
        LogUtils.e("发送更新病例请求=== mParamsMap.toString()===" + mParamsMap.toString());
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.CaseManager_ChangeCase)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("发送更新病例请求===onError===" + e);
                        showError(listener -> {
//                            sendRequest(mParamsMap);
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            LogUtils.e("发送更新病例请求===onResponse===" + response);

                            AddCaseBean mBean = mGson.fromJson(response, AddCaseBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                toast("保存成功!");
                                //socket告知上位机更新病例
                                sendSocketPointMessage(Constants.UDP_13);
                                ActivityManager.getInstance().finishActivity(AddCaseActivity.class);

                            } else {
                                showError(listener -> {

                                });
                            }
                        } else {
                            showError(listener -> {
                            });
                        }
                    }
                });


    }

    /*******************************************************************UDP通讯模块*****************************************************************************/
    private static DatagramSocket mReceiveSocket = null;
    private volatile static boolean isRuning = true;


    /**
     * 开启消息接收线程
     */
    private void initReceiveThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
                byte[] receiveData = new byte[1024];
                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    if (mReceiveSocket == null) {
                        mReceiveSocket = new DatagramSocket(null);
                        mReceiveSocket.setReuseAddress(true);
                        mReceiveSocket.bind(new InetSocketAddress(Constants.RECEIVE_PORT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (isRuning) {
                        try {
                            LogUtils.e("======DetailFragment=====000==");
                            LogUtils.e("======DetailFragment=====mReceivePacket.getAddress()==" + mReceivePacket.getAddress());
                            LogUtils.e("======DetailFragment=====mReceivePacket.getData()==" + mReceivePacket.getAddress());
                            LogUtils.e("======DetailFragment=====currentIP==" + currentIP);
                            if (!currentIP.equals(mReceivePacket.getAddress())) {   //不是自己的IP不接受
                                mReceiveSocket.receive(mReceivePacket);
                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
//                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
                                //过滤不是发送给我的消息全部不接受
                                int length = mReceivePacket.getLength() * 2;
                                String resultData = rec.substring(0, length);
                                LogUtils.e("======DetailFragment=====获取长度==length==" + length);
                                LogUtils.e("======DetailFragment=====获取长度数据==substring==" + resultData);
                                LogUtils.e("======DetailFragment=====接受到数据==原始数据====mReceivePacket.getData()=" + mReceivePacket.getData());
                                LogUtils.e("======DetailFragment=====3333==" + mReceivePacket.getData());
                                if (mReceivePacket != null) {
                                    LogUtils.e("======DetailFragment=====66666==");
                                    boolean flag = false;//是否可用的ip---此ip是服务器ip
                                    String finalOkIp = "";
                                    if (CalculateUtils.getDataIfForMe(resultData, getAttachActivity())) {
                                        finalOkIp = CalculateUtils.getOkIp(mReceivePacket.getAddress().toString());
                                        flag = true;//正确的服务器ip地址
                                    }
                                    //正确的服务器ip地址,才开始计算获取自己需要的数据
                                    if (flag) {
                                        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(resultData);//随机数之后到data结尾的String
                                        String deviceType = CalculateUtils.getSendDeviceType(resultData);
                                        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(resultData);
                                        String currentCMD = CalculateUtils.getCMD(resultData);
                                        LogUtils.e("======DetailFragment==回调===随机数之后到data结尾的String=mRun2End4==" + mRun2End4);
                                        LogUtils.e("======DetailFragment==回调===设备类型deviceType==" + deviceType);
                                        LogUtils.e("======DetailFragment==回调===设备ID=deviceOnlyCode==" + deviceOnlyCode);
                                        LogUtils.e("======DetailFragment==回调===CMD=currentCMD==" + currentCMD);

                                        switch (currentCMD) {
                                            case Constants.UDP_HAND://握手
                                                LogUtils.e("======DetailFragment==回调===握手==");
                                                //判断数据是否是发个自己的
                                                Boolean dataIfForMe = CalculateUtils.getDataIfForMe(resultData, getAttachActivity());
                                                LogUtils.e("======DetailFragment=====dataIfForMe==" + dataIfForMe);
                                                //设备在线握手成功
                                                if (dataIfForMe) {
                                                    Message message = new Message();
                                                    message.what = UDP_Hand;
                                                    message.obj = true;
                                                    mHandler.sendMessage(message);
                                                }
                                                break;
                                        }
                                    }
                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }


            }
        }.start();

    }

    /**
     * 发送握手消息
     */
    public void sendHandLinkMessage() {
        HandBean handBean = new HandBean();
        handBean.setHelloPc("HelloPc");
        handBean.setComeFrom("Android");

        byte[] sendByteData = CalculateUtils.getSendByteData(getAttachActivity(), mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                Constants.UDP_HAND);
        if (("".equals(mSocketPort))) {
            toast("通讯端口不能为空!");
            return;
        }
        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
    }

    /**
     * 发送点对点消息,必须握手成功
     *
     * @param CMDCode 命令cmd
     */
    public void sendSocketPointMessage(String CMDCode) {
        if (UDP_HAND_TAG) {
            HandBean handBean = new HandBean();
            handBean.setHelloPc("HelloPc");
            handBean.setComeFrom("Android");
            byte[] sendByteData = CalculateUtils.getSendByteData(getAttachActivity(), mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空!");
                return;
            }
            SocketManage.startSendMessageBySocket(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), false);
        } else {
            toast("请先建立握手链接!");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        isFatherExit = false;
        mFirstIn = true;
        isRuning = true;
        sendListDictsRequest();
        sendHandLinkMessage();
        initReceiveThread();

    }

    @Override
    public void onPause() {
        super.onPause();
        isRuning = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRuning = false;
    }
}
