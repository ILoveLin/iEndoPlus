package com.company.iendo.mineui.offline;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.green.db.CaseDBUtils;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.offline.entity.ChildEntity;
import com.company.iendo.mineui.offline.entity.GroupEntity;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.donkingliang.groupedadapter.layoutmanger.GroupedGridLayoutManager;
import com.donkingliang.groupedadapter.widget.StickyHeaderLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：第2个tab-fragment
 */
public class AFragment extends TitleBarFragment<MainActivity> {

    private RecyclerView mRecyclerView;
    private StickyHeaderLayout mStickyLayout;
    private OffCaseAdapter mAdapter;

    public static AFragment newInstance() {
        return new AFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_a;
    }

    @Override
    protected void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mStickyLayout = (StickyHeaderLayout) findViewById(R.id.sticky_layout);

        ArrayList<GroupEntity> groupsList = new ArrayList<GroupEntity>();
        List<CaseDBBean> mCaseDBList = CaseDBUtils.queryAll(getActivity());

        for (int i = 0; i < mCaseDBList.size(); i++) {  //添加所有的集合

            ArrayList<ChildEntity> childrenList = new ArrayList<>();
            CaseDBBean caseDBBean = mCaseDBList.get(i);


            for (int i1 = 0; i1 < mCaseDBList.size(); i1++) {  //添加子集

                ChildEntity childEntity = new ChildEntity(i1+"=="+caseDBBean.getName());
                childrenList.add(childEntity);
            }
            GroupEntity groupEntity = new GroupEntity(caseDBBean.getRecord_date(), "", childrenList);

            groupsList.add(groupEntity);

        }


//        mAdapter = new OffCaseAdapter(getActivity(), GroupModel.getGroups(10, 5));
        mAdapter = new OffCaseAdapter(getActivity(), groupsList);
        mAdapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {

                toast("组头：groupPosition = " + groupPosition);

                Log.e("eee", adapter.toString() + "  " + holder.toString());
            }
        });

        mAdapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                toast("子项：groupPosition = " + childPosition);

            }
        });

        GroupedGridLayoutManager gridLayoutManager = new GroupedGridLayoutManager(getActivity(), 2, mAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
