package com.company.iendo.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/31 13:56
 * desc： 二维码扫描之后刷新设备界面数据
 * 设备搜索之后刷新设备界面数据
 */
public class RefreshEvent {
    private String type;
    private String Str;

    public String getStr() {
        return Str;
    }

    public void setStr(String str) {
        Str = str;
    }

    public RefreshEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
