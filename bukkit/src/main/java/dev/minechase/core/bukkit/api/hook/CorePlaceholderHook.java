package dev.minechase.core.bukkit.api.hook;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.placeholder.model.PlaceholderProvider;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;

public class CorePlaceholderHook implements PlaceholderProvider {

    @Override
    public String getId() {
        return "mcore";
    }

    @Override
    public String applyPlaceholders(Player player, String s) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

        for (CoreServer server : CorePlugin.getInstance().getServerHandler().getServers().values()) {
            s = s
                    .replaceAll("%server_" + server.getName() + "_online%", APIConstants.formatNumber(server.getPlayerCount()))
                    .replaceAll("%server_" + server.getName() + "_max%", APIConstants.formatNumber(server.getMaxPlayers()))
                    .replaceAll("%server_" + server.getName() + "_queued%", APIConstants.formatNumber(server.getQueueSize()))
                    .replaceAll("%server_" + server.getName() + "_status%", server.getStatus().getDisplayName());
        }

        return s
                .replaceAll("%server_name%", CorePlugin.getInstance().getServerName())
                .replaceAll("%player_skin_texture%", user.getHeadTexture())
                .replaceAll("%player_skin_signature%", user.getHeadSignature())
                .replaceAll("%player_display_name%", user.getDisplayName())
                .replaceAll("%player_colored_name%", user.getColoredName())
                .replaceAll("%player_rank%", user.getRank().getDisplayName());
    }
}
