package com.company.iendo.mineui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/31 15:01
 * desc：获取单例线程池
 */
public class SocketManager {
    private static ThreadPoolExecutor mExecutorService;

    private SocketManager() {
    }

    public static ThreadPoolExecutor getInstance() {
        if (null == mExecutorService) {
            synchronized (SocketManager.class) {
                if (null == mExecutorService) {
                    mExecutorService = new ThreadPoolExecutor(8, 30,
                            60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));

                }
            }
        }
        return mExecutorService;
    }
}
