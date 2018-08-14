package com.sunmi.blockchainlottery.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Worker {

    private static final Worker instance = new Worker();
    private Worker() {
        SERVICE = Executors.newScheduledThreadPool(1);
    }

    private final ScheduledExecutorService SERVICE;
    private Runnable job;
    private ScheduledFuture<?> self;

    public static void setJob(Runnable runnable) {
        if (instance.job != null){
            pause();
//            throw new RuntimeException("job has been assigned");
        }
        instance.job = runnable;
    }

    public static void pause() {
        instance.self.cancel(false);
    }

    public static void resume(int initialDelay, int delay) {
        instance.self = instance.SERVICE.scheduleWithFixedDelay(instance.job
                , initialDelay, delay, TimeUnit.SECONDS);
    }

}
