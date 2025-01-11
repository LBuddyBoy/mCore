package dev.minechase.core.bukkit.listener;

import dev.minechase.core.bukkit.util.totp.TwoFactorUtil;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TotpListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (TwoFactorUtil.isLocked(player)) {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() == Material.FILLED_MAP && itemStack.getItemMeta().hasLore()) {
                    List<String> lore = itemStack.getItemMeta().getLore();

                    if (!lore.isEmpty() && lore.get(0).equalsIgnoreCase("QR Code Map")) {
                        player.getInventory().remove(itemStack);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            String command = event.getMessage().toLowerCase();

            // iterate whitelisted commands instead of using .contains because of case sensitivity
            for (String whitelistedCommand : Arrays.asList(
                    "auth",
                    "2fasetup",
                    "setup2fa"
            )) {
                if (command.equalsIgnoreCase(whitelistedCommand)) {
                    event.getPlayer().sendMessage(TwoFactorUtil.getMessage(event.getPlayer()));
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
                event.getTo().getBlockY() == event.getFrom().getBlockY() &&
                event.getTo().getBlockZ() == event.getFrom().getBlockZ()
        ) return;

        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.getPlayer().sendMessage(TwoFactorUtil.getMessage(event.getPlayer()));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.getPlayer().sendMessage(TwoFactorUtil.getMessage(event.getPlayer()));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (TwoFactorUtil.isLocked((Player) event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && TwoFactorUtil.isLocked((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (TwoFactorUtil.isLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (TwoFactorUtil.isLocked((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

}
