package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/14 10:26
 * desc：发送广播之后接收到的数据
 */
public class BroadCastReceiveBean {
    /**
     * {"title":"AIO-ENT","remark":"1号内镜室","endotype":"3","accept":"1","pinAccess":"123456"}
     * title:标题
     * remark:备注
     * endotype:科室代码
     * accept：是否准许接入  1准许 0不准
     * pinAccess:接入密码
     */

    @SerializedName("title")
    private String title;
    @SerializedName("remark")
    private String remark;
    @SerializedName("endotype")
    private String endotype;
    @SerializedName("accept")     //是否准许接入
    private String accept;
    @SerializedName("pinAccess")
    private String pinAccess;


    private Boolean mSelected;    //自己添加的字段 是否选中
    private String ip;               //自己添加的字段 数据来源的ip地址值
    private String receiveType;    //接收方设备类型----广播回调获取到什么设备发来的消息,发送接入消息的时候,需要发到receiveType这个设备里去
    private String receiveID;    //接收方设备类型----广播回调获取到什么设备发来的消息,发送接入消息的时候,需要发到receiveType这个设备里去
    private String deviceType;    //自己添加的字段 设备类型,后续bean对象存入数据库,更具此字段,设置默认值
    private String deviceCode;    //自己添加的字段 这个是智能搜索之后返回过来的设备码     和receiveID字段一样的
    private String itemId;        //标识,点击选中和未选中状态切换
    private Boolean inDB;           //是否存入数据库
    private Boolean isCheckAccess;       //需要登入密码的情况下,是否检验接入过,true表示校验接入过,false表示没有


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEndotype() {
        return endotype;
    }

    public void setEndotype(String endotype) {
        this.endotype = endotype;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getPinAccess() {
        return pinAccess;
    }

    public void setPinAccess(String pinAccess) {
        this.pinAccess = pinAccess;
    }

    public Boolean getSelected() {
        return mSelected;
    }

    public void setSelected(Boolean mSelected) {
        this.mSelected = mSelected;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Boolean getCheckAccess() {
        return isCheckAccess;
    }

    public void setCheckAccess(Boolean checkAccess) {
        isCheckAccess = checkAccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(String receiveID) {
        this.receiveID = receiveID;
    }

    @Override
    public String toString() {
        return "BroadCastReceiveBean{" +
                "title='" + title + '\'' +
                ", remark='" + remark + '\'' +
                ", endotype='" + endotype + '\'' +
                ", accept='" + accept + '\'' +
                ", pinAccess='" + pinAccess + '\'' +
                ", mSelected=" + mSelected +
                ", ip='" + ip + '\'' +
                ", receiveType='" + receiveType + '\'' +
                ", receiveID='" + receiveID + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", itemId='" + itemId + '\'' +
                ", inDB=" + inDB +
                ", isCheckAccess=" + isCheckAccess +
                '}';
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public Boolean getInDB() {
        return inDB;
    }

    public void setInDB(Boolean inDB) {
        this.inDB = inDB;
    }
}
