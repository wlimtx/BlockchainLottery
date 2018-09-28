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
    private Runnable updateJob;
    private Runnable queryJob;
    private ScheduledFuture<?> update;
    private ScheduledFuture<?> query;

    public static void setJob(Runnable updateJob, Runnable queryJob) {
        if (instance.updateJob != null) {
            pause();
//            throw new RuntimeException("job has been assigned");
        }
        instance.updateJob = updateJob;
        instance.queryJob = queryJob;
    }

    public static void pause() {
        if (instance.update!=null)
        instance.update.cancel(false);
        if (instance.query!=null)
        instance.query.cancel(false);
    }

    public static void resume(int initialDelay, int delay, int initialDelay2, int delay2) {

        if (instance.updateJob != null)
            instance.update = instance.SERVICE.scheduleWithFixedDelay(instance.updateJob
                    , initialDelay, delay, TimeUnit.SECONDS);
        if (instance.queryJob != null)
            instance.query = instance.SERVICE.scheduleWithFixedDelay(instance.queryJob
                    , initialDelay2, delay2, TimeUnit.SECONDS);
    }

}
