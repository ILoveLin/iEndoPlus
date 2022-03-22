package com.company.iendo.bean.socket.params;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/22 10:09
 * desc：
 *
 * 亮度 = brightness
 * 饱和度 = saturation
 * 清晰度 = sharpness
 * 放大倍数 = zoomrate
 * 影像翻转 = reversal
 * 血管增强 = bloodenhance
 *
 * 范围：
 * 光源：
 *  亮度：1 ~ 100
 *
 * 摄像机：
 *  亮度：0 ~ 63   brightess
 *  清晰度： 0 ~ 31  sharpenss
 *  饱和度： 0 ~ 64   saturation
 *  放大倍数： 0 ~ 15   zoom
 *  影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）
 *
 *
 *
 * {"01":{"brightess":"30","zoom":"1","sharpenss":"10","saturation":"30"},"02":{"brightess":"30"}}
 *
 * 血管增强 取值：  0：表示开，0x08：表示关闭
 */
public class DeviceParamsBean {
    @SerializedName("01")
    private _$01DTO $01;
    @SerializedName("02")
    private _$02DTO $02;
    @SerializedName("type01")
    private Type01 type01;
    @SerializedName("type02")
    private Type02 type02;

    public _$01DTO get$01() {
        return $01;
    }

    public void set$01(_$01DTO $01) {
        this.$01 = $01;
    }

    public _$02DTO get$02() {
        return $02;
    }

    public void set$02(_$02DTO $02) {
        this.$02 = $02;
    }

    public Type01 getType01() {
        return type01;
    }

    public void setType01(Type01 type01) {
        this.type01 = type01;
    }

    public Type02 getType02() {
        return type02;
    }

    public void setType02(Type02 type02) {
        this.type02 = type02;
    }

    public static class _$01DTO {
        @SerializedName("bloodenhance")
        private String bloodenhance;
        @SerializedName("brightness")
        private String brightness;
        @SerializedName("reversal")
        private String reversal;
        @SerializedName("saturation")
        private String saturation;
        @SerializedName("sharpness")
        private String sharpness;
        @SerializedName("zoomrate")
        private String zoomrate;

        @Override
        public String toString() {
            return "_$01DTO{" +
                    "bloodenhance='" + bloodenhance + '\'' +
                    ", brightness='" + brightness + '\'' +
                    ", reversal='" + reversal + '\'' +
                    ", saturation='" + saturation + '\'' +
                    ", sharpness='" + sharpness + '\'' +
                    ", zoomrate='" + zoomrate + '\'' +
                    '}';
        }

        public String getBloodenhance() {
            return bloodenhance;
        }

        public void setBloodenhance(String bloodenhance) {
            this.bloodenhance = bloodenhance;
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }

        public String getReversal() {
            return reversal;
        }

        public void setReversal(String reversal) {
            this.reversal = reversal;
        }

        public String getSaturation() {
            return saturation;
        }

        public void setSaturation(String saturation) {
            this.saturation = saturation;
        }

        public String getSharpness() {
            return sharpness;
        }

        public void setSharpness(String sharpness) {
            this.sharpness = sharpness;
        }

        public String getZoomrate() {
            return zoomrate;
        }

        public void setZoomrate(String zoomrate) {
            this.zoomrate = zoomrate;
        }
    }

    public static class _$02DTO {
        @SerializedName("brightness")
        private String brightness;

        @Override
        public String toString() {
            return "_$02DTO{" +
                    "brightness='" + brightness + '\'' +
                    '}';
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }
    }

    public static class Type01 {
        @SerializedName("bloodenhance")
        private String bloodenhance;  //8是关闭 0是打开
        @SerializedName("brightness")
        private String brightness;
        @SerializedName("reversal")
        private String reversal;
        @SerializedName("saturation")
        private String saturation;
        @SerializedName("sharpness")
        private String sharpness;
        @SerializedName("zoomrate")
        private String zoomrate;

        @Override
        public String toString() {
            return "Type01{" +
                    "bloodenhance='" + bloodenhance + '\'' +
                    ", brightness='" + brightness + '\'' +
                    ", reversal='" + reversal + '\'' +
                    ", saturation='" + saturation + '\'' +
                    ", sharpness='" + sharpness + '\'' +
                    ", zoomrate='" + zoomrate + '\'' +
                    '}';
        }

        public String getBloodenhance() {
            return bloodenhance;
        }

        public void setBloodenhance(String bloodenhance) {
            this.bloodenhance = bloodenhance;
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }

        public String getReversal() {
            return reversal;
        }

        public void setReversal(String reversal) {
            this.reversal = reversal;
        }

        public String getSaturation() {
            return saturation;
        }

        public void setSaturation(String saturation) {
            this.saturation = saturation;
        }

        public String getSharpness() {
            return sharpness;
        }

        public void setSharpness(String sharpness) {
            this.sharpness = sharpness;
        }

        public String getZoomrate() {
            return zoomrate;
        }

        public void setZoomrate(String zoomrate) {
            this.zoomrate = zoomrate;
        }
    }

    public static class Type02 {
        @SerializedName("brightness")
        private String brightness;

        @Override
        public String toString() {
            return "Type02{" +
                    "brightness='" + brightness + '\'' +
                    '}';
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }
    }

    @Override
    public String toString() {
        return "DeviceParamsBean{" +
                "$01=" + $01 +
                ", $02=" + $02 +
                ", type01=" + type01 +
                ", type02=" + type02 +
                '}';
    }
//
//    @SerializedName("01")  //ios使用的
//    private _$01DTO $01;
//    @SerializedName("02") //ios使用的
//    private _$02DTO $02;
//    @SerializedName("type01") //我们使用的
//    private Type01 type01;
//    @SerializedName("type02")//我们使用的
//    private Type02 type02;
//
//    public _$01DTO get$01() {
//        return $01;
//    }
//
//    public void set$01(_$01DTO $01) {
//        this.$01 = $01;
//    }
//
//    public _$02DTO get$02() {
//        return $02;
//    }
//
//    public void set$02(_$02DTO $02) {
//        this.$02 = $02;
//    }
//
//    public Type01 getType01() {
//        return type01;
//    }
//
//    public void setType01(Type01 type01) {
//        this.type01 = type01;
//    }
//
//    public Type02 getType02() {
//        return type02;
//    }
//
//    public void setType02(Type02 type02) {
//        this.type02 = type02;
//    }
//
//    public static class _$01DTO {
//        @SerializedName("bloodenhance")
//        private String bloodenhance;
//        @SerializedName("brightness")
//        private String brightness;
//        @SerializedName("reversal")
//        private String reversal;
//        @SerializedName("saturation")
//        private String saturation;
//        @SerializedName("sharpness")
//        private String sharpness;
//        @SerializedName("zoomrate")
//        private String zoomrate;
//
//        public String getBloodenhance() {
//            return bloodenhance;
//        }
//
//        public void setBloodenhance(String bloodenhance) {
//            this.bloodenhance = bloodenhance;
//        }
//
//        public String getBrightness() {
//            return brightness;
//        }
//
//        public void setBrightness(String brightness) {
//            this.brightness = brightness;
//        }
//
//        public String getReversal() {
//            return reversal;
//        }
//
//        public void setReversal(String reversal) {
//            this.reversal = reversal;
//        }
//
//        public String getSaturation() {
//            return saturation;
//        }
//
//        public void setSaturation(String saturation) {
//            this.saturation = saturation;
//        }
//
//        public String getSharpness() {
//            return sharpness;
//        }
//
//        public void setSharpness(String sharpness) {
//            this.sharpness = sharpness;
//        }
//
//        public String getZoomrate() {
//            return zoomrate;
//        }
//
//        public void setZoomrate(String zoomrate) {
//            this.zoomrate = zoomrate;
//        }
//    }
//
//    public static class _$02DTO {
//        @SerializedName("brightness")
//        private String brightness;
//
//        public String getBrightness() {
//            return brightness;
//        }
//
//        public void setBrightness(String brightness) {
//            this.brightness = brightness;
//        }
//    }
//
//    public static class Type01 {
//        @SerializedName("bloodenhance")
//        private String bloodenhance;
//        @SerializedName("brightness")
//        private String brightness;
//        @SerializedName("reversal")
//        private String reversal;
//        @SerializedName("saturation")
//        private String saturation;
//        @SerializedName("sharpness")
//        private String sharpness;
//        @SerializedName("zoomrate")
//        private String zoomrate;
////        亮度 = brightness
////        饱和度 = saturation
////        清晰度 = sharpness
////        放大倍数 = zoomrate
////        影像翻转 = reversal       :影像翻转取值：  0（无翻转），1（水平翻转），2（垂直翻转），3（水平翻转+垂直翻转）
////        血管增强 = bloodenhance
//
//
//        public String getBloodenhance() {
//            return bloodenhance;
//        }
//
//        public void setBloodenhance(String bloodenhance) {
//            this.bloodenhance = bloodenhance;
//        }
//
//        public String getBrightness() {
//            return brightness;
//        }
//
//        public void setBrightness(String brightness) {
//            this.brightness = brightness;
//        }
//
//        public String getReversal() {
//            return reversal;
//        }
//
//        public void setReversal(String reversal) {
//            this.reversal = reversal;
//        }
//
//        public String getSaturation() {
//            return saturation;
//        }
//
//        public void setSaturation(String saturation) {
//            this.saturation = saturation;
//        }
//
//        public String getSharpness() {
//            return sharpness;
//        }
//
//        public void setSharpness(String sharpness) {
//            this.sharpness = sharpness;
//        }
//
//        public String getZoomrate() {
//            return zoomrate;
//        }
//
//        public void setZoomrate(String zoomrate) {
//            this.zoomrate = zoomrate;
//        }
//    }
//
//    public static class Type02 {
//        @SerializedName("brightness")
//        private String brightness;
//
//        public String getBrightness() {
//            return brightness;
//        }
//
//        public void setBrightness(String brightness) {
//            this.brightness = brightness;
//        }
//    }
}
