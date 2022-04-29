package com.company.iendo.bean.downvideo;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/22 10:35
 * desc：视频下载总长度的event
 */
public class DownStartEvent {
    private String tag;             //当前视频的tag,用文件名区别比如:2022-04-22-08-41-51.mp4

    private long  contentLength;  //当前被下载视频的总长度
    private long  currentContentLength;  //当前被下载视频的总长度
    private String  formatContentLength;  //被格式化过的
    private String statue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中


    private String  downStatueDes;  //下载状态描述   开始下载  下载中... 下载完成  下载错误

    public long getCurrentContentLength() {
        return currentContentLength;
    }

    public void setCurrentContentLength(long currentContentLength) {
        this.currentContentLength = currentContentLength;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getDownStatueDes() {
        return downStatueDes;
    }

    public void setDownStatueDes(String downStatueDes) {
        this.downStatueDes = downStatueDes;
    }

    public String getFormatContentLength() {
        return formatContentLength;
    }

    public void setFormatContentLength(String formatContentLength) {
        this.formatContentLength = formatContentLength;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
