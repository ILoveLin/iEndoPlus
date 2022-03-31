package com.company.iendo.mineui.activity.login.device;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.RefreshEvent;
import com.company.iendo.bean.event.RefreshDeviceListEvent;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.ZXingActivity;
import com.company.iendo.mineui.activity.casemanage.ImageReportActivity;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceAdapter;
import com.company.iendo.mineui.activity.login.device.search.DeviceSearchActivity;
import com.company.iendo.other.Constants;
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
import com.hjq.base.action.AnimAction;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.WrapRecyclerView;
import com.hjq.widget.view.ClearEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/1 15:59
 * desc：设备添加界面
 * 设备类型（一代一代一体机）     endtype 3     扫码的结果对应数字是7
 * 设备类型（耳鼻喉治疗台）   endtype 3     扫码的结果对应数字是8
 * 设备类型（妇科治疗台）    //4            扫码的结果对应数字是9
 * 设备类型（泌尿治疗台）   //6            扫码的结果对应数字是10
 */
public class DeviceActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener, BaseAdapter.OnChildClickListener, BaseAdapter.OnChildLongClickListener {
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
    private String currentDeviceCode;
    private String currentChangeType;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
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
        mAdapter.setOnChildLongClickListener(R.id.relative_item, this);
        mAdapter.setOnChildLongClickListener(R.id.iv_current_chose_image, this);
        mAdapter.setOnChildClickListener(R.id.relative_item, this);
        mAdapter.setOnChildClickListener(R.id.iv_current_chose_image, this);
        mAdapter.setOnChildClickListener(R.id.tv_current_chose_msg, this);
        mAdapter.setOnChildClickListener(R.id.tv_change, this);
        mAdapter.setOnChildClickListener(R.id.tv_delete, this);
        mAdapter.setData(mDataLest);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DeviceActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        refreshRecycleViewData();
        mDeviceBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                //退出界面清除修改删除功能布局
                dismissChangeDeleteLayout();
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
     * @param recyclerView RecyclerView 对象
     * @param childView    被点击的条目子 View
     * @param position     被点击的条目位置
     * @return
     * @des 不管是长按还是点击
     * 其他item都不必须隐藏修改和删除功能!
     * //点击的item  是当前选择的item 删除修改布局存在,则不隐藏
     */
    @Override
    public boolean onChildLongClick(RecyclerView recyclerView, View childView, int position) {
        dismissChangeDeleteLayout();
        showChangeDeleteLayout(position);
        setChoseItem(position);
        return false;
    }


    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        DeviceDBBean item = mAdapter.getItem(position);
        switch (childView.getId()) {
            case R.id.relative_item:
            case R.id.iv_current_chose_image:
                /**
                 * 点击的item  是当前选择的item 删除修改布局存在,则不隐藏
                 */
                if (!"true".equals(item.getUsemsg01())) {
                    dismissChangeDeleteLayout();
                }
                dismissChangeDeleteLayout();
                setChoseItem(position);

                break;
            case R.id.tv_change:
                showModifyItemDialog(mAdapter.getItem(position));
                break;
            case R.id.tv_delete:
                showDeleteItemDialog(mAdapter.getItem(position));
                break;
        }

    }

    /**
     * 输入方式选择
     *
     * @param view
     */
    private void showSelectDialog(View view) {
        // 菜单弹窗
        new ListPopup.Builder(this)
                .setList("搜一搜", "扫一扫", "填一填")
                .setListener((ListPopup.OnListener<String>) (popupWindow, position, str) -> {
                    if ("填一填".equals(str)) {
                        showComplete();
                        showMultiDialog(str);
                    } else if ("扫一扫".equals(str)) {
                        showComplete();
                        GoToZXingInput();
                    } else {
                        startActivity(DeviceSearchActivity.class);
                    }
                })
                .setGravity(Gravity.CENTER_VERTICAL)
                .setAutoDismiss(true)
                .setOutsideTouchable(false) //80dp
                .setAnimStyle(AnimAction.ANIM_SCALE)
                .showAsDropDown(view);
    }

    private void GoToZXingInput() {
        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
                //.permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Intent intent = new Intent(getActivity(), ZXingActivity.class);
//                            intent.putExtra("currentUsername", currentUsername);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getActivity(), permissions);
                        } else {
                            toast("获取存储权限失败");
                        }
                    }
                });
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
                .setList(Constants.Type_V1_YiTiJi, Constants.Type_EarNoseTable, Constants.Type_FuKeTable, Constants.Type_MiNiaoTable)
//                .setList(Constants.Type_V1_YiTiJi, Constants.Type_EarNoseTable, Constants.Type_FuKeTable, Constants.Type_MiNiaoTable)
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(1)
                .setListener(new SelectDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        LogUtils.e("showMultiDialog===" + data.toString()); //{0=妇科治疗台}
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        LogUtils.e("showMultiDialog===str==" + str); //{0=妇科治疗台}
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
            case Constants.Type_FuKeTable:
                // 输入对话框
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                // 标题可以不用填写
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent(Constants.Type_FuKeTable)
                        .setDeviceNoteContent("妇科治疗台备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("7001")
                        .setLivePortContent("7788")
                        .setSocketPortContent("8005")
                        .setMicPortContent("7789")
                        .setTypeContent(Constants.Type_FuKeTable)
                        // 提示可以不用填写
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setCanceledOnTouchOutside(false)
                        .setListener(new InputDeviceDialog.OnListener() {

                            //                            @Override  //mDeviceCode  这个是智能搜索之后返回过来的设备码
                            public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage,
                                                  String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort,
                                                  String mSocketPort, String mLivePort, String mMicPort, String mDeviceType) {


                                //添加设备妇科治疗台
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setDeviceName(mDeviceName);  //设备名
                                deviceDBBean.setDeviceCode(mDeviceCode); //设备码---//mDeviceCode  这个是智能搜索之后返回过来的设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setType_num(getTypeNum(mDeviceType)); //设备类型数字
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
                ClearEditText et = mCurrentChoseDialog.getDeviceTypeView();
                //让EditText失去焦点，然后获取点击事件
                et.setFocusable(false);
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyTypeDialog(et.getText().toString(), "添加类型");
                    }
                });


                break;
            case Constants.Type_V1_YiTiJi:
                // 输入对话框  mOneDeviceDialog
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                // 标题可以不用填写
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent(Constants.Type_V1_YiTiJi)
                        .setDeviceNoteContent("一代一体机备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("7001")
                        .setLivePortContent("7788")
                        .setSocketPortContent("8005")
                        .setMicPortContent("7789")
                        .setTypeContent(Constants.Type_V1_YiTiJi)
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
                                //添加设备妇科治疗台
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setDeviceName(mDeviceName);  //设备名
                                deviceDBBean.setDeviceCode(mDeviceCode); //设备码---//mDeviceCode  这个是智能搜索之后返回过来的设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setType_num(getTypeNum(mDeviceType)); //设备类型数字

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
            case Constants.Type_EarNoseTable:
                // 输入对话框
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent(Constants.Type_EarNoseTable)
                        .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("7001")
                        .setLivePortContent("7788")
                        .setSocketPortContent("8005")

                        .setMicPortContent("7789")
                        .setTypeContent(Constants.Type_EarNoseTable)
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
                                //添加设备妇科治疗台
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setDeviceName(mDeviceName);  //设备名
                                deviceDBBean.setDeviceCode(mDeviceCode); //设备码---//mDeviceCode  这个是智能搜索之后返回过来的设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setType_num(getTypeNum(mDeviceType)); //设备类型数字

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
            case Constants.Type_MiNiaoTable:
                // 输入对话框
                mCurrentChoseDialog = new InputDeviceDialog.Builder(this);
                mCurrentChoseDialog.setTitle("添加设备")
                        // 内容可以不用填写
                        .setDeviceNameContent(Constants.Type_MiNiaoTable)
                        .setDeviceNoteContent("泌尿治疗台备注信息")
                        .setDeviceIPContent("192.168.1.200")
                        .setAccountContent("root")
                        .setPasswordContent("root")
                        .setHttpPortContent("7001")
                        .setLivePortContent("7788")
                        .setSocketPortContent("8005")
                        .setMicPortContent("7789")
                        .setTypeContent(Constants.Type_MiNiaoTable)
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
                                //添加设备泌尿治疗台
                                DeviceDBBean deviceDBBean = new DeviceDBBean();
                                deviceDBBean.setDeviceName(mDeviceName);  //设备名
                                deviceDBBean.setDeviceCode(mDeviceCode); //设备码---//mDeviceCode  这个是智能搜索之后返回过来的设备码
                                deviceDBBean.setMsg(mDeviceNoteMessage);//备注信息
                                deviceDBBean.setIp(mDeviceIP);          //ip
                                deviceDBBean.setUsername(mDeviceAccount);//设备账号
                                deviceDBBean.setPassword(mDevicePassword);//设备密码
                                deviceDBBean.setHttpPort(mHttpPort);    //http端口
                                deviceDBBean.setSocketPort(mSocketPort);//socket端口
                                deviceDBBean.setLivePort(mLivePort);   //直播端口
                                deviceDBBean.setMicPort(mMicPort);     //语音端口
                                deviceDBBean.setType(mDeviceType);     //设备类型
                                deviceDBBean.setType_num(getTypeNum(mDeviceType)); //设备类型数字

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
                ClearEditText ett = mCurrentChoseDialog.getDeviceTypeView();
                //让EditText失去焦点，然后获取点击事件
                ett.setFocusable(false);
                ett.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModifyTypeDialog(ett.getText().toString(), "添加类型");
                    }
                });
                break;

        }

    }



    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        LogUtils.e("选中设备的String" + mAdapter.getItem(position).toString());
    }

    private int mSelectedPos = -1;


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
        currentDeviceCode = item.getDeviceCode();
        currentChangeType = item.getType();
        mChangeDialog = new ModifyDeviceDialog.Builder(this);
        mChangeDialog.setTitle("修改设备")
                .setDeviceNameContent(item.getDeviceName())
                .setDeviceCodeContent(item.getDeviceCode())
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
                        item.setDeviceName(mDeviceName);  //设备名
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
                        item.setType_num(getTypeNum(mDeviceType)); //设备类型数字

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
                .setList(Constants.Type_V1_YiTiJi, Constants.Type_EarNoseTable, Constants.Type_FuKeTable, Constants.Type_MiNiaoTable)
//                .setList(Constants.Type_V1_YiTiJi, Constants.Type_EarNoseTable, Constants.Type_FuKeTable, Constants.Type_MiNiaoTable)
                // 设置单选模式
                .setSingleSelect()
                // 设置默认选中
                .setSelect(1)
                .setBackgroundDimEnabled(false)
//                .setWidth(ScreenSizeUtil.getScreenWidth(this) /2)
                .setCanceledOnTouchOutside(false)
                .setListener(new SelectModifyTypeDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                        LogUtils.e("showMultiDialog===" + data.toString()); //{0=妇科治疗台}
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        LogUtils.e("showMultiDialog===str==" + str); //{0=妇科治疗台}

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
     * str:当前切换的设备类型,比如:耳鼻喉治疗台
     */
    private void setChangeTypeData(String str, String type) {
        String code = "";
        if(str.equals(currentChangeType)){
            code =currentDeviceCode;
        }else {
            code="";
        }
        switch (str) {
            case Constants.Type_FuKeTable:
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent(Constants.Type_FuKeTable)
                            .setDeviceNoteContent("")
                            .setDeviceCodeContent(code+"")//当修改的时候,选择切换设备类型时候,还是选择同一设备类型,默认使用之前该设备类型的deviceCode
                            .setDeviceNoteContent("妇科治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_FuKeTable)
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent(Constants.Type_FuKeTable)
                            .setDeviceNoteContent("妇科治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_FuKeTable)
                            .show();
                }

                break;
            case Constants.Type_MiNiaoTable:
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent(Constants.Type_MiNiaoTable)
                            .setDeviceNoteContent("")
                            .setDeviceCodeContent(code+"")
                            .setDeviceNoteContent("泌尿治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_MiNiaoTable)
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent(Constants.Type_MiNiaoTable)
                            .setDeviceNoteContent("泌尿治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_MiNiaoTable)
                            .show();
                }

                break;
            case Constants.Type_V1_YiTiJi:
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent(Constants.Type_V1_YiTiJi)
                            .setDeviceNoteContent("")
                            .setDeviceCodeContent(code+"")
                            .setDeviceNoteContent("一代一体机备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_V1_YiTiJi)
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent(Constants.Type_V1_YiTiJi)
                            .setDeviceNoteContent("一代一体机备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_V1_YiTiJi)
                            .show();
                }

                break;
            case Constants.Type_EarNoseTable:
                if ("修改类型".equals(type)) {
                    mChangeDialog.setDeviceNameContent(Constants.Type_EarNoseTable)
                            .setDeviceNoteContent("")
                            .setDeviceCodeContent(code+"")
                            .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_EarNoseTable)
                            .show();
                } else {
                    mCurrentChoseDialog.setDeviceNameContent(Constants.Type_EarNoseTable)
                            .setDeviceNoteContent("耳鼻喉治疗台备注信息")
                            .setDeviceIPContent("192.168.1.200")
                            .setAccountContent("root")
                            .setPasswordContent("root")
                            .setHttpPortContent("7001")
                            .setLivePortContent("7788")
                            .setSocketPortContent("8005")
                            .setMicPortContent("7789")
                            .setTypeContent(Constants.Type_EarNoseTable)
                            .show();
                }
                break;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        //把当前选择的itembean的数据信息存到sp里面去
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        for (int i = 0; i < deviceDBBeans.size(); i++) {
            if (deviceDBBeans.get(i).getMSelected()) {
                mDBBean = deviceDBBeans.get(i);
                continue;
            }


        }
        if (null != mDBBean) {
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceID, mDBBean.getDeviceID() + "");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceCode, mDBBean.getDeviceCode() + "");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
            LogUtils.e("添加病例=== mDBBean.toString()===" + mDBBean.toString());   //通过此字段判断EndoType
            /**
             *mDeviceCode 这个是智能搜索之后返回过来的设备码
             * 需要再搜索完成后创建dialog的时候设置上去,不然为null
             */
            LogUtils.e("添加病例=== mDBBean.getDeviceCode()===" + mDBBean.getDeviceCode());   ///
            LogUtils.e("添加病例=== mDBBean.getType()===" + mDBBean.getType());   //通过此字段判断设备类型(中文)
            LogUtils.e("添加病例=== mDBBean.getType_num()===" + mDBBean.getType_num());   //通过此字段判断设备类型(数字)
            switch (mDBBean.getType()) {
                case Constants.Type_FuKeTable:
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, "4");//妇科
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, Constants.Type_FuKeTable);
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type_Num, mDBBean.getType_num());

                    break;
                case Constants.Type_V1_YiTiJi:
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, "3");//一体机
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, Constants.Type_V1_YiTiJi);
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type_Num, mDBBean.getType_num());


                    break;
                case Constants.Type_EarNoseTable:
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, "3");//耳鼻喉
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, Constants.Type_EarNoseTable);
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type_Num, mDBBean.getType_num());


                    break;
                case Constants.Type_MiNiaoTable:
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, "6");//泌尿
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, Constants.Type_MiNiaoTable);
                    SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type_Num, mDBBean.getType_num());

                    break;

            }


            LogUtils.e("添加病例=== mDBBean.getType()===" + mDBBean.getType());   //通过此字段判断EndoType

            String endotype = (String) SharePreferenceUtil.get(DeviceActivity.this, SharePreferenceUtil.Current_EndoType, "5");
            LogUtils.e("选择的设备=== 存入的设备类型是===" + endotype);
            LogUtils.e("选择的设备=== mDBBean.getEndoType()===" + mDBBean.getEndoType());
            LogUtils.e("选择的设备=== mDBBean.getDeviceID()===" + mDBBean.getDeviceID());
            LogUtils.e("选择的设备=== mDBBean.toString===" + mDBBean.toString());
            //这个主键ID是需要绑定用户表中的deviceID(code码),确保是这个设备下,离线模式能通过此字段查询绑定用户
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceID, mDBBean.getDeviceID() + "");
            /**
             * 设备ID
             * 这个用户是在哪个设备上的     用户和病例都是和设备绑定的
             * 当前选中设备的主键id,因为离线模式下就能通过这个主键id查找这个设备下的所有用户
             * 主键id==deviceID---->下载图片的时候文件夹: 文件夹（设备ID-病例ID）
             */
            String deviceCode = mDBBean.getDeviceCode();//设备码
//            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceID, mDBBean.getId() + "");
            String o = (String) SharePreferenceUtil.get(DeviceActivity.this, SharePreferenceUtil.Current_DeviceID, "");
            LogUtils.e("选择的设备=== 存入的设备id是===" + o);

            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type_Msg, mDBBean.getMsg());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_IP, mDBBean.getIp());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_HttpPort, mDBBean.getHttpPort());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_SocketPort, null != mDBBean.getSocketPort() ? mDBBean.getSocketPort() : "1");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_LivePort, mDBBean.getLivePort());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_MicPort, null != mDBBean.getMicPort() ? mDBBean.getMicPort() : "1");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceUsername, mDBBean.getUsername());
//            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_Type, mDBBean.getDeviceName());//mDBBean.getDeviceName()
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DevicePassword, mDBBean.getPassword());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceName, mDBBean.getDeviceName());
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_DeviceCode, null != mDBBean.getDeviceCode() ? mDBBean.getDeviceCode() : "code码为空");
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_MSelected, mDBBean.getMSelected());

            //http://192.168.66.42:8008
            SharePreferenceUtil.put(DeviceActivity.this, SharePreferenceUtil.Current_BaseUrl, "http://" + mDBBean.getIp() + ":" + mDBBean.getHttpPort());
            String mBaseUrl = (String) SharePreferenceUtil.get(DeviceActivity.this, SharePreferenceUtil.Current_BaseUrl, "111");
            LogUtils.e("========当前设备的备注信息~~~~====DeviceActivity==mBaseUrl===" + mBaseUrl);

            EventBus.getDefault().post(new RefreshEvent("refresh"));

        }

    }

    /**
     * 获取设备类型对呀的数字
     * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机，06-耳鼻喉控制板，
     * 07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台
     * A0-iOS，A1-Android，FF-所有设备
     */
    public String getTypeNum(String str) {
        if (Constants.Type_FuKeTable.equals(str)) {
            return "9";
        } else if (Constants.Type_V1_YiTiJi.equals(str)) {
            return "07";

        } else if (Constants.Type_EarNoseTable.equals(str)) {
            return "8";

        } else if (Constants.Type_MiNiaoTable.equals(str)) {
            return "10";

        } else if (Constants.Type_FuKeTable.equals(str)) {
            return "9";

        }
        return "07";
    }

    /**
     * 刷新列表数据
     */

    private void refreshRecycleViewData() {
        post(new Runnable() {
            @Override
            public void run() {
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
        });

    }

    /**
     * eventbus 刷新扫码数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        mDataLest.clear();

        if ("toast".equals(event.getType())) {
            toast(event.getStr()+"");
        } else if (deviceDBBeans.size() == 0) {
            showEmpty();
        } else {
            showComplete();
            mAdapter.setData(deviceDBBeans);
        }

    }

    /**
     * 选择item为当前的设备
     */
    private void setChoseItem(int position) {
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
        }
        refreshRecycleViewData();
    }

    //展示删除和修改功能布局
    private void showChangeDeleteLayout(int position) {
        DeviceDBBean selectedItemBean = mAdapter.getItem(position);
        //点击就是选中
        selectedItemBean.setId(selectedItemBean.getId());
        selectedItemBean.setUsemsg01("true");
        DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, selectedItemBean);

        refreshRecycleViewData();
    }

    //隐藏删除和修改功能布局
    private void dismissChangeDeleteLayout() {
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);

        for (int i = 0; i < deviceDBBeans.size(); i++) {
            DeviceDBBean deviceDBBean = deviceDBBeans.get(i);
            String currentID = deviceDBBean.getId() + "";
            LogUtils.e(currentID + "========currentID===选中开始点击了===");
            deviceDBBean.setUsemsg01("false");
            DeviceDBUtils.insertOrReplaceInTx(DeviceActivity.this, deviceDBBean);

        }
        refreshRecycleViewData();
    }



    @Override
    protected void onResume() {
        super.onResume();
        DeviceDBBean bean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(DeviceActivity.this, "30000000000000000546017FE6BC28949一代一体机");
        LogUtils.e("SocketManage回调==onResume==onResume=onResume==" + bean);
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(DeviceActivity.this);
        LogUtils.e("SocketManage回调==onResume==onResume=onResume==" + deviceDBBeans.size());

        for (int i = 0; i < deviceDBBeans.size(); i++) {
            DeviceDBBean deviceDBBean = deviceDBBeans.get(i);

            LogUtils.e("SocketManage回调==onResume==onResume=getAcceptAndInsertDB==" + deviceDBBean);

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
