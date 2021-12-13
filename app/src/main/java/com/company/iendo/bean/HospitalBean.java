package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/13 14:37
 * desc：医院信息
 */
public class HospitalBean {


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
        private String ID;
        @SerializedName("szHospital")
        private String szHospital;
        @SerializedName("szSlave")
        private String szSlave;
        @SerializedName("szAddress")
        private String szAddress;
        @SerializedName("szTelephone")
        private String szTelephone;
        @SerializedName("szPostCode")
        private String szPostCode;
        @SerializedName("szTitle")
        private String szTitle;
        @SerializedName("szIconPath")
        private String szIconPath;
        @SerializedName("szFooter")
        private Object szFooter;
        @SerializedName("EndoType")
        private int EndoType;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getSzHospital() {
            return szHospital;
        }

        public void setSzHospital(String szHospital) {
            this.szHospital = szHospital;
        }

        public String getSzSlave() {
            return szSlave;
        }

        public void setSzSlave(String szSlave) {
            this.szSlave = szSlave;
        }

        public String getSzAddress() {
            return szAddress;
        }

        public void setSzAddress(String szAddress) {
            this.szAddress = szAddress;
        }

        public String getSzTelephone() {
            return szTelephone;
        }

        public void setSzTelephone(String szTelephone) {
            this.szTelephone = szTelephone;
        }

        public String getSzPostCode() {
            return szPostCode;
        }

        public void setSzPostCode(String szPostCode) {
            this.szPostCode = szPostCode;
        }

        public String getSzTitle() {
            return szTitle;
        }

        public void setSzTitle(String szTitle) {
            this.szTitle = szTitle;
        }

        public String getSzIconPath() {
            return szIconPath;
        }

        public void setSzIconPath(String szIconPath) {
            this.szIconPath = szIconPath;
        }

        public Object getSzFooter() {
            return szFooter;
        }

        public void setSzFooter(Object szFooter) {
            this.szFooter = szFooter;
        }

        public int getEndoType() {
            return EndoType;
        }

        public void setEndoType(int EndoType) {
            this.EndoType = EndoType;
        }
    }
}
