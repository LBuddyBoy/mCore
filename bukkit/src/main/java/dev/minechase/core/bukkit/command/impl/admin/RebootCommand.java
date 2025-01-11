package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.command.CommandSender;

@CommandAlias("reboot")
@CommandPermission("core.command.reboot")
public class RebootCommand extends BaseCommand {

    @Subcommand("start")
    @CommandCompletion("<delay>")
    public void start(CommandSender sender, @Name("delay") TimeDuration duration) {
        if (CorePlugin.getInstance().getRebootHandler().isForceRebooting()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>There's already an auto reboot occurring.</>"));
            return;
        }

        CorePlugin.getInstance().getRebootHandler().forceReboot(duration.transform());
        sender.sendMessage(CC.translate("&aSuccessfully started an auto reboot."));
    }

    @Subcommand("cancel")
    public void cancel(CommandSender sender) {
        if (!CorePlugin.getInstance().getRebootHandler().isForceRebooting()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>There's no reboot occurring.</>"));
            return;
        }

        CorePlugin.getInstance().getRebootHandler().stopReboot();
        sender.sendMessage(CC.translate("&aSuccessfully cancelled an auto reboot."));
    }

}
