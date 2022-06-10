package com.company.iendo.mineui.fragment.casemanage.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.CaseManageListBean;
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

        private final TextView mName, mAge, mNum;
        private TextView mSexLogo;
        private final RelativeLayout mItemAll;

        private ViewHolder() {
            super(R.layout.item_case_listv2);
            mName = findViewById(R.id.tv_case_name);
            mSexLogo = findViewById(R.id.iv_sex_logo);
            mAge = findViewById(R.id.tv_case_age);
            mNum = findViewById(R.id.tv_case_num);
            mItemAll = findViewById(R.id.relative_item_all);
//            mCheckDate = findViewById(R.id.tv_check_date);
        }

        @Override
        public void onBindView(int position) {
            CaseManageListBean.DataDTO item = getItem(position);
            mName.setText(item.getName() + "");
            mAge.setText(item.getPatientAge() + " " + item.getAgeUnit());
            mNum.setText(item.getCaseNo());
            mSexLogo.setText(item.getSex() + "");
            boolean selected = item.isSelected();
            if (selected){
                LogUtils.e("适配器====true"+item.getName());
            }else {
                LogUtils.e("适配器====flase");

            }

            if (selected) {
                mItemAll.setBackgroundResource(R.drawable.shape_bg_chose_picture_pre);
            }else {
                mItemAll.setBackgroundResource(R.drawable.shape_bg_chose_picture_nor);
            }
//            if ("男".equals(item.getSex())) {
//                mSexLogo.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_man));
//            } else {
//                mSexLogo.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_woman));
//            }
        }
    }
}