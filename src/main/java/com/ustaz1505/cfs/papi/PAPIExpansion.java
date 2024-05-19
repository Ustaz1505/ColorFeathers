package com.ustaz1505.cfs.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.ustaz1505.cfs.ColorFeathers.cfs;
import static com.ustaz1505.cfs.ColorFeathers.log;
import static com.ustaz1505.cfs.handlers.SignCommand.hexColor;

public class PAPIExpansion extends PlaceholderExpansion {
    private final Plugin plugin;

    public PAPIExpansion(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("feather_color")) {
            if (player.getPlayer() == null) {
                return null;
            }
            PlayerInventory inventory = player.getPlayer().getInventory();
            ItemStack toolItem = inventory.getItemInOffHand();
            NamespacedKey colorKey = new NamespacedKey(cfs, "featherColor");
            if (toolItem.getType() == Material.FEATHER) {
                ItemMeta toolItemMeta = toolItem.getItemMeta();
                PersistentDataContainer toolData = Objects.requireNonNull(toolItemMeta).getPersistentDataContainer();
                if (toolData.has(colorKey, PersistentDataType.STRING)) {
                    String hexColor = toolData.get(colorKey, PersistentDataType.STRING);
                    return hexColor(hexColor);
                }
            }
            return "";
        }
        return null;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ustaz1505";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cfs";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }
}
