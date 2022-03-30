package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/30 8:37
 * <p>
 * desc：语音接入请求Bean
 * {"title":"1号内镜室""，calltype":"rtmp"，"servicemode":"0"，"online"："1"}
 * 说明
 * title:手机厂商_手机型号_UserName
 * servicemode:1表示我们要从服务器获取url
 * *  *  *  *  *  *  *
 * title:接入名称
 * calltype:流类型
 * servicemode:服务方式   （0：客户端 1：服务器，需要返回服务地址）
 * online:是否在线     (0：离线 1:上线)
 *
 */
public class MicRequestBean {

    @SerializedName("title")
    private String title;
    @SerializedName("calltype")
    private String calltype;
    @SerializedName("servicemode")
    private String servicemode;
    @SerializedName("online")
    private String online;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getServicemode() {
        return servicemode;
    }

    public void setServicemode(String servicemode) {
        this.servicemode = servicemode;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
