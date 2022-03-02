package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 11:51
 * desc：病例添加
 */
public class AddCaseBean {


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
        @SerializedName("ID")
        private int ID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }
    }

    @Override
    public String toString() {
        return "AddCaseBean{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
