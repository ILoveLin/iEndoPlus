package com.vlc.lib.listener;



/**
 * Created by yyl on 2016/3/25/025.
 */
public interface MediaListenerEvent {

    int EVENT_ERROR = 0;
    int EVENT_ERROR_PATH = 2;
    int EVENT_BUFFING = 1;

    void eventBuffing(final int event, final float buffing);//子线程

    void eventStop(final boolean isPlayError);//子线程

    void eventError(final int event, final boolean show);//子线程

    void eventPlay(final boolean isPlaying);//子线程
    void eventSystemEnd(String isStringed);//子线程
    /**
     * 直播回调的时间  时间都是线性增长的比如:250,456,780,979
     * @param time
     */
    void eventCurrentTime(String time);//子线程
    /**
     * 录像回调的时间  时间都是线下一秒一秒相同的250数字
     * @param time   录像的视频 时间戳有bug 有些线性增长,有些非线性增长,有些前办法线性增长后半部分非限制增长
     */
    void eventRecordCurrentTime(String time);//子线程
    /**
     * 初始化开始加载  当前线程为调用者线程
     *
     * @param openClose true加载视频中  false回到初始化  比如显示封面图
     */
    void eventPlayInit(boolean openClose);//调用线程
}
