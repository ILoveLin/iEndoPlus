package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 10:38
 * desc：删除图片  更新同步
 */
public class DeletedPictureBean {
    @SerializedName("recordid")   //病例id
    private String recordid;
    @SerializedName("imageid")    //图片id
    private String imageid;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    @Override
    public String toString() {
        return "ShotPictureCallBlackBean{" +
                "recordid='" + recordid + '\'' +
                ", imageid='" + imageid + '\'' +
                '}';
    }
}
