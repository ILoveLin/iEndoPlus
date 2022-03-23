package com.company.iendo.mineui.activity.login.device.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.utils.LogUtils;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 15:40
 * desc：设备
 * 长按弹出---修改,和删除功能
 * 单击-------选中当前设备
 */
public class DeviceAdapter extends AppAdapter<DeviceDBBean> {
    private String mID;
    private RecyclerView mRv;
    private List<DeviceDBBean> mDataLest;

    //
    public DeviceAdapter(Context context, String mID) {
        super(context);
        this.mID = mID;

    }


    private int mSelectedPos = -1;

    public DeviceAdapter(@NonNull Context context, RecyclerView mRv, List<DeviceDBBean> mDataLest) {
        super(context);
        this.mRv = mRv;
        this.mDataLest = mDataLest;
        //找到默认选中的position
        LogUtils.e(mDataLest.size() + "mDataLest===Adapter==" + mDataLest.size());
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new DeviceAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        public final RelativeLayout mRelativeLayout;
        public final LinearLayout mLinearStatus;
        public final TextView mMsgChose, mChange, mDelete, mTitle, mRemark, mIP;
        private final ImageView mImageChose;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_devicev2);
            mRelativeLayout = findViewById(R.id.relative_item);
            mLinearStatus = findViewById(R.id.linear_status);
            mImageChose = findViewById(R.id.iv_current_chose_image);
            mMsgChose = findViewById(R.id.tv_current_chose_msg);
            mChange = findViewById(R.id.tv_change);
            mDelete = findViewById(R.id.tv_delete);
            mTitle = findViewById(R.id.tv_current_title_msg);
            mRemark = findViewById(R.id.tv_current_remark_msg);
            mIP = findViewById(R.id.tv_current_ip_msg);
        }

        @Override
        public void onBindView(int position) {

            DeviceDBBean mDBBean = getItem(position);
            mTitle.setText("" + mDBBean.getDeviceName());
            mRemark.setText("" + mDBBean.getMsg());
            mIP.setText("" + mDBBean.getIp());

            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==DeviceAdapter===" + mDBBean.toString());

            if (null != mDBBean.getUsemsg01()) {
                if ("false".equals(mDBBean.getUsemsg01())) {
                    mLinearStatus.setVisibility(View.INVISIBLE);
                } else {
                    mLinearStatus.setVisibility(View.VISIBLE);
                }
            } else {
                mLinearStatus.setVisibility(View.INVISIBLE);
            }

            Boolean mSelected = mDBBean.getMSelected();

            if (mSelected) {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_pre);
                mMsgChose.setVisibility(View.VISIBLE);
            } else {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_nor);
                mMsgChose.setVisibility(View.INVISIBLE);

            }
            if (null != mDBBean.getType()) {
                switch (mDBBean.getType()) {
                    case "一代一体机":
                        mImageChose.setImageResource(R.drawable.icon_yitiji);
                        break;
                    case "耳鼻喉治疗台":
                        mImageChose.setImageResource(R.drawable.icon_erbihou);
                        break;
                    case "妇科治疗台":
                        mImageChose.setImageResource(R.drawable.icon_erbihou);
                    case "泌尿治疗台":
                        mImageChose.setImageResource(R.drawable.icon_erbihou);
                        break;
                }
            }

//            http://192.168.64.28:7001/ID/FilePath
//            Log.e("adapter", "" + "Path:" + bean.getFilePath());
//            mTextView.setText("Path:" + mDBBean.getMsg());
//            mTitle.setText(mDBBean.getDeviceName()); //设备名字
//            mMake.setText(mDBBean.getMsg()); //备注信息
////            mType.setText(mDBBean.getType());
//            LogUtils.e(mDBBean.getMsg() + "========当前设备的备注信息~~~~======");
//            LogUtils.e(mDBBean.getType() + "========当前设备的类型~~~~======");
//
//            mSelect.setSelected(mDBBean.getMSelected());


        }
    }
}