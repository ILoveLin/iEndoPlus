package com.company.iendo.green.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/16 15:13
 * desc：数据库设备表
 * <p>
 * <p>
 * //////////////////////////////////////////////////////////////////////////
 * //  EndoType.h 版本：v1.01
 * //  修订时间：2021年12月29日 最后修订人:dzh
 * //
 * //  定义软件的应用科室或应用场景（内镜类型）
 * //////////////////////////////////////////////////////////////////////////
 * <p>
 * #pragma once
 * <p>
 * #define  ALL_TYPE_NEW 			      -1 //全科室 新状态 不会 出现 选择其他 科室的 按钮
 * #define  ALL_TYPE					  0 //全科室 默认状态   会 出现 选择其他 科室的 按钮
 * #define  UNKNOWN_TYPE				  0 //未知科室
 * //内镜科室
 * #define  GASTROSCOPY                  1 //胃镜
 * #define  COLONOSCOPY                  2 //肠镜
 * #define  ENT                          3 //耳鼻喉镜              接入授权-Type:设备类型
 * #define  HYSTEROSCOPY				  4 //宫腔镜
 * #define  CHOLEDOCHOSCOPE              5 //胆道镜
 * #define  CYSTOSCOPY					  6 //膀胱镜
 * #define  DUODENOSCOPY				  7 //十二指肠镜
 * #define  BRONCHOSCOPY                 8 //支气管镜
 * #define  ULTRASOUND                   9 //超声
 * #define  DUCTOSCOPY                   10 //乳管镜
 * #define  GASTROINTESTINAL_ENDOSCOPY   11//胃肠镜
 * <p>
 * <p>
 * <p>
 * #define  GYNECOLOGICAL_LAPAROSCOPY   100 //妇科腹腔镜
 * #define  LAPAROSCOPY                 101 //腹腔镜
 * #define  FETOSCOPY					 102 //胎儿镜
 * #define  MICROSCOPY                  103 //显微镜
 * #define  OPHTHALMOLOGY               104 //眼科
 * #define  RESECTOSCOPY				 105 //前列腺电切镜
 * #define  URETERSCOPY				 106 //输尿管镜
 * #define  NEURO_SURGERY               107 //神经外科镜
 * #define  CEREBRAL_SURGERY            108 //脑外科
 * #define  CARDIOTHORACIC_SURGERY      109 //心胸外科
 * #define  AESTHETIC_ENDOSCOPY         110 //美容镜
 * #define  ARTHROSCOPY                 111 //关节镜
 * #define  TRANSCUTANEOUS_NEPHROSCOPY  112 //经皮肾镜
 * #define  TRANSFORAMINAL_ENDOSCOPY    113 //椎间孔镜
 * #define  HYSTEROSCOPY_LAPAROSCOPY    114 //宫腹腔镜
 * <p>
 * <p>
 * 通过设备表查找用户
 */

@Entity
public class DeviceDBBean {
    //主键
    @Id(autoincrement = true)
    private Long id;            //这个主键ID是需要绑定用户表中的deviceID,确保是这个设备下,离线模式能通过id查询绑定用户
    //设备唯一标识
    private String deviceID;      //deviceID=deviceCode    作用一样的
    //设备码
    private String deviceCode;  //  这个是智能搜索之后返回过来的设备码//  这个是智能搜索之后返回过来的设备码//  这个是智能搜索之后返回过来的设备码
    //设备ip
    private String ip;
    //设备http端口--就是ode js 服务端口
    private String httpPort;
    //设备socket端口
    private String socketPort;
    //设备直播端口
    private String livePort;
    //设备语音端口
    private String micPort;
    //设备账号(直播)
    private String username;
    //设备密码(直播)
    private String password;
    //设备标题
    private String title;
    //设备描述 --备注
    private String msg;
    //设备类型
    private String type;  //34567  数字对应  比如10是耳鼻喉治疗台            type和endotype不是相等的!!!!
    //工作站类型
    private String endoType;//
    ////mDeviceCode  这个是智能搜索之后返回过来的设备码
    private String deviceName;  //设备名字
    //备用字段1
    private String usemsg01;  //备用字段1    是否长按选中了,显示修改和删除功能  true为选中
    //备用字段2
    private String usemsg02;  //备用字段2
    //是否被选中
    private Boolean mSelected;    //是否被选中  默认未选中  :0未选中,1被选中

    @Generated(hash = 1981403596)
    public DeviceDBBean(Long id, String deviceID, String deviceCode, String ip, String httpPort, String socketPort,
            String livePort, String micPort, String username, String password, String title, String msg, String type,
            String endoType, String deviceName, String usemsg01, String usemsg02, Boolean mSelected) {
        this.id = id;
        this.deviceID = deviceID;
        this.deviceCode = deviceCode;
        this.ip = ip;
        this.httpPort = httpPort;
        this.socketPort = socketPort;
        this.livePort = livePort;
        this.micPort = micPort;
        this.username = username;
        this.password = password;
        this.title = title;
        this.msg = msg;
        this.type = type;
        this.endoType = endoType;
        this.deviceName = deviceName;
        this.usemsg01 = usemsg01;
        this.usemsg02 = usemsg02;
        this.mSelected = mSelected;
    }

    @Generated(hash = 1828217020)
    public DeviceDBBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHttpPort() {
        return this.httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getSocketPort() {
        return this.socketPort;
    }

    public void setSocketPort(String socketPort) {
        this.socketPort = socketPort;
    }

    public String getLivePort() {
        return this.livePort;
    }

    public void setLivePort(String livePort) {
        this.livePort = livePort;
    }

    public String getMicPort() {
        return this.micPort;
    }

    public void setMicPort(String micPort) {
        this.micPort = micPort;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndoType() {
        return this.endoType;
    }

    public void setEndoType(String endoType) {
        this.endoType = endoType;
    }

    public String getUsemsg01() {
        return this.usemsg01;
    }

    public void setUsemsg01(String usemsg01) {
        this.usemsg01 = usemsg01;
    }

    public String getUsemsg02() {
        return this.usemsg02;
    }

    public void setUsemsg02(String usemsg02) {
        this.usemsg02 = usemsg02;
    }

    @Override
    public String toString() {
        return "DeviceDBBean{" +
                "id=" + id +
                ", deviceID='" + deviceID + '\'' +
                ", ip='" + ip + '\'' +
                ", httpPort='" + httpPort + '\'' +
                ", socketPort='" + socketPort + '\'' +
                ", livePort='" + livePort + '\'' +
                ", micPort='" + micPort + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                ", endoType='" + endoType + '\'' +
                ", usemsg01='" + usemsg01 + '\'' +
                ", usemsg02='" + usemsg02 + '\'' +
                ", mSelected=" + mSelected +
                '}';
    }

    public Boolean getMSelected() {
        return this.mSelected;
    }

    public void setMSelected(Boolean mSelected) {
        this.mSelected = mSelected;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return this.deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
