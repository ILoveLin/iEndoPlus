package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/2 13:56
 * 获取预览报告
 * desc：{"reporturl":"http://192.168.64.13:8010/1518/Report/22212cme004.bmp"}
 */
public class LookReportBean {
    @SerializedName("reporturl")
    private String reporturl;

    @Override
    public String toString() {
        return "LookReportBean{" +
                "reporturl='" + reporturl + '\'' +
                '}';
    }

    public String getReporturl() {
        return reporturl;
    }

    public void setReporturl(String reporturl) {
        this.reporturl = reporturl;
    }
}
