package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/17 15:09
 * desc：
 UserMan 			-- 用户管理
 CanPsw 			-- 设置口令
 SnapVideoRecord	-- 拍照录像
 CanNew 			-- 登记病人
 CanEdit 			-- 修改病历
 CanDelete 			-- 删除病历
 CanPrint 			-- 打印病历
 UnPrinted 			-- 未打印病历
 OnlySelf 			-- 本人病历
 HospitalInfo 		-- 医院信息

 已下App未使用到的权限
 ReportStyle 		-- 报告样式
 DictsMan 			-- 词典管理
 GlossaryMan 		-- 术语管理
 TempletMan 		-- 模板管理
 CanBackup 			-- 备份数据
 ViewBackup 		-- 查看备份
 VideoSet 			-- 视频设置
 FtpSet            	-- FTP设置
 ChangeDepartment  	-- 切换科室
 ExportRecord  	 	-- 导出病历
 ExportImage      	-- 导出图片
 ExportVideo      	-- 导出视频
 DeviceSet		 	-- 设备设置
 SeatAdjust		 	-- 座椅调节
 LiveStream		 	-- 直播
 Role				-- 角色
 */
public class LoginBean {

    @SerializedName("data")
    private DataDTO data;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    @Override
    public String toString() {
        return "LoginBean{" +
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
        @SerializedName("userID")
        private String userID;
        @SerializedName("Role")
        private int Role;
        @SerializedName("purview")
        private PurviewDTO purview;

        @Override
        public String toString() {
            return "DataDTO{" +
                    "userID='" + userID + '\'' +
                    ", Role=" + Role +
                    ", purview=" + purview +
                    '}';
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public int getRole() {
            return Role;
        }

        public void setRole(int Role) {
            this.Role = Role;
        }

        public PurviewDTO getPurview() {
            return purview;
        }

        public void setPurview(PurviewDTO purview) {
            this.purview = purview;
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
