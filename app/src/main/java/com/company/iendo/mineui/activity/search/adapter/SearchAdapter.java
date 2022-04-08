package com.company.iendo.mineui.activity.search.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.CaseManageListBean;
import com.company.iendo.bean.SearchListBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class SearchAdapter extends AppAdapter<SearchListBean.DataDTO> {

    public SearchAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mName, mAge, mNum;
        private TextView mSexLogo;

        private ViewHolder() {
            super(R.layout.item_case_listv3);
            mName = findViewById(R.id.tv_case_name);
            mSexLogo = findViewById(R.id.iv_sex_logo);
            mAge = findViewById(R.id.tv_case_age);
            mNum = findViewById(R.id.tv_case_num);
        }

        @Override
        public void onBindView(int position) {
            SearchListBean.DataDTO item = getItem(position);
            mName.setText(item.getName() + "");
            mAge.setText(item.getPatientAge() + " " + item.getAgeUnit());
            mNum.setText(item.getCaseNo());
            mSexLogo.setText(item.getSex() + "");
        }
    }
}