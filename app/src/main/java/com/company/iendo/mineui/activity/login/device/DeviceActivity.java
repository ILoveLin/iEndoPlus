package com.company.iendo.mineui.activity.login.device;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.mineui.activity.login.device.adapter.DeviceAdapter;
import com.company.iendo.ui.dialog.InputDeviceDialog;
import com.company.iendo.ui.dialog.SelectDialog;
import com.company.iendo.ui.popup.ListPopup;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.StatusLayout;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.layout.WrapRecyclerView;
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
        mAdapter = new DeviceAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnChildClickListener(R.id.tv_video_title, this);
        mAdapter.setOnChildClickListener(R.id.tv_video_type, this);
        mAdapter.setOnChildClickListener(R.id.tv_video_make, this);
        mAdapter.setOnChildClickListener(R.id.delBtn, this);
        mAdapter.setOnChildClickListener(R.id.reInputBtn, this);

        mAdapter.setData(getDBDeviceData());
        mRecyclerView.setAdapter(mAdapter);

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
                        toast("确定了：" + data.toString());
                        int start = data.toString().indexOf("=");
                        String str = data.toString().substring(start + 1, data.toString().length() - 1);
                        LogUtils.e("showMultiDialog===str==" + str); //{0=HD3}
                        showMulti2Dialog(str);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
                    }
                })
                .show();


    }

    /**
     * 弹出第一个对话框
     *
     * @param str 当前选择类型
     */
    private void showMulti2Dialog(String str) {
        switch (str) {
            case "HD3":
                // 输入对话框
                new InputDeviceDialog.Builder(this)
                        // 标题可以不用填写
                        .setTitle("我是标题")
                        // 内容可以不用填写
                        .setAccountContent("我是Account内容")
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
                            public void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage, String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort, String mSocketPort, String mLivePort, String mMicPort, String content) {
                                toast("确定了：" + mDeviceName);

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();

                break;
            case "一体机":
                break;
            case "耳鼻喉治疗台":
                break;

        }

    }

    /**
     * 查询数据库获取到数据
     *
     * @return
     */
    private List<DeviceDBBean> getDBDeviceData() {
        //先模拟几条数据
//        for (int i = 0; i < 10; i++) {
//            DeviceDBBean deviceDBBean = new DeviceDBBean();
//            deviceDBBean.setDeviceID("设备ID" + i);
//            deviceDBBean.setHttpPort("httpPort:" + i);
//            deviceDBBean.setTitle("标题:" + i + 2);
//            deviceDBBean.setMsg("备注:" + i + 3);
//            deviceDBBean.setType("类别:" + i + 4);
//            DeviceDBUtils.insertOrReplaceInTx(this, deviceDBBean);
//        }
        List<DeviceDBBean> deviceDBBeans = DeviceDBUtils.queryAll(this, mDeviceDBBean);
        for (int i = 0; i < deviceDBBeans.size(); i++) {
            DeviceDBBean deviceDBBean = deviceDBBeans.get(i);
            String msg = deviceDBBean.getMsg();
            LogUtils.e("data==" + msg);


        }
        mDataLest.clear();
        mDataLest.addAll(deviceDBBeans);
        return mDataLest;
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

    }


    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        switch (childView.getId()) {
            case R.id.tv_video_title:
                toast("标题");

                break;
            case R.id.tv_video_type:
                toast("类型");

                break;
            case R.id.tv_video_make:
                toast("备注");

                break;
            case R.id.delBtn:
                toast("删除");

                break;
            case R.id.reInputBtn:
                toast("修改");
                break;
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
