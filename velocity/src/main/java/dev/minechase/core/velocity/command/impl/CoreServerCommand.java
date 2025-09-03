package dev.minechase.core.velocity.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.server.packet.ServerRebootPacket;
import dev.minechase.core.velocity.CoreVelocity;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */

@CommandAlias("coreserver|cserver|cs")
@CommandPermission("core.command.coreserver")
public class CoreServerCommand extends BaseCommand {

    @Subcommand("rebootall")
    public void rebootAll(CommandSource sender) {
        CoreAPI.getInstance().getServerHandler().getServers().values().forEach(server -> {
            new ServerRebootPacket(server.getName()).send();
        });
    }

}
