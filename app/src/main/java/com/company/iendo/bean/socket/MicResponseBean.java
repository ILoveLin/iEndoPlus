package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/30 8:37
 * <p>
 * desc：语音接入响应Bean
 * {"url":"rtmp://192.168.64.13:8350/live/2022001","online":"0"}
 * url:流地址
 * online:是否在线
 * (0：离线 1:上线)
 */
public class MicResponseBean {
    @SerializedName("url")
    private String url;
    @SerializedName("online")
    private String online;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
