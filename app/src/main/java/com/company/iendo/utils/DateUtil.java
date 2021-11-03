package com.company.iendo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:49
 * desc：
 */
public class DateUtil {

    public static String getSystemDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        return dateNow;
    }
}
