package dev.minechase.core.bukkit.menu.disguise;

import dev.iiahmed.disguise.Disguise;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.CoreAPI;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DisguiseNameMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Choose a name...";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (String name : CoreAPI.getDisguiseNames()) {
            buttons.add(new IButton() {

                @Override
                public ItemStack getItem(Player player) {
                    return new ItemFactory("PLAYER_HEAD")
                            .displayName(CC.translate("<blend:&6;&e>" + name + "</>"))
                            .lore("<blend:&7;&f>&o(( Click to choose " + name + " as your name.</>")
                            .build();
                }

                @Override
                public void action(Player player, ClickType clickType, int slot) {
                    new DisguiseSkinMenu(CoreAPI.getDisguiseSkins().entrySet(), name).openMenu(player);
                }

            });
        }

        return buttons;
    }
}
