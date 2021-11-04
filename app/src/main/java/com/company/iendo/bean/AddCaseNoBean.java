package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 11:15
 * desc：病例添加前,获取病例编号的数据bean
 */
public class AddCaseNoBean {

    @SerializedName("data")
    private DataDTO data;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataDTO {
        @SerializedName("CaseNo")
        private String CaseNo;

        public String getCaseNo() {
            return CaseNo;
        }

        public void setCaseNo(String CaseNo) {
            this.CaseNo = CaseNo;
        }
    }
}
