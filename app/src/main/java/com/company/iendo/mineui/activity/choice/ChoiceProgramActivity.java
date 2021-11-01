package com.company.iendo.mineui.activity.choice;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.mineui.bean.ProgramEntity;
import com.company.iendo.mineui.program.ChannelAdapter;
import com.company.iendo.mineui.program.help.ItemDragHelperCallback;
import com.company.iendo.utils.LogUtils;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/27 13:40
 * desc：选择方案界面
 */
public class ChoiceProgramActivity extends AppActivity {
    private List<ProgramEntity> mChoiceItems;
    private List<ProgramEntity> mOtherItems;
    private TextView mChoice;
    private RecyclerView mRecycleview;
    private ChannelAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.choice_program_activity;
    }

    @Override
    protected void initView() {
        mChoice = findViewById(R.id.tv_choice);
        mRecycleview = (RecyclerView) findViewById(R.id.recy);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onLeftClick(View view) {
        super.onLeftClick(view);
        List<ProgramEntity> myChannelItems = mAdapter.getMyChannelItems();
        List<ProgramEntity> myOtherChannelItems = mAdapter.getMyOtherChannelItems();
        LogUtils.e("TAG===选择后====myChannelItems===="+myChannelItems.size());
        LogUtils.e("TAG===选择后====myOtherChannelItems===="+myOtherChannelItems.size());
        for (int i = 0; i < myChannelItems.size(); i++) {
            LogUtils.e("TAG===选择后==我的==第===="+i+myChannelItems.get(i).getName());

        }
        for (int i = 0; i < myOtherChannelItems.size(); i++) {
            LogUtils.e("TAG===选择后==其他==第===="+i+myOtherChannelItems.get(i).getName());

        }



    }

    @Override
    protected void initData() {

        mChoiceItems = (List<ProgramEntity>) getIntent().getSerializableExtra("mChoiceItems");
        mOtherItems = (List<ProgramEntity>) getIntent().getSerializableExtra("mOtherItems");

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecycleview.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycleview);

        mAdapter = new ChannelAdapter(this, helper, this.mChoiceItems, mOtherItems);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mAdapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        mRecycleview.setAdapter(mAdapter);
        mAdapter.setOnMyChannelItemClickListener(new ChannelAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                toast(ChoiceProgramActivity.this.mChoiceItems.get(position).getName());
            }
        });

        mAdapter.setOnMyOtherChannelItemClickListener(new ChannelAdapter.OnMyOtherChannelItemClickListener() {
            @Override
            public void onOtherItemClick(View v, ProgramEntity otherEntity) {
                toast("onOtherItemClick===" + otherEntity.getName());
            }
        });
    }

}
