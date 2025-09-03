package dev.minechase.core.velocity.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.instance.model.InstanceType;
import dev.minechase.core.velocity.util.CC;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */

@CommandAlias("instance|instances|im")
public class InstanceCommand extends BaseCommand {

    @Subcommand("create")
    public void create(CommandSource sender, @Name("type") InstanceType type, @Name("amount") int amount) {
        if (sender instanceof Player) {
            sender.sendPlainMessage("This command can only be executed by console");
            return;
        }

        int created = type.createInstance(amount);

        sender.sendMessage(CC.translate("&aCreated " + created + " '" + type.getId() + "' instances successfully!"));
    }

    @Subcommand("reboot all")
    public void rebootAll(CommandSource sender, @Name("type") InstanceType type) {
        if (sender instanceof Player) {
            sender.sendPlainMessage("This command can only be executed by console");
            return;
        }

        type.getInstances().forEach(CoreServer::reboot);
        sender.sendMessage(CC.translate("&aAll '" + type.getId() + "' instances rebooted"));
    }

    @Subcommand("delete amount")
    public void deleteAmount(CommandSource sender, @Name("type") InstanceType type, @Name("amount") int amount) {
        if (sender instanceof Player) {
            sender.sendPlainMessage("This command can only be executed by console");
            return;
        }

        int deleted = type.deleteInstanceAmount(amount);

        sender.sendMessage(CC.translate("&cDeleted " + deleted + " '" + type.getId() + "' instances successfully!"));
    }

    @Subcommand("delete specific")
    public void deleteSpecific(CommandSource sender, @Name("type") InstanceType type, @Name("name") String name) {
        if (sender instanceof Player) {
            sender.sendPlainMessage("This command can only be executed by console");
            return;
        }

        type.deleteInstanceByName(name);
    }

}
