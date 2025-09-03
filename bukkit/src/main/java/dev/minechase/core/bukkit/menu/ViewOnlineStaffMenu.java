package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViewOnlineStaffMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Online Staff";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (!staff.hasPermission(CoreConstants.STAFF_PERM)) continue;

            buttons.add(new StaffButton(staff));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class StaffButton extends IButton {

        private final Player staff;

        @Override
        public ItemStack getItem(Player player) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(staff.getUniqueId());

            return new ItemFactory(user.getHeadTexture())
                    .displayName(CC.translate(user.getDisplayName()))
                    .lore(
                            "&fMod Mode&7: " + (CorePlugin.getInstance().getModModeHandler().isActive(player) ? "&aYes" : "&cNo"),
                            "&fVanished&7: " + (CorePlugin.getInstance().getModModeHandler().isVanished(player) ? "&aYes" : "&cNo")
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {

        }
    }

}
