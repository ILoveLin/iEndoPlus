package com.company.iendo.green.db.downcase;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:55
 * desc：病例下载的时候   病例相关联的视频
 */
public class CaseVideoListBean {

    private String VideoPath;
    private String FileName;//tag下载filename==tag
    // 最大值
    private Long maxProcess;
    // 完整的url,下载之后存入本地的地址值
    private String url;

    //是否下载过
    private Boolean isDown;//默认未下载,false

    public Boolean getDown() {
        return isDown;
    }

    public void setDown(Boolean down) {
        isDown = down;
    }

    public Long getMaxProcess() {
        return maxProcess;
    }

    public void setMaxProcess(Long maxProcess) {
        this.maxProcess = maxProcess;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
    public String getVideoPath() {
        return VideoPath;
    }

    public void setVideoPath(String videoPath) {
        VideoPath = videoPath;
    }

    @Override
    public String toString() {
        return "CaseVideoListBean{" +
                "VideoPath='" + VideoPath + '\'' +
                ", FileName='" + FileName + '\'' +
                ", maxProcess=" + maxProcess +
                ", url='" + url + '\'' +
                ", isDown=" + isDown +
                '}';
    }
}
