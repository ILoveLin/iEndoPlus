package com.company.iendo.green.db.downcase;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:55
 * desc：记录下载病历时,是谁下载的病历,比如登入用户是admin 则存入admin,张三则新增一个张三bean,如果存在一样的 就不增加下载者的名字
 */
public class DownloadedNameListBean {


    private String downloadedByName;

    @Override
    public String toString() {
        return "DownloadedNameListBean{" +
                "downloadedByName='" + downloadedByName + '\'' +
                '}';
    }

    /**
     * 依据对象属性值是否相同来判断ArrayList是否包含某一对象，则需要重写Object的equals()和hashCode()，并在equals()中一一比较对象的每个属性值。
     *
     * 为什么要重写？因为contains方法里面用的是equals方法，equals方法里面是==，判断的是两个对象的内存地址是否相等。
     * ————————————————
     * 版权声明：本文为CSDN博主「Darren Gong」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/axin1240101543/article/details/113619331
     * @param obj
     * @return
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //如果省市区都相等则相等
        if (obj instanceof DownloadedNameListBean) {
            DownloadedNameListBean district = (DownloadedNameListBean) obj;
            return this.downloadedByName.equals(district.downloadedByName);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getDownloadedByName() {
        return downloadedByName;
    }

    public void setDownloadedByName(String downloadedByName) {
        this.downloadedByName = downloadedByName;
    }
}
