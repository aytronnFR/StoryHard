package fr.aytronn.storyhard.config;

import com.google.common.io.Files;
import fr.aytronn.storyhard.tools.threads.CustomThread;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

    private final Object2ObjectMap<String, Lock> locks;

    public DiscUtil() {
        this.locks = new Object2ObjectOpenHashMap<>();
    }

    public byte[] readBytes(File file) {
        final CompletableFuture<byte[]> completableFuture = CompletableFuture.supplyAsync(() -> {
            final int length = (int) file.length();
            final var output = new byte[length];
            try (final var in = new FastBufferedInputStream(new FileInputStream(file))) {
                var offset = 0;
                while (offset < length) {
                    offset += in.read(output, offset, (length - offset));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
        return completableFuture.join();
    }

    public void writeBytes(File file, byte[] bytes) {
        final Runnable runnable = () -> {
            try (final var out = new FastBufferedOutputStream(new FileOutputStream(file))) {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        CustomThread.FILE_EXECUTOR.execute(runnable);
    }

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public void writeCatch(final File file, final String content) {
        final String name = file.getName();
        // Create lock for each file if there isn't already one.
        final var lock = locks.computeIfAbsent(name, function -> {
            final var rwl = new ReentrantReadWriteLock();
            return rwl.writeLock();
        });

        final Runnable runnable = () -> {
            lock.lock();
            try {
                file.createNewFile();
                Files.asCharSink(file, StandardCharsets.UTF_8).write(content);
                //Files.write(content, file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
        CustomThread.FILE_EXECUTOR.execute(runnable);
    }

    /**
     * Allow to write
     *
     * @param file File
     * @param content Content
     */
    public void write(File file, String content) {
        writeBytes(file, utf8(content));
    }

    /**
     * Utf8
     *
     * @param string String
     * @return Bytes
     */
    public byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Utf8
     *
     * @param bytes Bytes
     * @return String
     */
    public String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Allow to read catch
     *
     * @param file File
     * @return The string
     */
    public String readCatch(File file) {
        return read(file);
    }

    /**
     * Allow to read
     *
     * @param file File
     * @return String
     */
    public String read(File file) {
        return utf8(readBytes(file));
    }

}
