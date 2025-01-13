package dev.minechase.core.bukkit.menu.disguise;

import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.SkinAPI;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.util.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class DisguiseSkinMenu extends IPagedMenu {

    // Skin Name & Skin Texture
    private final Set<Map.Entry<String, Pair<String, String>>> entries;
    private final String disguiseName;

    @Override
    public String getPageTitle(Player player) {
        return "Choose a skin...";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Map.Entry<String, Pair<String, String>> entry : entries) {
            buttons.add(new IButton() {

                @Override
                public ItemStack getItem(Player player) {
                    return new ItemFactory(entry.getValue().first)
                            .displayName(CC.translate("<blend:&6;&e>" + entry.getKey() + "</>"))
                            .lore("<blend:&7;&f>&o(( Click to choose " + entry.getKey() + " as your skin.</>")
                            .build();
                }

                @Override
                public void action(Player player, ClickType clickType, int slot) {
                    new DisguiseRankMenu(disguiseName, entry.getValue()).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
