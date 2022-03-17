package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/17 14:37
 * desc：  更新病例bean
 * recordid--->病历ID(919)的hex串，如03 97
 */
public class UpdateCaseBean {

    @SerializedName("recordid")
    private String recordid;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    @Override
    public String toString() {
        return "UpdateCaseBean{" +
                "recordid='" + recordid + '\'' +
                '}';
    }
}
