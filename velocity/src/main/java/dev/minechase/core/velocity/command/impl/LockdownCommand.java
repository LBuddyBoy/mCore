package dev.minechase.core.velocity.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.velocitypowered.api.command.CommandSource;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.velocity.CoreLocale;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.util.CC;

import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("lockdown")
@CommandPermission("core.command.lockdown")
public class LockdownCommand extends BaseCommand {

    @Subcommand("toggle")
    public void toggle(CommandSource sender, @Name("duration") @Optional TimeDuration duration) {
        if (duration == null) duration = new TimeDuration("perm");

        if (CoreLocale.LOCK_DOWN_ACTIVE.getBoolean()) {
            CoreVelocity.getInstance().getLockdownHandler().deactivateLockDown();
            sender.sendMessage(CC.translate("&3[Lockdown] &bLockdown has just been &cdeactivated&b!"));
            return;
        }

        CoreVelocity.getInstance().getLockdownHandler().activateLockDown(duration.transform());
        sender.sendMessage(CC.translate("&3[Lockdown] &bLockdown has just been &aactivated&b!"));
    }

    @Subcommand("rank")
    @CommandCompletion("@rank")
    public void toggle(CommandSource sender, @Name("rank") Rank rank) {
        CoreLocale.LOCK_DOWN_RANK.update(rank.getName());
        CoreVelocity.getInstance().getLockdownHandler().reload();
        sender.sendMessage(CC.translate("&3[Lockdown] &bThe " + rank.getName() + " and other ranks above that will be able to join the server now!"));
    }

    @Subcommand("bypass add")
    @CommandCompletion("@players")
    public void whitelist(CommandSource sender, @Name("uuid") UUID player) {
        CoreVelocity.getInstance().getLockdownHandler().getBypassList().add(player);
        CoreLocale.LOCK_DOWN_BYPASS.update(CoreVelocity.getInstance().getLockdownHandler().getBypassList().stream().map(UUID::toString).collect(Collectors.toList()));
        CoreVelocity.getInstance().getLockdownHandler().reload();
        sender.sendMessage(CC.translate("&3[Lockdown] &a" + UUIDUtils.getName(player) + " can now join when the server is locked down!"));
    }

    @Subcommand("bypass remove")
    @CommandCompletion("@players")
    public void whitelistRemove(CommandSource sender, @Name("uuid") UUID player) {
        CoreVelocity.getInstance().getLockdownHandler().getBypassList().remove(player);
        CoreLocale.LOCK_DOWN_BYPASS.update(CoreVelocity.getInstance().getLockdownHandler().getBypassList().stream().map(UUID::toString).collect(Collectors.toList()));
        CoreVelocity.getInstance().getLockdownHandler().reload();
        sender.sendMessage(CC.translate("&3[Lockdown] &c" + UUIDUtils.getName(player) + " can no longer join when the server is locked down!"));
    }

    @Subcommand("bypass addip")
    public void whitelist(CommandSource sender, @Name("ip") String ip) {
        CoreVelocity.getInstance().getLockdownHandler().getBypassIps().add(ip);
        CoreLocale.LOCK_DOWN_IP_BYPASS.update(CoreVelocity.getInstance().getLockdownHandler().getBypassIps());
        CoreVelocity.getInstance().getLockdownHandler().reload();
        sender.sendMessage(CC.translate("&3[Lockdown] &a" + ip + " can now join when the server is locked down!"));
    }

    @Subcommand("bypass removeip")
    public void whitelistRemove(CommandSource sender, @Name("uuid") String ip) {
        CoreVelocity.getInstance().getLockdownHandler().getBypassIps().remove(ip);
        CoreLocale.LOCK_DOWN_IP_BYPASS.update(CoreVelocity.getInstance().getLockdownHandler().getBypassIps());
        CoreVelocity.getInstance().getLockdownHandler().reload();
        sender.sendMessage(CC.translate("&3[Lockdown] &c" + ip + " can no longer join when the server is locked down!"));
    }

}
