package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 15:49
 * desc：详情界面视频Bean,特定下载视频使用,新增了本地字段
 */
public class DetailDownVideoBean implements Serializable{

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

    public static class DataDTO  implements Serializable {
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
        private boolean isSelected;  //是否选中    自定义字段 : 选中了,就要添加到下载队列中,


        @SerializedName("allUrl")
        private String allUrl;       //完整的下载地址    自定义字段 比如：http://192.168.31.249:7001/4/2022-04-19-17-54-07.mp4
        @SerializedName("FileName")
        private String FileName;       //视频名字    自定义字段 比如:2022-04-19-17-54-07.mp4和下载视频中的tag 一致
         @SerializedName("localFolderName")
        private String localFolderName;       //视频文件夹    经常为null,所以此处,pass自定义字段 比如:/storage/emulated/0/ MyDownVideos/31376335613463353432626432363561_9
         @SerializedName("isDowned")
        private boolean isDowned;       //是否下载过    自定义字段
         @SerializedName("isInQueue")
        private boolean isInQueue;       //是否在下载列表中   自定义字段
        @SerializedName("processMax")
        private long processMax;       //进度条最大值    自定义字段

        @SerializedName("processOffset")
        private long processOffset;             //进度条当前下载量    自定义字段
        @SerializedName("processformatOffset")
        private String processformatOffset;       //进度条当前下载量-格式化    自定义字段
        @SerializedName("downStatue")
        private String downStatue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中


        @SerializedName("downStatueDes")
        private String downStatueDes;  //下载状态描述   开始下载  下载中... 下载完成  下载错误

        @SerializedName("speed")
        private String speed;       //进度条当前速度    自定义字段


        public boolean isInQueue() {
            return isInQueue;
        }

        public void setInQueue(boolean inQueue) {
            isInQueue = inQueue;
        }

        public String getSpeed() {
            return speed;
        }

        public String getDownStatueDes() {
            return downStatueDes;
        }

        public void setDownStatueDes(String downStatueDes) {
            this.downStatueDes = downStatueDes;
        }

        public String getDownStatue() {
            return downStatue;
        }

        public void setDownStatue(String downStatue) {
            this.downStatue = downStatue;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public long getProcessMax() {
            return processMax;
        }

        public long getProcessOffset() {
            return processOffset;
        }

        public void setProcessOffset(long processOffset) {
            this.processOffset = processOffset;
        }

        public String getProcessformatOffset() {
            return processformatOffset;
        }

        public void setProcessformatOffset(String processformatOffset) {
            this.processformatOffset = processformatOffset;
        }

        public void setProcessMax(long processMax) {
            this.processMax = processMax;
        }

        public String getLocalFolderName() {
            return localFolderName;
        }

        public void setLocalFolderName(String localFolderName) {
            this.localFolderName = localFolderName;
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
                    ", isSelected=" + isSelected +
                    ", allUrl='" + allUrl + '\'' +
                    ", FileName='" + FileName + '\'' +
                    ", localFolderName='" + localFolderName + '\'' +
                    ", isDowned=" + isDowned +
                    ", processMax=" + processMax +
                    ", processOffset=" + processOffset +
                    ", processformatOffset='" + processformatOffset + '\'' +
                    ", downStatue='" + downStatue + '\'' +
                    ", downStatueDes='" + downStatueDes + '\'' +
                    ", speed='" + speed + '\'' +
                    '}';
        }

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String fileName) {
            FileName = fileName;
        }

        public boolean isDowned() {
            return isDowned;
        }

        public void setDowned(boolean downed) {
            isDowned = downed;
        }

        public String getAllUrl() {
            return allUrl;
        }

        public void setAllUrl(String allUrl) {
            this.allUrl = allUrl;
        }

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

    }
}
