package com.zhx.lib_router_lrouter.lrouter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolFactory {

    private static int defaultCorePoolSize = Runtime.getRuntime().availableProcessors() + 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "EasyRouter #" + mCount.getAndIncrement());
        }
    };
    public static ThreadPoolExecutor mThreadPool;

    public static ThreadPoolExecutor getThreadPool(int corePoolSize) {
        if (0 == corePoolSize) return null;
        if (null == mThreadPool) {
            int poolSize = Math.min(corePoolSize, defaultCorePoolSize);
            mThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64), sThreadFactory);
            //核心线程也会被销毁
            mThreadPool.allowCoreThreadTimeOut(true);
        }
        return mThreadPool;
    }
}
