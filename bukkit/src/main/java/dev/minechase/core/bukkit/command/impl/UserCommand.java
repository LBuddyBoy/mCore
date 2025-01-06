package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.HTTPUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;

@CommandAlias("user")
@CommandPermission("core.command.user")
public class UserCommand extends BaseCommand {

    @Subcommand("info")
    @CommandCompletion("@players")
    public void info(CommandSender sender, @Name("player") AsyncCorePlayer player) {
        player.getUser().whenCompleteAsyncExcept((user) -> {
            String firstJoin = TimeUtils.formatIntoDetailedString(System.currentTimeMillis() - user.getFirstJoinAt()) + " ago";

            sender.sendMessage(CC.translate("&a" + user.getName() + "'s Info"));
            sender.sendMessage(CC.translate("&fFirst Joined&7: &a" + (user.hasPlayedBefore() ? firstJoin : "&cNever Joined")));
            sender.sendMessage(CC.translate("&fActive Grant&7: &a" + user.getActiveGrant().getInitialRankName()));
        }, (throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
            sender.sendMessage(CoreConstants.INVALID_NAME(player));
        });
    }

}
