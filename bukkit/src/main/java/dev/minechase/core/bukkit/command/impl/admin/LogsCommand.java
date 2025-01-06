package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.bukkit.menu.ViewLogsMenu;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("logs")
@CommandPermission("core.command.logs")
public class LogsCommand extends BaseCommand {

    @Subcommand("query")
    public void query(Player sender, @Name("filterType") String filterType) {
        // TODO Add filters

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

}
