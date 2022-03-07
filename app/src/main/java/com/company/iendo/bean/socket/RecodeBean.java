package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/7 9:26
 * desc：录像socket回调的bean{"recordid":"0396","qrycode":"0"}
 *
 * record_base->RecordID:595 (02 53)
 * qrycode：0：查询录像状态 1：开始录像，2：停止录像，3：正在录像  4：未录像
 */
public class RecodeBean {
    @SerializedName("recordid")
    private String recordid;

    @SerializedName("qrycode")
    private String qrycode;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getQrycode() {
        return qrycode;
    }

    public void setQrycode(String qrycode) {
        this.qrycode = qrycode;
    }
}
