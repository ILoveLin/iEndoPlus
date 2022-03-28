package com.company.iendo.mineui.offline.fragment;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.CaseDetailBean;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.LinesEditView;
import com.company.iendo.widget.StatusLayout;
import com.hjq.widget.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * company：江西神州医疗设备有限公司
 * <p>
 * <p>
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：离线模式的--详情fragment
 */
public class DetailOfflineFragment extends TitleBarFragment<MainActivity> implements StatusAction {

    private StatusLayout mStatusLayout;
    private LinesEditView et_01_i_tell_you, et_01_bad_tell;
    private TextView tv_01_age_type;
    private ClearEditText et_01_check_num, et_01_name, et_01_sex_type, et_01_age, et_01_jop, et_01_fee, et_01_get_check_doctor;
    //            et_01_i_tell_you, et_01_bad_tell;
    private LinesEditView etlines_02_mirror_see, etlines_02_mirror_result, etlines_02_live_check, etlines_02_cytology, etlines_02_test,
            etlines_02_pathology, etlines_02_advice,et_03_case_history, et_03_family_case_history;
    private ClearEditText et_02_mirror_see, et_02_mirror_result, et_02_live_check, et_02_cytology, et_02_test, et_02_pathology,
            et_02_advice, et_02_check_doctor;
    private ClearEditText et_03_door_num, et_03_protection_num, et_03_section, et_03_device, et_03_case_num, et_03_in_hospital_num,
            et_03_case_area_num, et_03_case_bed_num, et_03_native_place, et_03_ming_zu, et_03_is_married, et_03_tel, et_03_address,
            et_03_my_id_num;
    private String currentItemCaseID;
    private HashMap<String, String> mPathMap;     //例如imageName=001.jpg  url=http://192.168.64.56:7001/1_3/001.jpg
    private ArrayList<LinesEditView> linesEditViewList;
    private ClearEditText lines_edit_01_i_tell_you;
    private ClearEditText lines_edit_01_i_bad_tell;

    public static DetailOfflineFragment newInstance() {
        return new DetailOfflineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_offline_detail_message;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.detail_hint);
        currentItemCaseID = MainActivity.getCurrentItemID();
        initLayoutViewDate();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //activity和fragment 通信回调
    }


    /**
     * 设置数据
     *
     * @param
     */
    private void setLayoutData(CaseDBBean mDataBean) {

        LogUtils.e("病例详情界面数据====" + mDataBean);

        et_01_check_num.setText("" + mDataBean.getCaseNo());     //病例编号
        et_01_name.setText("" + mDataBean.getName());
        et_03_is_married.setText("" + mDataBean.getMarried());
        et_01_sex_type.setText("" + mDataBean.getSex());
        et_03_tel.setText("" + mDataBean.getTel());
        et_03_address.setText("" + mDataBean.getAddress());
        //        String PatientNo = et_01_check_num.getText().toString().trim();       //病人编号---检查号???
        et_03_my_id_num.setText("" + mDataBean.getCardID());
        et_03_case_history.setContentText("" + mDataBean.getMedHistory());
        et_03_family_case_history.setContentText("" + mDataBean.getFamilyHistory());
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

        et_01_get_check_doctor.setText("" + mDataBean.getSubmitDoctor());
        etlines_02_test.setContentText("" + mDataBean.getTest());
        etlines_02_advice.setContentText("" + mDataBean.getAdvice());
        et_03_in_hospital_num.setText("" + mDataBean.getInpatientID());
        et_03_door_num.setText("");
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
        setLayoutData(CaseManageOfflineFragment.currentItemClickDBBean);
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

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
