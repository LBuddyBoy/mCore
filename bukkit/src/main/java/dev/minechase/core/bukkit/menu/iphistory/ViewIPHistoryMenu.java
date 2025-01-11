package dev.minechase.core.bukkit.menu.iphistory;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ViewIPHistoryMenu extends IPagedMenu {

    private final UUID playerUUID;
    private final List<HistoricalIP> ips;

    @Override
    public String getPageTitle(Player player) {
        return UUIDUtils.getName(this.playerUUID) + "'s IP History";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (HistoricalIP ip : this.ips) {
            buttons.add(new HistoryButton(ip));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class HistoryButton extends IButton {

        private final HistoricalIP ip;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.BOOK)
                    .displayName("<blend:&6;&e>" + this.ip.getIpAddress() + "</>")
                    .lore(
                            "&fFirst Login Date&7: &e" + this.ip.getOriginalChangeDate() + " (" + TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - this.ip.getOriginalChangedAt()) + ")",
                            "&fLast Login Date&7: &e" + this.ip.getLastChangeDate() + " (" + TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - this.ip.getLastChangedAt()) + ")",
                            "&fTotal Logins&7: &e" + this.ip.getLogins().size(),
                            " ",
                            "<blend:&6;&e>&o(( Click to view all logins on this IP ))</>",
                            " "
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new LoginHistoryMenu(this.ip).openMenu(player);
        }
    }

}
