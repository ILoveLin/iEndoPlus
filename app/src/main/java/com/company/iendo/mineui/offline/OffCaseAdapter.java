package com.company.iendo.mineui.offline;

import android.content.Context;

import com.company.iendo.mineui.offline.entity.GroupEntity;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;

import java.util.ArrayList;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/1/13 15:11
 * desc：离线数据病例列表
 */
public class OffCaseAdapter  extends GroupedListAdapter{
    public OffCaseAdapter(Context context, ArrayList<GroupEntity> groups) {
        super(context, groups);
    }


    /**
     * 返回false表示没有组尾
     *
     * @param groupPosition
     * @return
     */
    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    /**
     * 当hasFooter返回false时，这个方法不会被调用。
     *
     * @return
     */
    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    /**
     * 当hasFooter返回false时，这个方法不会被调用。
     *
     * @param holder
     * @param groupPosition
     */
    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }
}
