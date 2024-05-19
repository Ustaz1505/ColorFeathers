package com.ustaz1505.cfs.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.ustaz1505.cfs.ColorFeathers.config;
import static com.ustaz1505.cfs.handlers.SignCommand.hexColor;

import java.util.Objects;

import static com.ustaz1505.cfs.ColorFeathers.cfs;

public class ChatHandler implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (!config.getBoolean("colored-messages")) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack toolItem = inventory.getItemInOffHand();
        NamespacedKey colorKey = new NamespacedKey(cfs, "featherColor");
        if (toolItem.getType() == Material.FEATHER) {
            ItemMeta toolItemMeta = toolItem.getItemMeta();
            PersistentDataContainer toolData = Objects.requireNonNull(toolItemMeta).getPersistentDataContainer();
            String hexColor = "";
            if (toolData.has(colorKey, PersistentDataType.STRING)) {
                hexColor = toolData.get(colorKey, PersistentDataType.STRING);
                event.setFormat(hexColor(hexColor) + "<" + player.getDisplayName() + "> " + event.getMessage());
            }
        }
    }
}
