package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 13:47
 * 获取当前操作病历号（ID）
 * desc：{"recordid":"A2"}
 *
 * A2是十六进制 需要转换成十进制
 */
public class UserIDBean {
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
