package com.company.iendo.mineui.activity.search;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.ui.dialog.DateDialog;
import com.company.iendo.ui.dialog.SelectModifyTypeDialog;
import com.company.iendo.utils.DateUtil;
import com.company.iendo.utils.LogUtils;
import com.hjq.base.BaseDialog;
import com.hjq.shape.view.ShapeButton;
import com.hjq.shape.view.ShapeTextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/28 12:01
 * desc：选择搜索条件界面
 */
public class SearchSelectedActivity extends AppActivity {

    private ShapeTextView mStartDate;
    private ShapeTextView mEndDate;
    private EditText mEtCheckNum;
    private EditText mEtCheckName;
    private EditText mEtStartAge;
    private EditText mEtEndAge;
    private TextView mAgeType;
    private TextView mSexType;
    private EditText mEtWorker;
    private TextView mMarriedType;
    private EditText mEtSection;
    private EditText mEtDevice;
    private EditText mEtCheckDoctor;
    private EditText mEtGetDoctor;
    private ShapeButton mReStart;
    private ShapeButton mSearch;
    private DateDialog.Builder mDateDialog;
    private String mChoiceDate;
    private ArrayList<String> mAgeList = new ArrayList<String>();
    private ArrayList<String> mSexList = new ArrayList<String>();
    private ArrayList<String> mMarriedList = new ArrayList<String>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_selected_search;
    }

    @Override
    protected void initView() {
        mStartDate = findViewById(R.id.sp_start_date);
        mEndDate = findViewById(R.id.sp_end_date);
        mEtCheckNum = findViewById(R.id.edit_check_num);
        mEtCheckName = findViewById(R.id.edit_check_name);
        mEtStartAge = findViewById(R.id.edit_start_age);
        mEtEndAge = findViewById(R.id.edit_end_age);
        mAgeType = findViewById(R.id.tv_age_type);
        mSexType = findViewById(R.id.tv_sex_type);
        mEtWorker = findViewById(R.id.edit_worker);
        mMarriedType = findViewById(R.id.tv_married_type);
        mEtSection = findViewById(R.id.edit_section);
        mEtDevice = findViewById(R.id.edit_device);
        mEtCheckDoctor = findViewById(R.id.edit_check_doctor);
        mEtGetDoctor = findViewById(R.id.edit_get_doctor);  //送检医生
        mReStart = findViewById(R.id.sp_restart);
        mSearch = findViewById(R.id.sp_search);
        setOnClickListener(R.id.sp_restart, R.id.sp_search, R.id.sp_start_date, R.id.sp_end_date, R.id.tv_age_type, R.id.tv_sex_type, R.id.tv_married_type);

    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sp_start_date: //开始时间
                showDateDialog("开始");
                break;
            case R.id.sp_end_date:  //结束时间
                showDateDialog("结束");
                break;
            case R.id.tv_age_type:  //年龄类别   type=1
                mAgeList.add("岁");
                mAgeList.add("月");
                mAgeList.add("天");
                showSelectTypeDialog(mAgeList, "1");
                break;
            case R.id.tv_sex_type:  //性别类别  type=2
                mSexList.add("全部");
                mSexList.add("男");
                mSexList.add("女");
                showSelectTypeDialog(mSexList, "2");
                break;
            case R.id.tv_married_type: //结婚类别  type=3
                mMarriedList.add("全部");
                mMarriedList.add("男");
                mMarriedList.add("女");
                showSelectTypeDialog(mMarriedList, "3");
                break;
            case R.id.sp_restart:
                setDefaultDate();
                break;
            case R.id.sp_search:
                //                break;
                String CheckDateStart = mStartDate.getText().toString().trim();
                String CheckDateEnd = mEndDate.getText().toString().trim();
                String CaseNo = mEtCheckNum.getText().toString().trim();
                String Name = mEtCheckName.getText().toString().trim();
                String PatientAgeStart = mEtStartAge.getText().toString().trim();
                String PatientAgeEnd = mEtEndAge.getText().toString().trim();

                String AgeUnit = mAgeType.getText().toString().trim();
                String Sex = mSexType.getText().toString().trim();
                String Occupatior = mEtWorker.getText().toString().trim();
                String Married = mMarriedType.getText().toString().trim();
                String Department = mEtSection.getText().toString().trim();
                String Device = mEtDevice.getText().toString().trim();
                String ExaminingPhysician = mEtCheckDoctor.getText().toString().trim();
                String SubmitDoctor = mEtGetDoctor.getText().toString().trim();
                HashMap<String, String> parmasMap = new HashMap<>();
                parmasMap.put("CheckDateStart", CheckDateStart);
                parmasMap.put("CheckDateEnd", CheckDateEnd);
                parmasMap.put("CaseNo", CaseNo);
                parmasMap.put("Name", Name);
                if ("".equals(PatientAgeStart)) {

                } else {
                    parmasMap.put("PatientAgeStart", PatientAgeStart);
                }
                if ("".equals(PatientAgeEnd)) {

                } else {
                    parmasMap.put("PatientAgeEnd", PatientAgeEnd);
                }
                parmasMap.put("AgeUnit", AgeUnit);
                parmasMap.put("Sex", Sex);
                parmasMap.put("Occupatior", Occupatior);
                parmasMap.put("Married", Married);
                parmasMap.put("Department", Department);
                parmasMap.put("Device", Device);
                parmasMap.put("ExaminingPhysician", ExaminingPhysician);
                parmasMap.put("SubmitDoctor", SubmitDoctor);
                parmasMap.put("EndoType", "3");


                Intent intent = new Intent(SearchSelectedActivity.this, SearchActivity.class);
                intent.putExtra("parmasMap", (Serializable) parmasMap);
                startActivity(intent);

        }
    }

    private void showSelectTypeDialog(ArrayList<String> mAgeList, String type) {
        new SelectModifyTypeDialog.Builder(this)
                .setTitle("请选择")
                .setList(mAgeList)
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(0)
                .setBackgroundDimEnabled(false)
//                .setWidth(ScreenSizeUtil.getScreenWidth(this) /2)
                .setCanceledOnTouchOutside(false)
                .setListener(new SelectModifyTypeDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        LogUtils.e("showMultiDialog===" + data.toString()); //{0=HD3}
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        LogUtils.e("showMultiDialog===str==" + str); //{0=HD3}
                        if ("1".equals(type)) {
                            mAgeType.setText("" + str);
                        } else if ("2".equals(type)) {
                            mSexType.setText("" + str);
                        } else if ("3".equals(type)) {
                            mMarriedType.setText("" + str);
                        }
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
    }

    private void setDefaultDate() {
        String systemDate = DateUtil.getSystemDate();
        mStartDate.setText("" + systemDate);
        mEndDate.setText("" + systemDate);
        mAgeType.setText("岁");
        mSexType.setText("全部");
        mMarriedType.setText("全部");
    }


    //选择日期
    private void showDateDialog(String type) {
        // 日期选择对话框
        mDateDialog = new DateDialog.Builder(getActivity());
        mDateDialog.setTitle("请选择日期")
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(new DateDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, int year, int month, int day) {
                        // 如果不指定时分秒则默认为现在的时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        // 月份从零开始，所以需要减 1
                        calendar.set(Calendar.MONTH, month - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        mChoiceDate = new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime());
                        LogUtils.e("TTTTT" + mChoiceDate);
                        String replaceDate = mChoiceDate.replace("年", "-").replace("月", "-").replace("日", "");
//                        toast("时间：" + mChoiceDate);
                        if ("开始".equals(type)) {
                            mStartDate.setText(replaceDate);
                        } else {
                            mEndDate.setText(replaceDate);

                        }


                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();


    }
}
