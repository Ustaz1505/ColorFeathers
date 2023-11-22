package com.ustaz1505.cfs.handlers;

import com.ustaz1505.cfs.CooldownManager;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ustaz1505.cfs.ColorFeathers.*;
import static com.ustaz1505.cfs.ColorFeathers.getMessagesConfig;


public class SignCommand implements CommandExecutor {

    private final CooldownManager cooldownManager = new CooldownManager();

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            Duration timeLeft = cooldownManager.getRemainingCooldown(playerId);
            if (timeLeft.isZero() || timeLeft.isNegative()) {
                PlayerInventory inventory = player.getInventory();
                ItemStack renamingItem = inventory.getItemInMainHand();
                ItemStack toolItem = inventory.getItemInOffHand();
                if (renamingItem.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + getMessagesConfig().getString("no-item-err"));
                    return true;
                }
                if (toolItem.getType() != Material.FEATHER) {
                    player.sendMessage(ChatColor.RED + getMessagesConfig().getString("no-tool-err"));
                    return true;
                }

                NamespacedKey isSignedKey = new NamespacedKey(cfs, "isSigned");
                NamespacedKey colorKey = new NamespacedKey(cfs, "featherColor");
                ItemMeta toolItemMeta = toolItem.getItemMeta();
                PersistentDataContainer toolData = Objects.requireNonNull(toolItemMeta).getPersistentDataContainer();
                String hexColor = "";
                if (toolData.has(colorKey, PersistentDataType.STRING)) {
                    hexColor = toolData.get(colorKey, PersistentDataType.STRING);
                }
                PersistentDataContainer renamingItemData = Objects.requireNonNull(renamingItem.getItemMeta()).getPersistentDataContainer();
                long DEFAULT_COOLDOWN = cfs.getConfig().getInt("cooldown");
                cooldownManager.setCooldown(playerId, Duration.ofSeconds(DEFAULT_COOLDOWN));
                boolean isNotSigned = true;

                if (renamingItemData.has(isSignedKey, PersistentDataType.BOOLEAN)) {
                    if (Boolean.TRUE.equals(renamingItemData.get(isSignedKey, PersistentDataType.BOOLEAN))) {
                        player.sendMessage(ChatColor.RED + getMessagesConfig().getString("already-signed-err"));
                        return true;
                    }
                }

                // Signing code

                for (ItemStack item:inventory) {
                    if (item != null) {
                        if (item.getType() == Material.INK_SAC) {
                            ItemStack newRenamingItem = renamingItem.clone();
                            newRenamingItem.setAmount(1);
                            ItemMeta newItemMeta = newRenamingItem.getItemMeta();
                            assert newItemMeta != null;
                            PersistentDataContainer data = newItemMeta.getPersistentDataContainer();
                            data.set(isSignedKey, PersistentDataType.BOOLEAN, true);
                            List<String> currentLore = Objects.requireNonNull(newItemMeta).getLore();
                            if (currentLore == null) {
                                currentLore = List.of((Objects.equals(hexColor, "") ? ChatColor.DARK_GRAY : hexColor(hexColor)) +
                                        "#" + player.getDisplayName());
                            } else if (currentLore.size() == 1) {
                                currentLore.add((Objects.equals(hexColor, "") ? ChatColor.DARK_GRAY : hexColor(hexColor)) +
                                        "#" + player.getDisplayName());
                            }
                            newItemMeta.setLore(currentLore);
                            newRenamingItem.setItemMeta(newItemMeta);
                            if ((inventory.firstEmpty() == -1)  && (getAmount(player, newRenamingItem) % newRenamingItem.getMaxStackSize() == 0)) {
                                player.getWorld().dropItem(player.getLocation(), newRenamingItem);
                            } else {
                                inventory.addItem(newRenamingItem);
                            }
                            isNotSigned = false;
                            player.playSound(player.getLocation(), Objects.requireNonNull(config.getString("sign-sound")), 100, 1);
                            renamingItem.setAmount(renamingItem.getAmount() - 1);
                            item.setAmount(item.getAmount() - 1);

                            // Выпадение пера!!111!!!!1!1
                            if (!toolData.has(colorKey, PersistentDataType.STRING)) {
                                double chance = Math.random();
                                if (chance < (cfs.getConfig().getDouble("legendary-percentage") / 100)) {
                                    String hex = String.format("#%02x%02x%02x",
                                            (int) (Math.random() * 256),
                                            (int) (Math.random() * 256),
                                            (int) (Math.random() * 256));

                                    // Advancement granting
                                    if (config.getBoolean("grant-advancement")) {
                                        CommandSender console = Bukkit.getConsoleSender();
                                        if (isFolia()) {
                                            sender.getServer().getGlobalRegionScheduler().run(cfs, task -> Bukkit.dispatchCommand(console, "advancement grant " + player.getName() + " only " + config.getString("advancement")));
                                        } else {
                                            Bukkit.dispatchCommand(console, "advancement grant " + player.getName() + " only " + config.getString("advancement"));
                                        }
                                    }

                                    toolItemMeta.setLore(List.of(hexColor(hex)+"★"));
                                    toolData.set(colorKey, PersistentDataType.STRING, hex);
                                    if (toolItem.getAmount() == 1) {
                                        toolItem.setItemMeta(toolItemMeta);
                                    } else {
                                        ItemStack newToolItem = toolItem.clone();
                                        toolItem.setAmount(toolItem.getAmount() - 1);
                                        newToolItem.setAmount(1);
                                        newToolItem.setItemMeta(toolItemMeta);

                                        if ((inventory.firstEmpty() == -1)  && (getAmount(player, newToolItem) % newToolItem.getMaxStackSize() == 0)) {
                                            player.getWorld().dropItem(player.getLocation(), newToolItem);
                                        } else {
                                            inventory.addItem(newToolItem);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                if (isNotSigned) {
                    player.sendMessage(ChatColor.RED + getMessagesConfig().getString("no-ink-err"));
                }

            } else {
                player.sendMessage(ChatColor.RED + getMessagesConfig().getString("cooldown-err"));
            }
            return true;
        } else {
            log.info(notPlayerError);
            return true;
        }

    }

    public static String hexColor(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text);
    }

    public static int getAmount(Player arg0, ItemStack arg1) {
        if (arg1 == null)
            return 0;
        int amount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = arg0.getInventory().getItem(i);
            if (slot == null || !slot.isSimilar(arg1))
                continue;
            amount += slot.getAmount();
        }
        return amount;
    }
}