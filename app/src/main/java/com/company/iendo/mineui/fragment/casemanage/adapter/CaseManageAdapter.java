package com.company.iendo.mineui.fragment.casemanage.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.ui.adapter.StatusAdapter;
import com.company.iendo.utils.LogUtils;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class CaseManageAdapter extends AppAdapter<CaseManageListBean.DataDTO> {

    public CaseManageAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public CaseManageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaseManageAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;

        private ViewHolder() {
            super(R.layout.status_item);
            mTextView = findViewById(R.id.tv_status_text);
        }

        @Override
        public void onBindView(int position) {
            CaseManageListBean.DataDTO item = getItem(position);
            mTextView.setText("ID:"+item.getID());
        }
    }
}