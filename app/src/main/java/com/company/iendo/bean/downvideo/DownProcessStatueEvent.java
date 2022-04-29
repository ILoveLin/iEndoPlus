package com.company.iendo.bean.downvideo;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/22 10:35
 * desc：视频下载状态的实时更新的event
 */
public class DownProcessStatueEvent {

    private String speed;  //下载速度  2M/m
    private String statue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中    英文
    private String statueDes; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中  中文字

    private String tag;             //当前视频的tag,用文件名区别比如:2022-04-22-08-41-51.mp4

    private String url;             //当前视频的url

    private long currentOffset;  //当前下载进度偏移量

    private String formatCurrentOffset;  //被格式化过的,当前下载进度偏移量
    private long processMax;

    public String getStatueDes() {
        return statueDes;
    }

    public void setStatueDes(String statueDes) {
        this.statueDes = statueDes;
    }

    public long getProcessMax() {
        return processMax;
    }

    public void setProcessMax(long processMax) {
        this.processMax = processMax;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getFormatCurrentOffset() {
        return formatCurrentOffset;
    }

    @Override
    public String toString() {
        return "DownProcessStatueEvent{" +
                "speed='" + speed + '\'' +
                ", statue='" + statue + '\'' +
                ", tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", currentOffset=" + currentOffset +
                ", formatCurrentOffset='" + formatCurrentOffset + '\'' +
                ", processMax=" + processMax +
                '}';
    }

    public void setFormatCurrentOffset(String formatCurrentOffset) {
        this.formatCurrentOffset = formatCurrentOffset;
    }


    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(long currentOffset) {
        this.currentOffset = currentOffset;
    }


}
