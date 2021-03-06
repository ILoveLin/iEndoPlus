package com.company.iendo.bean.socket.searchdevice;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/14 14:44
 * desc：授权接入成功之后返回的数据Bean
 */
public class PutInDeviceMsgBean {


    /**
     *    Id:RTSP直播的账号id；
     *     Pw:RTSP直播的账号密码；
     *     from:设备名称
     *     IP：RTSP直播IP地址；
     *     Zpt:RTSP直播端口号；
     *     Stp:socke udp接收端口；
     *     Remark：备注
     *     Hpt：node js 服务端口
     *     Type:设备类型
     *     Et:科室类型
     *     retcode:授权返回码 0:接入成功 1：密码错误 2：不准接入
     *     16进制的json
     *     7b226970223a223139322e3136382e36342e3133222c227a7074223a2237373838222c226964223a22726f6f74222c227077223a22726f6f74222c2266726f6d223a2241494f2d454e54222c22737470223a2238303035222c22687074223a2237303031222c2272656d61726b223a2231E58FB7E58685E9959CE5AEA4222c2274797065223a223037222c226574223a2233222c22726574636f6465223a2230227d
     *     {"ip":"192.168.64.13","zpt":"7788","id":"root","pw":"root","from":"AIO-ENT","stp":"8005","hpt":"7001","remark":"1号内镜室","type":"07","et":"3","retcode":"0"}
     */




    @SerializedName("ip")
    private String ip;
    @SerializedName("zpt")
    private String zpt;
    @SerializedName("id")
    private String id;
    @SerializedName("pw")
    private String pw;
    @SerializedName("from")
    private String from;
    @SerializedName("stp")
    private String stp;
    @SerializedName("hpt")
    private String hpt;
    @SerializedName("remark")
    private String remark;
    @SerializedName("type")
    private String type;
    @SerializedName("et")
    private String et;
    @SerializedName("retcode")
    private String retcode;

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

    public String getStp() {
        return stp;
    }

    public void setStp(String stp) {
        this.stp = stp;
    }

    public String getHpt() {
        return hpt;
    }

    public void setHpt(String hpt) {
        this.hpt = hpt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    @Override
    public String toString() {
        return "PutInDeviceMsgBean{" +
                "ip='" + ip + '\'' +
                ", zpt='" + zpt + '\'' +
                ", id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", from='" + from + '\'' +
                ", stp='" + stp + '\'' +
                ", hpt='" + hpt + '\'' +
                ", remark='" + remark + '\'' +
                ", type='" + type + '\'' +
                ", et='" + et + '\'' +
                ", retcode='" + retcode + '\'' +
                '}';
    }
}
