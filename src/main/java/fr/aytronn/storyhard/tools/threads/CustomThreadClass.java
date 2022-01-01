package fr.aytronn.storyhard.tools.threads;

import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadClass implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger();
    private final String nameFormat;
    private final boolean daemon;
    private final int priority;

    /**
     * Zakary thread class
     *
     * @param nameFormat Name Format
     * @param daemon Daemon
     * @param priority Priority
     */
    public CustomThreadClass(String nameFormat, boolean daemon, int priority) {
        this.nameFormat = nameFormat;
        this.daemon = daemon;
        this.priority = priority;
    }

    /**
     * Allow to create a new thread
     *
     * @param r Runnable
     * @return Thread
     */
    @Override
    public Thread newThread(Runnable r) {
        final var name = String.format(nameFormat, threadNumber.getAndIncrement());
        final Thread thread = new FastThreadLocalThread(r, name);
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        return thread;
    }
}
