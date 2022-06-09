package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/19 13:51
 * desc：
 */
public class UserDeletedBean {


    @SerializedName("code")
    private String code;
    @SerializedName("data")
    private DataDTO data;
    @SerializedName("msg")
    private String msg;

    @Override
    public String toString() {
        return "UserDeletedBean{" +
                "code='" + code + '\'' +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataDTO {
        @SerializedName("data")
        private String data;

        @Override
        public String toString() {
            return "DataDTO{" +
                    "data='" + data + '\'' +
                    '}';
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
