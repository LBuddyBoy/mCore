package dev.minechase.core.bukkit.menu.disguise;

import dev.iiahmed.disguise.Disguise;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.Pair;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DisguiseRankMenu extends IPagedMenu {

    // Skin Name & Skin Texture
    private final String disguiseName;
    private final Pair<String, String> skinPair;

    @Override
    public String getPageTitle(Player player) {
        return "Choose a rank...";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Rank rank : CorePlugin.getInstance().getRankHandler().getSortedRanks()) {
            if (!rank.isDisguiseRank()) continue;

            buttons.add(new IButton() {

                @Override
                public ItemStack getItem(Player player) {
                    return new ItemFactory(Material.getMaterial(rank.getMaterialString()))
                            .displayName(rank.getDisplayName())
                            .lore("<blend:&7;&f>&o(( Click to choose " + WordUtils.capitalize(rank.getName()) + " as your rank.</>")
                            .build();
                }

                @Override
                public void action(Player player, ClickType clickType, int slot) {
                    player.closeInventory();
                    CorePlugin.getInstance().getUserHandler().disguise(player, Disguise.builder()
                            .setName(disguiseName)
                            .setSkin(skinPair.first, skinPair.second), rank);

                    player.sendMessage(CC.translate("<blend:&2;&a>You are now disguised as " + disguiseName + " with the " + WordUtils.capitalize(rank.getName()) + " rank.</>"));
                }

            });
        }

        return buttons;
    }
}
