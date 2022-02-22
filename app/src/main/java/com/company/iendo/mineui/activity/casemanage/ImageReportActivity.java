package com.company.iendo.mineui.activity.casemanage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.socket.BroadCastReceiveBean;
import com.company.iendo.mineui.activity.casemanage.fragment.adapter.ImageSelectedAdapter;
import com.company.iendo.widget.StatusLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;

import java.util.ArrayList;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 图片选择--报告
 */
public final class ImageReportActivity extends AppActivity implements StatusAction, BaseAdapter.OnItemClickListener {

    private StatusLayout mStatusLayout;
    private ArrayList<BroadCastReceiveBean> mReceiveList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageSelectedAdapter mAdapter;
    private TextView mAllSelected;
    private TextView mAllUnSelected;
    private TextView mClearAll;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_report;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.status_hint);
        mRecyclerView = findViewById(R.id.rv_image_recyclerview);
        mAllSelected = findViewById(R.id.tv_all_selected);
        mClearAll = findViewById(R.id.tv_clear_all);
        mAllUnSelected = findViewById(R.id.tv_all_unselected);

    }

    @Override
    protected void initData() {

        for (int i = 0; i < 10; i++) {
            BroadCastReceiveBean bean = new BroadCastReceiveBean();
            bean.setSelected(false);
            bean.setAccept("1");
            bean.setTitle("标题==" + i);
            bean.setItemId(i + "");
            mReceiveList.add(bean);
        }


        mAdapter = new ImageSelectedAdapter(this, mRecyclerView, mReceiveList);
        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ImageReportActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(mReceiveList);
        mAllSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setTypeData(true);
            }
        });

        mClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setTypeData(false);
            }
        });
        mAllUnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setUnSelectedData();
            }
        });
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }


    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        if (null != mReceiveList) {
            BroadCastReceiveBean bean = mReceiveList.get(position);
            String newID = bean.getItemId();
            for (int i = 0; i < mReceiveList.size(); i++) {
                BroadCastReceiveBean oldBean = mReceiveList.get(i);
                String oldID = oldBean.getItemId();
                if (newID.equals(oldID)) {
                    if (oldBean.getSelected()) {
                        oldBean.setSelected(false);
                    } else {
                        oldBean.setSelected(true);

                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();

    }
}