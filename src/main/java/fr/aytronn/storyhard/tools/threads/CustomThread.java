package fr.aytronn.storyhard.tools.threads;

import fr.aytronn.storyhard.StoryHard;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public enum CustomThread {

    TASK_THREAD("Task Thread", true, Thread.MAX_PRIORITY, 4),
    FILE_EXECUTOR("File I/O Thread", true, Thread.NORM_PRIORITY + 2, 2);

    private final ExecutorService executor;

    CustomThread(String name, boolean daemon, int priority, int cores) {
        if (cores <= 1) {
            executor = Executors.newSingleThreadScheduledExecutor(new CustomThreadClass(name, daemon, priority));
        } else {
            executor = new ScheduledThreadPoolExecutor(cores, new CustomThreadClass(name, daemon, priority));
        }
    }

    /**
     * Allow to get the executor
     *
     */
    public ExecutorService get() {
        return executor;
    }

    /**
     * Allow to stop
     */
    public void stop() {
        executor.shutdown();
    }

    /**
     * Allow to Submit
     *
     * @param runnable Runnable
     * @return Future
     */
    public Future<?> submit(Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * Allow to submit
     *
     * @param runnable Runnable
     * @return Future
     */
    public Future<?> submit(Callable<?> runnable) {
        return executor.submit(runnable);
    }

    /**
     * Allow to execute
     *
     * @param runnable Runnable
     */
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Allow to schedule Later
     *
     * @param cmd      Runnable
     * @param time     Timer
     * @param timeUnit TimeUnit
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleLater(Runnable cmd, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) executor).schedule(cmd, time, timeUnit);
    }

    /**
     * Allow to schedule repeatedly
     *
     * @param cmd      cmd
     * @param initial  initial time
     * @param time     Time
     * @param timeUnit TimeUnit
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleRepeated(Runnable cmd, long initial, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) executor).scheduleAtFixedRate(cmd, initial, time, timeUnit);
    }

    /**
     * Allow to schedule a delay
     *
     * @param cmd      Runnable
     * @param initial  initial time
     * @param time     Time between
     * @param timeUnit Time unit
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleDelay(Runnable cmd, long initial, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) executor).scheduleWithFixedDelay(cmd, initial, time, timeUnit);
    }

    /**
     * Allow to shutdown
     */
    public void shutdown() {
        this.executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                if (!executor.awaitTermination(3L, TimeUnit.MINUTES)) {
                    StoryHard.getInstance().getLogger().log(Level.WARNING,
                            "Server is still waiting for Thread to be done.");
                }
            } catch (InterruptedException ignored) {
                //
            }
        }
    }

    /**
     * Allow to shutdown all the threads
     */
    public static void shutdownAll() {
        for (CustomThread thread : values()) {
            thread.shutdown();
        }
    }

}
