package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/1/26 15:28
 * desc：UDP广播发起的Deta
 */
public class BroadCastDataBean {

    /**
     * broadcaster：广播发起者名称
     * ramdom:广播发起随机时间戳
     */
    @SerializedName("broadcaster")
    private String broadcaster;
    @SerializedName("ramdom")
    private String ramdom;

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getRamdom() {
        return ramdom;
    }

    public void setRamdom(String ramdom) {
        this.ramdom = ramdom;
    }

    @Override
    public String toString() {
        return "BroadCastDataBean{" +
                "broadcaster='" + broadcaster + '\'' +
                ", ramdom='" + ramdom + '\'' +
                '}';
    }
}
