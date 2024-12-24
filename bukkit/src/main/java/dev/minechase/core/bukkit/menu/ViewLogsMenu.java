package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
@RequiredArgsConstructor
public class ViewLogsMenu extends IPagedMenu {

    private final List<CoreLog> logs;
    private CoreLogType filterType;

    @Override
    public String getPageTitle(Player player) {
        return "Viewing " + logs.size() + " logs &7(" + (this.filterType == null ? "All" : this.filterType.name()) + ")";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (CoreLog log : this.getLogs()) {
            buttons.add(new LogButton(log));
        }

        return buttons;
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        return new HashMap<>(){{
            put(5, new FilterButton());
        }};
    }

    public List<CoreLog> getLogs() {
        return this.filterType == null ? this.logs : this.logs.stream().filter(log -> log.getType() == this.filterType).toList();
    }

    public boolean isActive(String s) {
        if (s.equalsIgnoreCase("All") && this.filterType == null) return true;

        return this.filterType != null && s.equalsIgnoreCase(this.filterType.name());
    }

    public class FilterButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();
            boolean all = isActive("All");

            lore.add("&7&m-------------------");
            lore.add((all ? "&a" : "&c") + "- All");

            for (CoreLogType type : CoreLogType.values()) {
                boolean active = isActive(type.name());

                lore.add((active ? "&a" : "&c") + "- " + type.name());
            }

            lore.add("&7&m-------------------");
            lore.add("&aClick to scroll through the filters.");
            lore.add("&7&m-------------------");

            return new ItemFactory(Material.OAK_SIGN)
                    .displayName("<blend:&6;&e>Filter by Type</>")
                    .lore(lore)
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            int ordinal = filterType == null ? -1 : filterType.ordinal();

            ordinal++;

            if (ordinal >= CoreLogType.values().length) {
                filterType = null;
            } else {
                for (CoreLogType type : CoreLogType.values()) {
                    if (type.ordinal() != ordinal) continue;

                    filterType = type;
                    break;
                }
            }

            updateMenu(player, true);
        }
    }

    @AllArgsConstructor
    public class LogButton extends IButton {

        private final CoreLog log;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(Material.getMaterial(log.getDisplayMaterial()))
                    .displayName(log.getTitle())
                    .lore(log.getLog())
                    .build();
        }

    }

}
