package com.ustaz1505.cfs.handlers;

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
        if (args.length != 1) {
            log.info(getMessagesConfig().getString("wrong-usage"));
            return false;
        }
        if (Objects.equals(args[0], "reload")) {
            if (sender instanceof Player) {

                reloadConfigs();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msgPrefix + getMessagesConfig().getString("reload-msg")));
                return true;
            } else {

                reloadConfigs();

                log.info(getMessagesConfig().getString("reload-msg"));
                return true;
            }
        }
        return true;
    }

    public static void reloadConfigs() {
        cfs.reloadConfig();

        config = cfs.getConfig();

        // Reload message config

        customConfig = new YamlConfiguration();
        customConfigFile = new File(msgFolder, Objects.requireNonNull(config.getString("messages-file")));
        try {
            customConfig.load(customConfigFile);
        } catch (Exception e) {
            log.info("Caught an exception" + e);
        }

        //

        msgPrefix = config.getString("msg-prefix") + " ";

        notPlayerError = getMessagesConfig().getString("not-player-err");
    }
}