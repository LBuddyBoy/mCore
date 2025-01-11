package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.log.model.impl.GrantRemoveLog;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
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
            ItemFactory factory = new ItemFactory(HeadUtil.DARK_GREEN_BASE_64);

            if (this.grant.isTemporary()) factory = new ItemFactory(HeadUtil.YELLOW_BASE_64);
            if (this.grant.isExpired() || this.grant.isRemoved()) factory = new ItemFactory(HeadUtil.DARK_RED_BASE_64);

            factory.displayName("&6" + this.grant.getSentAtDate());
            factory.lore(
                    "&7&m--------------------------",
                    "&fRank&7: " + this.grant.getInitialRankName(),
                    "&fSender&7: &e" + this.grant.getSenderName(),
                    "&fTarget&7: &6" + this.grant.getTargetName(),
                    "&fReason&7: &b" + this.grant.getReason(),
                    "&fDuration&7: &a" + this.grant.getDurationString(),
                    "&fScopes&7: &b" + StringUtils.join(this.grant.getScopes(), ", "),
                    "&7&m--------------------------"
            );

            if (this.grant.isRemoved()) {
                factory.addToLore("&fRemoved Reason&7: &c" + this.grant.getRemovedReason());
                factory.addToLore("&fRemoved By&7: &c" + this.grant.getRemovedByName());
                factory.addToLore("&fRemoved At&7: &c" + this.grant.getRemovedAtDate());
                factory.addToLore("&fRemoved On&7: &c" + this.grant.getRemovedOn());
                factory.addToLore("&7&m--------------------------");
            } else {
                factory.addToLore("&cClick to remove this grant.");
                factory.addToLore("&7&m--------------------------");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.grant.isExpired() || this.grant.isRemoved() || !this.grant.isRemovable()) {
                return;
            }

            this.grant.setRemovedBy(player.getUniqueId());
            this.grant.setRemovedAt(System.currentTimeMillis());
            this.grant.setRemovedReason("None specified");
            this.grant.setRemovedOn(CorePlugin.getInstance().getServerName());

            new GrantUpdatePacket(this.grant).send();
            new GrantRemoveLog(this.grant).createLog();
        }
    }

}
