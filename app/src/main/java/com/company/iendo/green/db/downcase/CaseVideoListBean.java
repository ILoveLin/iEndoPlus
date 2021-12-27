package com.company.iendo.green.db.downcase;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:55
 * desc：病例下载的时候   病例相关联的视频
 */
public class CaseVideoListBean {

    private String VideoPath;

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
                '}';
    }
}
