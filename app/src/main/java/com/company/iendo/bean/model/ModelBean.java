package com.company.iendo.bean.model;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/7/2 9:02
 * desc：病例模板  的bean
 */
public class ModelBean {
    @SerializedName("ID")
    private String iD;
    private String iParentId;
    private String szName;
    private String szEndoDesc;
    private String szResult;
    private String szTherapy;
    @SerializedName("EndoType")
    private String endoType;

    @Override
    public String toString() {
        return "ModelBean{" +
                "iD='" + iD + '\'' +
                ", iParentId='" + iParentId + '\'' +
                ", szName='" + szName + '\'' +
                ", szEndoDesc='" + szEndoDesc + '\'' +
                ", szResult='" + szResult + '\'' +
                ", szTherapy='" + szTherapy + '\'' +
                ", endoType=" + endoType +
                '}';
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getiParentId() {
        return iParentId;
    }

    public void setiParentId(String iParentId) {
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

    public String getEndoType() {
        return endoType;
    }

    public void setEndoType(String endoType) {
        this.endoType = endoType;
    }
}
