package fr.aytronn.storyhard.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.storyhard.StoryHard;
import fr.aytronn.storyhard.tools.threads.CustomThread;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;


public class Persist {

    private final Gson gson;
    private final DiscUtil discUtil;

    public Persist() {
        this.gson = buildGson().create();
        this.discUtil = new DiscUtil();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(Object instance, File file) {
        final Runnable runnable = () -> {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            discUtil.writeCatch(file, gson.toJson(instance));
        };
        CustomThread.FILE_EXECUTOR.execute(runnable);
    }

    /**
     * This function is used to save the instance of a class
     * to the data folder folder with its name in lowercase
     * @param instance of the class to save
     */
    public void save(Object instance) {
        save(instance, getFile(instance));
    }

    /**
     * This function is used to get a
     * file from it's type class name
     * @param type of the file
     * @return the file
     */
    public File getFile(Type type) {
        return getFile(getName(type));
    }

    public File getFile(String name) {
        return new File(StoryHard.getInstance().getDataFolder(), name + ".json");
    }

    /**
     * This function is used to get a
     * file from it's object name
     * @param object of the file
     * @return the file
     */
    public File getFile(Object object) {
        return getFile(getName(object));
    }

    /**
     * This function is used to get a
     * file from it's class name
     * @param objectClass of the file
     * @return the file
     */
    public File getFile(Class<?> objectClass) {
        return getFile(getName(objectClass));
    }

    /**
     * This function is used to get a
     * name from it's object class
     * @param object of the type
     * @return the name
     */
    public String getName(Object object) {
        return getName(object.getClass());
    }

    public String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }



    private <T> T basicLoad(Class<T> clazz, File file) {
        final CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            final var content = discUtil.readCatch(file);
            if (content == null) {
                return null;
            }

            try {
                return gson.fromJson(content, clazz);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }, CustomThread.FILE_EXECUTOR.get());
        return completableFuture.join();
    }

    private <T> T basicLoad(Class<T> clazz, String content) {
        final CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (content == null || content.isEmpty()) {
                return null;
            }

            try {
                return gson.fromJson(content, clazz);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }, CustomThread.FILE_EXECUTOR.get());
        return completableFuture.join();
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    /**
     * This function is used to load a class
     * from server config dir
     * @param classT instance to load
     * @return the loaded class
     */
    public <T> T load(Class<T> classT) {
        return load(classT, getFile(classT));
    }

    public <T> T load(Class<T> clazz, String content) {
        final var object = basicLoad(clazz, content);
        save(object);
        return object;
    }

    public String load(File file) {
        return CompletableFuture.supplyAsync(() -> discUtil.readCatch(file), CustomThread.FILE_EXECUTOR.get()).join();
    }

    public <T> T load(Class<T> clazz, File file) {
        final var object = basicLoad(clazz, file);
        save(object, file);
        return object;
    }
}