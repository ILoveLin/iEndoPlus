package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/6/17 9:04
 * desc：实时获取当前设备VoiceID(VoiceStationID)
 * 255 或者socket 加入列表后,获取到的id相等才开启视频声音
 * @author Administrator
 */
public class MicVoiceBean {


    private DataDTO data;
    private int code;
    private String msg;

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
        @SerializedName("ID")
        private int iD;
        @SerializedName("RecordID")
        private String recordID;
        @SerializedName("UserID")
        private String userID;
        @SerializedName("OpenLive")
        private boolean openLive;
        @SerializedName("OpenVoiceMC")
        private boolean openVoiceMC;
        @SerializedName("VoiceStationID")
        private int voiceStationID;
        @SerializedName("OpenPacs")
        private boolean openPacs;
        @SerializedName("OpenWorkList")
        private boolean openWorkList;
        @SerializedName("OpenHis")
        private boolean openHis;
        @SerializedName("OpenLight")
        private boolean openLight;
        @SerializedName("OpenCamera")
        private boolean openCamera;
        @SerializedName("LightBrightness")
        private int lightBrightness;
        @SerializedName("CameraBrightness")
        private int cameraBrightness;
        @SerializedName("CameraSaturation")
        private int cameraSaturation;
        @SerializedName("CameraSharpness")
        private int cameraSharpness;
        @SerializedName("CameraZoom")
        private int cameraZoom;
        @SerializedName("UpdateTime")
        private String updateTime;

        public int getID() {
            return iD;
        }

        public void setID(int iD) {
            this.iD = iD;
        }

        public String getRecordID() {
            return recordID;
        }

        public void setRecordID(String recordID) {
            this.recordID = recordID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public boolean isOpenLive() {
            return openLive;
        }

        public void setOpenLive(boolean openLive) {
            this.openLive = openLive;
        }

        public boolean isOpenVoiceMC() {
            return openVoiceMC;
        }

        public void setOpenVoiceMC(boolean openVoiceMC) {
            this.openVoiceMC = openVoiceMC;
        }

        public int getVoiceStationID() {
            return voiceStationID;
        }

        public void setVoiceStationID(int voiceStationID) {
            this.voiceStationID = voiceStationID;
        }

        public boolean isOpenPacs() {
            return openPacs;
        }

        public void setOpenPacs(boolean openPacs) {
            this.openPacs = openPacs;
        }

        public boolean isOpenWorkList() {
            return openWorkList;
        }

        public void setOpenWorkList(boolean openWorkList) {
            this.openWorkList = openWorkList;
        }

        public boolean isOpenHis() {
            return openHis;
        }

        public void setOpenHis(boolean openHis) {
            this.openHis = openHis;
        }

        public boolean isOpenLight() {
            return openLight;
        }

        public void setOpenLight(boolean openLight) {
            this.openLight = openLight;
        }

        public boolean isOpenCamera() {
            return openCamera;
        }

        public void setOpenCamera(boolean openCamera) {
            this.openCamera = openCamera;
        }

        public int getLightBrightness() {
            return lightBrightness;
        }

        public void setLightBrightness(int lightBrightness) {
            this.lightBrightness = lightBrightness;
        }

        public int getCameraBrightness() {
            return cameraBrightness;
        }

        public void setCameraBrightness(int cameraBrightness) {
            this.cameraBrightness = cameraBrightness;
        }

        public int getCameraSaturation() {
            return cameraSaturation;
        }

        public void setCameraSaturation(int cameraSaturation) {
            this.cameraSaturation = cameraSaturation;
        }

        public int getCameraSharpness() {
            return cameraSharpness;
        }

        public void setCameraSharpness(int cameraSharpness) {
            this.cameraSharpness = cameraSharpness;
        }

        public int getCameraZoom() {
            return cameraZoom;
        }

        public void setCameraZoom(int cameraZoom) {
            this.cameraZoom = cameraZoom;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}

