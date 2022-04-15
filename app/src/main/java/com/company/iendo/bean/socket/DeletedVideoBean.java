package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 10:38
 * desc：删除视频   更新同步
 */
public class DeletedVideoBean {
    @SerializedName("recordid")   //病例id
    private String recordid;
    @SerializedName("archive")    //图片id
    private String archive;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    @Override
    public String toString() {
        return "DeletedVideoBean{" +
                "recordid='" + recordid + '\'' +
                ", archive='" + archive + '\'' +
                '}';
    }
}
