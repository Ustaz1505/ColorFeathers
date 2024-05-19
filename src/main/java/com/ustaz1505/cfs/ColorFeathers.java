package com.ustaz1505.cfs;

import com.ustaz1505.cfs.handlers.CFSCommand;
import com.ustaz1505.cfs.handlers.ChatHandler;
import com.ustaz1505.cfs.handlers.SignCommand;

import com.ustaz1505.cfs.papi.PAPIExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bstats.bukkit.Metrics;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class ColorFeathers extends JavaPlugin {

    public static Plugin cfs;
    public static Logger log;
    public static String msgPrefix;
    public static String notPlayerError;
    public static FileConfiguration config;
    public static File messagesConfigFile;
    public static FileConfiguration messagesConfig;
    public static File msgFolder;
    public static PAPIExpansion papiExpansion = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cfs = this;
        log = cfs.getLogger();

        log.info("Plugin is starting...");

        cfs.saveDefaultConfig();
        config = cfs.getConfig();
        msgFolder = new File (getDataFolder(), "languages");

        createMessagesConfig();

        msgPrefix = config.getString("msg-prefix") + " ";
        notPlayerError = getMessagesConfig().getString("not-player-err");

        int pluginId = 20337;
        Metrics metrics = new Metrics(this, pluginId);


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.info("PlaceholderAPI detected...");
            papiExpansion = new PAPIExpansion(this);
            boolean result = papiExpansion.register();
            if (result) {
                log.info("PlaceholderAPI hook registered successfully!");
            } else {
                log.info("PlaceholderAPI hook registration failed!");
            }
        }

        getServer().getPluginManager().registerEvents(new ChatHandler(), cfs);
        Objects.requireNonNull(this.getCommand("sign")).setExecutor(new SignCommand());
        Objects.requireNonNull(this.getCommand("colorfeathers")).setExecutor(new CFSCommand());

        log.info("Plugin startup success!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Plugin stopped!");
    }

    public void createMessagesConfig() {

        messagesConfigFile = new File(msgFolder, Objects.requireNonNull(config.getString("messages-file")));
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("languages/messages_ru.yml", false);
            saveResource("languages/messages_en-us.yml", false);
            saveResource("languages/messages_be-by.yml", false);
        }

        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesConfigFile);
        } catch (Exception e) {
            log.info("Caught an exception" + e);
        }
    }

    public static FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
}
