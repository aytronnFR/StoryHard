package fr.aytronn.storyhard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.storyhard.config.Configuration;
import fr.aytronn.storyhard.config.Persist;
import fr.aytronn.storyhard.tools.threads.CustomThread;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;

public final class StoryHard extends JavaPlugin {
    private static StoryHard instance;
    private Gson gson;
    private Persist persist;
    private Configuration configuration;

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
            loadConfiguration();

            getLogger().info(ChatColor.YELLOW
                    + "Configuration loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void onDisable() {
        CustomThread.shutdownAll();
    }

    public static StoryHard getInstance() {
        return instance;
    }

    private void loadConfiguration() {
        if (getPersist().getFile(Configuration.class).exists()) {
            configuration = getPersist().load(Configuration.class);
        } else {
            configuration = new Configuration();
            getPersist().save(getConfiguration());
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Gson getGson() {
        return gson;
    }

    public Persist getPersist() {
        return persist;
    }
}
