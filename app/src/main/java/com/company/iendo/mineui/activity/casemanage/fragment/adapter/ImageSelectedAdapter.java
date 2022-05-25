package com.company.iendo.mineui.activity.casemanage.fragment.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.socket.searchdevice.BroadCastReceiveBean;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 15:40
 * desc：设备
 * 长按弹出---修改,和删除功能
 * 单击-------选中当前设备
 */
public class ImageSelectedAdapter extends AppAdapter<BroadCastReceiveBean> {
    private String mID;
    private RecyclerView mRv;
    private List<BroadCastReceiveBean> mDataLest;

    //
    public ImageSelectedAdapter(Context context, String mID) {
        super(context);
        this.mID = mID;

    }

    /**
     * 根据全选或取消设置数据
     *
     * @param isAll 是否全选
     */
    public void setTypeData(boolean isAll) {

        for (int i = 0; i < mDataLest.size(); i++) {
            BroadCastReceiveBean bean = mDataLest.get(i);
            if (isAll) {
                bean.setSelected(true);
            } else {
                bean.setSelected(false);
            }
        }
        //刷新数据
        notifyDataSetChanged();
    }

    /**
     * 反选
     */
    public void setUnSelectedData() {

        for (int i = 0; i < mDataLest.size(); i++) {
            BroadCastReceiveBean bean = mDataLest.get(i);
            if (bean.getSelected()) {
                bean.setSelected(false);
            } else {
                bean.setSelected(true);

            }
        }
        //刷新数据
        notifyDataSetChanged();
    }
    /**
     * 获取已经选中的List
     */

    public List<BroadCastReceiveBean> getSelectedData() {
        ArrayList<BroadCastReceiveBean> arraysList = new ArrayList<>();
        for (int i = 0; i < mDataLest.size(); i++) {
            BroadCastReceiveBean bean = mDataLest.get(i);
            if (bean.getSelected()) {
                arraysList.add(bean);
            }
        }
        return arraysList;
    }

    private int mSelectedPos = -1;

    public ImageSelectedAdapter(@NonNull Context context, RecyclerView mRv, List<BroadCastReceiveBean> mDataLest) {
        super(context);
        this.mRv = mRv;
        this.mDataLest = mDataLest;
        //找到默认选中的position
    }

    @NonNull
    @Override
    public ImageSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new ImageSelectedAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        public final RelativeLayout mRelativeLayout;
        public final TextView mMsgChose;
        private final ImageView mImageChose;
        private final AppCompatCheckBox  mCheck;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_devicev4);
            mRelativeLayout = findViewById(R.id.relative_item);
            mImageChose = findViewById(R.id.iv_current_chose_image);
            mMsgChose = findViewById(R.id.tv_current_chose_msg);
            mCheck = findViewById(R.id.iv_check);

        }

        @Override
        public void onBindView(int position) {

            BroadCastReceiveBean mBean = getItem(position);
            mImageChose.setVisibility(View.VISIBLE);
            if (mBean.getSelected()) {
                //被选
                mCheck.setChecked(true);
//                mCheck.setImageResource(R.drawable.image_check);
            } else {
                //未被选
                mCheck.setChecked(false);
//                mCheck.setImageResource(R.drawable.image_uncheck);
            }
//            if (null != mDBBean.getUsemsg01()) {
//                if ("false".equals(mDBBean.getUsemsg01())) {
//                    mLinearStatus.setVisibility(View.INVISIBLE);
//                } else {
//                    mLinearStatus.setVisibility(View.VISIBLE);
//                }
//            } else {
//                mLinearStatus.setVisibility(View.INVISIBLE);
//            }
//
//            Boolean mSelected = mDBBean.getMSelected();
//
//            if (mSelected) {
//                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_pre);
//                mMsgChose.setVisibility(View.VISIBLE);
//            } else {
//                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_device_all_nor);
//                mMsgChose.setVisibility(View.INVISIBLE);
//
//            }
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
//            mSelect.setSelected(mDBBean.getMSelected());


        }
    }
}