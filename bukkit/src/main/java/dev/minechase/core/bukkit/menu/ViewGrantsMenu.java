package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewGrantsMenu extends IPagedMenu {

    private final UUID targetUUID;
    private final List<Grant> grants;

    public ViewGrantsMenu(UUID targetUUID, List<Grant> grants) {
        this.targetUUID = targetUUID;
        this.grants = grants;
    }

    @Override
    public String getPageTitle(Player player) {
        return UUIDUtils.getName(this.targetUUID) + "'s Grants";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Grant grant : this.grants) {
            buttons.add(new GrantButton(grant));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class GrantButton extends IButton {

        private final Grant grant;

        @Override
        public ItemStack getItem(Player player) {
            ItemFactory factory = new ItemFactory(Material.GREEN_WOOL);

            if (this.grant.isTemporary()) factory = new ItemFactory(Material.YELLOW_WOOL);
            if (this.grant.isExpired() || this.grant.isRemoved()) factory = new ItemFactory(Material.RED_WOOL);

            factory.displayName("&6" + this.grant.getSentAtDate());
            factory.lore(
                    "&7&m--------------------------",
                    "&fRank&7: " + this.grant.getInitialRankName(),
                    "&fSender&7: " + this.grant.getSenderName(),
                    "&fTarget&7: " + this.grant.getTargetName(),
                    "&fReason&7: " + this.grant.getReason(),
                    "&fDuration&7: " + this.grant.getDurationString(),
                    "&7&m--------------------------"
            );

            if (this.grant.isRemoved()) {
                factory.addToLore("&fRemoved Reason&7: &c" + this.grant.getRemovedReason());
                factory.addToLore("&fRemoved By&7: &c" + this.grant.getRemovedByName());
                factory.addToLore("&fRemoved At&7: &c" + this.grant.getRemovedAtDate());
                factory.addToLore("&7&m--------------------------");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {

        }
    }

}
