package com.ustaz1505.cfs.handlers;

import com.ustaz1505.cfs.papi.PAPIExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.ustaz1505.cfs.ColorFeathers.*;

import java.io.File;
import java.util.Objects;

public class CFSCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @ NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            log.info(getMessagesConfig().getString("wrong-usage"));
            return false;
        }
        if (Objects.equals(args[0], "reload")) {
            if (args.length == 2 && Objects.equals(args[1], "config")) {
                if (sender instanceof Player) {
                    reloadConfigs();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgPrefix + getMessagesConfig().getString("reload-config-msg")));
                    return true;
                } else {
                    reloadConfigs();
                    log.info(getMessagesConfig().getString("reload-msg"));
                    return true;
                }
            } else if (args.length == 2 && Objects.equals(args[1], "papi")) {
                reloadPAPI();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgPrefix + getMessagesConfig().getString("reload-papi-msg")));
                return true;
            } else if ((args.length == 2 && Objects.equals(args[1], "all")) || args.length == 1) {
                reloadConfigs();
                reloadPAPI();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgPrefix + getMessagesConfig().getString("reload-msg")));
                return true;
            } else {
                log.info(getMessagesConfig().getString("wrong-usage"));
                return false;
            }
        }
        return true;
    }

    public static boolean reloadPAPI() {
        if (papiExpansion != null) {
            papiExpansion.unregister();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.info("PlaceholderAPI detected...");
            papiExpansion = new PAPIExpansion(cfs);
            boolean result = papiExpansion.register();
            if (result) {
                log.info("PlaceholderAPI hook registered successfully!");
                return true;
            } else {
                log.info("PlaceholderAPI hook registration failed!");
                return false;
            }
        } else {
            log.info("PlaceholderAPI not found!");
            return false;
        }
    }

    public static void reloadConfigs() {
        cfs.reloadConfig();

        config = cfs.getConfig();

        // Reload message config

        messagesConfig = new YamlConfiguration();
        messagesConfigFile = new File(msgFolder, Objects.requireNonNull(config.getString("messages-file")));
        try {
            messagesConfig.load(messagesConfigFile);
        } catch (Exception e) {
            log.info("Caught an exception" + e);
        }

        msgPrefix = config.getString("msg-prefix") + " ";

        notPlayerError = getMessagesConfig().getString("not-player-err");
    }

}