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
    private String mDeviceCode;  //当前设备id-code
    private String mUserID;    //当前用户id
    private String currentItemCaseID;
    private ArrayList<String> ageList;
    private ArrayList<String> mNameList;
    private HashMap<String, String> mPathMap;     //例如imageName=001.jpg  url=http://192.168.64.56:7001/1_3/001.jpg
    private ArrayList<LinesEditView> linesEditViewList;
    private ClearEditText lines_edit_01_i_tell_you;
    private ClearEditText lines_edit_01_i_bad_tell;
    private String mUserName;
    private String itemID;
    private String mCaseID;
    private String mCurrentDonwTime;

    public static DetailOfflineFragment newInstance() {
        return new DetailOfflineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_offline_detail_message;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.detail_hint);
        mBaseUrl = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_BaseUrl, "192.168.132.102");
        mDeviceCode = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_DeviceCode, "");
        mUserID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserID, "");
        mUserName = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Login_UserName, "Admin");
        mCaseID = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Chose_CaseID, "4600");

        currentItemCaseID = MainActivity.getCurrentItemID();
        initLayoutViewDate();
        setEditStatus();

//        setLayoutData();
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

        et_01_check_num.setText(""+mDataBean.getCaseNo());     //病例编号
        et_01_name.setText(""+mDataBean.getName());
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
//        //编辑状态为true,不可编辑状态为flase,默认false不可编辑
        if (!mEditStatus) {//切换到了不可编辑模式,发送请求
            if (mFirstIn) {  //解决  首次进来 tosat 提示
                mFirstIn = false;
            } else {
            }
        }
        if (isFatherExit) {//父类界面主动退出,保存当前数据
            if (mEditStatus) {
                showComplete();
            }

        }

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
     * eventbus 刷新socket数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketRefreshEvent(SocketRefreshEvent event) {
        LogUtils.e("Socket回调==DetailFragment==event.getData()==" + event.getData());

//        setLayoutData();

    }


    @Override
    public void onResume() {
        super.onResume();
        isFatherExit = false;
    }

    @Override
    public void onPause() {
        mFirstIn = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
