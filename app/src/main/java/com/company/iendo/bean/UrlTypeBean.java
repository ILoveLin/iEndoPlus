package com.company.iendo.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/29 11:43
 * desc：vlc 切换清晰度的bean
 */
public class UrlTypeBean {
    private String type;
    private String url;

    @Override
    public String toString() {
        return "UrlTypeBean{" +
                "type='" + type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
