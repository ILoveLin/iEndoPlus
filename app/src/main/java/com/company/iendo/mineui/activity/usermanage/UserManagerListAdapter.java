package com.company.iendo.mineui.activity.usermanage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.UserManagerListBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class UserManagerListAdapter extends AppAdapter<UserManagerListBean.DataDTO> {
    private String mLoginUserName;

    public UserManagerListAdapter(Context context, String mLoginUserName) {
        super(context);
        this.mLoginUserName = mLoginUserName;
    }

    @NonNull
    @Override
    public UserManagerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserManagerListAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mName, mCurrentFlag, mRelo, mUserStatue;
        private final LinearLayout mItemView;

        private ViewHolder() {
            super(R.layout.item_user_manager_list);
            mItemView = findViewById(R.id.item_linear_all);
            mName = findViewById(R.id.tv_item_name);
            mUserStatue = findViewById(R.id.tv_user_statue);
            mRelo = findViewById(R.id.tv_item_user_relo);
            mCurrentFlag = findViewById(R.id.tv_current_flag);

        }

        @Override
        public void onBindView(int position) {
            UserManagerListBean.DataDTO item = getItem(position);

            if (item.isCanUSE()) {
                mUserStatue.setText("状态:激活");
            } else {
                mUserStatue.setText("状态:冻结");
            }

            //角色权限:0-管理员 1-操作员 2-查询员
            String userName = item.getUserName();
            if (mLoginUserName.equals(userName)) {
                mItemView.setBackgroundResource(R.drawable.shape_item_user_pre);
                mCurrentFlag.setVisibility(View.VISIBLE);
            } else {
                mItemView.setBackgroundResource(R.drawable.shape_item_user_nor);
                mCurrentFlag.setVisibility(View.INVISIBLE);
            }
            mName.setText("" + item.getUserName());
            //角色权限:0-管理员 1-操作员 2-普通用户 3-自定义
            ////0:管理员,1:操作员,2:普通用户,3:自定义
            switch (item.getRole()) {
                case 0:
//                    if ("Admin".equals(item.getUserName())) {
//                        mRelo.setText("超级管理员");
//                    } else {
                    mRelo.setText("角色:管理员");
//                    }
                    break;
                case 1:
                    mRelo.setText("角色:操作员");
                    break;
                case 2:
                    mRelo.setText("角色:普通用户");
                    break;
                case 3:
                    mRelo.setText("角色:自定义");
                    break;
            }
        }
    }
}