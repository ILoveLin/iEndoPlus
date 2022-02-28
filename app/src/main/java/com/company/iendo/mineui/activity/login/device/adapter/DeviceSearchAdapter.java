package com.company.iendo.mineui.activity.login.device.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.socket.searchdevice.BroadCastReceiveBean;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.utils.LogUtils;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 15:40
 * desc：设备搜索
 * 长按弹出---修改,和删除功能
 * 单击-------选中当前设备
 */
public class DeviceSearchAdapter extends AppAdapter<BroadCastReceiveBean> {
    private String mID;
    private RecyclerView mRv;
    private Context mContext;
    private List<BroadCastReceiveBean> mDataLest;


    private int mSelectedPos = -1;

    public DeviceSearchAdapter(@NonNull Context context, RecyclerView mRv, List<BroadCastReceiveBean> mDataLest) {
        super(context);
        this.mRv = mRv;
        this.mContext = context;
        this.mDataLest = mDataLest;
        //找到默认选中的position
        LogUtils.e(mDataLest.size() + "mDataLest===Adapter==" + mDataLest.size());
    }

    @NonNull
    @Override
    public DeviceSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new DeviceSearchAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        public final RelativeLayout mRelativeLayout;
        public final TextView mMsgChose, mInDB;
        private final ImageView mImageChose;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_devicev3);
            mRelativeLayout = findViewById(R.id.relative_item);
//            mLinearStatus = findViewById(R.id.linear_status);
            mImageChose = findViewById(R.id.iv_current_chose_image);
            mMsgChose = findViewById(R.id.tv_current_chose_msg);
            mInDB = findViewById(R.id.tv_isindb);
//            mChange = findViewById(R.id.tv_change);
//            mDelete = findViewById(R.id.tv_delete);
        }

        @Override
        public void onBindView(int position) {
            BroadCastReceiveBean mItemBean = getItem(position);

            Boolean mSelected = mItemBean.getSelected();
            if (mSelected) {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_pre);
                mMsgChose.setVisibility(View.VISIBLE);
            } else {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_nor);
                mMsgChose.setVisibility(View.INVISIBLE);

            }
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==Adapter===" + mItemBean.toString());
            String tag = mItemBean.getEndotype() + mItemBean.getDeviceCode() + mItemBean.getDeviceType();

            DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(mContext, tag);
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==tag===" + tag); //192.168.132.10200000000000000005618B1F96D92837C一代一体机
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==codeBean===" + codeBean);
//            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==getAcceptAndInsertDB===" + codeBean.getAcceptAndInsertDB());


            if (null != codeBean) {  //数据库表存在更新数据即可
                if (tag.equals(codeBean.getAcceptAndInsertDB())) {
                    mInDB.setVisibility(View.VISIBLE);
                } else {
                    mInDB.setVisibility(View.INVISIBLE);
                }

            }

            Boolean inDB = mItemBean.getInDB();
            if (null != inDB)
                if (inDB) {
                    mInDB.setVisibility(View.VISIBLE);
                } else {
                    mInDB.setVisibility(View.INVISIBLE);

                }
            if (mItemBean.getCheckAccess()) {

            }

//            switch (mDBBean.getType()) {
//                case "一代一体机":
//                    mImageChose.setImageResource(R.drawable.icon_yitiji);
//                    break;
//                case "耳鼻喉治疗台":
//                    mImageChose.setImageResource(R.drawable.icon_erbihou);
//
//                    break;
//                case "妇科治疗台":
//                    mImageChose.setImageResource(R.drawable.icon_shenzhou4k);
//
//                    break;
//                case "泌尿治疗台":
//                    mImageChose.setImageResource(R.drawable.icon_shenzhou4k);
//                    break;
//            }
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