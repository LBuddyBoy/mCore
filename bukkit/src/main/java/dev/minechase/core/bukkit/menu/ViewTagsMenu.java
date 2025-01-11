package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.tag.model.Tag;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViewTagsMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Tags";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Tag tag : CorePlugin.getInstance().getTagHandler().getLocalTags().values()) {
            buttons.add(new TagButton(tag));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class TagButton extends IButton {

        private final Tag tag;

        @Override
        public ItemStack getItem(Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
            boolean active = user.getActiveTag() != null && user.getActiveTag().equals(this.tag);
            String footerText = active ?
                    "<blend:&7;&f>&o(( Click to activate the " + this.tag.getName() + " tag ))</>" :
                    "<blend:&7;&f>&o(( Click to deactivate the " + this.tag.getName() + " tag ))</>";
            Material material = active ? Material.BARRIER : Material.getMaterial(tag.getMaterialString());

            return new ItemFactory(material)
                    .displayName(this.tag.getDisplayName())
                    .lore(
                            "&fSuffix&7: " + this.tag.getSuffix(),
                            " ",
                            footerText,
                            " "
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());
            boolean active = user.getActiveTag() != null && user.getActiveTag().equals(this.tag);

            if (active) {
                user.getPersistentMetadata().remove(User.ACTIVE_TAG_KEY);
                player.sendMessage(CC.translate("<blend:&4;&c>You deactivated the " + tag.getName() + " tag.</>"));
            } else {
                user.getPersistentMetadata().setUUID(User.ACTIVE_TAG_KEY, this.tag.getId());
                player.sendMessage(CC.translate("<blend:&2;&a>You activated the " + tag.getName() + " tag.</>"));
            }

            updateMenu(player, true);
        }
    }

}
