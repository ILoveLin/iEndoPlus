package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/5/25 16:20
 * desc：用户详情bean
 */
public class UserDetailBean {


    @SerializedName("data")
    private DataDTO data;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    @Override
    public String toString() {
        return "UserDetailBean{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

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

    public static class DataDTO {
        @SerializedName("user")
        private UserDTO user;
        @SerializedName("purview")
        private PurviewDTO purview;

        @Override
        public String toString() {
            return "DataDTO{" +
                    "user=" + user +
                    ", purview=" + purview +
                    '}';
        }

        public UserDTO getUser() {
            return user;
        }

        public void setUser(UserDTO user) {
            this.user = user;
        }

        public PurviewDTO getPurview() {
            return purview;
        }

        public void setPurview(PurviewDTO purview) {
            this.purview = purview;
        }

        public static class UserDTO {
            @SerializedName("UserID")
            private String UserID;
            @SerializedName("UserName")
            private String UserName;
            @SerializedName("Des")
            private String Des;
            @SerializedName("CreatedAt")
            private String CreatedAt;
            @SerializedName("LastLoginAt")
            private String LastLoginAt;
            @SerializedName("LoginTimes")
            private int LoginTimes;
            @SerializedName("CanUSE")
            private boolean CanUSE;
            @SerializedName("Role")
            private int Role;

            @Override
            public String toString() {
                return "UserDTO{" +
                        "UserID='" + UserID + '\'' +
                        ", UserName='" + UserName + '\'' +
                        ", Des='" + Des + '\'' +
                        ", CreatedAt='" + CreatedAt + '\'' +
                        ", LastLoginAt='" + LastLoginAt + '\'' +
                        ", LoginTimes=" + LoginTimes +
                        ", CanUSE=" + CanUSE +
                        ", Role=" + Role +
                        '}';
            }

            public String getUserID() {
                return UserID;
            }

            public void setUserID(String UserID) {
                this.UserID = UserID;
            }

            public String getUserName() {
                return UserName;
            }

            public void setUserName(String UserName) {
                this.UserName = UserName;
            }

            public String getDes() {
                return Des;
            }

            public void setDes(String Des) {
                this.Des = Des;
            }

            public String getCreatedAt() {
                return CreatedAt;
            }

            public void setCreatedAt(String CreatedAt) {
                this.CreatedAt = CreatedAt;
            }

            public String getLastLoginAt() {
                return LastLoginAt;
            }

            public void setLastLoginAt(String LastLoginAt) {
                this.LastLoginAt = LastLoginAt;
            }

            public int getLoginTimes() {
                return LoginTimes;
            }

            public void setLoginTimes(int LoginTimes) {
                this.LoginTimes = LoginTimes;
            }

            public boolean isCanUSE() {
                return CanUSE;
            }

            public void setCanUSE(boolean CanUSE) {
                this.CanUSE = CanUSE;
            }

            public int getRole() {
                return Role;
            }

            public void setRole(int Role) {
                this.Role = Role;
            }
        }

        public static class PurviewDTO {
            @SerializedName("UserID")
            private String UserID;
            @SerializedName("UserMan")
            private boolean UserMan;
            @SerializedName("CanPsw")
            private boolean CanPsw;
            @SerializedName("CanNew")
            private boolean CanNew;
            @SerializedName("CanEdit")
            private boolean CanEdit;
            @SerializedName("CanDelete")
            private boolean CanDelete;
            @SerializedName("CanPrint")
            private boolean CanPrint;
            @SerializedName("ReportStyle")
            private boolean ReportStyle;
            @SerializedName("DictsMan")
            private boolean DictsMan;
            @SerializedName("GlossaryMan")
            private boolean GlossaryMan;
            @SerializedName("TempletMan")
            private boolean TempletMan;
            @SerializedName("HospitalInfo")
            private boolean HospitalInfo;
            @SerializedName("CanBackup")
            private boolean CanBackup;
            @SerializedName("ViewBackup")
            private boolean ViewBackup;
            @SerializedName("VideoSet")
            private boolean VideoSet;
            @SerializedName("OnlySelf")
            private boolean OnlySelf;
            @SerializedName("UnPrinted")
            private boolean UnPrinted;
            @SerializedName("FtpSet")
            private boolean FtpSet;
            @SerializedName("ChangeDepartment")
            private boolean ChangeDepartment;
            @SerializedName("ExportRecord")
            private boolean ExportRecord;
            @SerializedName("ExportImage")
            private boolean ExportImage;
            @SerializedName("ExportVideo")
            private boolean ExportVideo;
            @SerializedName("DeviceSet")
            private boolean DeviceSet;
            @SerializedName("SeatAdjust")
            private boolean SeatAdjust;
            @SerializedName("SnapVideoRecord")
            private boolean SnapVideoRecord;
            @SerializedName("LiveStream")
            private boolean LiveStream;
            @SerializedName("Role")
            private int Role;

            @Override
            public String toString() {
                return "PurviewDTO{" +
                        "UserID='" + UserID + '\'' +
                        ", UserMan=" + UserMan +
                        ", CanPsw=" + CanPsw +
                        ", CanNew=" + CanNew +
                        ", CanEdit=" + CanEdit +
                        ", CanDelete=" + CanDelete +
                        ", CanPrint=" + CanPrint +
                        ", ReportStyle=" + ReportStyle +
                        ", DictsMan=" + DictsMan +
                        ", GlossaryMan=" + GlossaryMan +
                        ", TempletMan=" + TempletMan +
                        ", HospitalInfo=" + HospitalInfo +
                        ", CanBackup=" + CanBackup +
                        ", ViewBackup=" + ViewBackup +
                        ", VideoSet=" + VideoSet +
                        ", OnlySelf=" + OnlySelf +
                        ", UnPrinted=" + UnPrinted +
                        ", FtpSet=" + FtpSet +
                        ", ChangeDepartment=" + ChangeDepartment +
                        ", ExportRecord=" + ExportRecord +
                        ", ExportImage=" + ExportImage +
                        ", ExportVideo=" + ExportVideo +
                        ", DeviceSet=" + DeviceSet +
                        ", SeatAdjust=" + SeatAdjust +
                        ", SnapVideoRecord=" + SnapVideoRecord +
                        ", LiveStream=" + LiveStream +
                        ", Role=" + Role +
                        '}';
            }

            public String getUserID() {
                return UserID;
            }

            public void setUserID(String UserID) {
                this.UserID = UserID;
            }

            public boolean isUserMan() {
                return UserMan;
            }

            public void setUserMan(boolean UserMan) {
                this.UserMan = UserMan;
            }

            public boolean isCanPsw() {
                return CanPsw;
            }

            public void setCanPsw(boolean CanPsw) {
                this.CanPsw = CanPsw;
            }

            public boolean isCanNew() {
                return CanNew;
            }

            public void setCanNew(boolean CanNew) {
                this.CanNew = CanNew;
            }

            public boolean isCanEdit() {
                return CanEdit;
            }

            public void setCanEdit(boolean CanEdit) {
                this.CanEdit = CanEdit;
            }

            public boolean isCanDelete() {
                return CanDelete;
            }

            public void setCanDelete(boolean CanDelete) {
                this.CanDelete = CanDelete;
            }

            public boolean isCanPrint() {
                return CanPrint;
            }

            public void setCanPrint(boolean CanPrint) {
                this.CanPrint = CanPrint;
            }

            public boolean isReportStyle() {
                return ReportStyle;
            }

            public void setReportStyle(boolean ReportStyle) {
                this.ReportStyle = ReportStyle;
            }

            public boolean isDictsMan() {
                return DictsMan;
            }

            public void setDictsMan(boolean DictsMan) {
                this.DictsMan = DictsMan;
            }

            public boolean isGlossaryMan() {
                return GlossaryMan;
            }

            public void setGlossaryMan(boolean GlossaryMan) {
                this.GlossaryMan = GlossaryMan;
            }

            public boolean isTempletMan() {
                return TempletMan;
            }

            public void setTempletMan(boolean TempletMan) {
                this.TempletMan = TempletMan;
            }

            public boolean isHospitalInfo() {
                return HospitalInfo;
            }

            public void setHospitalInfo(boolean HospitalInfo) {
                this.HospitalInfo = HospitalInfo;
            }

            public boolean isCanBackup() {
                return CanBackup;
            }

            public void setCanBackup(boolean CanBackup) {
                this.CanBackup = CanBackup;
            }

            public boolean isViewBackup() {
                return ViewBackup;
            }

            public void setViewBackup(boolean ViewBackup) {
                this.ViewBackup = ViewBackup;
            }

            public boolean isVideoSet() {
                return VideoSet;
            }

            public void setVideoSet(boolean VideoSet) {
                this.VideoSet = VideoSet;
            }

            public boolean isOnlySelf() {
                return OnlySelf;
            }

            public void setOnlySelf(boolean OnlySelf) {
                this.OnlySelf = OnlySelf;
            }

            public boolean isUnPrinted() {
                return UnPrinted;
            }

            public void setUnPrinted(boolean UnPrinted) {
                this.UnPrinted = UnPrinted;
            }

            public boolean isFtpSet() {
                return FtpSet;
            }

            public void setFtpSet(boolean FtpSet) {
                this.FtpSet = FtpSet;
            }

            public boolean isChangeDepartment() {
                return ChangeDepartment;
            }

            public void setChangeDepartment(boolean ChangeDepartment) {
                this.ChangeDepartment = ChangeDepartment;
            }

            public boolean isExportRecord() {
                return ExportRecord;
            }

            public void setExportRecord(boolean ExportRecord) {
                this.ExportRecord = ExportRecord;
            }

            public boolean isExportImage() {
                return ExportImage;
            }

            public void setExportImage(boolean ExportImage) {
                this.ExportImage = ExportImage;
            }

            public boolean isExportVideo() {
                return ExportVideo;
            }

            public void setExportVideo(boolean ExportVideo) {
                this.ExportVideo = ExportVideo;
            }

            public boolean isDeviceSet() {
                return DeviceSet;
            }

            public void setDeviceSet(boolean DeviceSet) {
                this.DeviceSet = DeviceSet;
            }

            public boolean isSeatAdjust() {
                return SeatAdjust;
            }

            public void setSeatAdjust(boolean SeatAdjust) {
                this.SeatAdjust = SeatAdjust;
            }

            public boolean isSnapVideoRecord() {
                return SnapVideoRecord;
            }

            public void setSnapVideoRecord(boolean SnapVideoRecord) {
                this.SnapVideoRecord = SnapVideoRecord;
            }

            public boolean isLiveStream() {
                return LiveStream;
            }

            public void setLiveStream(boolean LiveStream) {
                this.LiveStream = LiveStream;
            }

            public int getRole() {
                return Role;
            }

            public void setRole(int Role) {
                this.Role = Role;
            }
        }
    }
}
