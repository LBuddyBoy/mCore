package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InvseeCommand extends BaseCommand implements Listener {

    public static int[] ARMOR_SLOTS = new int[] {
            40, 39, 38, 37
    };

    public static int[] EXTRA_SLOTS = new int[] {
            45
    };

    public static int[] GLASS_SLOTS = new int[] {
            41,42,43,44,
            46,47,48,49,50,51,52,53,54
    };

    public InvseeCommand() {
        Bukkit.getPluginManager().registerEvents(this, CorePlugin.getInstance());
    }

    @CommandAlias("invsee")
    @CommandCompletion("@players")
    @CommandPermission("arrow.command.invsee")
    public static void invsee(Player sender, @Name("player") OnlinePlayer targetPlayer) {
        if (targetPlayer == null) {
            return;
        }

        Player target = targetPlayer.getPlayer();
        Inventory inventory = Bukkit.createInventory(target, 54, target.getName() + "'s Inventory");

        int i = 0;
        for (int slot : ARMOR_SLOTS) {
            inventory.setItem(slot - 1, target.getInventory().getArmorContents()[i++]);
        }

        i = 0;
        for (int slot : EXTRA_SLOTS) {
            inventory.setItem(slot - 1, target.getInventory().getExtraContents()[i++]);
        }

        for (int slot = 0; slot < 36; slot++) {
            inventory.setItem(slot, target.getInventory().getStorageContents()[slot]);
        }

        for (int slot : GLASS_SLOTS) {
            inventory.setItem(slot - 1, new ItemFactory(Material.WHITE_STAINED_GLASS_PANE).displayName(" ").enchant(Enchantment.UNBREAKING, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).build());
        }

        sender.openInventory(inventory);
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().endsWith("'s Inventory")) return;
        Player target = Bukkit.getPlayer(event.getView().getTitle().replaceAll("'s Inventory", ""));
        if (target == null) return;

        if (Arrays.stream(GLASS_SLOTS).anyMatch(i -> i == event.getRawSlot())) {
            event.setCancelled(true);
            return;
        }

        Tasks.run(() -> {
            ItemStack[] armor = new ItemStack[4], contents = new ItemStack[36], extras = new ItemStack[1];
            int i = 0;
            for (int slot : ARMOR_SLOTS) {
                armor[i++] = inventory.getItem(slot - 1);
            }

            i = 0;
            for (int slot : EXTRA_SLOTS) {
                extras[i++] = inventory.getItem(slot - 1);
            }

            for (int slot = 0; slot < 36; slot++) {
                contents[slot] = inventory.getItem(slot);
            }

            target.getInventory().setArmorContents(armor);
            target.getInventory().setStorageContents(contents);
            target.getInventory().setExtraContents(extras);
        });
    }

}
