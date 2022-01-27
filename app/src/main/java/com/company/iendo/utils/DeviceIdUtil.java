package com.company.iendo.utils;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/7/26 17:48
 * desc：
 */

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/**
 * 获取手机的唯一标识ID
 */
public class DeviceIdUtil {

    public static String getDeviceId(Context context) {

        StringBuilder sbDeviceId = new StringBuilder();

        String imei = getIMEI(context);
        String androidId = getAndroidId(context);
        String serial = getSerial();
        String uuid = getDeviceUUID();

        //附加imei     与设备有关，与具体应用无关，不受应用卸载或者重装系统、刷机等影响
        if (imei != null && imei.length() > 0) {
            sbDeviceId.append(imei);
            sbDeviceId.append("|");
        }
        //附加androidId   与系统首次启动有关，不受应用卸载重装影响，但是会受到重装或者恢复出厂影响
        if (androidId != null && androidId.length() > 0) {
            sbDeviceId.append(androidId);
            sbDeviceId.append("|");
        }
        //附加serial   与设备有关，不受应用卸载影响，不受系统重装或者恢复出厂影响
        if (serial != null && serial.length() > 0) {
            sbDeviceId.append(serial);
            sbDeviceId.append("|");
        }
        //附加uuid
        if (uuid != null && uuid.length() > 0) {
            sbDeviceId.append(uuid);
        }

        if (sbDeviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(sbDeviceId.toString());
                String sha1 = bytesToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    //返回最终的DeviceId
                    return sha1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String string;
        for (int i = 0; i < data.length; i++) {
            string = (Integer.toHexString(data[i] & 0xFF));
            if (string.length() == 1) {
                sb.append("0");
            }
            sb.append(string);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * 取 SHA1
     *
     * @param data 数据
     * @return 对应的Hash值
     */
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }


    /**
     * 获取硬件的UUID
     *
     * @return
     */
    private static String getDeviceUUID() {
        String deviceId = "9527" + Build.ID +
                Build.DEVICE +
                Build.BOARD +
                Build.BRAND +
                Build.HARDWARE +
                Build.PRODUCT +
                Build.MODEL +
                Build.SERIAL;
        return new UUID(deviceId.hashCode(), Build.SERIAL.hashCode()).toString().replace("-", "");
    }

    private static String getSerial() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Build.getSerial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取AndroidId
     *
     * @param context 上下文
     * @return AndroidId
     */
    private static String getAndroidId(Context context) {
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取IMEI
     *
     * @param context 上下文
     * @return IMEI
     */
    private static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}

