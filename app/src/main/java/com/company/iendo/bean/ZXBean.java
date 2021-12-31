package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/31 13:37
 * desc：二维码扫描Bean
 */
public class ZXBean {


    @SerializedName("deviceID")
    private String deviceID;
    @SerializedName("deviceType")
    private int deviceType;
    @SerializedName("endoType")
    private int endoType;
    @SerializedName("httpPort")
    private int httpPort;
    @SerializedName("ip")
    private String ip;
    @SerializedName("makemsg")
    private String makemsg;
    @SerializedName("micport")
    private int micport;
    @SerializedName("password")
    private String password;
    @SerializedName("port")
    private int port;
    @SerializedName("socketPort")
    private int socketPort;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private int type;
    @SerializedName("username")
    private String username;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getEndoType() {
        return endoType;
    }

    public void setEndoType(int endoType) {
        this.endoType = endoType;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMakemsg() {
        return makemsg;
    }

    public void setMakemsg(String makemsg) {
        this.makemsg = makemsg;
    }

    public int getMicport() {
        return micport;
    }

    public void setMicport(int micport) {
        this.micport = micport;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
