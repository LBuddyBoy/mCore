package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.velocity.packet.PlayerSendToServerPacket;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("hub|lobby")
public class HubCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        List<CoreServer> hubs = CorePlugin.getInstance().getHubs();

        if (hubs.isEmpty()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Fallback Error] We couldn't find a hub server to connect you to.</>"));
            return;
        }

        if (CorePlugin.getInstance().getServerHandler().getLocalServer().isHub()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Fallback Error] You are already connected to a hub server.</>"));
            return;
        }

        CoreServer server = hubs.getFirst();

        sender.sendMessage(CC.translate("<blend:&2;&a>Sending you to " + server.getName() + "...</>"));
        new PlayerSendToServerPacket(sender.getUniqueId(), server.getName()).send();
    }

}
