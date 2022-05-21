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
        public final TextView mMsgChose, mInDB, mTitle, mRemark, mIP;
        private final ImageView mImageChose;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_devicev3);
            mRelativeLayout = findViewById(R.id.relative_item);
//            mLinearStatus = findViewById(R.id.linear_status);
            mImageChose = findViewById(R.id.iv_current_chose_image);
            mMsgChose = findViewById(R.id.tv_current_chose_msg);
            mInDB = findViewById(R.id.tv_isindb);
            mTitle = findViewById(R.id.tv_current_title_msg);
            mRemark = findViewById(R.id.tv_current_remark_msg);
            mIP = findViewById(R.id.tv_current_ip_msg);
//            mChange = findViewById(R.id.tv_change);
//            mDelete = findViewById(R.id.tv_delete);
        }

        @Override
        public void onBindView(int position) {
            BroadCastReceiveBean mItemBean = getItem(position);
            LogUtils.e("SocketManage回调==模拟数据==mItemBean.mItemBean==mItemBean===" + mItemBean);
//            {title='AIO-ENT', remark='1号内镜室', endotype='3', accept='1', mSelected=false, ip='192.168.132.102',
//            receiveType='07', receiveID='0000000000000000ED3A93DA80A9BA8B', deviceType='一代一体机',
//            deviceCode='0000000000000000ED3A93DA80A9BA8B', itemId='F9432B11B93E8BB4AE34539B7472C20E', inDB=null, isCheckAccess=false}
            Boolean mSelected = mItemBean.getSelected();

            mTitle.setText("" + mItemBean.getTitle());
            mRemark.setText("" + mItemBean.getRemark());
            mIP.setText("" + mItemBean.getIp());

            if (mSelected) {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_nor);
                mMsgChose.setVisibility(View.VISIBLE);
            } else {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_nor);
                mMsgChose.setVisibility(View.INVISIBLE);

            }
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.toString==SearchAdapter===" + mItemBean.toString());
            String tag = mItemBean.getEndotype() + mItemBean.getDeviceCode() + mItemBean.getDeviceType();

            DeviceDBBean codeBean = DeviceDBUtils.getQueryBeanByAcceptAndInsertDB(mContext, tag);
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==tag===" + tag); //192.168.132.10200000000000000005618B1F96D92837C一代一体机
            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==codeBean===" + codeBean);
//            LogUtils.e("SocketManage回调==模拟数据==DeviceDBBean.codeBean==getAcceptAndInsertDB===" + codeBean.getAcceptAndInsertDB());


            if (null != codeBean) {  //数据库表存在更新数据即可
                if (tag.equals(codeBean.getAcceptAndInsertDB())) {
                    mInDB.setText("已添加");
                    mInDB.setBackgroundResource(R.drawable.shape_search_device_insert);
                } else {
                    mInDB.setText("未添加");
                    mInDB.setBackgroundResource(R.drawable.shape_search_device_insert_no);

                }

            }

            Boolean inDB = mItemBean.getInDB();
            if (null != inDB)
                if (inDB) {
                    mInDB.setText("已添加");
                    mInDB.setBackgroundResource(R.drawable.shape_search_device_insert);
                    //数据库存在,说明授权接入过
                    mItemBean.setCheckAccess(true);
                } else {
                    mInDB.setText("未添加");
                    mInDB.setBackgroundResource(R.drawable.shape_search_device_insert_no);
                    //数据不库存在,说明授没有权接入过
                    mItemBean.setCheckAccess(false);


                }

            switch (mItemBean.getDeviceType()) {
                case "一代一体机":
                    mImageChose.setImageResource(R.drawable.icon_yitiji);
                    break;
                case "耳鼻喉治疗台":
                    mImageChose.setImageResource(R.drawable.icon_erbihou);
                    break;
                case "妇科治疗台":
                    mImageChose.setImageResource(R.drawable.icon_erbihou);
                    break;
                case "泌尿治疗台":
                    mImageChose.setImageResource(R.drawable.icon_erbihou);
                    break;
            }

        }
    }
}