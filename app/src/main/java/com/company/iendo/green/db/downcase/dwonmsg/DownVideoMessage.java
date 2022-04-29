package com.company.iendo.green.db.downcase.dwonmsg;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/24 10:39
 * desc：下载视频保存的bean
 * 离线模式下需要单独去查询当前设备当前病例下的视频数目
 * 下载了什么视频, 会存在  不像TaskDBBeanUtils 的TaskDBBean 是队列中下载数目  随着下载完毕而删除
 *
 *
 */
@Entity
public class DownVideoMessage {
    //主键
    @Id(autoincrement = true)
    private Long id;
    /**
     * 设备码0000000000000000546017FE6BC28949
     */
    private String DeviceCode;

    //当前DeviceCode设备下的的某个病例ID比如设备:0000000000000000546017FE6BC28949下的-->1195号病例
    private String saveCaseID;

    // 最大值
    private Long maxProcess;

    // tag下载filename==tag
    private String tag;

    // 完整的url 本地播放url
    private String url;

    //是否下载过
    private Boolean isDown;//默认未下载,false下载成功之后设置为flase

    @Generated(hash = 760374497)
    public DownVideoMessage(Long id, String DeviceCode, String saveCaseID,
            Long maxProcess, String tag, String url, Boolean isDown) {
        this.id = id;
        this.DeviceCode = DeviceCode;
        this.saveCaseID = saveCaseID;
        this.maxProcess = maxProcess;
        this.tag = tag;
        this.url = url;
        this.isDown = isDown;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public String getSaveCaseID() {
        return saveCaseID;
    }

    public void setSaveCaseID(String saveCaseID) {
        this.saveCaseID = saveCaseID;
    }



    @Generated(hash = 738164071)
    public DownVideoMessage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaxProcess() {
        return this.maxProcess;
    }

    public void setMaxProcess(Long maxProcess) {
        this.maxProcess = maxProcess;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsDown() {
        return this.isDown;
    }

    public void setIsDown(Boolean isDown) {
        this.isDown = isDown;
    }

    @Override
    public String toString() {
        return "DownVideoMessage{" +
                "id=" + id +
                ", DeviceCode='" + DeviceCode + '\'' +
                ", saveCaseID='" + saveCaseID + '\'' +
                ", maxProcess=" + maxProcess +
                ", tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", isDown=" + isDown +
                '}';
    }
}
