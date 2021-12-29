package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/29 16:17
 * desc：
 */
public class DialogItemBean {
    @SerializedName("ID")
    private String ID;
    @SerializedName("ParentId")
    private String ParentId;
    @SerializedName("DictName")
    private String DictName;
    @SerializedName("DictItem")
    private String DictItem;
    @SerializedName("EndoType")
    private int EndoType;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    public String getDictName() {
        return DictName;
    }

    public void setDictName(String DictName) {
        this.DictName = DictName;
    }

    public String getDictItem() {
        return DictItem;
    }

    public void setDictItem(String DictItem) {
        this.DictItem = DictItem;
    }

    public int getEndoType() {
        return EndoType;
    }

    public void setEndoType(int EndoType) {
        this.EndoType = EndoType;
    }

    @Override
    public String toString() {
        return "DialogItemBean{" +
                "ID='" + ID + '\'' +
                ", ParentId='" + ParentId + '\'' +
                ", DictName='" + DictName + '\'' +
                ", DictItem='" + DictItem + '\'' +
                ", EndoType=" + EndoType +
                '}';
    }
}
