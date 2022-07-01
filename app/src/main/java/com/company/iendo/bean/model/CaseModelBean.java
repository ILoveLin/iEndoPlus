package com.company.iendo.bean.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/6/30 11:30
 * desc：
 */
public class CaseModelBean {

    private List<DataDTO> data;
    private int code;
    private String msg;

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
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
        private String iD;
        private String iParentId;
        private String szName;
        private String szEndoDesc;
        private String szResult;
        private String szTherapy;
        @SerializedName("EndoType")
        private int endoType;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getIParentId() {
            return iParentId;
        }

        public void setIParentId(String iParentId) {
            this.iParentId = iParentId;
        }

        public String getSzName() {
            return szName;
        }

        public void setSzName(String szName) {
            this.szName = szName;
        }

        public String getSzEndoDesc() {
            return szEndoDesc;
        }

        public void setSzEndoDesc(String szEndoDesc) {
            this.szEndoDesc = szEndoDesc;
        }

        public String getSzResult() {
            return szResult;
        }

        public void setSzResult(String szResult) {
            this.szResult = szResult;
        }

        public String getSzTherapy() {
            return szTherapy;
        }

        public void setSzTherapy(String szTherapy) {
            this.szTherapy = szTherapy;
        }

        public int getEndoType() {
            return endoType;
        }

        @Override
        public String toString() {
            return "DataDTO{" +
                    "iD='" + iD + '\'' +
                    ", iParentId='" + iParentId + '\'' +
                    ", szName='" + szName + '\'' +
                    ", szEndoDesc='" + szEndoDesc + '\'' +
                    ", szResult='" + szResult + '\'' +
                    ", szTherapy='" + szTherapy + '\'' +
                    ", endoType=" + endoType +
                    '}';
        }

        public void setEndoType(int endoType) {
            this.endoType = endoType;
        }
    }

    @Override
    public String toString() {
        return "CaseModelAllBean{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
