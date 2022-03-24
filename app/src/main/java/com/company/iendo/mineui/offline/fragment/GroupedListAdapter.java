package com.company.iendo.mineui.offline.fragment;

import android.content.Context;

import com.company.iendo.R;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.mineui.offline.entitydb.GroupEntity;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.google.gson.Gson;
import com.hjq.gson.factory.GsonFactory;

import java.util.ArrayList;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    protected  ArrayList<GroupEntity> mGroups;
    private final Gson mGson;

    public GroupedListAdapter(Context context, ArrayList<GroupEntity> groups) {
        super(context);
        mGroups = groups;
        mGson = GsonFactory.getSingletonGson();

    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<CaseDBBean> children = mGroups.get(groupPosition).getChildren();
//        ArrayList<ChildEntity> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    public void clear(){
        mGroups.clear();
        notifyDataChanged();
    }

    public void setGroups(ArrayList<GroupEntity> groups){
        mGroups = groups;
        notifyDataChanged();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.adapter_footer;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_case_listv2_offline;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity groupEntity = mGroups.get(groupPosition);
//        GroupEntity entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_header, groupEntity.getHeader());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity groupEntity = mGroups.get(groupPosition);
//        GroupEntity entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_footer, groupEntity.getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        CaseDBBean caseDBBean = mGroups.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tv_case_name, caseDBBean.getName()+"");
        holder.setText(R.id.iv_sex_logo, caseDBBean.getSex()+"");
        holder.setText(R.id.tv_case_age,caseDBBean.getPatientAge()+""+caseDBBean.getAgeUnit() );
        holder.setText(R.id.tv_case_num, caseDBBean.getCaseNo()+"");
//        ChildEntity entity = mGroups.get(groupPosition).getChildren().get(childPosition);
//        holder.setText(R.id.tv_child, entity.getChild());
    }
}
