package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/3 15:51
 * desc：握手消息的event
 */
public class SocketRefreshEvent {
    //成功或者失败 true 握手成功
    private Boolean tga;
    //当前UDP的CMD
    private String udpCmd;
    //回调的数据
    private String data;
    //回调的ip--->data对应的ip地址
    private String ip;


    public String getUdpCmd() {
        return udpCmd;
    }

    public void setUdpCmd(String udpCmd) {
        this.udpCmd = udpCmd;
    }

    public Boolean getTga() {
        return tga;
    }

    public void setTga(Boolean tga) {
        this.tga = tga;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
