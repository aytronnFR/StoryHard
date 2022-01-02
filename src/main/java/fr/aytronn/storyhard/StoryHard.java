package fr.aytronn.storyhard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.storyhard.config.Configuration;
import fr.aytronn.storyhard.config.Persist;
import fr.aytronn.storyhard.listeners.PlayerListener;
import fr.aytronn.storyhard.tools.threads.CustomThread;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;

public final class StoryHard extends JavaPlugin {
    private static StoryHard instance;
    private Gson gson;
    private Persist persist;
    private Configuration configuration;


    /**
     * On plugin is enabled
     *
     */
    @Override
    public void onEnable() {
        try {
            getDataFolder().mkdirs();
            instance = this;

            long startMillis = System.currentTimeMillis();
            getLogger().info(ChatColor.BLUE + "==========- " + ChatColor.GOLD
                    + "StoryHard" + ChatColor.BLUE + " -==========");
            getLogger().info(ChatColor.YELLOW + "Loading configuration");
            this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization()
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();
            this.persist = new Persist();
            //loadConfiguration();

            getLogger().info(ChatColor.YELLOW
                    + "Configuration loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");
            startMillis = System.currentTimeMillis();

            getLogger().info(ChatColor.YELLOW + "Loading class");
            registerListeners();
            registerCommands();
            getLogger().info(ChatColor.YELLOW
                    + "Class loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * On plugin disabled
     *
     */
    @Override
    public void onDisable() {
        long startMillis = System.currentTimeMillis();
        getLogger().info(ChatColor.BLUE + "==========- " + ChatColor.GOLD
                + "StoryHard shutdown" + ChatColor.BLUE + " -==========");
        getLogger().info(ChatColor.YELLOW + "Shutdown thread");
        CustomThread.shutdownAll();
        getLogger().info(ChatColor.YELLOW
                + "Thread shutdown (" + (System.currentTimeMillis() - startMillis) + ") ms.");
    }

    /**
     * Allow to get instance of this plugin
     *
     * @return instance of plugin
     */
    public static StoryHard getInstance() {
        return instance;
    }

    /**
     * Allow registering commands
     *
     */
    private void registerCommands() {
    }

    /**
     * Allow registering listeners
     *
     */
    private void registerListeners() {
        getInstance().getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    /**
     * Allow loading configuration
     *
     */
    private void loadConfiguration() {
        if (getPersist().getFile(Configuration.class).exists()) {
            configuration = getPersist().load(Configuration.class);
        } else {
            configuration = new Configuration();
            getPersist().save(getConfiguration());
        }
    }

    /**
     * Allow to get the configuration
     *
     * @return The configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Allow to get the gson serializer
     *
     * @return Gson serializer
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Allow getting persist class
     *
     * @return Persist class
     */
    public Persist getPersist() {
        return persist;
    }
}
