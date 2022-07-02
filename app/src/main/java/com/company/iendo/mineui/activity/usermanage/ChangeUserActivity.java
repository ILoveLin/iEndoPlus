package com.company.iendo.mineui.activity.usermanage;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.UserDeletedBean;
import com.company.iendo.bean.UserDetailBean;
import com.company.iendo.bean.event.RefreshUserListEvent;
import com.company.iendo.other.Constants;
import com.company.iendo.other.HttpConstant;
import com.company.iendo.ui.dialog.InputDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.MD5ChangeUtil;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.mmkv.MMKV;
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
 * desc   : 修改用户权限的界面
 */
public final class ChangeUserActivity extends AppActivity implements StatusAction, CompoundButton.OnCheckedChangeListener {
    private StatusLayout mStatusLayout;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TitleBar mTitleBar;
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
    private String Role;  //角色权限类型,表现形式:0,1,2,3  而不是中文
    private Button mBtnSave, mBtnChangePassword;
    private String cUserID;
    private String mLoginRole;
    private String userID;
    private String changedUserRelo;
    private TextView mUserNameView;
    private RadioButton mRadioOpen;
    private RadioButton mRadioClose;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_eidt;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mTitleBar = findViewById(R.id.userlist_titlebar);
        mSmartRefreshLayout = findViewById(R.id.rl_userlist_refresh);
        mRecyclerView = findViewById(R.id.rv_userlist_recyclerview);
        mBtnSave = findViewById(R.id.btn_login_save);
        mBtnChangePassword = findViewById(R.id.btn_login_change_password);

        mUserNameView = findViewById(R.id.tv_current_name);
        //角色,描述
        mIVReloType = findViewById(R.id.user_iv_relo_type);
        mReloType = findViewById(R.id.user_et_relo_type);
        mUserDesc = findViewById(R.id.user_msg);
        //状态
        mRadioGroup = findViewById(R.id.radio_add_group);
        mRadioOpen = findViewById(R.id.radio_btn_add_open);
        mRadioClose = findViewById(R.id.radio_btn_add_close);
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
        cUserID = getIntent().getStringExtra("cUserID");
        changedUserRelo = getIntent().getStringExtra("changedUserRelo");
        userID = (String) SharePreferenceUtil.get(ChangeUserActivity.this, SharePreferenceUtil.Current_Login_UserID, "");
        mLoginRole = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Login_Role, "2");
        responseListener();
    }

    private void sendRequest() {
        getRequestParamsToSendRequest();

    }

    /**
     * 设置界面数据
     *
     * @param mBean
     */
    private void setLayoutData(UserDetailBean mBean) {
        UserDetailBean.DataDTO.PurviewDTO purviewBean = mBean.getData().getPurview();
        UserDetailBean.DataDTO.UserDTO userBean = mBean.getData().getUser();
        /**
         * 设置描述
         */
        String userName = userBean.getUserName();
        String des = userBean.getDes();
        int role = userBean.getRole();
        //是否激活
        boolean canUSE = userBean.isCanUSE();
        if (canUSE) {//激活
            mRadioOpen.setChecked(true);
            mRadioClose.setChecked(false);
            CanUSE = "1";
        } else {//冻结
            mRadioOpen.setChecked(false);
            mRadioClose.setChecked(true);
            CanUSE = "0";
        }
        mUserNameView.setText("" + userName);
        mUserDesc.setText("" + des);
        switch (role) {
            case 0:
                if ("Admin".equals(userName)) {
                    mReloType.setText("超级管理员");

                } else {
                    mReloType.setText("管理员");
                }
                Role = "0";
                break;
            case 1:
                mReloType.setText("操作员");
                Role = "1";
                break;
            case 2:
                mReloType.setText("普通用户");
                Role = "2";
                break;
            case 3:
                mReloType.setText("自定义");
                Role = "3";
                break;
        }


        /**
         * 设置权限
         */

        //用户管理
        boolean userMan = purviewBean.isUserMan();
        if (userMan) {
            UserMan = "1";
            userMan01.setChecked(true);
        } else {
            UserMan = "0";
            userMan01.setChecked(false);

        }
        //设置口令
        boolean canPsw = purviewBean.isCanPsw();
        if (canPsw) {
            CanPsw = "1";
            CanPsw01.setChecked(true);
        } else {
            CanPsw = "0";
            CanPsw01.setChecked(false);

        }
        //拍照录像
        boolean snapVideoRecord = purviewBean.isSnapVideoRecord();
        if (snapVideoRecord) {
            SnapVideoRecord = "1";
            SnapVideoRecord01.setChecked(true);
        } else {
            SnapVideoRecord = "0";
            SnapVideoRecord01.setChecked(false);

        }
        //直播
        boolean liveStream = purviewBean.isLiveStream();
        if (liveStream) {
            LiveStream = "1";
            LiveStream02.setChecked(true);
        } else {
            LiveStream = "0";
            LiveStream02.setChecked(false);

        }
        //喷吸吹设置
        boolean deviceSet = purviewBean.isDeviceSet();
        if (deviceSet) {
            DeviceSet = "1";
            DeviceSet02.setChecked(true);
        } else {
            DeviceSet = "0";
            DeviceSet02.setChecked(false);
        }
        //登记病人
        boolean canNew = purviewBean.isCanNew();
        if (canNew) {
            CanNew = "1";
            CanNew02.setChecked(true);
        } else {
            CanNew = "0";
            CanNew02.setChecked(false);
        }
        //修改病人
        boolean canEdit = purviewBean.isCanEdit();
        if (canEdit) {
            CanEdit = "1";
            CanEdit03.setChecked(true);
        } else {
            CanEdit = "0";
            CanEdit03.setChecked(false);
        }
        //删除病人
        boolean canDelete = purviewBean.isCanDelete();
        if (canDelete) {
            CanDelete = "1";
            CanDelete03.setChecked(true);
        } else {
            CanDelete = "0";
            CanDelete03.setChecked(false);
        }
        //打印病例
        boolean canPrint = purviewBean.isCanPrint();
        if (canPrint) {
            CanPrint = "1";
            CanPrint03.setChecked(true);
        } else {
            CanPrint = "0";
            CanPrint03.setChecked(false);
        }
        //仅限未打印病例
        boolean unPrinted = purviewBean.isUnPrinted();
        if (unPrinted) {
            UnPrinted = "1";
            UnPrinted04.setChecked(true);
        } else {
            UnPrinted = "0";
            UnPrinted04.setChecked(false);
        }
        //导出病历
        boolean exportRecord = purviewBean.isExportRecord();
        if (exportRecord) {
            ExportRecord = "1";
            ExportRecord04.setChecked(true);
        } else {
            ExportRecord = "0";
            ExportRecord04.setChecked(false);
        }
        //导出录像
        boolean exportVideo = purviewBean.isExportVideo();
        if (exportVideo) {
            ExportVideo = "1";
            ExportVideo04.setChecked(true);
        } else {
            ExportVideo = "0";
            ExportVideo04.setChecked(false);
        }
        //导出图片
        boolean exportImage = purviewBean.isExportImage();
        if (exportImage) {
            ExportImage = "1";
            ExportImage05.setChecked(true);
        } else {
            ExportImage = "0";
            ExportImage05.setChecked(false);
        }
        //备份数据
        boolean canBackup = purviewBean.isCanBackup();
        if (canBackup) {
            CanBackup = "1";
            CanBackup05.setChecked(true);
        } else {
            CanBackup = "0";
            CanBackup05.setChecked(false);
        }
        //仅限本人病例
        boolean onlySelf = purviewBean.isOnlySelf();
        if (onlySelf) {
            OnlySelf = "1";
            OnlySelf05.setChecked(true);
        } else {
            OnlySelf = "0";
            OnlySelf05.setChecked(false);
        }
        //视频设置
        boolean videoSet = purviewBean.isVideoSet();
        if (videoSet) {
            VideoSet = "1";
            VideoSet06.setChecked(true);
        } else {
            VideoSet = "0";
            VideoSet06.setChecked(false);
        }
        //医院信息
        boolean hospitalInfo = purviewBean.isHospitalInfo();
        if (hospitalInfo) {
            HospitalInfo = "1";
            HospitalInfo06.setChecked(true);
        } else {
            HospitalInfo = "0";
            HospitalInfo06.setChecked(false);
        }
        //报告样式
        boolean reportStyle = purviewBean.isReportStyle();
        if (reportStyle) {
            ReportStyle = "1";
            ReportStyle06.setChecked(true);
        } else {
            ReportStyle = "0";
            ReportStyle06.setChecked(false);
        }
        //座椅操作
        boolean seatAdjust = purviewBean.isSeatAdjust();
        if (seatAdjust) {
            SeatAdjust07.setChecked(true);
            SeatAdjust = "1";
        } else {
            SeatAdjust07.setChecked(false);
            SeatAdjust = "0";
        }

    }

    private void getRequestParamsToSendRequest() {

        mParamsMap = new HashMap<>();

        //是否激活
        if (mRadioOpen.isChecked()) {
            CanUSE = "1";
        } else if (mRadioClose.isChecked()) {
            CanUSE = "0";
        }

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
        String mReloTypeString = mReloType.getText().toString().trim();

        if ("".equals(mReloTypeString)) {
            toast("角色不能为空");
            showComplete();
            return;
        } else {
            //必选参数
            mParamsMap.put("oUserID", userID);//当前用户ID
            mParamsMap.put("cUserID", cUserID);//被修改用户ID
            mParamsMap.put("Role", Role);//新增的用户角色
            //已下是可选参数
            mParamsMap.put("CanUSE", CanUSE); //是否激活
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
                    .url(mBaseUrl + HttpConstant.UserManager_changePurviewDetail)
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
                                LogUtils.e("用户管理===编辑=" + mBean.toString());
                                if (mBean.getCode().equals("0")) {
                                    String typeRelo = mReloType.getText().toString();
                                    if ("超级管理员".equals(typeRelo)) {
                                        toast("超级管理员不能被修改");
                                    } else {
                                        toast("修改成功");
                                    }
                                } else {
                                    toast("修改失败");
                                }
                                EventBus.getDefault().post(new RefreshUserListEvent(true));
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

        //获取当前用户权限数据
        sendGetDataRequest();

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
                showDeleteDialog();
            }
        });

        mIVReloType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialogIconAnim(true, mIVReloType);
                // 单选对话框
                new SelectDialog.Builder(ChangeUserActivity.this)
                        .setTitle("请选择")//0:管理员,1:操作员,2:普通用户,3:自定义
                        .setList("管理员", "操作员", "普通用户", "自定义")
                        // 设置单选模式
                        .setSingleSelect()
                        // 设置默认选中
                        .setSelect(0)
                        .setCanceledOnTouchOutside(false)
                        .setListener(new SelectDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                String position = data.toString().substring(1, 2);
                                String str = data.get(Integer.parseInt(position));
                                Role = position;
                                mReloType.setText(str + "");
                                setDefaultReloData(str);
                                startDialogIconAnim(false, mIVReloType);

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                startDialogIconAnim(false, mIVReloType);
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

        //修改权限,保存
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        //修改密码
        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MMKV mmkv = MMKV.defaultMMKV();
                if (mmkv.decodeBool(Constants.KEY_CanPsw)) {
                    showChangePasswordDialog();
                } else {
                    toast("暂无权限");
                }

            }
        });
    }


    /**
     * 设置不同角色,默认的权限
     *
     * @param str
     */
    private void setDefaultReloData(String str) {
        switch (str) {
            case "管理员":
                userMan01.setChecked(true);
                CanPsw01.setChecked(true);
                SnapVideoRecord01.setChecked(true);
                LiveStream02.setChecked(true);
                DeviceSet02.setChecked(true);
                CanNew02.setChecked(true);
                CanEdit03.setChecked(true);
                CanDelete03.setChecked(true);
                CanPrint03.setChecked(true);
                UnPrinted04.setChecked(false);
                ExportRecord04.setChecked(true);
                ExportVideo04.setChecked(true);
                ExportImage05.setChecked(true);
                CanBackup05.setChecked(true);
                OnlySelf05.setChecked(false);
                VideoSet06.setChecked(true);
                HospitalInfo06.setChecked(true);
                ReportStyle06.setChecked(true);
                SeatAdjust07.setChecked(true);
                break;
            case "操作员":
                userMan01.setChecked(false);
                CanPsw01.setChecked(false);
                SnapVideoRecord01.setChecked(false);
                LiveStream02.setChecked(false);
                DeviceSet02.setChecked(false);
                CanNew02.setChecked(true);
                CanEdit03.setChecked(true);
                CanDelete03.setChecked(true);
                CanPrint03.setChecked(true);
                UnPrinted04.setChecked(false);
                ExportRecord04.setChecked(false);
                ExportVideo04.setChecked(false);
                ExportImage05.setChecked(false);
                CanBackup05.setChecked(true);
                OnlySelf05.setChecked(true);
                VideoSet06.setChecked(false);
                HospitalInfo06.setChecked(false);
                ReportStyle06.setChecked(true);
                SeatAdjust07.setChecked(false);
                break;
            case "普通用户":
                userMan01.setChecked(false);
                CanPsw01.setChecked(false);
                SnapVideoRecord01.setChecked(false);
                LiveStream02.setChecked(false);
                DeviceSet02.setChecked(false);
                CanNew02.setChecked(false);
                CanEdit03.setChecked(false);
                CanDelete03.setChecked(false);
                CanPrint03.setChecked(true);
                UnPrinted04.setChecked(false);
                ExportRecord04.setChecked(false);
                ExportVideo04.setChecked(false);
                ExportImage05.setChecked(false);
                CanBackup05.setChecked(false);
                OnlySelf05.setChecked(false);
                VideoSet06.setChecked(false);
                HospitalInfo06.setChecked(false);
                ReportStyle06.setChecked(false);
                SeatAdjust07.setChecked(false);
                break;
            case "自定义":
                userMan01.setChecked(false);
                CanPsw01.setChecked(false);
                SnapVideoRecord01.setChecked(false);
                LiveStream02.setChecked(false);
                DeviceSet02.setChecked(false);
                CanNew02.setChecked(false);
                CanEdit03.setChecked(false);
                CanDelete03.setChecked(false);
                CanPrint03.setChecked(false);
                UnPrinted04.setChecked(false);
                ExportRecord04.setChecked(false);
                ExportVideo04.setChecked(false);
                ExportImage05.setChecked(false);
                CanBackup05.setChecked(false);
                OnlySelf05.setChecked(false);
                VideoSet06.setChecked(false);
                HospitalInfo06.setChecked(false);
                ReportStyle06.setChecked(false);
                SeatAdjust07.setChecked(false);
                break;
        }


    }

    /**
     * 获取当前被修改用户的详情数据
     */
    private void sendGetDataRequest() {
        OkHttpUtils.get()
                .url(mBaseUrl + HttpConstant.UserManager_userDetail)
                .addParams("UserID", cUserID)
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
                            UserDetailBean mBean = mGson.fromJson(response, UserDetailBean.class);

                            LogUtils.e("用户管理===获取详情=" + mBean.toString());
                            String code = mBean.getCode() + "";
                            if (code.equals("0")) {
                                setLayoutData(mBean);
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
     * 修改密码对话框
     */
    private void showChangePasswordDialog() {
        new InputDialog.Builder(this)
                .setTitle("提示")
                .setHint("请输入新密码")
                .setCancel("取消")
                .setConfirm("确定")
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String password) {
                        sendChangePasswordRequest(MD5ChangeUtil.Md5_32(password));
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();
    }

    /**
     * 发送其他人修改密码请求
     *
     * @param password
     */
    private void sendChangePasswordRequest(String password) {
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_ChangeElsePassword)
                .addParams("userID", userID)//自己的ID
                .addParams("changedUserID", cUserID)//被修改用户ID
                .addParams("userRelo", mLoginRole)//自己的权限
                .addParams("changedUserRelo", changedUserRelo + "")//被修改用户的权限
                .addParams("changedPassword", password)//新密码
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
                            if (mBean.getCode().equals("0")) {
                                toast("修改成功");
                            } else if (mBean.getCode().equals("1")) {
                                toast(mBean.getMsg() + "");

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
     * 删除对话框
     */
    private void showDeleteDialog() {
        new MessageDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除用户吗?")
                .setConfirm("确定")
                .setCancel("取消")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        sendDeleteRequest();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                }).show();

    }

    /**
     * 删除请求
     */
    private void sendDeleteRequest() {
        showLoading();
        OkHttpUtils.post()
                .url(mBaseUrl + HttpConstant.UserManager_Delete)
                .addParams("DeleteUserID", cUserID)//被删除用户的ID
                .addParams("CurrentUserID", mUserID)//当前用户ID
                .addParams("CurrentRelo", mLoginRole + "")//当前用户权限
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
                            if (mBean.getCode().equals("0")) {
                                toast("删除成功");
                                EventBus.getDefault().post(new RefreshUserListEvent(true));
                                finish();
                            } else {
                                toast("" + mBean.getMsg());
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