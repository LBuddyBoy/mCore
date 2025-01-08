package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.ScopedLog;
import dev.minechase.core.api.log.model.SenderLog;
import dev.minechase.core.api.log.model.TargetLog;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.menu.ViewLogsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("logs")
@CommandPermission("core.command.logs")
public class LogsCommand extends BaseCommand {

    @Subcommand("checkall")
    public void query(Player sender) {
        sender.sendMessage(CC.translate("&aLoading all logs this may take a few seconds..."));

        CoreAPI.getInstance().getLogHandler().getAllLogs().whenCompleteAsync((logs, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                sender.sendMessage(CC.translate("&cError loading server logs. Check console for more info."));
                return;
            }

            logs = logs.stream().sorted(Comparator.comparingLong(CoreLog::getLoggedAt).reversed()).toList();

            new ViewLogsMenu(logs).openMenu(sender);
        });
    }

    @Subcommand("check target")
    @CommandCompletion("@players @servers")
    public void checkplayer(Player sender, @Name("player") AsyncCorePlayer player, @Name("server") @Optional CoreServer server) {
        sender.sendMessage(CC.translate("&aLoading all logs this may take a few seconds..."));

        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            CoreAPI.getInstance().getLogHandler().getAllLogs().whenCompleteAsync((logs, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    sender.sendMessage(CC.translate("&cError loading server logs. Check console for more info."));
                    return;
                }

                logs = logs.stream()
                        .filter(log -> log instanceof TargetLog)
                        .filter(log -> ((TargetLog)log).getTargetUUID() != null)
                        .filter(log -> ((TargetLog)log).getTargetUUID().equals(uuid))
                        .filter(log -> (!(log instanceof ScopedLog scopedLog) || server == null || scopedLog.getSentOn().equalsIgnoreCase(server.getName())))
                        .sorted(Comparator.comparingLong(CoreLog::getLoggedAt).reversed()).toList();

                new ViewLogsMenu(logs).openMenu(sender);
            });
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

    @Subcommand("check sender")
    @CommandCompletion("@players @servers")
    public void checkSender(Player sender, @Name("player") AsyncCorePlayer player, @Name("server") @Optional CoreServer server) {
        sender.sendMessage(CC.translate("&aLoading all logs this may take a few seconds..."));

        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            CoreAPI.getInstance().getLogHandler().getAllLogs().whenCompleteAsync((logs, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    sender.sendMessage(CC.translate("&cError loading server logs. Check console for more info."));
                    return;
                }

                logs = logs.stream()
                        .filter(log -> log instanceof SenderLog)
                        .filter(log -> ((SenderLog)log).getSenderUUID() != null)
                        .filter(log -> ((SenderLog)log).getSenderUUID().equals(uuid))
                        .filter(log -> (!(log instanceof ScopedLog scopedLog) || server == null || scopedLog.getSentOn().equalsIgnoreCase(server.getName())))
                        .sorted(Comparator.comparingLong(CoreLog::getLoggedAt).reversed()).toList();

                new ViewLogsMenu(logs).openMenu(sender);
            });
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));

    }

}
