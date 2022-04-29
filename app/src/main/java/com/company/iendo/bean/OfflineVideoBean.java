package com.company.iendo.bean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/4/24 16:01
 * desc：
 */
public class OfflineVideoBean {
    private String url;
    private String title;
    private Long maxProcess;

    public String getUrl() {
        return url;
    }

    public Long getMaxProcess() {
        return maxProcess;
    }

    public void setMaxProcess(Long maxProcess) {
        this.maxProcess = maxProcess;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "OfflineVideoBean{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", maxProcess=" + maxProcess +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
