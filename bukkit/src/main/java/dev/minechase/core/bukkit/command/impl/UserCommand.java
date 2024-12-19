package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.model.CorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("user")
@CommandPermission("core.command.user")
public class UserCommand extends BaseCommand {

    @Subcommand("info")
    @CommandCompletion("@players")
    public void info(CommandSender sender, @Name("player") CorePlayer player) {
        player.getUser().whenCompleteAsync((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            String firstJoin = TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - user.getFirstJoinAt()) + " ago";

            sender.sendMessage(CC.translate("&a" + user.getName() + "'s Info"));
            sender.sendMessage(CC.translate("&fFirst Joined&7: &a" + (user.hasPlayedBefore() ? firstJoin : "&cNever Joined")));
        });
    }

    @Subcommand("punishment")
    @CommandCompletion("@players")
    public void punishment(CommandSender sender, @Name("player") CorePlayer player, @Name("seconds") long seconds) {
        Punishment punishment = new Punishment(
                CommandUtil.getSender(sender),
                player.getUuid(),
                PunishmentType.BAN,
                seconds * 1000L,
                CorePlugin.getInstance().getServerName()
        );

        new PunishmentUpdatePacket(punishment).send();
        sender.sendMessage(CC.translate("&aTested punishment on " + player.getName() + "."));
    }

    @Subcommand("punishments")
    @CommandCompletion("@players")
    public void punishments(CommandSender sender, @Name("player") CorePlayer player) {
        sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s punishments..."));

        CorePlugin.getInstance().getPunishmentHandler().getPunishments(player.getUuid()).whenCompleteAsync(((punishments, throwable) -> {

            sender.sendMessage(CC.translate("&a" + player.getName() + " has " + punishments.size() + " punishments."));
        }));

    }

}
