package com.company.iendo.mineui.bean.event;

import com.company.iendo.mineui.bean.ProgramEntity;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/27 14:05
 * desc：传输数据--已选择的方案list和全部设备的list
 */
public class ProgramDataEvent {


   private List<ProgramEntity> mChoiceItems;
   private List<ProgramEntity> mAllItems;


    public ProgramDataEvent(List<ProgramEntity> mChoiceItems, List<ProgramEntity> mAllItems) {
        this.mChoiceItems = mChoiceItems;
        this.mAllItems = mAllItems;
    }

    public List<ProgramEntity> getChoiceItems() {
        return mChoiceItems;
    }

    public void setChoiceItems(List<ProgramEntity> mChoiceItems) {
        this.mChoiceItems = mChoiceItems;
    }

    public List<ProgramEntity> getAllItems() {
        return mAllItems;
    }

    public void setAllItems(List<ProgramEntity> mAllItems) {
        this.mAllItems = mAllItems;
    }
}
