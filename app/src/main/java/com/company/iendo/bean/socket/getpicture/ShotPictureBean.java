package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 13:47
 * 截图
 * 病例ID--十六进制
 */
public class ShotPictureBean {
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
        return "UserIDBean{" +
                "recordid='" + recordid + '\'' +
                '}';
    }
}
