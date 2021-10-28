package com.company.iendo.mineui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.company.iendo.R;
import com.company.iendo.app.AppActivity;
import com.company.iendo.mineui.bean.ProgramEntity;
import com.company.iendo.mineui.bean.event.ProgramDataEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/27 13:40
 * desc：方案界面
 */
public class ProgramActivity extends AppActivity {

    private TextView mChoice;
    private List<ProgramEntity> mChoiceItems;
    private List<ProgramEntity> mOtherItems;

    @Override
    protected int getLayoutId() {
        return R.layout.program_activity;
    }

    @Override
    protected void initView() {
        mChoice = findViewById(R.id.tv_choice);

        setOnClickListener(R.id.tv_choice);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_choice:
                Intent intent = new Intent(ProgramActivity.this,ChoiceProgramActivity.class);
                intent.putExtra("mChoiceItems",(Serializable) mChoiceItems);
                intent.putExtra("mOtherItems",(Serializable)mOtherItems);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mChoiceItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ProgramEntity entity = new ProgramEntity();
            if (i%2==0){
                entity.setName("HD3" + i);
            }else{
                entity.setName("一体机" + i);
            }
            mChoiceItems.add(entity);
        }
        mOtherItems = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            ProgramEntity entity = new ProgramEntity();
            entity.setName("其他" + i);
            mOtherItems.add(entity);
        }
    }

}
