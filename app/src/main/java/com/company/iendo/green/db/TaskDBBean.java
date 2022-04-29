package com.company.iendo.green.db;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/28 14:32
 * desc：下载任务表 mDeviceCode + "_" + currentItemCaseID 为标识
 *
 * 下载完毕删除当前
 *
 * 下载队列中下载任务的bean  下载完毕了就删除
 *
 */
@Entity
public class TaskDBBean {

    //主键
    @Id(autoincrement = true)
    private Long id;



    //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始, READY:准备, DOWNING:下载中,CANCELED:暂停.    英文
    private String downStatue;

    //状态值:成功或者失败  COMPLETED:已下载, ERROR:失败,  START:开始, READY:准备中, DOWNING:下载中,CANCELED:暂停  中文
    private String downStatueDes;


    private String commonCode;  //mDeviceCode + "_" + currentItemCaseID         :确保了设备,和病例

    private String singleCode;  //mDeviceCode + "_" + currentItemCaseID + "_" +TAG   :确保了设备,病例,和具体的哪个视频   :唯一标识


    private String taskString;    //当前下载任务的bean String json数据

    @Generated(hash = 1967681179)
    public TaskDBBean(Long id, String downStatue, String downStatueDes, String commonCode, String singleCode,
            String taskString) {
        this.id = id;
        this.downStatue = downStatue;
        this.downStatueDes = downStatueDes;
        this.commonCode = commonCode;
        this.singleCode = singleCode;
        this.taskString = taskString;
    }

    @Generated(hash = 898533664)
    public TaskDBBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommonCode() {
        return commonCode;
    }

    public void setCommonCode(String commonCode) {
        this.commonCode = commonCode;
    }

    public String getSingleCode() {
        return singleCode;
    }

    public void setSingleCode(String singleCode) {
        this.singleCode = singleCode;
    }

    public String getTaskString() {
        return taskString;
    }

    public void setTaskString(String taskString) {
        this.taskString = taskString;
    }

    public String getDownStatue() {
        return this.downStatue;
    }

    public void setDownStatue(String downStatue) {
        this.downStatue = downStatue;
    }

    public String getDownStatueDes() {
        return this.downStatueDes;
    }

    public void setDownStatueDes(String downStatueDes) {
        this.downStatueDes = downStatueDes;
    }
}
