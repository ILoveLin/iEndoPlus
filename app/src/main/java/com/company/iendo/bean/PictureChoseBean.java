package com.company.iendo.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/2 17:30
 * desc：获取预览报告之前,需要选择图片
 */
public class PictureChoseBean {
    private String url;
    private boolean selected;
    private String itemID;

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
