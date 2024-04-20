package com.ustaz1505.cfs;

import com.ustaz1505.cfs.handlers.CFSCommand;
import com.ustaz1505.cfs.handlers.ChatHandler;
import com.ustaz1505.cfs.handlers.SignCommand;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class ColorFeathers extends JavaPlugin {

    public static Plugin cfs;
    public static Logger log;
    public static String msgPrefix;
    public static String notPlayerError;
    public static FileConfiguration config;
    public static File customConfigFile;
    public static FileConfiguration customConfig;
    public static File msgFolder;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cfs = this;
        log = cfs.getLogger();
        cfs.saveDefaultConfig();
        config = cfs.getConfig();
        msgFolder = new File (getDataFolder(), "languages");

        createMessagesConfig();

        msgPrefix = config.getString("msg-prefix") + " ";
        notPlayerError = getMessagesConfig().getString("not-player-err");

        log.info("Plugin started!");
        getServer().getPluginManager().registerEvents(new ChatHandler(), cfs);
        Objects.requireNonNull(this.getCommand("sign")).setExecutor(new SignCommand());
        Objects.requireNonNull(this.getCommand("colorfeathers")).setExecutor(new CFSCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Plugin stopped!");
    }

    public void createMessagesConfig() {

        customConfigFile = new File(msgFolder, Objects.requireNonNull(config.getString("messages-file")));
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("languages/messages_ru.yml", false);
            saveResource("languages/messages_en-us.yml", false);
            saveResource("languages/messages_be-by.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (Exception e) {
            log.info("Caught an exception" + e);
        }
    }

    public static FileConfiguration getMessagesConfig() {
        return customConfig;
    }
}
