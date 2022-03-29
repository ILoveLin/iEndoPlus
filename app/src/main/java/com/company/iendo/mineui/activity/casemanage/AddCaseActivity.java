package com.company.iendo.mineui.activity.casemanage;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.AddCaseBean;
import com.company.iendo.bean.DialogItemBean;
import com.company.iendo.bean.ListDialogDateBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.bean.socket.HandBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.MenuDialog;
import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.CommonUtil;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.utils.SocketUtils;
import com.company.iendo.widget.LinesEditView;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
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
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 添加病例
 */
public final class AddCaseActivity extends AppActivity implements StatusAction {
    private StatusLayout mStatusLayout;
    private TitleBar mTitleBar;
    private boolean mFragClickable = false;  //dialog数据请求错误,相对于dialog不允许弹窗,不然会闪退
    private HashMap mDialogItemMap;
    private TextView tv_01_age_type;
    private LinesEditView lines_01_i_tell_you, lines_01_bad_tell, lines_03_case_history, lines_03_family_case_history;
    private ClearEditText et_01_name, et_01_sex_type, et_01_age, et_01_jop, et_01_fee, et_01_get_check_doctor, edit_01_i_tell_you, edit_01_i_bad_tell;

    private LinesEditView etlines_02_mirror_see, etlines_02_mirror_result, etlines_02_live_check, etlines_02_cytology,
            etlines_02_test, etlines_02_pathology, etlines_02_advice;
    private ClearEditText et_02_mirror_see, et_02_mirror_result, et_02_live_check, et_02_cytology, et_02_test, et_02_pathology, et_02_advice,
            et_02_check_doctor;
    private ClearEditText et_03_door_num, et_03_protection_num, et_03_section, et_03_device, et_03_case_num, et_03_in_hospital_num, et_03_case_area_num, et_03_case_bed_num,
            et_03_native_place, et_03_ming_zu, et_03_is_married, et_03_tel, et_03_address, et_03_my_id_num, edit_03_case_history, edit_03_family_case_history;

    private HashMap<String, String> mParamsMap;
    private ArrayList ageList;
    private ImageView iv_01_age_type;
    private ImageView iv_01_jop;
    private ImageView tv_01_get_check_doctor;
    private ImageView iv_01_i_tell_you;
    private ImageView iv_01_bad_tell;
    private ImageView iv_02_mirror_see;
    private ImageView iv_02_mirror_result;
    private ImageView iv_02_live_check;
    private ImageView iv_02_cytology;
    private ImageView iv_02_test;
    private ImageView iv_02_pathology;
    private ImageView iv_02_advice;
    private ImageView iv_02_check_doctor;
    private ImageView iv_03_section;
    private ImageView iv_03_device;
    private ImageView iv_03_ming_zu;
    private ImageView iv_03_is_married;
    private ArrayList<ImageView> mImageViewList;
    private ImageView iv_01_sex_type;
    private static boolean UDP_HAND_TAG = false; //握手成功表示  true 成功
    private NestedScrollView mScrollView;

    private ArrayList<ClearEditText> mEditList =new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_case;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.titlebar);
        mScrollView = findViewById(R.id.add_nestedsv);
        initLayoutViewDate();
        responseListener();


    }


    private void responseListener() {


        //年纪类别的List数据本地写:岁,月,天,
        setOnClickListener(R.id.iv_01_sex_type, R.id.iv_01_age_type, R.id.iv_01_jop, R.id.tv_01_get_check_doctor,
                R.id.iv_02_mirror_see, R.id.iv_02_mirror_result, R.id.iv_02_live_check, R.id.iv_02_cytology, R.id.iv_02_test, R.id.iv_02_pathology,
                R.id.iv_02_advice, R.id.iv_02_check_doctor, R.id.iv_03_section, R.id.iv_03_device, R.id.iv_03_ming_zu, R.id.iv_03_is_married);

        //01--layout
        iv_01_i_tell_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(lines_01_i_tell_you, "11");

            }
        });
        iv_01_bad_tell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(lines_01_bad_tell, "12");

            }
        });
        //02-layout

        iv_02_mirror_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_mirror_see, "13");

            }
        });
        iv_02_mirror_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_mirror_result, "14");

            }
        });
        iv_02_live_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_live_check, "15");

            }
        });
        iv_02_cytology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_cytology, "16");

            }
        });
        iv_02_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_test, "17");

            }
        });
        iv_02_pathology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_pathology, "18");

            }
        });
        iv_02_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showITellyouMenuDialog(etlines_02_advice, "19");

            }
        });


        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                checkDataAndRequest();
            }
        });
    }

    private void checkDataAndRequest() {
        String Name = et_01_name.getText().toString().trim();
        String age = et_01_age.getText().toString().trim();
        String sex = et_01_sex_type.getText().toString().trim();

        if (Name.isEmpty()) {
            toast("姓名不能为空!");
        } else if (age.isEmpty()) {
            toast("年龄不能为空!");
        } else if (sex.isEmpty()) {
            toast("性别不能为空!");
        } else {
            getElseCanSelected();

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
        String CardID = et_03_my_id_num.getText().toString().trim();       //身份证号
        String MedHistory = lines_03_case_history.getContentText().toString().trim();       //医疗病史
        String FamilyHistory = lines_03_family_case_history.getContentText().toString().trim();       //家族病史
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
        String ChiefComplaint = lines_01_i_tell_you.getContentEdit().getText().toString().trim();       //主诉
//        String ChiefComplaint = et_01_i_tell_you.getText().toString().trim();       //主诉
        String Test = etlines_02_test.getContentText().toString().trim();       //试验
        String Advice = etlines_02_advice.getContentText().toString().trim();       //建议
        String InpatientID = et_03_in_hospital_num.getText().toString().trim();       //住院号
        String OutpatientID = et_03_door_num.getText().toString().trim();       //门诊号
        String Biopsy = etlines_02_live_check.getContentText().toString().trim();       //活检
        String Ctology = etlines_02_cytology.getContentText().toString().trim();       //细胞学
        String Pathology = etlines_02_pathology.getContentText().toString().trim();       //病理学
        String ExaminingPhysician = etlines_02_live_check.getContentText().toString().trim();       //检查医生
        String ClinicalDiagnosis = lines_01_bad_tell.getContentText().trim();       //临床诊断
//        String ClinicalDiagnosis = et_01_bad_tell.getText().toString().trim();       //临床诊断
        String CheckContent = etlines_02_mirror_see.getContentText().toString().trim();       //检查内容（镜检所见）
        String CheckDiagnosis = etlines_02_mirror_result.getContentText().toString().trim();       //镜检诊断


        //添加三个必须添加的参数
        String UserName = (String) SharePreferenceUtil.get(AddCaseActivity.this, SharePreferenceUtil.Current_Login_UserName, "Admin");
        String EndoType = (String) SharePreferenceUtil.get(AddCaseActivity.this, SharePreferenceUtil.Current_EndoType, "3");
        mParamsMap.put("Name", et_01_name.getText().toString().trim());
        mParamsMap.put("UserName", UserName);
        mParamsMap.put("EndoType", endoType);
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
        sendRequest();


    }

    /**
     * 添加病例
     */
    private void sendRequest() {
        LogUtils.e("添加病例=== mParamsMap.toString()===" + mParamsMap.toString());
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.CaseManager_AddCase)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("添加病例===onError===" + e);
                        showError(listener -> {
                            sendRequest();
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if ("" != response) {
                            LogUtils.e("添加病例===onResponse===" + response);
                            AddCaseBean mBean = mGson.fromJson(response, AddCaseBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                toast("添加成功");
                                //socket告知上位机新增病例
                                sendSocketPointMessage(Constants.UDP_12);
                                ActivityManager.getInstance().finishActivity(AddCaseActivity.class);
                            } else {
                                showError(listener -> {
                                    sendRequest();

                                });
                            }
                        } else {
                            showError(listener -> {
                                sendRequest();
                            });
                        }
                    }
                });

    }


    /**
     * ***************************************************************************通讯模块**************************************************************************
     */

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
        SocketUtils.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), AddCaseActivity.this);
//        SocketManage.startSendHandMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort));
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
            byte[] sendByteData = CalculateUtils.getSendByteData(this, mGson.toJson(handBean), mCurrentTypeNum, mCurrentReceiveDeviceCode,
                    CMDCode);
            if (("".equals(mSocketPort))) {
                toast("通讯端口不能为空!");
                return;
            }
            SocketUtils.startSendPointMessage(sendByteData, mSocketOrLiveIP, Integer.parseInt(mSocketPort), AddCaseActivity.this);

        } else {
            sendHandLinkMessage();
            toast("请先建立握手链接!");
        }

    }

    /**
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==AddCaseActivity==event.getData()==" + event.getData());
        String mRun2End4 = CalculateUtils.getReceiveRun2End4String(event.getData());//随机数之后到data结尾的String
        String deviceType = CalculateUtils.getSendDeviceType(event.getData());
        String deviceOnlyCode = CalculateUtils.getSendDeviceOnlyCode(event.getData());
        String currentCMD = CalculateUtils.getCMD(event.getData());
        LogUtils.e("Socket回调==AddCaseActivity==随机数之后到data的Str==mRun2End4==" + mRun2End4);
        LogUtils.e("Socket回调==AddCaseActivity==发送方设备类型==deviceType==" + deviceType);
        LogUtils.e("Socket回调==AddCaseActivity==获取发送方设备Code==deviceOnlyCode==" + deviceOnlyCode);
        LogUtils.e("Socket回调==AddCaseActivity==当前UDP命令==currentCMD==" + currentCMD);
        LogUtils.e("Socket回调==AddCaseActivity==当前UDP命令==event.getUdpCmd()==" + event.getUdpCmd());
        String data = event.getData();
        switch (event.getUdpCmd()) {
            case Constants.UDP_HAND://握手
                UDP_HAND_TAG = true;
                //获取当前病例ID
                break;
        }

    }

    /**
     * ***************************************************************************通讯模块**************************************************************************
     */
    @Override
    protected void initData() {


    }


    /**
     * 获取需要Dialog选择数据的集合
     */
    private void sendListDictsRequest() {
        //获取Dialog item的数据
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseDialogDate)
//                .addParams("EndoType", "4")
                .addParams("EndoType", endoType)
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_01_sex_type:  //性别
                showMenuDialog(et_01_sex_type, "100");
                break;
            case R.id.iv_01_age_type:  //年龄类别  --本地写数据List
                showMenuElseDialog();
                break;
            case R.id.iv_01_jop:        //职业
                showMenuDialog(et_01_jop, "5");
                break;
            case R.id.tv_01_get_check_doctor://送检医生
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
            case R.id.iv_02_check_doctor://检查医生
                showMenuDialog(et_02_check_doctor, "20");
                break;
            case R.id.iv_03_section: //科室
                showMenuDialog(et_03_section, "9");
                break;
            case R.id.iv_03_device://设备
                showMenuDialog(et_03_device, "10");
                break;
            case R.id.iv_03_ming_zu://民族
                showMenuDialog(et_03_ming_zu, "23");
                break;
            case R.id.iv_03_is_married://婚否
                showMenuDialog(et_03_is_married, "101");
                break;

        }
    }

    private void showITellyouMenuDialog(LinesEditView mEdit, String key) {
        if (mFragClickable && null != mDialogItemMap) {
            ArrayList<DialogItemBean> mDataList = (ArrayList<DialogItemBean>) mDialogItemMap.get(key);

            ArrayList<String> stringList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                stringList.add(mDataList.get(i).getDictItem());
            }
            // 底部选择框
            new MenuDialog.Builder(this)
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(stringList)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String data) {
                            String s = stringList.get(position);
                            String s1 = mEdit.getContentText() + "" + s;
                            if (s1.length() >= 800) {
                                toast("字数不能超过800!");
                            } else {
                                mEdit.setContentText(mEdit.getContentText() + "" + s);

                            }

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                        }
                    })
                    .show();
        }
    }


    private void showMenuElseDialog() {

        // 底部选择框
        new MenuDialog.Builder(this)
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


    private void showMenuDialog(ClearEditText mEdit, String key) {
        if (mFragClickable && null != mDialogItemMap) {
            ArrayList<DialogItemBean> mDataList = (ArrayList<DialogItemBean>) mDialogItemMap.get(key);

            ArrayList<String> stringList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                stringList.add(mDataList.get(i).getDictItem());
            }
            // 底部选择框
            new MenuDialog.Builder(this)
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
                            mEdit.setText(mEdit.getText().toString() + "" + s);

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {

                        }
                    })
                    .show();
        }

    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    private void initLayoutViewDate() {
        ageList = new ArrayList<>();
        ageList.add("岁");
        ageList.add("月");
        ageList.add("天");
        /**
         * 获取基本信息id
         */
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
        lines_01_i_tell_you = findViewById(R.id.et_01_i_tell_you);
        //临床诊断
        lines_01_bad_tell = findViewById(R.id.et_01_bad_tell);

        //主诉---多行显示的edit
        edit_01_i_tell_you = lines_01_i_tell_you.getContentEdit();
        //临床诊断---多行显示的edit
        edit_01_i_bad_tell = lines_01_bad_tell.getContentEdit();


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
        et_02_check_doctor = findViewById(R.id.et_02_check_doctor);//常规的edittext

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
        lines_03_case_history = findViewById(R.id.et_03_case_history);
        //家族病史
        lines_03_family_case_history = findViewById(R.id.et_03_family_case_history);


        edit_03_case_history = lines_03_case_history.getContentEdit();
        edit_03_family_case_history = lines_03_family_case_history.getContentEdit();

        //获取点击图标弹出dialog
        iv_01_sex_type = findViewById(R.id.iv_01_sex_type);
        iv_01_age_type = findViewById(R.id.iv_01_age_type);
        iv_01_jop = findViewById(R.id.iv_01_jop);
        tv_01_get_check_doctor = findViewById(R.id.tv_01_get_check_doctor);
        iv_01_i_tell_you = findViewById(R.id.iv_01_i_tell_you);
        iv_01_bad_tell = findViewById(R.id.iv_01_bad_tell);


        //获取点击图标弹出dialog
        iv_02_mirror_see = findViewById(R.id.iv_02_mirror_see);
        iv_02_mirror_result = findViewById(R.id.iv_02_mirror_result);
        iv_02_live_check = findViewById(R.id.iv_02_live_check);
        iv_02_cytology = findViewById(R.id.iv_02_cytology);
        iv_02_test = findViewById(R.id.iv_02_test);
        iv_02_pathology = findViewById(R.id.iv_02_pathology);
        iv_02_advice = findViewById(R.id.iv_02_advice);
        iv_02_check_doctor = findViewById(R.id.iv_02_check_doctor);


        //获取点击图标弹出dialog
        iv_03_section = findViewById(R.id.iv_03_section);
        iv_03_device = findViewById(R.id.iv_03_device);
        iv_03_ming_zu = findViewById(R.id.iv_03_ming_zu);
        iv_03_is_married = findViewById(R.id.iv_03_is_married);



        mEditList.add(edit_01_i_bad_tell);
        mEditList.add(et_02_mirror_see);
        mEditList.add(et_02_mirror_result);
        mEditList.add(et_02_live_check);
        mEditList.add(et_02_cytology);
        mEditList.add(et_02_test);
        mEditList.add(et_02_pathology);
        mEditList.add(et_02_advice);
        mEditList.add(et_02_check_doctor);
        mEditList.add(et_03_section);
        mEditList.add(et_03_device);
        mEditList.add(et_03_ming_zu);
        mEditList.add(et_03_is_married);
        mEditList.add(edit_03_case_history);
        mEditList.add(edit_03_family_case_history);
        mEditList.add(edit_01_i_tell_you);
        mEditList.add(et_01_name);


        for (int i = 0; i < mEditList.size(); i++) {
            mEditList.get(i).setFocusableInTouchMode(true);
            mEditList.get(i).setFocusable(true);
            mEditList.get(i).requestFocus();

        }





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

    @Override
    protected void onResume() {
        super.onResume();
        sendListDictsRequest();
        //握手通讯
        LogUtils.e("onResume===AddCaseActivity===开始建立握手链接!");
        sendHandLinkMessage();
    }


}