package com.company.iendo.mineui.activity.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.DialogItemBean;
import com.company.iendo.bean.ListDialogDateBean;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.DateDialog;
import com.company.iendo.ui.dialog.MenuDialog;
import com.company.iendo.ui.dialog.SelectModifyTypeDialog;
import com.company.iendo.utils.DateUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.shape.view.ShapeButton;
import com.hjq.shape.view.ShapeTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/28 12:01
 * desc：选择搜索条件界面
 * <p>
 * 年龄默认给---岁
 * 性别默认给---全部
 */
public class SearchSelectedActivity extends AppActivity  {
    private boolean mFragClickable = false;  //dialog数据请求错误,相对于dialog不允许弹窗,不然会闪退
    private HashMap mDialogItemMap;
    private ShapeTextView mStartDate;
    private ShapeTextView mEndDate;
    private EditText mEtCheckNum;
    private EditText mEtCheckName;
    //    private EditText mEtStartAge;
    private TextView mSexType;
    private EditText mEtWorker;
    private TextView mMarriedType;
    private EditText mEtSection;
    private EditText mEtDevice;
    private EditText mEtCheckDoctor;
    private EditText mEtGetDoctor;
    private ShapeButton mSearch;
    private DateDialog.Builder mDateDialog;
    private String mChoiceDate;
    private ArrayList<String> mAgeList = new ArrayList<String>();
    private ArrayList<String> mSexList = new ArrayList<String>();
    private ArrayList<String> mMarriedList = new ArrayList<String>();
    private TitleBar mTitleBar;
    private String todayTime;
    private EditText mEtYibaohao;
    private ImageView mIvAgeType;
    private TextView mAgeType;
    private EditText mEtAgeTimeStart;
    private EditText mEtAgeTimeEnd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_selected_search;
    }

    @Override
    protected void initView() {
        mStartDate = findViewById(R.id.sp_start_date);
        mEndDate = findViewById(R.id.sp_end_date);
        mEtCheckNum = findViewById(R.id.edit_check_num);
        mEtAgeTimeStart = findViewById(R.id.edit_age_start);
        mEtAgeTimeEnd = findViewById(R.id.edit_age_end);
        mEtCheckName = findViewById(R.id.edit_check_name);
//        mEtStartAge = findViewById(R.id.edit_start_age);
        mEtYibaohao = findViewById(R.id.edit_yibaohao);
        mIvAgeType = findViewById(R.id.iv_age);
//        mEtEndAge = findViewById(R.id.edit_end_age);
        mAgeType = findViewById(R.id.tv_age_type);
        mSexType = findViewById(R.id.tv_sex_type);
        mEtWorker = findViewById(R.id.edit_worker);
        mMarriedType = findViewById(R.id.tv_married_type);
        mEtSection = findViewById(R.id.edit_section);
        mEtDevice = findViewById(R.id.edit_device);
        mEtCheckDoctor = findViewById(R.id.edit_check_doctor);
        mEtGetDoctor = findViewById(R.id.edit_get_doctor);  //送检医生
        mSearch = findViewById(R.id.sp_search);
        mTitleBar = findViewById(R.id.titlebar);
        setOnClickListener(R.id.iv_time_start, R.id.iv_time_end, R.id.tv_sex_type, R.id.edit_worker, R.id.edit_section, R.id.edit_device, R.id.edit_check_doctor,
                R.id.edit_get_doctor, R.id.sp_search, R.id.sp_start_date, R.id.sp_end_date, R.id.tv_married_type, R.id.iv_age);

        getCurrentDataTime();
        mStartDate.setText(todayTime + "");
        mEndDate.setText(todayTime + "");

        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                setDefaultDate();

            }
        });

        sendListDictsRequest();
    }

    private void getCurrentDataTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(formatter.format(calendar.getTime()));
        todayTime = formatter.format(calendar.getTime());
//        todayTime = year+"-"+month+"-"+day;

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
                        mFragClickable = false;
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onResponse(String response, int id) {
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


    private void showMenuDialog(EditText mEdit, String key) {
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
                            mEdit.setText(mEdit.getText().toString() + "" + s);

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();
        }
    }

    private void setDefaultDate() {
        String systemDate = DateUtil.getSystemDate();
        mStartDate.setText("" + systemDate);
        mEndDate.setText("" + systemDate);
        mSexType.setText("请选择性别");
        mMarriedType.setText("请选择婚否");
        mEtCheckNum.setText("");
        mEtCheckNum.setHint("请输入检查号");
        mEtCheckName.setText("");
        mEtCheckName.setHint("请输入姓名");
        mEtAgeTimeStart.setText("");
        mEtAgeTimeEnd.setText("");
        mEtAgeTimeStart.setHint("请输入年龄");
        mEtAgeTimeEnd.setHint("请输入年龄");
        mEtWorker.setText("");
        mEtWorker.setHint("请输入职业");
        mEtSection.setText("");
        mEtSection.setHint("请输入科室");
        mEtDevice.setText("");
        mEtDevice.setHint("请输入设备");
        mEtCheckDoctor.setText("");
        mEtCheckDoctor.setHint("请输入检查医生");
        mEtGetDoctor.setText("");
        mEtGetDoctor.setHint("请输入送检医生");
    }


    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sp_start_date: //开始时间
            case R.id.iv_time_start: //开始时间
                showDateDialog("开始");
                break;
            case R.id.sp_end_date:  //结束时间
            case R.id.iv_time_end: //开始时间
                showDateDialog("结束");
                break;
            case R.id.edit_worker:  //职业
//                showMenuDialog(mEtWorker, "5");
                break;
            case R.id.edit_section:  //科室
//                showMenuDialog(mEtSection, "9");
                break;
            case R.id.edit_device:  //设备
//                showMenuDialog(mEtDevice, "10");
                break;
            case R.id.edit_check_doctor:  //检查医生
//                showMenuDialog(mEtCheckDoctor, "20");
                break;
            case R.id.edit_get_doctor:  //送检医生
//                showMenuDialog(mEtGetDoctor, "8");
                break;
            case R.id.iv_age:  //性别类别  type=1 岁 月 天
                mAgeList.clear();
                mAgeList.add("岁");
                mAgeList.add("月");
                mAgeList.add("天");
                showSelectTypeDialog(mAgeList, "1", mAgeType);
                break;
            case R.id.tv_sex_type:  //性别类别  type=2
                mSexList.clear();
                mSexList.add("男");
                mSexList.add("女");
                showSelectTypeDialog(mSexList, "2", mSexType);
                break;
            case R.id.tv_married_type: //结婚类别  type=3
                mMarriedList.clear();
                mMarriedList.add("已婚");
                mMarriedList.add("未婚");

                showSelectTypeDialog(mMarriedList, "3", mMarriedType);
                break;
            case R.id.sp_search:
                //                break;
                String CheckDateStart = mStartDate.getText().toString().trim();
                String CheckDateEnd = mEndDate.getText().toString().trim();

                String replaceStart = CheckDateStart.replace("-", "");
                String replaceEnd = CheckDateEnd.replace("-", "");
                if (Integer.parseInt(replaceEnd) < Integer.parseInt(replaceStart)) {
                    toast("开始时间不能大于结束时间");
                    return;
                }

                String CaseNo = mEtCheckNum.getText().toString().trim();
                String Name = mEtCheckName.getText().toString().trim();
                String PatientAgeStart = mEtAgeTimeStart.getText().toString().trim();
                String PatientAgeEnd = mEtAgeTimeEnd.getText().toString().trim();

//                String AgeUnit = mAgeType.getText().toString().trim();
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

                String trimStart = mEtAgeTimeStart.getText().toString().trim();
                String trimEnd = mEtAgeTimeEnd.getText().toString().trim();

                if (!"".equals(trimStart)) {
                    parmasMap.put("PatientAgeStart", trimStart);
                }
                if (!"".equals(trimEnd)) {
                    parmasMap.put("PatientAgeEnd", trimEnd);
                }
                parmasMap.put("CaseNo", CaseNo);
                parmasMap.put("Name", Name);
                if ("".equals(PatientAgeEnd)) {

                } else {
                    parmasMap.put("PatientAgeEnd", PatientAgeEnd);
                }
                parmasMap.put("AgeUnit", mAgeType.getText().toString());
                parmasMap.put("Sex", Sex);
                parmasMap.put("Occupatior", Occupatior);
                parmasMap.put("Married", Married);
                parmasMap.put("Department", Department);
                parmasMap.put("Device", Device);
                parmasMap.put("ExaminingPhysician", ExaminingPhysician);
                parmasMap.put("SubmitDoctor", SubmitDoctor);
                parmasMap.put("EndoType", endoType);

                Intent intent = new Intent(SearchSelectedActivity.this, SearchActivity.class);
                intent.putExtra("parmasMap", (Serializable) parmasMap);
                startActivity(intent);

        }
    }

    private void showSelectTypeDialog(ArrayList<String> mAgeList, String type, TextView mType) {
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
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        mType.setText("" + str);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
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
