package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 10:38
 * desc：握手bean,本质pc不需要这里为了gosn不会报错发一个数据报给他
 */
public class HandBean {
    @SerializedName("helloPc")
    private String helloPc;

    @SerializedName("comeFrom")
    private String comeFrom;

    public String getHelloPc() {
        return helloPc;
    }

    public void setHelloPc(String helloPc) {
        this.helloPc = helloPc;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }
}
