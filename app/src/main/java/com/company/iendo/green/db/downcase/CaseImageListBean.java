package com.company.iendo.green.db.downcase;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:55
 * desc：病例下载的时候   病例相关联的图片
 */
public class CaseImageListBean {
    private String ImagePath;

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    @Override
    public String toString() {
        return "CaseImageListBean{" +
                "ImagePath='" + ImagePath + '\'' +
                '}';
    }
}
