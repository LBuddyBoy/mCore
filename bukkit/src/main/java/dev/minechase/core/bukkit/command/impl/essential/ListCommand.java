package dev.minechase.core.bukkit.command.impl.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.ServerStatus;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ListCommand extends BaseCommand {

    @CommandAlias("glist")
    @CommandPermission("core.command.glist")
    public void glist(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&aOnline &cOffline &bPaused &eWhitelisted"));
        sender.sendMessage(" ");
        for (CoreServer server : CorePlugin.getInstance().getServerHandler().getServers().values()) {
            List<UUID> players = server.getPlayers();
            String statusColor = server.getStatus() == ServerStatus.OFFLINE ? "&c" : server.getStatus() == ServerStatus.WHITELISTED ? "&e" : server.getStatus() == ServerStatus.PAUSED ? "&b" : "&a";

            sender.sendMessage(CC.translate(statusColor + server.getName() + " &7[" + server.getPlayerCount() + "/" + server.getMaxPlayers() + "]"));
            sender.sendMessage(CC.translate(StringUtils.join(players.stream().map(UUIDUtils::getName).map(s -> "&e" + s).toList(), "&f, ")));
            if (!players.isEmpty()) {
                sender.sendMessage(" ");
            }
        }
    }

    @CommandAlias("list")
    @CommandPermission("core.command.list")
    public void list(CommandSender sender) {
        int playerCount = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        List<Rank> ranks = new ArrayList<>();
        List<User> users = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            ranks.add(user.getRank());
            users.add(user);
        }

        ranks = ranks.stream().filter(Objects::nonNull).distinct().sorted(Comparator.comparingInt(Rank::getWeight)).toList();
        users = users.stream().filter(u -> Objects.nonNull(u.getRank())).sorted(Comparator.comparingInt(user -> user.getRank().getWeight())).toList();

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate(StringUtils.join(ranks.stream().map(Rank::getDisplayName).toList(), " ")));
        sender.sendMessage(" ");

        sender.sendMessage(CC.translate("&f(" + playerCount + "/" + maxPlayers + ") " + StringUtils.join(users.stream().map(user -> user.getRank().getDisplayName() + "&f" + user.getEditedName()).toList(), ", ")));

    }

}
