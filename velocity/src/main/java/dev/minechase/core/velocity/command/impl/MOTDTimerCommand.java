package dev.minechase.core.velocity.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.velocitypowered.api.command.CommandSource;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.motd.MOTDHandler;
import dev.minechase.core.velocity.motd.model.MOTDTimer;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.minechase.core.velocity.util.CC;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd
 * @since 2/16/2024
 */

@CommandAlias("motdtimer|mtimer")
@CommandPermission("core.command.motdtimer")
public class MOTDTimerCommand extends BaseCommand {

    private final MOTDHandler motdHandler = CoreVelocity.getInstance().getMotdHandler();

    @Subcommand("start")
    @CommandCompletion("@motd-timers @motd-timers @durations @context-example")
    public void create(CommandSource sender, @Name("name") String name, @Name("display {- for spaces}") String displayName, @Name("duration") TimeDuration duration, @Name("context") String context) {
        if (this.motdHandler.getMOTDTimer(name).isPresent()) {
            sender.sendMessage(CC.translate("&c[MOTD Error] That motd timer already exists."));
            return;
        }

        MOTDTimer timer = new MOTDTimer(name, displayName, context, duration.transform());

        this.motdHandler.createMOTDTimer(timer);

        sender.sendMessage(CC.translate("&aSuccessfully created the '" + name + "' motd timer!"));
        sender.sendMessage(CC.translate("&e→ &fDisplay Name&7: " + displayName));
        sender.sendMessage(CC.translate("&e→ &fContext Raw&7: "));
        sender.sendMessage(CC.translate("   &7- &f" + timer.getContext()));
        sender.sendMessage(CC.translate("&e→ &fContext Formatted&7: "));
        sender.sendMessage(CC.translate("   &7- &f" + timer.getContextFormatted()));
        sender.sendMessage(CC.translate("&e→ &fDuration&7: " + duration.fancy()));
    }

    @Subcommand("setdisplay")
    @CommandCompletion("@motd-timers")
    public void setDisplay(CommandSource sender, @Name("timer") MOTDTimer timer, @Name("context") String display) {
        timer.setDisplayName(display);
        sender.sendMessage(CC.translate("&aSuccessfully updated the '" + timer.getName() + "' timer's display name!"));
    }

    @Subcommand("setcontext")
    @CommandCompletion("@motd-timers @context-example")
    public void setContext(CommandSource sender, @Name("timer") MOTDTimer timer, @Name("context") String context) {
        timer.setContext(context);

        sender.sendMessage(CC.translate("&aSuccessfully updated the '" + timer.getName() + "' timer's context!"));
        sender.sendMessage(CC.translate("&e→ &fContext Raw&7: "));
        sender.sendMessage(CC.translate("   &7- &f" + timer.getContext()));
        sender.sendMessage(CC.translate("&e→ &fContext Formatted&7: "));
        sender.sendMessage(CC.translate("   &7- &f" + timer.getContextFormatted()));
    }

    @Subcommand("duration add")
    @CommandCompletion("@motd-timers @durations")
    public void addTime(CommandSource sender, @Name("timer") MOTDTimer timer, @Name("time") TimeDuration duration) {
        timer.setDuration(timer.getDuration() + duration.transform());
        sender.sendMessage(CC.translate("&a" + timer.getName() + " duration is now " + TimeUtils.formatIntoHHMMSS((int) (timer.getDuration() / 1000))));
    }

    @Subcommand("duration subtract")
    @CommandCompletion("@motd-timers @durations")
    public void delTime(CommandSource sender, @Name("timer") MOTDTimer timer, @Name("time") TimeDuration duration) {
        timer.setDuration(timer.getDuration() - duration.transform());
        sender.sendMessage(CC.translate("&a" + timer.getName() + " duration is now " + TimeUtils.formatIntoHHMMSS((int) (timer.getDuration() / 1000))));
    }

    @Subcommand("duration set")
    @CommandCompletion("@motd-timers @durations")
    public void setTime(CommandSource sender, @Name("timer") MOTDTimer timer, @Name("time") TimeDuration duration) {
        timer.setDuration(duration.transform());
        timer.setStartedAt(System.currentTimeMillis());

        sender.sendMessage(CC.translate("&a" + timer.getName() + " duration is now " + TimeUtils.formatIntoHHMMSS((int) (timer.getDuration() / 1000))));
    }

    @Subcommand("resume")
    @CommandCompletion("@motd-timers")
    public void resume(CommandSource sender, @Name("timer") MOTDTimer timer) {
        if (!timer.isPaused()) {
            sender.sendMessage(CC.translate("&cThe " + timer.getName() + " is not paused."));
            return;
        }

        timer.resume();
        sender.sendMessage(CC.translate("&a" + timer.getName() + " has just been resumed."));
    }

    @Subcommand("pause|stop")
    @CommandCompletion("@motd-timers")
    public void pause(CommandSource sender, @Name("timer") MOTDTimer timer) {
        timer.pause();
        sender.sendMessage(CC.translate("&c" + timer.getName() + " has just been paused. This will not show up on the motd until resumed."));
        sender.sendMessage(CC.translate("&cIf you want to delete it completely do '/mtimer delete " + timer.getName() + "'!"));
    }

    @Subcommand("delete")
    @CommandCompletion("@motd-timers")
    public void delete(CommandSource sender, @Name("timer") MOTDTimer timer) {
        this.motdHandler.deleteMOTDTimer(timer);
        sender.sendMessage(CC.translate("&c" + timer.getName() + " has just been deleted completely."));
    }

}
