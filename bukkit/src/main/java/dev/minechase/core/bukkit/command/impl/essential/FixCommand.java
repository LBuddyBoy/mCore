package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import de.tr7zw.nbtapi.NBT;
import dev.lbuddyboy.commons.api.util.GradientUtils;
import dev.lbuddyboy.commons.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

public class FixCommand extends BaseCommand {

    public static String UNFIXABLE_NBT_DATA = "CORE_UNFIXABLE";

    @CommandAlias("fix|repair")
    @CommandPermission("core.command.fix")
    public void fix(Player sender) {
        PlayerInventory inventory = sender.getInventory();
        ItemStack item = inventory.getItemInMainHand();

        if (item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You need to have an item in your main hand.</>"));
            return;
        }

        if (NBT.get(item, (tag) -> {
            return tag.hasTag(UNFIXABLE_NBT_DATA);
        })) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That item is unrepairable.</>"));
            return;
        }

        ItemStack fixed = fix(item);

        if (fixed == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>That item cannot be repaired.</>"));
            return;
        }

        sender.sendMessage(CC.translate("&6Successfully fixed the item in your main hand!</>"));
    }

    @CommandAlias("fixall|repairall")
    @CommandPermission("core.command.fixall")
    public void fixAll(Player sender) {
        PlayerInventory inventory = sender.getInventory();

        int i = -1;
        for (ItemStack content : inventory.getStorageContents()) {
            i++;

            if (content == null || content.getType() == Material.AIR) continue;
            if (NBT.get(content, (tag) -> {
                return tag.hasTag(UNFIXABLE_NBT_DATA);
            })) {
                continue;
            }

            ItemStack stack = fix(content);
            if (stack == null) continue;

            inventory.setItem(i, stack);
        }

        ItemStack[] armor = new ItemStack[inventory.getArmorContents().length];

        for (int index = 0; index < inventory.getArmorContents().length; index++) {
            ItemStack content = inventory.getArmorContents()[index];

            if (content == null || content.getType() == Material.AIR) {
                armor[index] = content;
                continue;
            }
            if (NBT.get(content, (tag) -> {
                return tag.hasTag(UNFIXABLE_NBT_DATA);
            })) {
                continue;
            }

            ItemStack stack = fix(content);
            if (stack == null) {
                armor[index] = content;
                continue;
            }

            armor[index] = content;
            i++;
        }

        inventory.setArmorContents(armor);
        sender.sendMessage(CC.translate("<blend:&2;&a>Successfully fixed " + i + " items in your inventory!</>"));
    }

    public ItemStack fix(ItemStack stack) {
        if (stack.getItemMeta() instanceof Damageable damageable && !(stack.getItemMeta().isUnbreakable())) {
            damageable.setDamage(0);
            stack.setItemMeta(damageable);
            return stack;
        }
        return null;
    }

}
