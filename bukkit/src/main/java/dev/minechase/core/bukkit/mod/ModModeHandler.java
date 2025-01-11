package dev.minechase.core.bukkit.mod;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.mod.model.ModItem;
import dev.minechase.core.bukkit.mod.model.ModMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ModModeHandler implements IModule, Listener {

    public static final String FROZEN_METADATA = "FROZEN";
    
    private final Map<UUID, ModMode> modModes;

    public ModModeHandler() {
        this.modModes = new HashMap<>();
    }

    @Override
    public void load() {
        CorePlugin.getInstance().getServer().getPluginManager().registerEvents(this, CorePlugin.getInstance());
    }

    @Override
    public void unload() {
        this.modModes.forEach(((uuid, modMode) -> modMode.unload()));
        this.modModes.clear();
    }

    public boolean isActive(Player player) {
        return CorePlugin.getInstance().getModModeHandler().getModModes().containsKey(player.getUniqueId());
    }
    
    public boolean isFrozen(Player player) {
        return player.hasMetadata(FROZEN_METADATA);
    }
    
    public void freeze(Player player) {
        player.setMetadata(FROZEN_METADATA, new FixedMetadataValue(CorePlugin.getInstance(), true));

        Arrays.asList(
                " ",
                "<blend:&4;&c>YOU HAVE BEEN FROZEN</>",
                "<blend:&4;&c>You have 3 minutes to join discord.gg/minechase</>",
                "",
                "<blend:&7:&f>Failure to do so in time, will result in a ban.</>",
                " "
        ).forEach(s -> player.sendMessage(CC.translate(s)));
    }
    
    public void unfreeze(Player player) {
        player.removeMetadata(FROZEN_METADATA, CorePlugin.getInstance());

        Arrays.asList(
                " ",
                "<blend:&2;&a>You have been unfrozen, have a nice day.</>",
                " "
        ).forEach(s -> player.sendMessage(CC.translate(s)));
    }

    public void activate(Player player) {
        if (this.modModes.containsKey(player.getUniqueId())) return;

        this.modModes.put(player.getUniqueId(), new ModMode(player));
    }

    public void deactivate(Player player) {
        if (!this.modModes.containsKey(player.getUniqueId())) return;

        ModMode modMode = this.modModes.get(player.getUniqueId());

        modMode.unload();
        CorePlugin.getInstance().getModModeHandler().getModModes().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!isActive(other)) continue;

            player.hidePlayer(CorePlugin.getInstance(), other);
        }

        if (!player.hasPermission(CoreConstants.STAFF_PERM)) return;

        this.activate(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.deactivate(player);
    }
    
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!isFrozen(damager)) return;
        
        event.setCancelled(true);
        damager.sendMessage(CC.translate("<blend:&4;&c>You cannot do this whilst frozen.</>"));
    }
    
    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!isFrozen(victim)) return;
        
        event.setCancelled(true);
    }
    
    @EventHandler
    private void onBreakFrozen(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!isFrozen(player)) return;
        
        event.setCancelled(true);
        player.sendMessage(CC.translate("<blend:&4;&c>You cannot do this whilst frozen.</>"));
    }
    
    @EventHandler
    private void onPlaceFrozen(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!isFrozen(player)) return;
        
        event.setCancelled(true);
        player.sendMessage(CC.translate("<blend:&4;&c>You cannot do this whilst frozen.</>"));
    }
    
    @EventHandler
    private void onPlaceFrozen(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
                event.getTo().getBlockY() == event.getFrom().getBlockY() &&
                event.getTo().getBlockZ() == event.getFrom().getBlockZ()
        ) return;

        Player player = event.getPlayer();
        if (!isFrozen(player)) return;
        
        event.setCancelled(true);
        player.sendMessage(CC.translate("<blend:&4;&c>You cannot do this whilst frozen.</>"));
    }

    @EventHandler
    private void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getHand());

        if (item == null) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        for (ModItem modItem : ModItem.values()) {
            if (!modItem.getItem().isSimilar(item)) continue;

            modItem.getConsumer().accept(player, target);
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    private void onRandomTP(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (ModItem.RANDOM_TP.getItem().isSimilar(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ModItem.RANDOM_TP.getConsumer().accept(player, null);
        event.setCancelled(true);
    }

    @EventHandler
    private void onSpectator(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (ModItem.SPECTATOR.getItem().isSimilar(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ModItem.SPECTATOR.getConsumer().accept(player, null);
        event.setCancelled(true);
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isActive(player)) return;
        if (event.getClickedBlock() == null) return;

        event.setCancelled(true);
    }

}
