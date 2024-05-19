package com.ustaz1505.cfs.papi;

import com.ustaz1505.cfs.ColorFeathers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.ustaz1505.cfs.ColorFeathers.cfs;
import static com.ustaz1505.cfs.handlers.SignCommand.hexColor;

public class PAPIExpansion extends PlaceholderExpansion {
    private final Plugin plugin;

    public PAPIExpansion(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("feathercolor")) {
            if (player == null) {
                return "";
            }
            PlayerInventory inventory = player.getInventory();
            ItemStack toolItem = inventory.getItemInOffHand();
            NamespacedKey colorKey = new NamespacedKey(cfs, "featherColor");
            if (toolItem.getType() == Material.FEATHER) {
                ItemMeta toolItemMeta = toolItem.getItemMeta();
                PersistentDataContainer toolData = Objects.requireNonNull(toolItemMeta).getPersistentDataContainer();
                String hexColor = "";
                if (toolData.has(colorKey, PersistentDataType.STRING)) {
                    hexColor = toolData.get(colorKey, PersistentDataType.STRING);
                    StringBuilder formatted_color = new StringBuilder("&x");
                    assert hexColor != null;
                    for (char c : hexColor.toCharArray()) {
                        formatted_color.append("&").append(c);
                    }
                    return formatted_color.toString();
                }
            }
        }
        return "";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ustaz1505";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "colorFeathers";
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
