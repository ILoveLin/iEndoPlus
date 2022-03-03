package com.company.iendo.bean.socket.getpicture;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/2 13:56
 * 获取预览报告
 * desc：{"printcode":"00"}
 * 00打印成功，其他值打印失败
 */
public class PrintReportBean {
    @SerializedName("printcode")
    private String printcode;

    public String getPrintcode() {
        return printcode;
    }

    public void setPrintcode(String printcode) {
        this.printcode = printcode;
    }

    @Override
    public String toString() {
        return "PrintReportBean{" +
                "printcode='" + printcode + '\'' +
                '}';
    }
}
