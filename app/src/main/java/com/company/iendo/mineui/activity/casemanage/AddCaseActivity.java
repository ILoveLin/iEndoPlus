package com.company.iendo.mineui.activity.casemanage;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.AddCaseBean;
import com.company.iendo.bean.DialogItemBean;
import com.company.iendo.bean.ListDialogDateBean;
import com.company.iendo.manager.ActivityManager;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.MenuDialog;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 添加病例
 *
 */
public final class AddCaseActivity extends AppActivity implements StatusAction {
    private StatusLayout mStatusLayout;
    private TitleBar mTitleBar;
    private boolean mFragClickable = false;  //dialog数据请求错误,相对于dialog不允许弹窗,不然会闪退
    private HashMap mDialogItemMap;
    private TextView tv_01_age_type;
    private ClearEditText et_01_check_num, et_01_name, et_01_sex_type, et_01_age, et_01_jop, et_01_fee, et_01_get_check_doctor,
            et_01_i_tell_you, et_01_bad_tell;
    private ClearEditText et_02_mirror_see, et_02_mirror_result, et_02_live_check, et_02_cytology, et_02_test, et_02_pathology, et_02_advice, et_02_check_doctor;
    private ClearEditText et_03_door_num, et_03_protection_num, et_03_section, et_03_device, et_03_case_num, et_03_in_hospital_num, et_03_case_area_num, et_03_case_bed_num, et_03_native_place, et_03_ming_zu, et_03_is_married, et_03_tel, et_03_address, et_03_my_id_num, et_03_case_history, et_03_family_case_history;
    private HashMap<String, String> mParamsMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_case;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.titlebar);
        responseListener();
        initLayoutViewDate();

        //年纪类别的List数据本地写:岁,月,天,
        setOnClickListener(R.id.et_01_sex_type, R.id.tv_01_age_type, R.id.et_01_jop, R.id.et_01_get_check_doctor, R.id.et_01_i_tell_you, R.id.et_01_bad_tell,
                R.id.et_02_mirror_see, R.id.et_02_mirror_result, R.id.et_02_live_check, R.id.et_02_cytology, R.id.et_02_test, R.id.et_02_pathology,
                R.id.et_02_advice, R.id.et_02_check_doctor, R.id.et_03_section, R.id.et_03_device, R.id.et_03_ming_zu, R.id.et_03_is_married);

    }


    private void responseListener() {

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
        String ChiefComplaint = et_01_i_tell_you.getText().toString().trim();       //主诉
        String Test = et_02_test.getText().toString().trim();       //试验
        String Advice = et_02_advice.getText().toString().trim();       //建议
        String InpatientID = et_03_in_hospital_num.getText().toString().trim();       //住院号
        String OutpatientID = et_03_door_num.getText().toString().trim();       //门诊号
        String Biopsy = et_02_live_check.getText().toString().trim();       //活检
        String Ctology = et_02_cytology.getText().toString().trim();       //细胞学
        String Pathology = et_02_pathology.getText().toString().trim();       //病理学
        String ExaminingPhysician = et_02_live_check.getText().toString().trim();       //检查医生
        String ClinicalDiagnosis = et_01_bad_tell.getText().toString().trim();       //临床诊断
        String CheckContent = et_02_mirror_see.getText().toString().trim();       //检查内容（镜检所见）
        String CheckDiagnosis = et_02_mirror_result.getText().toString().trim();       //镜检诊断


        //添加三个必须添加的参数
        String UserName = (String) SharePreferenceUtil.get(AddCaseActivity.this, SharePreferenceUtil.Current_Login_UserName, "Admin");
        String EndoType = (String) SharePreferenceUtil.get(AddCaseActivity.this, SharePreferenceUtil.Current_EndoType, "3");
        mParamsMap.put("Name", et_01_name.getText().toString().trim());
        mParamsMap.put("UserName", UserName);
        mParamsMap.put("EndoType", EndoType);
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
                            LogUtils.e("添加病例===onError===" + response);

                            AddCaseBean mBean = mGson.fromJson(response, AddCaseBean.class);
                            if (0 == mBean.getCode()) {  //成功
                                showComplete();
                                toast("" + mBean.getMsg());
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

    @Override
    protected void initData() {


    }


    @Override
    protected void onResume() {
        super.onResume();
        sendListDictsRequest();
    }

    /**
     * 获取需要Dialog选择数据的集合
     */
    private void sendListDictsRequest() {
        //获取Dialog item的数据
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.CaseManager_CaseDialogDate)
                .addParams("EndoType", "3")
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
            case R.id.et_01_i_tell_you:    //主诉
                showMenuDialog(et_01_i_tell_you, "11");
                break;
            case R.id.et_01_bad_tell:     //临床诊断
                showMenuDialog(et_01_bad_tell, "12");
                break;
            case R.id.et_02_mirror_see:   //镜检所见
                showMenuDialog(et_02_mirror_see, "13");
                break;
            case R.id.et_02_mirror_result://镜检诊断
                showMenuDialog(et_02_mirror_result, "14");
                break;
            case R.id.et_02_live_check://活检
                showMenuDialog(et_02_live_check, "15");
                break;
            case R.id.et_02_cytology://细胞学
                showMenuDialog(et_02_cytology, "16");
                break;
            case R.id.et_02_test://试验
                showMenuDialog(et_02_test, "17");
                break;
            case R.id.et_02_pathology://病理学
                showMenuDialog(et_02_pathology, "18");
                break;
            case R.id.et_02_advice://建议
                showMenuDialog(et_02_advice, "19");
                break;
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
        new MenuDialog.Builder(this)
                // 设置 null 表示不显示取消按钮
                //.setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setList("岁", "月", "天")
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {
                        toast("位置：" + position + "，文本：" + string);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
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
                            mEdit.setText("" + s);

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast("取消了");
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
        /**
         *获取镜信息id
         */
        //镜检所见
        et_02_mirror_see = findViewById(R.id.et_02_mirror_see);
        //镜检诊断
        et_02_mirror_result = findViewById(R.id.et_02_mirror_result);
        //活检
        et_02_live_check = findViewById(R.id.et_02_live_check);
        //细胞学
        et_02_cytology = findViewById(R.id.et_02_cytology);
        //试验
        et_02_test = findViewById(R.id.et_02_test);
        //病理学
        et_02_pathology = findViewById(R.id.et_02_pathology);
        //建议
        et_02_advice = findViewById(R.id.et_02_advice);
        //检查医生
        et_02_check_doctor = findViewById(R.id.et_02_check_doctor);
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


    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

}