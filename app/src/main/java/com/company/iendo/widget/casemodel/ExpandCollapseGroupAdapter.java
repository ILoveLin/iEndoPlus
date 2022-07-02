package com.company.iendo.widget.casemodel;

import android.content.Context;

import com.company.iendo.R;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.LogUtils;
import com.sunfusheng.ExpandableGroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;

/**
 * @author sunfusheng on 2018/2/9.
 */
public class ExpandCollapseGroupAdapter<T>  extends ExpandableGroupRecyclerViewAdapter<String> {


  public ExpandCollapseGroupAdapter(Context context, String[][] groups) {
    super(context, groups);
  }

  @Override
  public boolean showHeader() {
    return true;
  }

  @Override
  public boolean showFooter() {
    return false;
  }

  @Override
  public int getHeaderLayoutId(int viewType) {
    return R.layout.item_case_model_header_layout;
  }

  @Override
  public int getChildLayoutId(int viewType) {
    return R.layout.item_case_model_child_layout;
  }

  @Override
  public int getFooterLayoutId(int viewType) {
    return R.layout.item_case_model_footer_layout;
  }

  @Override
  public void onBindHeaderViewHolder(GroupViewHolder holder, String item, int groupPosition) {
    holder.setVisible(R.id.iv_arrow, true);
    holder.setImageResource(R.id.iv_arrow, isExpand(groupPosition) ? R.mipmap.ic_down_arrow : R.mipmap.ic_right_arrow);
    holder.setText(R.id.tv_title, item);
  }

  @Override
  public void onBindChildViewHolder(GroupViewHolder holder, String item, int groupPosition, int childPosition) {
    holder.setText(R.id.tv_title, item);
  }

  @Override
  public void onBindFooterViewHolder(GroupViewHolder holder, String item, int groupPosition) {
    holder.setText(R.id.tv_title, item + Constants.FOOTER_SUFFIX);
  }

}
