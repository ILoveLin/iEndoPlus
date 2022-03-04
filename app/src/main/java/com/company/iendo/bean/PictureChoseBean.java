package com.company.iendo.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/2 17:30
 * desc：获取预览报告之前,需要选择图片
 */
public class PictureChoseBean {
    private String url;
    private boolean oldSelected;  //请求过来已经选中的id,这个字段不会改变,只做原来选中的图片标识
    private boolean newSelected;  //自己点击选中的id(包含了已经选中的id哦,之后的点击改变此字段)
    private String itemID;
    private String pictureID;

    public boolean isNewSelected() {
        return newSelected;
    }

    public void setNewSelected(boolean newSelected) {
        this.newSelected = newSelected;
    }

    public String getPictureID() {
        return pictureID;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

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

    public boolean isOldSelected() {
        return oldSelected;
    }

    public void setOldSelected(boolean oldSelected) {
        this.oldSelected = oldSelected;
    }

    @Override
    public String toString() {
        return "PictureChoseBean{" +
                "url='" + url + '\'' +
                ", oldSelected=" + oldSelected +
                ", newSelected=" + newSelected +
                ", itemID='" + itemID + '\'' +
                ", pictureID='" + pictureID + '\'' +
                '}';
    }
}
