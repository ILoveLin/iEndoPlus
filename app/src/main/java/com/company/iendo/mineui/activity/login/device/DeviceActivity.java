package com.company.iendo.mineui.activity.login.device;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.LoginActivity;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceAdapter;
import com.company.iendo.ui.dialog.InputDeviceDialog;
import com.company.iendo.ui.dialog.MessageDialog;
import com.company.iendo.ui.dialog.ModifyDeviceDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.dialog.SelectModifyTypeDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.ClearEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 15:59
 * desc：设备添加界面
 */
public class DeviceActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, BaseAdapter.OnChildClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private StatusLayout mStatusLayout;
    private TitleBar mDeviceBar;
    private List<DeviceDBBean> mDataLest = new ArrayList<>();
    private DeviceAdapter mAdapter;
    private DeviceDBBean mDeviceDBBean;
    private String selectedData01;
    private ModifyDeviceDialog.Builder mChangeDialog;
    private InputDeviceDialog.Builder mCurrentChoseDialog;    //添加新设备的时候,再次选择不同类型的情况下
    private DeviceDBBean mDBBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_device_refresh);
        mRecyclerView = findViewById(R.id.rv_device_recyclerview);
        mStatusLayout = findViewById(R.id.device_hint);
        mDeviceBar = findViewById(R.id.device_bar);
    }

    @Override
    protected void initData() {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        mDataLest.clear();
        mDataLest.addAll(deviceDBBeans);
        mAdapter = new DeviceAdapter(this, mRecyclerView, mDataLest);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnChildClickListener(R.id.linear_item, this);
        mAdapter.setOnChildClickListener(R.id.tv_video_title, this);
        mAdapter.setOnChildClickListener(R.id.tv_video_type, this);
        mAdapter.setOnChildClickListener(R.id.tv_video_make, this);
        mAdapter.setOnChildClickListener(R.id.delBtn, this);
        mAdapter.setOnChildClickListener(R.id.reInputBtn, this);
//        mAdapter.setOnChildClickListener(R.id.iv_item_select, this);
        mAdapter.setData(mDataLest);
        mRecyclerView.setAdapter(mAdapter);
        refreshRecycleViewData();
        mDeviceBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                finish();

            }

            @Override
            public void onTitleClick(View view) {

            }

            @Override
            public void onRightClick(View view) {
                showSelectDialog(view);
            }
        });
    }

    /**
     * 输入方式选择
     *
     * @param view
     */
    private void showSelectDialog(View view) {
        // 菜单弹窗
        new ListPopup.Builder(this)
                .setList("扫一扫", "填一填")
                .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
                    if ("填一填".equals(str)) {
                        showMultiDialog(str);
                    } else {
                        toast("扫一扫");
                    }
                })
                .showAsDropDown(view);


    }

    /**
     * 填一填,连续弹出两个对话框
     * 弹出第一个对话框
     *
     * @param str
     */
    private void showMultiDialog(String str) {
        // 单选对话框
        new SelectDialog.Builder(this)
                .setTitle("请选择设备类型")
                .setList("HD3", "一体机", "耳鼻喉治疗台")
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(0)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        LogUtils.e("showMultiDialog===" + data.toString()); //{0=HD3}
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        LogUtils.e("showMultiDialog===str==" + str); //{0=HD3}
                        showMulti2Dialog(str);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();


    }

    /**
     * 弹出第一个对话框--当前选择类型
     *
     * @param str 当前选择类型
     */
    private void showMulti2Dialog(String str) {
        switch (str) {
            case "HD3":
                // 输入对话框
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                // 标题可以不用填写
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent("HD3")
                        .setDeviceNoteContent("HD3备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("Admin")
                        .setPasswordContent("12345")
                        .setLivePortContent("80")
                        .setTypeContent("HD3")
                        // 提示可以不用填写
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setCanceledOnTouchOutside(false)
                        .setListener(new InputDeviceDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage,
                                                  String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort,
                                                  String mSocketPort, String mLivePort, String mMicPort, String mDeviceType) {
                                //添加设备HD3
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setUsemsg01(mDeviceName);  //设备名
                                deviceDBBean.setUsername(mDeviceCode); //设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setMSelected(false);

                                DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);
                                refreshRecycleViewData();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        })
                        .show();


                //再次选择设备类型的时候,弹出对话框选择
                ClearEditText hd3TypeView = mCurrentChoseDialog.getDeviceTypeView();
                //让EditText失去焦点，然后获取点击事件
                hd3TypeView.setFocusable(false);
                hd3TypeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyTypeDialog(hd3TypeView.getText().toString(), "添加类型");
                    }
                });


                break;
            case "一体机":
                // 输入对话框  mOneDeviceDialog
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                // 标题可以不用填写
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent("一体机")
                        .setDeviceNoteContent("一体机备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("3000")
                        .setLivePortContent("7788")
                        .setMicPortContent("7789")
                        .setTypeContent("一体机")
                        // 提示可以不用填写
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setCanceledOnTouchOutside(false)
                        .setListener(new InputDeviceDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage,
                                                  String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort,
                                                  String mSocketPort, String mLivePort, String mMicPort, String mDeviceType) {
                                //添加设备HD3
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setUsemsg01(mDeviceName);  //设备名
                                deviceDBBean.setUsername(mDeviceCode); //设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setMSelected(false);

                                DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);
                                refreshRecycleViewData();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        })
                        .show();
                //再次选择设备类型的时候,弹出对话框选择
                ClearEditText oneDeviceDialog = mCurrentChoseDialog.getDeviceTypeView();
                //让EditText失去焦点，然后获取点击事件
                oneDeviceDialog.setFocusable(false);
                oneDeviceDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyTypeDialog(oneDeviceDialog.getText().toString(), "添加类型");
                    }
                });
                break;
            case "耳鼻喉治疗台":
                // 输入对话框
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent("耳鼻喉治疗台")
                        .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("3000")
                        .setLivePortContent("7788")
                        .setMicPortContent("7789")
                        .setTypeContent("耳鼻喉治疗台")
                        // 提示可以不用填写
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setCanceledOnTouchOutside(false)
                        .setListener(new InputDeviceDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage,
                                                  String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort,
                                                  String mSocketPort, String mLivePort, String mMicPort, String mDeviceType) {
                                //添加设备HD3
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setUsemsg01(mDeviceName);  //设备名
                                deviceDBBean.setUsername(mDeviceCode); //设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setMSelected(false);
                                DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);
                                refreshRecycleViewData();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        })
                        .show();


                //再次选择设备类型的时候,弹出对话框选择
                ClearEditText eyeProjectDialog = mCurrentChoseDialog.getDeviceTypeView();
                //让EditText失去焦点，然后获取点击事件
                eyeProjectDialog.setFocusable(false);
                eyeProjectDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyTypeDialog(eyeProjectDialog.getText().toString(), "添加类型");
                    }
                });
                break;

        }

    }

    private void refreshRecycleViewData() {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        showComplete();
        mAdapter.setData(deviceDBBeans);
        mAdapter.notifyDataSetChanged();
        int count = mAdapter.getCount();
        if (0 == count) {
            showEmpty();
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://192.168.1.200:3000");

        } else {
            showComplete();
        }
    }


    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        toast(mAdapter.getItem(position).getUsemsg01() + "~~~");
    }

    private int mSelectedPos = -1;

    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        switch (childView.getId()) {

            case R.id.tv_video_title:

                break;
            case R.id.linear_item:

                List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
                DeviceDBBean selectedItemBean = mAdapter.getItem(position);
                //点击就是选中
                selectedItemBean.setId(selectedItemBean.getId());
                selectedItemBean.setMSelected(true);
                DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, selectedItemBean);


                List<DeviceDBBean> deviceDBBeans1 = DeviceDBUtils.queryAll(DeviceActivity.this);
                DeviceDBBean deviceDBBean1 = deviceDBBeans1.get(0);
                LogUtils.e(deviceDBBean1.toString() + "========AAAAA===选中开始点击了===");

                String id = selectedItemBean.getId() + "";
                LogUtils.e(id + "========id===选中开始点击了===");

                for (int i = 0; i < deviceDBBeans.size(); i++) {


                    DeviceDBBean deviceDBBean = deviceDBBeans.get(i);
                    String currentID = deviceDBBean.getId() + "";
                    LogUtils.e(currentID + "========currentID===选中开始点击了===");

                    if (currentID.equals(id)) {
                        deviceDBBean.setMSelected(true);
                        DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);
                    } else {
                        deviceDBBean.setMSelected(false);
                        DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);

                    }
//                    LogUtils.e(deviceDBBean.getId() + "========其他ID===选中开始点击了===");
//
//                    Boolean mSelected = deviceDBBean.getMSelected();
//                    if (!mSelected) {
//                        deviceDBBean.setMSelected(false);
//
//                        DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);
//                    }

                }
                refreshRecycleViewData();
                break;
            case R.id.tv_video_type:

                break;
            case R.id.tv_video_make:


                break;
            case R.id.delBtn:
                showDeleteItemDialog(mAdapter.getItem(position));
                break;
            case R.id.reInputBtn:
                showModifyItemDialog(mAdapter.getItem(position));
                break;
        }

    }


    //删除当前数据
    private void showDeleteItemDialog(DeviceDBBean item) {
        new MessageDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除吗?")
                .setConfirm("确定")
                .setCancel("取消")
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        DeviceDBUtils.delete(DeviceActivity.this, item);
                        refreshRecycleViewData();
                        toast("删除成功");
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();

    }

    /**
     * 修改设备数据----修改当前数据
     *
     * @param item
     */
    private void showModifyItemDialog(DeviceDBBean item) {
        LogUtils.e("修改设备=====getMsg=====" + item.getMsg());
        LogUtils.e("修改设备=====getMsg=====" + item.toString());
        mChangeDialog = new ModifyDeviceDialog.Builder(this);
        mChangeDialog.setTitle("修改设备")
                .setDeviceNameContent(item.getUsemsg01())
                .setDeviceCodeContent(item.getDeviceID())
                .setDeviceNoteContent(item.getMsg())
                .setDeviceIPContent(item.getIp())
                .setAccountContent(item.getUsername())
                .setPasswordContent(item.getPassword())
                .setHttpPortContent(item.getHttpPort())
                .setSocketPortContent(item.getSocketPort())
                .setLivePortContent(item.getLivePort())
                .setMicPortContent(item.getMicPort())
                .setTypeContent(item.getType())
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setCanceledOnTouchOutside(false)
                .setListener(new ModifyDeviceDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage, String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort, String mSocketPort,
                                          String mLivePort, String mMicPort, String mDeviceType) {
                        LogUtils.e("不管是更换还是不变都update 数据库,再次刷新界面====");
                        LogUtils.e("不管是更换还是不变都update 数据库,再次刷新界面====");
                        //不管是更换还是不变都update 数据库,再次刷新界面
                        item.setUsemsg01(mDeviceName);  //设备名
                        item.setUsername(mDeviceCode); //设备码
                        item.setMsg(mDeviceNoteMessage);//备注信息
                        item.setIp(mDeviceIP);          //ip
                        item.setUsername(mDeviceAccount);//设备账号
                        item.setPassword(mDevicePassword);//设备密码
                        item.setHttpPort(mHttpPort);    //http端口
                        item.setSocketPort(mSocketPort);//socket端口
                        item.setLivePort(mLivePort);   //直播端口
                        item.setMicPort(mMicPort);     //语音端口
                        item.setType(mDeviceType);     //设备类型
                        DeviceDBUtils.update(DeviceActivity.this, item);
                        refreshRecycleViewData();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();

        //再次选择设备类型的时候,弹出对话框选择
        ClearEditText deviceTypeView = mChangeDialog.getDeviceTypeView();
        //让EditText失去焦点，然后获取点击事件
        deviceTypeView.setFocusable(false);
        deviceTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyTypeDialog(deviceTypeView.getText().toString(), "修改类型");
            }
        });


    }

    /**
     * 1,修改设备的时候切换设备类型
     * 2,添加设备的时候切换设备类型
     *
     * @param deviceType 设备类型
     * @param type       当前是修改类型的时候点击设备类型选择,  还是添加类型的时候点击设备类型选择
     */
    private void showModifyTypeDialog(String deviceType, String type) {
        new SelectModifyTypeDialog.Builder(this)
                .setTitle("请选择设备类型")
                .setList("HD3", "一体机", "耳鼻喉治疗台")
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

                        //刷新选择类型后,的默认数据---修改类型,或者添加类型
                        post(() -> {
                            setChangeTypeData(str, type);
                        });
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();


    }

    /**
     * type 当前是修改类型的时候点击设备类型选择,  还是添加类型的时候点击设备类型选择
     */
    private void setChangeTypeData(String str, String type) {
        switch (str) {
            case "HD3":
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent("HD3")
                            .setDeviceNoteContent("")
                            .setDeviceNoteContent("HD3备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("Admin")
                            .setPasswordContent("12345")
                            .setLivePortContent("80")
                            .setTypeContent("HD3")
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent("HD3")
                            .setDeviceNoteContent("HD3备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("Admin")
                            .setPasswordContent("12345")
                            .setLivePortContent("80")
                            .setTypeContent("HD3")
                            .show();
                }

                break;
            case "一体机":
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent("一体机")
                            .setDeviceNoteContent("")
                            .setDeviceNoteContent("一体机备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("3000")
                            .setLivePortContent("7788")
                            .setMicPortContent("7789")
                            .setTypeContent("一体机")
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent("一体机")
                            .setDeviceNoteContent("一体机备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("3000")
                            .setLivePortContent("7788")
                            .setMicPortContent("7789")
                            .setTypeContent("一体机")
                            .show();
                }


                break;
            case "耳鼻喉治疗台":
                if ("修改类型".equals(type)) {

                    mChangeDialog.setDeviceNameContent("耳鼻喉治疗台")
                            .setDeviceNoteContent("")
                            .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("3000")
                            .setLivePortContent("7788")
                            .setMicPortContent("7789")
                            .setTypeContent("耳鼻喉治疗台")
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent("耳鼻喉治疗台")
                            .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("3000")
                            .setLivePortContent("7788")
                            .setMicPortContent("7789")
                            .setTypeContent("耳鼻喉治疗台")
                            .show();
                }

                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("========当前设备的备注信息~~~~====DeviceActivity==onDestroy===");
        //把当前选择的itembean的数据信息存到sp里面去
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        for (int i = 0; i < deviceDBBeans.size(); i++) {
            if (deviceDBBeans.get(i).getMSelected()) {
                mDBBean = deviceDBBeans.get(i);
                continue;
            }


        }
        if (null != mDBBean) {
            LogUtils.e("添加病例=== mDBBean.toString()===" +  mDBBean.getUsemsg01());   //通过此字段判断EndoType
            switch (mDBBean.getUsemsg01()) {
                case "HD3":
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, null != mDBBean.getEndoType() ? mDBBean.getEndoType() : "1");
                    break;
                case "一体机":
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, null != mDBBean.getEndoType() ? mDBBean.getEndoType() : "2");
                    break;
                case "耳鼻喉治疗台":
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, null != mDBBean.getEndoType() ? mDBBean.getEndoType() : "3");
                    break;

            }
            LogUtils.e("添加病例=== mDBBean.getEndoType()===" +  mDBBean.getEndoType() );
            //这个主键ID是需要绑定用户表中的deviceID,确保是这个设备下,离线模式能通过id查询绑定用户
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_MainID, mDBBean.getId() + "");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceID, null != mDBBean.getDeviceID() ? mDBBean.getDeviceID() : "1");  //为null的时候全部给1表示
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_IP, mDBBean.getIp());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_HttpPort, mDBBean.getHttpPort());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_SocketPort, null != mDBBean.getSocketPort() ? mDBBean.getSocketPort() : "1");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_LivePort, mDBBean.getLivePort());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_MicPort, null != mDBBean.getMicPort() ? mDBBean.getMicPort() : "1");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceUsername, mDBBean.getUsername());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DevicePassword, mDBBean.getPassword());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, mDBBean.getType());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Usemsg01, mDBBean.getUsemsg01());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_MSelected, mDBBean.getMSelected());

            //http://192.168.66.42:8008
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://" + mDBBean.getIp() + ":" + mDBBean.getHttpPort());
            String mBaseUrl = (String) SharePreferenceUtil.get(DeviceActivity.this, SharePreferenceUtil.Current_BaseUrl, "111");
            LogUtils.e("========当前设备的备注信息~~~~====DeviceActivity==mBaseUrl===" + mBaseUrl);
        }

    }

    //
//
//    @Override
//    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mRefreshLayout.finishLoadMore();
//            mAdapter.setLastPage(true);
//            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
//
//        }, 1000);
//    }
//
//    @Override
//    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//        postDelayed(() -> {
//            mAdapter.clearData();
//            mAdapter.setData(mDataLest);
//            mRefreshLayout.finishRefresh();
//        }, 1000);
//    }
}
