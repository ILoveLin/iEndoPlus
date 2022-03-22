package com.company.iendo.bean.socket.params;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/22 10:41
 * desc：02-冷光源
 * {
 *
 *     "type02":{
 *         "brightess":"30"
 *     }
 * }
 */
public class Type02Bean {


    @SerializedName("type02")
    private Type02 type02;

    public Type02 getType02() {
        return type02;
    }

    public void setType02(Type02Bean.Type02 type02) {
        this.type02 = type02;
    }

    public static class Type02 {
        @SerializedName("brightness")
        private String brightness;

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }

        @Override
        public String toString() {
            return "_$02DTO{" +
                    "brightess='" + brightness + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Type02Bean{" +
                "type02=" + type02 +
                '}';
    }
}
