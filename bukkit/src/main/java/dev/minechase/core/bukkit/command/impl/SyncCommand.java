package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.api.report.packet.ReportUpdatePacket;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncCodeUpdatePacket;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncInformationRemovePacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeUpdatePacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncInformationRemovePacket;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.bukkit.packet.ReportCreatePacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("sync")
public class SyncCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        sender.sendMessage(CC.blend("Options: discord, website", "&2", "&a"));
    }

    @Subcommand("website")
    public void website(Player sender) {
        CorePlugin.getInstance().getWebsiteSyncHandler().getSyncInformation(sender.getUniqueId()).whenCompleteAsync(((information, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (information != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>Your account is already synced to: '" + information.getWebsiteUserId() + "'</>"));
                return;
            }

            SyncCode syncCode = CorePlugin.getInstance().getWebsiteSyncHandler().getSyncCode(sender.getUniqueId());

            if (syncCode != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>You already have a sync code: " + syncCode.getCode() + "</>"));
                return;
            }

            syncCode = new SyncCode(sender.getUniqueId(), this.generateWebsiteCode());

            Arrays.asList(
                    " ",
                    "<blend:&6;&e>&lHow to Sync Account</>",
                    "&eStep #1 &fHead over to https://mcore.com/sync",
                    "&eStep #2 &fCreate an account if you haven't",
                    "&eStep #3 &fEnter this code: " + syncCode.getCode(),
                    " ",
                    "&fAfter doing this your website account will be synced",
                    "&fto your Minecraft Account!",
                    " "
            ).forEach(s -> sender.sendMessage(CC.translate(s)));

            new WebsiteSyncCodeUpdatePacket(syncCode).send();
        }));
    }

    @Subcommand("discord")
    public void discord(Player sender) {
        CorePlugin.getInstance().getDiscordSyncHandler().getSyncInformation(sender.getUniqueId()).whenCompleteAsync(((information, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (information != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>Your account is already synced to: '" + information.getDiscordMemberId() + "'</>"));
                return;
            }

            SyncCode syncCode = CorePlugin.getInstance().getDiscordSyncHandler().getSyncCode(sender.getUniqueId());

            if (syncCode != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>You already have a sync code: " + syncCode.getCode() + "</>"));
                return;
            }

            syncCode = new SyncCode(sender.getUniqueId(), this.generateDiscordCode());

            Arrays.asList(
                    " ",
                    "<blend:&6;&e>&lHow to Sync Account</>",
                    "&eStep #1 &fJoin minechase.net/discord",
                    "&eStep #2 &fGo to #sync",
                    "&eStep #3 &fType /sync " + syncCode.getCode(),
                    " ",
                    "&fAfter doing this your discord account will be synced",
                    "&fto your Minecraft Account!",
                    " "
            ).forEach(s -> sender.sendMessage(CC.translate(s)));

            new DiscordSyncCodeUpdatePacket(syncCode).send();
        }));
    }

    @Subcommand("reset website")
    @CommandPermission("core.command.sync.reset")
    @CommandCompletion("@players")
    public void resetWeb(CommandSender sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(targetUUID -> {
            CorePlugin.getInstance().getWebsiteSyncHandler().getSyncInformation(targetUUID).whenCompleteAsync((information, throwable) -> {
                new WebsiteSyncInformationRemovePacket(information).send();
                new PlayerMessagePacket(Arrays.asList(
                        "&cYour account is no longer synced with " + information.getWebsiteUserId()
                ), information.getPlayerUUID()).send();
            });

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    public int generateDiscordCode() {
        int random = ThreadLocalRandom.current().nextInt(99999);

        if (CorePlugin.getInstance().getDiscordSyncHandler().getSyncCode(random) != null) return generateDiscordCode();

        return random;
    }

    public int generateWebsiteCode() {
        int random = ThreadLocalRandom.current().nextInt(99999);

        if (CorePlugin.getInstance().getWebsiteSyncHandler().getSyncCode(random) != null) return generateWebsiteCode();

        return random;
    }

}
