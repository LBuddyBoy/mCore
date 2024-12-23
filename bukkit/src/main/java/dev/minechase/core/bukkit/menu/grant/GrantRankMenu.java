package dev.minechase.core.bukkit.menu.grant;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class GrantRankMenu extends IPagedMenu {

    private final UUID targetUUID;

    @Override
    public String getPageTitle(Player player) {
        return "Select a Rank: " + UUIDUtils.getName(this.targetUUID);
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Rank rank : CorePlugin.getInstance().getRankHandler().getSortedRanks()) {
            buttons.add(new RankButton(rank));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class RankButton extends IButton {

        private final Rank rank;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.getMaterial(this.rank.getMaterialString()))
                    .displayName(this.rank.getDisplayName())
                    .lore(
                            "&7&m--------------------------",
                            "&aClick here to select the " + this.rank.getDisplayName() + "&a rank.",
                            "&7&m--------------------------"
                    )
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            new GrantScopesMenu(targetUUID, rank.getId()).openMenu(player);
        }

    }

}
