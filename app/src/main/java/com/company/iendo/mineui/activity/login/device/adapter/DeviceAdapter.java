package com.company.iendo.mineui.activity.login.device.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.green.db.DeviceDBBean;
import com.company.iendo.widget.SwipeMenuLayout;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 15:40
 * desc：设备
 */
public class DeviceAdapter extends AppAdapter<DeviceDBBean> {
//    private String mID;
//
//    public DeviceAdapter(Context context, String mID) {
//        super(context);
//        this.mID = mID;
//
//    }


    public DeviceAdapter(@NonNull Context context) {
        super(context);
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

        private ViewHolder() {
            super(R.layout.item_swipemenulayout_device);
            mSwipeMenuLay = findViewById(R.id.swipeMenuLay);
            mLinearItem = findViewById(R.id.linear_item);
            mTitle = findViewById(R.id.tv_video_title);
            mType = findViewById(R.id.tv_video_type);
            mMake = findViewById(R.id.tv_video_make);
            mDeleteBtn = findViewById(R.id.delBtn);
            mRePutBtn = findViewById(R.id.reInputBtn);
        }

        @Override
        public void onBindView(int position) {
            DeviceDBBean mDBBean = getItem(position);
//            http://192.168.64.28:7001/ID/FilePath
//            Log.e("adapter", "" + "Path:" + bean.getFilePath());

//            mTextView.setText("Path:" + mDBBean.getMsg());
            mTitle.setText(mDBBean.getTitle());
            mMake.setText(mDBBean.getMsg());
            mType.setText(mDBBean.getType());

        }
    }
}