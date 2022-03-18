package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/2/28 13:47
 * 编辑图片回调的bean
 * 病例ID--十六进制
 * <p>
 * 图像关联：
 * record_base->RecordID:918 (03 96)
 * images_of_record->id:2120 (08 48)
 */
public class EditPictureBean {
    @SerializedName("recordid")   //病例id
    private String recordid;
    @SerializedName("imageid")    //图片id
    private String imageid;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    @Override
    public String toString() {
        return "ShotPictureCallBlackBean{" +
                "recordid='" + recordid + '\'' +
                ", imageid='" + imageid + '\'' +
                '}';
    }
}
