package com.company.iendo.mineui.socket;

import com.company.iendo.utils.LogUtils;
import com.lzh.easythread.Callback;
import com.lzh.easythread.EasyThread;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/1/26 9:50
 * desc：线程池管理类
 */
public class ThreadManager {
    /**
     * android 系统源码 线程池给的数量
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private final static EasyThread io;
    private final static EasyThread cache;
    private final static EasyThread calculator;
    private final static EasyThread file;

    public static EasyThread getIO() {
        return io;
    }

    public static EasyThread getCache() {
        return cache;
    }

    public static EasyThread getCalculator() {
        return calculator;
    }

    public static EasyThread getFile() {
        return file;
    }

    static {
        //priority：1 --------------> 10，最低优先级 -----------> 最高优先级。
        io = EasyThread.Builder.createFixed(1).setName("ReceiveThread").setPriority(Thread.MAX_PRIORITY).setCallback(new DefaultCallback()).build(); //这里给接收线程
        //适合做大量的耗时较少的任务；
        cache = EasyThread.Builder.createCacheable().setName("SendThread").setCallback(new DefaultCallback()).build();
        calculator = EasyThread.Builder.createFixed(4).setName("calculator").setPriority(Thread.MAX_PRIORITY).setCallback(new DefaultCallback()).build();
        file = EasyThread.Builder.createFixed(4).setName("file").setPriority(3).setCallback(new DefaultCallback()).build();
    }

    private static class DefaultCallback implements Callback {

        @Override
        public void onError(String threadName, Throwable t) {
            LogUtils.e("Task with thread %s has occurs an error: %s==threadName==" + threadName + "==getMessage==" + t.getMessage());
        }

        @Override
        public void onCompleted(String threadName) {
            LogUtils.e("Task with thread %s completed==threadName==" + threadName);
        }

        @Override
        public void onStart(String threadName) {
            LogUtils.e("Task with thread %s start running!==threadName==" + threadName);
        }
    }
}
