package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.FilterUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RenameCommand extends BaseCommand {

    @CommandAlias("rename")
    @CommandPermission("core.command.rename")
    @CommandCompletion("<text>")
    public void enchant(Player sender, @Name("name") String name) {
        ItemStack item = sender.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You need to be holding an item.</>"));
            return;
        }

        if (FilterUtil.isDisallowed(CC.stripColor(CC.translate(name)))) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That name contains a filtered word.</>"));
            new StaffMessagePacket("&4[Filtered Rename] &c(" + CoreAPI.getInstance().getServerName() + ") &b" + sender.getName() + "&7: /rename " + ChatColor.stripColor(name)).send();
            return;
        }

        sender.getInventory().setItemInMainHand(new ItemFactory(item).displayName(name).build());
        sender.sendMessage(CC.translate("&6You have renamed your item in your hand."));
    }

}
