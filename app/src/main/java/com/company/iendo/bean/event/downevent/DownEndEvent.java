package com.company.iendo.bean.event.downevent;

import com.airbnb.lottie.L;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/22 10:35
 * desc：视频下载状态成功失败的event
 */
public class DownEndEvent {
    private String tag; // 标识
    private String statue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中

    private String downStatueDes;//下载状态描述   开始下载  下载中... 下载完成  下载错误
    //MediaScannerConnection.scanFile(getApplicationContext(), new String[]{commonFolderName + "/" + task.getFilename()}, null,
    private String refreshLocalVideoFolder; //需要刷新到相册的文件夹路径   和回调中的task.getParentFile()  一样

    private String refreshLocalFileName;  //需要刷新到相册的文件名字      一样和回调中的task.getFilename()  一样
    private Long TotalLength;  //下载的总长度
    private String localUrl;  //保存到本地的url地址,(文件路径)    task.getFile()

    private Long TotalOffsetLength; //在那个下载偏移量
    private String speed; //下载速度
    private String deviceCode; //  当前设备code
    private String currentItemCaseID; //当前病例id

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getCurrentItemCaseID() {
        return currentItemCaseID;
    }

    public void setCurrentItemCaseID(String currentItemCaseID) {
        this.currentItemCaseID = currentItemCaseID;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public Long getTotalOffsetLength() {
        return TotalOffsetLength;
    }

    public void setTotalOffsetLength(Long totalOffsetLength) {
        TotalOffsetLength = totalOffsetLength;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public Long getTotalLength() {
        return TotalLength;
    }

    public void setTotalLength(Long totalLength) {
        TotalLength = totalLength;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDownStatueDes() {
        return downStatueDes;
    }

    public void setDownStatueDes(String downStatueDes) {
        this.downStatueDes = downStatueDes;
    }

    @Override
    public String toString() {
        return "DownEndEvent{" +
                "tag='" + tag + '\'' +
                ", statue='" + statue + '\'' +
                ", downStatueDes='" + downStatueDes + '\'' +
                ", refreshLocalVideoFolder='" + refreshLocalVideoFolder + '\'' +
                ", refreshLocalFileName='" + refreshLocalFileName + '\'' +
                ", TotalLength='" + TotalLength + '\'' +
                ", localUrl='" + localUrl + '\'' +
                '}';
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getRefreshLocalVideoFolder() {
        return refreshLocalVideoFolder;
    }

    public void setRefreshLocalVideoFolder(String refreshLocalVideoFolder) {
        this.refreshLocalVideoFolder = refreshLocalVideoFolder;
    }

    public String getRefreshLocalFileName() {
        return refreshLocalFileName;
    }

    public void setRefreshLocalFileName(String refreshLocalFileName) {
        this.refreshLocalFileName = refreshLocalFileName;
    }
}
