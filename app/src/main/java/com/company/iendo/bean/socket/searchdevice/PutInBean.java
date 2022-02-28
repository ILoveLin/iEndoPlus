package com.company.iendo.bean.socket.searchdevice;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/14 14:26
 * desc：授权接入数据Bean
 */
public class PutInBean {


    @SerializedName("pinAccess")
    private String pinAccess;
    @SerializedName("broadcaster")
    private String broadcaster;
    @SerializedName("spt")
    private String spt;

    public String getPinAccess() {
        return pinAccess;
    }

    public void setPinAccess(String pinAccess) {
        this.pinAccess = pinAccess;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getSpt() {
        return spt;
    }

    public void setSpt(String spt) {
        this.spt = spt;
    }
}
