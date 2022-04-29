package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 15:49
 * desc：详情界面视频Bean
 */
public class DetailVideoBean {

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
        @SerializedName("Title")
        private String Title;
        @SerializedName("Description")
        private String Description;
        @SerializedName("FilePath")
        private String FilePath;
        @SerializedName("ThumbPath")
        private String ThumbPath;
        @SerializedName("RecordedAt")
        private String RecordedAt;
        @SerializedName("CreatedAt")
        private String CreatedAt;
        @SerializedName("ViewTimes")
        private int ViewTimes;

        @SerializedName("isSelected")
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

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

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
        }

        public String getThumbPath() {
            return ThumbPath;
        }

        public void setThumbPath(String ThumbPath) {
            this.ThumbPath = ThumbPath;
        }

        public String getRecordedAt() {
            return RecordedAt;
        }

        public void setRecordedAt(String RecordedAt) {
            this.RecordedAt = RecordedAt;
        }

        public String getCreatedAt() {
            return CreatedAt;
        }

        public void setCreatedAt(String CreatedAt) {
            this.CreatedAt = CreatedAt;
        }

        public int getViewTimes() {
            return ViewTimes;
        }

        public void setViewTimes(int ViewTimes) {
            this.ViewTimes = ViewTimes;
        }

        @Override
        public String toString() {
            return "DataDTO{" +
                    "ID='" + ID + '\'' +
                    ", RecordID='" + RecordID + '\'' +
                    ", FtpPath=" + FtpPath +
                    ", Title='" + Title + '\'' +
                    ", Description='" + Description + '\'' +
                    ", FilePath='" + FilePath + '\'' +
                    ", ThumbPath='" + ThumbPath + '\'' +
                    ", RecordedAt='" + RecordedAt + '\'' +
                    ", CreatedAt='" + CreatedAt + '\'' +
                    ", ViewTimes=" + ViewTimes +
                    '}';
        }
    }
}
