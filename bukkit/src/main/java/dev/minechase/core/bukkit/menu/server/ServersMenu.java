package dev.minechase.core.bukkit.menu.server;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ServersMenu extends IPagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Core Servers";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (CoreServer server : CorePlugin.getInstance().getServerHandler().getServers().values()) {
            buttons.add(new ServerButton(server));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class ServerButton extends IButton {

        private final CoreServer server;

        @Override
        public ItemStack getItem(Player player) {
            String time = server.isOffline() ?
                    "&fDowntime&7: &c" + TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - server.getStoppedAt()) :
                    "&fUptime&7: &a" + TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - server.getStartedAt());

            return new ItemFactory(
                    server.isOffline() ? HeadUtil.DARK_RED_BASE_64 : server.isWhitelisted() ? HeadUtil.YELLOW_BASE_64 : server.isQueuePaused() ? HeadUtil.BLUE_BASE_64 : HeadUtil.BLUE_BASE_64
            )
                    .displayName("<blend:&6;&e>&l" + this.server.getName() + "</>")
                    .lore(
                            "&7&m------------------------",
                            time,
                            "&fHub&7: &e" + (this.server.isHub() ? "&aYes" : "&cNo"),
                            "&fHost&7: &e" + this.server.getHost(),
                            "&fPort&7: &e" + this.server.getPort(),
                            "&fStatus&7: &e" + this.server.getStatus().name(),
                            "&fQueued&7: &e" + this.server.getQueuePlayers().size(),
                            "&fPlayers&7: &e" + this.server.getPlayerCount() + "/" + this.server.getMaxPlayers(),
                            "&7&m------------------------"
                    )
                    .build();
        }

    }

}
