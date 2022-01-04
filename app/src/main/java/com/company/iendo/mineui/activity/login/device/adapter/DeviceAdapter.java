package com.company.iendo.mineui.activity.login.device.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.aop.Log;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.green.db.DeviceDBUtils;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.widget.SwipeMenuLayout;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 15:40
 * desc：设备
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
        public final SwipeMenuLayout mSwipeMenuLay;
        public final LinearLayout mLinearItem;
        public final TextView mTitle;
        public final TextView mType;
        public final TextView mMake;
        private final Button mDeleteBtn;
        private final Button mRePutBtn;
        private final ImageView mSelect;

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_device);
            mSwipeMenuLay = findViewById(R.id.swipeMenuLay);
            mLinearItem = findViewById(R.id.linear_item);
            mTitle = findViewById(R.id.tv_video_title);
            mType = findViewById(R.id.tv_video_type);
            mMake = findViewById(R.id.tv_video_make);
            mDeleteBtn = findViewById(R.id.delBtn);
            mRePutBtn = findViewById(R.id.reInputBtn);
            mSelect = findViewById(R.id.iv_item_select);
        }

        @Override
        public void onBindView(int position) {


            DeviceDBBean mDBBean = getItem(position);
//            http://192.168.64.28:7001/ID/FilePath
//            Log.e("adapter", "" + "Path:" + bean.getFilePath());
//            mTextView.setText("Path:" + mDBBean.getMsg());
            mTitle.setText(mDBBean.getDeviceName()); //设备名字
            mMake.setText(mDBBean.getMsg()); //备注信息
//            mType.setText(mDBBean.getType());
            LogUtils.e(mDBBean.getMsg() + "========当前设备的备注信息~~~~======");
            LogUtils.e(mDBBean.getType() + "========当前设备的类型~~~~======");

            mSelect.setSelected(mDBBean.getMSelected());

        }
    }
}