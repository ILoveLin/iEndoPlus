package com.company.iendo.green.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/28 17:56
 * desc  数据库用户Bean
 *
 * 所有的用户都是绑定设备的也就是说用户表的deviceID==设备表的主键id
 */
@Entity
public class UserDBBean {
    //主键
    @Id(autoincrement = true)
    private Long id;


    /**
     * 设备ID
     * 这个用户是在哪个设备上的     用户和病例都是和设备绑定的
     * 当前选中设备的deviceID,因为离线模式下就能通过这个deviceID查找这个设备下的所有用户
     */
    private String deviceID;     //把设备表的deviceCode赋值给deviceID(相当于他是查询标识)

    //设备上主键ID   ---设备返回的ID      登录或者查询的时候后台返回的ID   用来修改密码等等接口请求使用这个ID
    private String deviceUserID;       //接口返回的userID
    //用户名
    private String UserName;

    //用户密码
    private String Password;

    //角色权限:0-管理员 1-操作员 2-查询员
    private String relo;

    //备用01字段--->是否被下载过  true 表示下载过, false 表示没有被下载(在线登入的时候在记住密码的时候会存入false)
    private String make01;
    //备用02字段--->离线登录的时候,是否通过点击列表选中的状态,整个列表仅有一个true  其他的都为false,初始化状态,默认第一个用户true
    private String make02;
    private Boolean isRememberPassword;

    @Generated(hash = 1001669383)
    public UserDBBean(Long id, String deviceID, String deviceUserID,
            String UserName, String Password, String relo, String make01,
            String make02, Boolean isRememberPassword) {
        this.id = id;
        this.deviceID = deviceID;
        this.deviceUserID = deviceUserID;
        this.UserName = UserName;
        this.Password = Password;
        this.relo = relo;
        this.make01 = make01;
        this.make02 = make02;
        this.isRememberPassword = isRememberPassword;
    }

    @Generated(hash = 202817274)
    public UserDBBean() {
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

    public String getDeviceUserID() {
        return this.deviceUserID;
    }

    public void setDeviceUserID(String deviceUserID) {
        this.deviceUserID = deviceUserID;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getRelo() {
        return this.relo;
    }

    public void setRelo(String relo) {
        this.relo = relo;
    }

    public String getMake01() {
        return this.make01;
    }

    public void setMake01(String make01) {
        this.make01 = make01;
    }

    public String getMake02() {
        return this.make02;
    }

    public void setMake02(String make02) {
        this.make02 = make02;
    }

    public Boolean getIsRememberPassword() {
        return this.isRememberPassword;
    }

    public void setIsRememberPassword(Boolean isRememberPassword) {
        this.isRememberPassword = isRememberPassword;
    }

    @Override
    public String toString() {
        return "UserDBBean{" +
                "id=" + id +
                ", deviceID='" + deviceID + '\'' +
                ", deviceUserID='" + deviceUserID + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Password='" + Password + '\'' +
                ", relo='" + relo + '\'' +
                ", make01='" + make01 + '\'' +
                ", make02='" + make02 + '\'' +
                ", isRememberPassword=" + isRememberPassword +
                '}';
    }
}
