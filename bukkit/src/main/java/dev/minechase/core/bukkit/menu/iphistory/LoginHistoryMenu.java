package dev.minechase.core.bukkit.menu.iphistory;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class LoginHistoryMenu extends IPagedMenu {

    private final HistoricalIP ip;

    @Override
    public String getPageTitle(Player player) {
        return "Logins: " + ip.getIpAddress() + " (" + UUIDUtils.getName(this.ip.getPlayerUUID()) + ")";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Long time : this.ip.getLogins()) {
            buttons.add(new IButton() {

                @Override
                public ItemStack getItem(Player player) {
                    return new ItemFactory(Material.PAPER).displayName("&e" + APIConstants.SDF.format(time)).build();
                }

            });
        }

        return buttons;
    }
}
