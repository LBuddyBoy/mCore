package dev.minechase.core.bukkit.mod.model;

import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class ModMode {

    private final Player player;
    private final GameMode gameMode;
    private final ItemStack[] inventoryContents;
    private boolean vanished;

    public ModMode(Player player) {
        this.player = player;
        this.inventoryContents = player.getInventory().getContents().clone();
        this.gameMode = player.getGameMode();

        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        for (ModItem item : ModItem.values()) {
            player.getInventory().setItem(item.getSlot() - 1, item.getItem().clone());
        }

        this.vanishOn();
    }

    public void unload() {
        player.getInventory().clear();
        player.getInventory().setContents(this.inventoryContents.clone());
        player.setGameMode(this.gameMode);

        this.vanishOff();
    }

    public void vanishOn() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.hasPermission(CoreConstants.STAFF_PERM)) continue;

            other.hidePlayer(CorePlugin.getInstance(), this.player);
        }

        this.vanished = true;
    }

    public void vanishOff() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(CorePlugin.getInstance(), this.player);
        }

        this.vanished = false;
    }

}
