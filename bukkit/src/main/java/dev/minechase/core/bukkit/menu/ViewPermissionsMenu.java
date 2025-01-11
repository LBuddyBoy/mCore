package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.log.model.impl.permission.PermissionRemoveLog;
import dev.minechase.core.api.permission.packet.PermissionUpdatePacket;
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

public class ViewPermissionsMenu extends IPagedMenu {

    private final UUID targetUUID;
    private final List<ScopedPermission> permissions;

    public ViewPermissionsMenu(UUID targetUUID, List<ScopedPermission> permissions) {
        this.targetUUID = targetUUID;
        this.permissions = permissions;
    }

    @Override
    public String getPageTitle(Player player) {
        return UUIDUtils.getName(this.targetUUID) + "'s Permissions";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (ScopedPermission permission : this.permissions) {
            buttons.add(new PermissionButton(permission));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class PermissionButton extends IButton {

        private final ScopedPermission permission;

        @Override
        public ItemStack getItem(Player player) {
            ItemFactory factory = new ItemFactory(HeadUtil.DARK_GREEN_BASE_64);

            if (this.permission.isTemporary()) factory = new ItemFactory(HeadUtil.YELLOW_BASE_64);
            if (this.permission.isExpired() || this.permission.isRemoved()) factory = new ItemFactory(HeadUtil.DARK_RED_BASE_64);

            factory.displayName("&6" + this.permission.getSentAtDate());
            factory.lore(
                    "&7&m--------------------------",
                    "&fPermission&7: " + this.permission.getPermissionNode(),
                    "&fSender&7: &e" + this.permission.getSenderName(),
                    "&fTarget&7: &6" + this.permission.getTargetName(),
                    "&fReason&7: &b" + this.permission.getReason(),
                    "&fDuration&7: &a" + this.permission.getDurationString(),
                    "&fScopes&7: &b" + StringUtils.join(this.permission.getScopes(), ", "),
                    "&7&m--------------------------"
            );

            if (this.permission.isRemoved()) {
                factory.addToLore("&fRemoved Reason&7: &c" + this.permission.getRemovedReason());
                factory.addToLore("&fRemoved By&7: &c" + this.permission.getRemovedByName());
                factory.addToLore("&fRemoved At&7: &c" + this.permission.getRemovedAtDate());
                factory.addToLore("&fRemoved On&7: &c" + this.permission.getRemovedOn());
                factory.addToLore("&7&m--------------------------");
            } else {
                factory.addToLore("&cClick to remove this grant.");
                factory.addToLore("&7&m--------------------------");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.permission.isExpired() || this.permission.isRemoved() || !this.permission.isRemovable()) {
                return;
            }

            this.permission.setRemovedBy(player.getUniqueId());
            this.permission.setRemovedAt(System.currentTimeMillis());
            this.permission.setRemovedReason("None specified");
            this.permission.setRemovedOn(CorePlugin.getInstance().getServerName());

            new PermissionUpdatePacket(this.permission).send();
            new PermissionRemoveLog(this.permission).createLog();
        }
    }

}
