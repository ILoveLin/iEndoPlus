package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/14 14:44
 * desc：授权接入成功之后返回的数据Bean
 */
public class PutInDeviceMsgBean {
    /**
     * Id:RTSP直播的账号id；
     * Pw:RTSP直播的账号密码；
     * from:设备名称
     * IP：RTSP直播IP地址；
     * Zpt:RTSP直播端口号；
     * Stp:socke udp接收端口；
     * Type:设备类型
     * Et:科室类型
     */
    @SerializedName("id")
    private String id;
    @SerializedName("pw")
    private String pw;
    @SerializedName("from")
    private String from;
    @SerializedName("ip")
    private String ip;
    @SerializedName("zpt")
    private String zpt;
    @SerializedName("spt")
    private String spt;
    @SerializedName("type")
    private String type;
    @SerializedName("et")
    private String et;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getZpt() {
        return zpt;
    }

    public void setZpt(String zpt) {
        this.zpt = zpt;
    }

    public String getSpt() {
        return spt;
    }

    public void setSpt(String spt) {
        this.spt = spt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et;
    }

    @Override
    public String toString() {
        return "PutInDeviceMsgBean{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", from='" + from + '\'' +
                ", ip='" + ip + '\'' +
                ", zpt='" + zpt + '\'' +
                ", spt='" + spt + '\'' +
                ", type='" + type + '\'' +
                ", et='" + et + '\'' +
                '}';
    }
}
