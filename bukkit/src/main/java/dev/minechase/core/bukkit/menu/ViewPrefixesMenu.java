package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViewPrefixesMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Prefixes";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Prefix prefix : CorePlugin.getInstance().getPrefixHandler().getLocalPrefixes().values()) {
            buttons.add(new PrefixButton(prefix));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class PrefixButton extends IButton {

        private final Prefix prefix;

        @Override
        public ItemStack getItem(Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
            boolean active = user.getActivePrefix() != null && user.getActivePrefix().equals(this.prefix);
            String footerText = active ?
                    "<blend:&7;&f>&o(( Click to deactivate the " + this.prefix.getName() + " prefix ))</>" :
                    "<blend:&7;&f>&o(( Click to activate the " + this.prefix.getName() + " prefix ))</>";
            Material material = active ? Material.BARRIER : Material.getMaterial(prefix.getMaterialString());

            return new ItemFactory(material)
                    .displayName(this.prefix.getDisplayName())
                    .lore(
                            "&fPrefix&7: " + this.prefix.getPrefix(),
                            " ",
                            footerText,
                            " "
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
            boolean active = user.getActivePrefix() != null && user.getActivePrefix().equals(this.prefix);

            if (active) {
                user.getPersistentMetadata().remove(User.ACTIVE_TAG_KEY);
                player.sendMessage(CC.translate("<blend:&4;&c>You deactivated the " + prefix.getName() + " prefix.</>"));
            } else {
                user.getPersistentMetadata().setUUID(User.ACTIVE_TAG_KEY, this.prefix.getId());
                player.sendMessage(CC.translate("<blend:&2;&a>You activated the " + prefix.getName() + " prefix.</>"));
            }

            updateMenu(player, true);
        }
    }

}
