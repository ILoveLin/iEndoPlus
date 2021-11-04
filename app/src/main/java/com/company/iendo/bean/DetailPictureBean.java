package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 15:48
 * desc：详情界面图片
 */
public class DetailPictureBean {


    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<DataDTO> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("ID")
        private String ID;
        @SerializedName("RecordID")
        private String RecordID;
        @SerializedName("FtpPath")
        private int FtpPath;
        @SerializedName("ImagePath")
        private String ImagePath;
        @SerializedName("ThumbPath")
        private String ThumbPath;
        @SerializedName("ImageDescription")
        private String ImageDescription;
        @SerializedName("ImageTitle")
        private String ImageTitle;
        @SerializedName("CreatedAt")
        private String CreatedAt;
        @SerializedName("ImageEdit")
        private String ImageEdit;
        @SerializedName("SketchMap")
        private String SketchMap;
        @SerializedName("ImageMarkX")
        private int ImageMarkX;
        @SerializedName("ImageMarkY")
        private int ImageMarkY;
        @SerializedName("Lesions")
        private String Lesions;
        @SerializedName("Position")
        private String Position;
        @SerializedName("Selected")
        private boolean Selected;
        @SerializedName("SOPInstanceUID")
        private String SOPInstanceUID;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getRecordID() {
            return RecordID;
        }

        public void setRecordID(String RecordID) {
            this.RecordID = RecordID;
        }

        public int getFtpPath() {
            return FtpPath;
        }

        public void setFtpPath(int FtpPath) {
            this.FtpPath = FtpPath;
        }

        public String getImagePath() {
            return ImagePath;
        }

        public void setImagePath(String ImagePath) {
            this.ImagePath = ImagePath;
        }

        public String getThumbPath() {
            return ThumbPath;
        }

        public void setThumbPath(String ThumbPath) {
            this.ThumbPath = ThumbPath;
        }

        public String getImageDescription() {
            return ImageDescription;
        }

        public void setImageDescription(String ImageDescription) {
            this.ImageDescription = ImageDescription;
        }

        public String getImageTitle() {
            return ImageTitle;
        }

        public void setImageTitle(String ImageTitle) {
            this.ImageTitle = ImageTitle;
        }

        public String getCreatedAt() {
            return CreatedAt;
        }

        public void setCreatedAt(String CreatedAt) {
            this.CreatedAt = CreatedAt;
        }

        public String getImageEdit() {
            return ImageEdit;
        }

        public void setImageEdit(String ImageEdit) {
            this.ImageEdit = ImageEdit;
        }

        public String getSketchMap() {
            return SketchMap;
        }

        public void setSketchMap(String SketchMap) {
            this.SketchMap = SketchMap;
        }

        public int getImageMarkX() {
            return ImageMarkX;
        }

        public void setImageMarkX(int ImageMarkX) {
            this.ImageMarkX = ImageMarkX;
        }

        public int getImageMarkY() {
            return ImageMarkY;
        }

        public void setImageMarkY(int ImageMarkY) {
            this.ImageMarkY = ImageMarkY;
        }

        public String getLesions() {
            return Lesions;
        }

        public void setLesions(String Lesions) {
            this.Lesions = Lesions;
        }

        public String getPosition() {
            return Position;
        }

        public void setPosition(String Position) {
            this.Position = Position;
        }

        public boolean isSelected() {
            return Selected;
        }

        public void setSelected(boolean Selected) {
            this.Selected = Selected;
        }

        public String getSOPInstanceUID() {
            return SOPInstanceUID;
        }

        public void setSOPInstanceUID(String SOPInstanceUID) {
            this.SOPInstanceUID = SOPInstanceUID;
        }
    }
}
