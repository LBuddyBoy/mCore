package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.model.QueuePlayer;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.BukkitServerHandler;
import dev.minechase.core.velocity.packet.PlayerSendToServerPacket;
import org.bukkit.entity.Player;

public class QueueCommand extends BaseCommand {

    private final BukkitServerHandler serverHandler = CorePlugin.getInstance().getServerHandler();

    @CommandAlias("queue")
    public void queue(Player sender) {
        if (!this.serverHandler.isQueued(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You are not in a queue.</>"));
            return;
        }

        QueuePlayer player = this.serverHandler.getQueuePlayer(sender.getUniqueId());
        CoreServer server = player.getServer();

        if (server == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>We couldn't find the queue you are in. If this issue continues please contact an admin.</>"));
            this.leaveq(sender);
            return;
        }

        sender.sendMessage(CC.translate("<blend:&2;&a>You are currently #" + player.getPosition() + " out of " + server.getQueueSize() + " others in the " + server.getName() + " queue.</>"));
    }

    @CommandAlias("joinqueue|joinq")
    @CommandCompletion("@servers")
    public void joinq(Player sender, @Name("server") CoreServer server) {
        if (server.isHub()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You cannot queue for hub servers.</>"));
            return;
        }

        if (this.serverHandler.isQueued(sender.getUniqueId())) {
            this.serverHandler.removeFromQueue(sender.getUniqueId());
        }

        if (sender.hasPermission(CoreConstants.QUEUE_BYPASS_PERM)) {
            this.server(sender, server);
            return;
        }

        this.serverHandler.addToQueue(sender.getUniqueId(), server);
        sender.sendMessage(CC.translate("<blend:&2;&a>Successfully joined the " + server.getName() + "'s queue!</>"));
    }

    @CommandAlias("leavequeue|leaveq")
    @CommandCompletion("@servers")
    public void leaveq(Player sender) {
        if (!this.serverHandler.isQueued(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You are not in a queue.</>"));
            return;
        }

        this.serverHandler.removeFromQueue(sender.getUniqueId());
        sender.sendMessage(CC.translate("<blend:&2;&a>Successfully left the queue!</>"));
    }

    @CommandAlias("server|go")
    @CommandPermission("core.command.server")
    @CommandCompletion("@servers")
    public void server(Player sender, @Name("server") CoreServer server) {
        new PlayerSendToServerPacket(sender.getUniqueId(), server.getName()).send();
    }

}
