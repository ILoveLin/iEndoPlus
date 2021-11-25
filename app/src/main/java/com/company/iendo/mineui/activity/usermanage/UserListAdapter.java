package com.company.iendo.mineui.activity.usermanage;

import android.content.Context;
import android.view.ViewGroup;
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

    public UserListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mName;
        private final TextView mRelo;
        private final TextView mPassword;
        private final TextView mDelete;

        private ViewHolder() {
            super(R.layout.item_user_list);
            mName = findViewById(R.id.tv_name);
            mRelo = findViewById(R.id.tv_change_relo);
            mPassword = findViewById(R.id.tv_change_password);
            mDelete = findViewById(R.id.tv_delete);
        }

        @Override
        public void onBindView(int position) {
            UserListBean.DataDTO item = getItem(position);
            mName.setText("Name:"+item.getUserName()+"角色:"+item.getRole());
        }
    }
}