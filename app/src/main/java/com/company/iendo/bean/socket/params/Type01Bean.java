package com.company.iendo.bean.socket.params;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/22 10:41
 * desc：01-HD3摄像机
 * {
 * "type01":{
 * "brightess":"30",
 * "zoom":"1",
 * "sharpenss":"10",
 * "saturation":"30"
 * }
 * <p>
 * }
 */
public class Type01Bean {

    @SerializedName("type01")
    private Type01 type01;

    public Type01 getType01() {
        return type01;
    }

    public void setType01(Type01 type01) {
        this.type01 = type01;
    }

    public static class Type01 {
        @SerializedName("brightness")//亮度
        private String brightness;
        @SerializedName("zoomrate") //放大倍数
        private String zoomrate;
        @SerializedName("sharpness")//清晰度
        private String sharpness;
        @SerializedName("saturation")//饱和度
        private String saturation;
        @SerializedName("reversal")//影像翻转    :影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）   // 先判断两者状态
        //水平和垂直都是关闭  发0
        //两者都是开         发3
        //水平开 垂直关       发1
        //水平关 垂直开       发2
        private String reversal;
        @SerializedName("bloodenhance")//血管增强   8是关闭 0是打开
        private String bloodenhance;

//        亮度 = brightness
//        饱和度 = saturation
//        清晰度 = sharpness
//        放大倍数 = zoomrate
//        影像翻转 = reversal       :影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）
//        血管增强 = bloodenhance   8


        public String getZoomrate() {
            return zoomrate;
        }

        public void setZoomrate(String zoomrate) {
            this.zoomrate = zoomrate;
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }

        @Override
        public String toString() {
            return "Type01{" +
                    "brightness='" + brightness + '\'' +
                    ", zoomrate='" + zoomrate + '\'' +
                    ", sharpness='" + sharpness + '\'' +
                    ", saturation='" + saturation + '\'' +
                    ", reversal='" + reversal + '\'' +
                    ", bloodenhance='" + bloodenhance + '\'' +
                    '}';
        }

        public String getSharpness() {
            return sharpness;
        }

        public void setSharpness(String sharpness) {
            this.sharpness = sharpness;
        }

        public String getSaturation() {
            return saturation;
        }

        public void setSaturation(String saturation) {
            this.saturation = saturation;
        }

        public String getReversal() {
            return reversal;
        }

        public void setReversal(String reversal) {
            this.reversal = reversal;
        }

        public String getBloodenhance() {
            return bloodenhance;
        }

        public void setBloodenhance(String bloodenhance) {
            this.bloodenhance = bloodenhance;
        }
    }

    @Override
    public String toString() {
        return "VideoDeviceBean{" +
                "type01=" + type01 +
                '}';
    }
}
