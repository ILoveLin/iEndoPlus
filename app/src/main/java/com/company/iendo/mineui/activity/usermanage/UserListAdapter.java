package com.company.iendo.mineui.activity.usermanage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.UserListBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class UserListAdapter extends AppAdapter<UserListBean.DataDTO> {
    private String mLoginUserName;

    public UserListAdapter(Context context, String mLoginUserName) {
        super(context);
        this.mLoginUserName = mLoginUserName;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mName, mCurrentFlag, mRelo;
        //        private final Button mChangeRelo, mDelete, mPassword;
        private final RelativeLayout mItemView;
        private final LinearLayout linear_relo,linear_change,linear_delete;

        private ViewHolder() {
            super(R.layout.item_user_list);
            mItemView = findViewById(R.id.item_relative_all);
            mName = findViewById(R.id.tv_name);
            mRelo = findViewById(R.id.tv_current_relo);
//            mChangeRelo = findViewById(R.id.tv_change_relo);
//            mPassword = findViewById(R.id.tv_change_password);
//            mDelete = findViewById(R.id.tv_delete);
            mCurrentFlag = findViewById(R.id.tv_current_flag);
            linear_relo = findViewById(R.id.linear_relo);
            linear_change = findViewById(R.id.linear_change);
            linear_delete = findViewById(R.id.linear_delete);
        }

        @Override
        public void onBindView(int position) {
            UserListBean.DataDTO item = getItem(position);
            //角色权限:0-管理员 1-操作员 2-查询员
            String userName = item.getUserName();
            if ("Admin".equals(userName)){
                linear_relo.setVisibility(View.GONE);
                linear_change.setVisibility(View.GONE);
                linear_delete.setVisibility(View.GONE);
            }else {
                linear_relo.setVisibility(View.VISIBLE);
                linear_change.setVisibility(View.VISIBLE);
                linear_delete.setVisibility(View.VISIBLE);
            }


            if (mLoginUserName.equals(userName)) {
                mItemView.setBackgroundResource(R.drawable.shape_item_user_pre);
                mCurrentFlag.setVisibility(View.VISIBLE);
            } else {
                mItemView.setBackgroundResource(R.drawable.shape_item_user_nor);
                mCurrentFlag.setVisibility(View.INVISIBLE);
            }
            mName.setText("" + item.getUserName());
            //角色权限:0-管理员 1-操作员 2-查询员
            switch (item.getRole()) {
                case 0:
                    if ("Admin".equals(item.getUserName())) {
                        mRelo.setText("超级管理员");
                    } else {
                        mRelo.setText("管理员");
                    }
                    break;
                case 1:
                    mRelo.setText("操作员");
                    break;
                case 2:
                    mRelo.setText("查询员");
                    break;
            }
        }
    }
}