package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 13:47
 * 冻结-解冻
 * {"freeze":"00"}
 * 00冻结，01解冻
 *
 */
public class ColdPictureBean {
    @SerializedName("freeze")
    private String freeze;

    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    @Override
    public String toString() {
        return "ColdPictureBean{" +
                "freeze='" + freeze + '\'' +
                '}';
    }
}
