package com.company.iendo.mineui.activity.usermanage;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.bean.event.RefreshUserListEvent;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.ClearEditText;
import com.hjq.widget.view.PasswordEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 新增用户的界面
 */
public final class AddUserActivity extends AppActivity implements StatusAction, CompoundButton.OnCheckedChangeListener {
    private StatusLayout mStatusLayout;
    private WrapRecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;
    private ClearEditText mAccount;
    private PasswordEditText mPassword;
    private ClearEditText mReloType;
    private ClearEditText mUserDesc;
    private RadioGroup mRadioGroup;

    private AppCompatCheckBox userMan01, CanPsw01, SnapVideoRecord01, LiveStream02, DeviceSet02, CanNew02,
            CanEdit03, CanDelete03, CanPrint03, UnPrinted04, ExportRecord04, ExportVideo04, ExportImage05,
            CanBackup05, OnlySelf05, VideoSet06, HospitalInfo06, ReportStyle06, SeatAdjust07;
    private Map<String, String> mParamsMap;
    //新用户是否激活1激活，0是未激活,此处默认激活
    private String CanUSE = "0";
    //0 关闭 1 开启,
    private String UserMan, CanPsw, SnapVideoRecord, LiveStream, DeviceSet, CanNew, CanEdit, CanDelete, CanPrint,
            UnPrinted, ExportRecord, ExportVideo, ExportImage, CanBackup, OnlySelf, VideoSet, HospitalInfo, ReportStyle, SeatAdjust;
    private ImageView mIVReloType;
    private String Role;
    private Button mBtnCommit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_add;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.userlist_titlebar);
        mSmartRefreshLayout = findViewById(R.id.rl_userlist_refresh);
        mRecyclerView = findViewById(R.id.rv_userlist_recyclerview);
        mBtnCommit = findViewById(R.id.btn_login_commit);

        //账户,密码
        mAccount = findViewById(R.id.user_account);
        mPassword = findViewById(R.id.user_password);
        //角色,描述
        mIVReloType = findViewById(R.id.user_iv_relo_type);
        mReloType = findViewById(R.id.user_et_relo_type);
        mUserDesc = findViewById(R.id.user_msg);
        //状态
        mRadioGroup = findViewById(R.id.radio_add_group);
        //权限相关

        userMan01 = findViewById(R.id.cb_01_manager);
        CanPsw01 = findViewById(R.id.cb_01_setting_kouling);
        SnapVideoRecord01 = findViewById(R.id.cb_01_record_shot);
        LiveStream02 = findViewById(R.id.cb_02_live);
        DeviceSet02 = findViewById(R.id.cb_02_setting_pen_xi_chui);
        CanNew02 = findViewById(R.id.cb_02_input_patient);
        CanEdit03 = findViewById(R.id.cb_03_change_patient);
        CanDelete03 = findViewById(R.id.cb_03_delete_patient);
        CanPrint03 = findViewById(R.id.cb_03_print_patient);
        UnPrinted04 = findViewById(R.id.cb_04_only_unprint_case);
        ExportRecord04 = findViewById(R.id.cb_04_output_case);
        ExportVideo04 = findViewById(R.id.cb_04_output_video);
        ExportImage05 = findViewById(R.id.cb_05_output_picture);
        CanBackup05 = findViewById(R.id.cb_05_copy_data);
        OnlySelf05 = findViewById(R.id.cb_05_only_myself_case);
        VideoSet06 = findViewById(R.id.cb_06_setting_video);
        HospitalInfo06 = findViewById(R.id.cb_06_hospital_msg);
        ReportStyle06 = findViewById(R.id.cb_06_report_style);
        SeatAdjust07 = findViewById(R.id.cb_07_setting_seat);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_01_manager:        //用户管理
                if (isChecked) {
                    UserMan = "1";
                } else {
                    UserMan = "0";
                }
                break;
            case R.id.cb_01_setting_kouling:        //设置口令
                if (isChecked) {
                    CanPsw = "1";
                } else {
                    CanPsw = "0";
                }
                break;
            case R.id.cb_01_record_shot:        //拍照录像
                if (isChecked) {
                    SnapVideoRecord = "1";
                } else {
                    SnapVideoRecord = "0";
                }
                break;
            case R.id.cb_02_live:        //直播
                if (isChecked) {
                    LiveStream = "1";
                } else {
                    LiveStream = "0";
                }
                break;
            case R.id.cb_02_setting_pen_xi_chui:        //喷吸吹设置
                if (isChecked) {
                    DeviceSet = "1";
                } else {
                    DeviceSet = "0";
                }
                break;
            case R.id.cb_02_input_patient:        //登记病人
                if (isChecked) {
                    CanNew = "1";
                } else {
                    CanNew = "0";
                }
                break;
            case R.id.cb_03_change_patient:        //修改病人
                if (isChecked) {
                    CanEdit = "1";
                } else {
                    CanEdit = "0";
                }
                break;
            case R.id.cb_03_delete_patient:        //删除病人
                if (isChecked) {
                    CanDelete = "1";
                } else {
                    CanDelete = "0";
                }
                break;
            case R.id.cb_03_print_patient:        //打印病例
                if (isChecked) {
                    CanPrint = "1";
                } else {
                    CanPrint = "0";
                }
                break;
            case R.id.cb_04_only_unprint_case:        //仅限未打印病例
                if (isChecked) {
                    UnPrinted = "1";
                } else {
                    UnPrinted = "0";
                }
                break;
            case R.id.cb_04_output_case:        //导出病历
                if (isChecked) {
                    ExportRecord = "1";
                } else {
                    ExportRecord = "0";
                }
                break;
            case R.id.cb_04_output_video:        //导出录像
                if (isChecked) {
                    ExportVideo = "1";
                } else {
                    ExportVideo = "0";
                }
                break;
            case R.id.cb_05_output_picture:        //导出图片
                if (isChecked) {
                    ExportImage = "1";
                } else {
                    ExportImage = "0";
                }
                break;
            case R.id.cb_05_copy_data:        //备份数据
                if (isChecked) {
                    CanBackup = "1";
                } else {
                    CanBackup = "0";
                }
                break;
            case R.id.cb_05_only_myself_case:        //仅限本人病例
                if (isChecked) {
                    OnlySelf = "1";
                } else {
                    OnlySelf = "0";
                }
                break;
            case R.id.cb_06_setting_video:        //视频设置
                if (isChecked) {
                    VideoSet = "1";
                } else {
                    VideoSet = "0";
                }
                break;
            case R.id.cb_06_hospital_msg:        //医院信息
                if (isChecked) {
                    HospitalInfo = "1";
                } else {
                    HospitalInfo = "0";
                }
                break;
            case R.id.cb_06_report_style:        //报告样式
                if (isChecked) {
                    ReportStyle = "1";
                } else {
                    ReportStyle = "0";
                }
                break;
            case R.id.cb_07_setting_seat:        //座椅操作
                if (isChecked) {
                    SeatAdjust = "1";
                } else {
                    SeatAdjust = "0";
                }
                break;
        }

    }

    @Override
    protected void initData() {
        responseListener();
    }

    private void sendRequest() {
        getRequestParamsToSendRequest();
    }

    private void getRequestParamsToSendRequest() {
        String UserID = (String) SharePreferenceUtil.get(AddUserActivity.this, SharePreferenceUtil.Current_Login_UserID, "");
        mParamsMap = new HashMap<>();
        //用户管理
        if (userMan01.isChecked()) {
            UserMan = "1";
        } else {
            UserMan = "0";
        }
        //设置口令
        if (CanPsw01.isChecked()) {
            CanPsw = "1";
        } else {
            CanPsw = "0";
        }
        //拍照录像
        if (SnapVideoRecord01.isChecked()) {
            SnapVideoRecord = "1";
        } else {
            SnapVideoRecord = "0";
        }
        //直播
        if (LiveStream02.isChecked()) {
            LiveStream = "1";
        } else {
            LiveStream = "0";
        }
        //喷吸吹设置
        if (DeviceSet02.isChecked()) {
            DeviceSet = "1";
        } else {
            DeviceSet = "0";
        }
        //登记病人
        if (CanNew02.isChecked()) {
            CanNew = "1";
        } else {
            CanNew = "0";
        }
        //修改病人
        if (CanEdit03.isChecked()) {
            CanEdit = "1";
        } else {
            CanEdit = "0";
        }
        //删除病人
        if (CanDelete03.isChecked()) {
            CanDelete = "1";
        } else {
            CanDelete = "0";
        }
        //打印病例
        if (CanPrint03.isChecked()) {
            CanPrint = "1";
        } else {
            CanPrint = "0";
        }
        //仅限未打印病例
        if (UnPrinted04.isChecked()) {
            UnPrinted = "1";
        } else {
            UnPrinted = "0";
        }
        //导出病历
        if (ExportRecord04.isChecked()) {
            ExportRecord = "1";
        } else {
            ExportRecord = "0";
        }
        //导出录像
        if (ExportVideo04.isChecked()) {
            ExportVideo = "1";
        } else {
            ExportVideo = "0";
        }
        //导出图片
        if (ExportImage05.isChecked()) {
            ExportImage = "1";
        } else {
            ExportImage = "0";
        }
        //备份数据
        if (CanBackup05.isChecked()) {
            CanBackup = "1";
        } else {
            CanBackup = "0";
        }
        //仅限本人病例
        if (OnlySelf05.isChecked()) {
            OnlySelf = "1";
        } else {
            OnlySelf = "0";
        }
        //视频设置
        if (VideoSet06.isChecked()) {
            VideoSet = "1";
        } else {
            VideoSet = "0";
        }
        //医院信息
        if (HospitalInfo06.isChecked()) {
            HospitalInfo = "1";
        } else {
            HospitalInfo = "0";
        }
        //报告样式
        if (ReportStyle06.isChecked()) {
            ReportStyle = "1";
        } else {
            ReportStyle = "0";
        }
        //座椅操作
        if (SeatAdjust07.isChecked()) {
            SeatAdjust = "1";
        } else {
            SeatAdjust = "0";
        }
        String UserName = mAccount.getText().toString().trim();
        String Password = mPassword.getText().toString().trim();
        String Des = mUserDesc.getText().toString().trim();
        String mReloTypeString = mReloType.getText().toString().trim();

        if ("".equals(UserName)) {
            toast("用户名不能为空");
            showComplete();
            return;
        } else if ("".equals(UserID)) {
            toast("用户ID不能为空");
            showComplete();
            return;
        } else if ("".equals(Password)) {
            toast("密码不能为空");
            showComplete();
            return;
        } else if ("".equals(mReloTypeString)) {
            toast("角色不能为空");
            showComplete();
            return;
        } else if ("".equals(Des)) {
            toast("描述不能为空");
            showComplete();
            return;
        } else {
            //必选参数
            mParamsMap.put("UserID", UserID);
            mParamsMap.put("Role", Role);//新增的用户角色
            mParamsMap.put("UserName", UserName);//新增的用户名
            mParamsMap.put("Password", Password);//新增用户的密码
            mParamsMap.put("Des", Des);//新增用户的描述
            mParamsMap.put("CanUSE", CanUSE); //是否激活
            //已下是可选参数
            mParamsMap.put("UserMan", UserMan);
            mParamsMap.put("CanPsw", CanPsw);
            mParamsMap.put("SnapVideoRecord", SnapVideoRecord);
            mParamsMap.put("LiveStream", LiveStream);
            mParamsMap.put("DeviceSet", DeviceSet);
            mParamsMap.put("CanNew", CanNew);
            mParamsMap.put("CanEdit", CanEdit);
            mParamsMap.put("CanDelete", CanDelete);
            mParamsMap.put("CanPrint", CanPrint);
            mParamsMap.put("UnPrinted", UnPrinted);
            mParamsMap.put("ExportRecord", ExportRecord);
            mParamsMap.put("ExportVideo", ExportVideo);
            mParamsMap.put("ExportImage", ExportImage);
            mParamsMap.put("CanBackup", CanBackup);
            mParamsMap.put("OnlySelf", OnlySelf);
            mParamsMap.put("VideoSet", VideoSet);
            mParamsMap.put("HospitalInfo", HospitalInfo);
            mParamsMap.put("ReportStyle", ReportStyle);
            mParamsMap.put("SeatAdjust", SeatAdjust);
            showLoading();
            OkHttpUtils.post()
                    .url(mBaseUrl + HttpConstant.UserManager_AddUser)
                    .params(mParamsMap)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            showError(new StatusLayout.OnRetryListener() {
                                @Override
                                public void onRetry(StatusLayout layout) {
                                    toast("请求错误");
                                }
                            });
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            showComplete();
                            if ("" != response) {
                                UserDeletedBean mBean = mGson.fromJson(response, UserDeletedBean.class);
                                toast("添加成功");
                                if (mBean.getCode().equals("0")) {
                                    EventBus.getDefault().post(new RefreshUserListEvent(true));

                                }
                            } else {
                                showError(listener -> {
                                    sendRequest();
                                });
                            }
                        }
                    });

        }


    }

    private void responseListener() {
        userMan01.setOnCheckedChangeListener(this);
        CanPsw01.setOnCheckedChangeListener(this);
        SnapVideoRecord01.setOnCheckedChangeListener(this);
        LiveStream02.setOnCheckedChangeListener(this);
        DeviceSet02.setOnCheckedChangeListener(this);
        CanNew02.setOnCheckedChangeListener(this);
        CanEdit03.setOnCheckedChangeListener(this);
        CanDelete03.setOnCheckedChangeListener(this);
        CanPrint03.setOnCheckedChangeListener(this);
        UnPrinted04.setOnCheckedChangeListener(this);
        ExportRecord04.setOnCheckedChangeListener(this);
        ExportVideo04.setOnCheckedChangeListener(this);
        ExportImage05.setOnCheckedChangeListener(this);
        CanBackup05.setOnCheckedChangeListener(this);
        OnlySelf05.setOnCheckedChangeListener(this);
        VideoSet06.setOnCheckedChangeListener(this);
        HospitalInfo06.setOnCheckedChangeListener(this);
        ReportStyle06.setOnCheckedChangeListener(this);
        SeatAdjust07.setOnCheckedChangeListener(this);

        mIVReloType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 单选对话框
                new SelectDialog.Builder(AddUserActivity.this)
                        .setTitle("请选择")//0:管理员,1:操作员,2:普通用户,3:自定义
                        .setList("管理员", "操作员", "普通用户", "自定义")
                        // 设置单选模式
                        .setSingleSelect()
                        // 设置默认选中
                        .setSelect(0)
                        .setListener(new SelectDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                String position = data.toString().substring(1, 2);
                                String str = data.get(Integer.parseInt(position));
                                Role = position;
                                mReloType.setText(str + "");
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        })
                        .show();

            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getId()) {
                    case R.id.radio_btn_add_open:
                        CanUSE = "1";
                        break;
                    case R.id.radio_btn_add_close:
                        CanUSE = "0";
                        break;
                }
            }
        });


        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
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