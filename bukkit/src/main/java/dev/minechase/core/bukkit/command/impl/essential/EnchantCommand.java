package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand extends BaseCommand {

    @CommandAlias("enchant")
    @CommandPermission("core.command.enchant")
    @CommandCompletion("@enchantments")
    public void enchant(Player sender, @Name("enchant") Enchantment enchantment, @Name("level") @Optional Integer level) {
        if (level == null) level = 1;

        ItemStack item = sender.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You need to be holding an item to enchant.</>"));
            return;
        }

        item.addUnsafeEnchantment(enchantment, level);
        sender.sendMessage(CC.translate("<blend:&2;&a>Updated the enchants for the item in your hand.</>"));
    }

}
