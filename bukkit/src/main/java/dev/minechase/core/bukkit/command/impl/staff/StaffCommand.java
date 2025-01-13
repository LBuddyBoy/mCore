package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.model.UserMetadata;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewOnlineStaffMenu;
import dev.minechase.core.bukkit.mod.ModModeHandler;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.settings.model.impl.StaffChatSetting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand extends BaseCommand {

    @CommandAlias("staffchat|sc")
    @CommandPermission("core.command.staffchat")
    public void staffChat(Player sender, @Name("message") @Optional String message) {
        if (message == null) {
            CorePlugin.getInstance().getSettingsHandler().getSetting(StaffChatSetting.class).toggle(sender.getUniqueId());
            return;
        }

        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

        new StaffMessagePacket(CC.translate("&9[Staff Chat] " + user.getDisplayName() + "&7: &f") + message).send();
    }

    @CommandAlias("mutechat")
    @CommandPermission("core.command.mutechat")
    public void muteChat(CommandSender sender, @Name("duration") @Optional TimeDuration duration) {
        if (duration == null) duration = new TimeDuration("perm");

        if (CorePlugin.getInstance().getChatHandler().isMuted()) {
            CorePlugin.getInstance().getChatHandler().unmute(sender);
            return;
        }

        CorePlugin.getInstance().getChatHandler().mute(sender, duration.transform());
    }

    @CommandAlias("slowchat")
    @CommandPermission("core.command.slowchat")
    public void slowChat(CommandSender sender, @Name("delaySeconds") @Optional Integer secondsDelay, @Name("duration") @Optional TimeDuration duration) {
        if (duration == null) duration = new TimeDuration("perm");
        if (secondsDelay == null) secondsDelay = 3;

        if (CorePlugin.getInstance().getChatHandler().isSlowed()) {
            CorePlugin.getInstance().getChatHandler().unslowChat(sender);
            return;
        }

        CorePlugin.getInstance().getChatHandler().slowChat(sender, secondsDelay, duration.transform());
    }

    @CommandAlias("slowchat")
    @CommandPermission("core.command.clearchat")
    public void clearChat(CommandSender sender) {
        Tasks.runAsync(() -> {
            String senderName = "";

            if (sender instanceof Player player) {
                User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

                senderName = user.getColoredName();
            } else {
                senderName = "&4&lCONSOLE";
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(CoreConstants.STAFF_PERM)) continue;

                for (int i = 0; i < 500; i++) {
                    Bukkit.broadcastMessage(" ");
                }
            }

            Bukkit.broadcastMessage(CC.translate(senderName + "&a cleared the chat."));
        });
    }

    @CommandAlias("staffmode|h|staff|modmode|mod")
    @CommandPermission(CoreConstants.STAFF_PERM)
    @CommandCompletion("@players")
    public void staffMode(CommandSender sender, @Name("player") @Optional OnlinePlayer target) {
        if (sender instanceof Player senderPlayer && target == null) {
            target = new OnlinePlayer(senderPlayer);
        }

        if (target == null) return;

        if (target.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            target.getPlayer().setGameMode(GameMode.CREATIVE);
            return;
        }

        if (CorePlugin.getInstance().getModModeHandler().isActive(target.getPlayer())) {
            CorePlugin.getInstance().getModModeHandler().deactivate(target.getPlayer());
            return;
        }

        CorePlugin.getInstance().getModModeHandler().activate(target.getPlayer());
    }

    @CommandAlias("vanish|v")
    @CommandPermission(CoreConstants.STAFF_PERM)
    @CommandCompletion("@players")
    public void vanish(CommandSender sender, @Name("player") @Optional OnlinePlayer target) {
        if (sender instanceof Player senderPlayer && target == null) {
            target = new OnlinePlayer(senderPlayer);
        }

        if (target == null) return;

        if (CorePlugin.getInstance().getModModeHandler().isVanished(target.getPlayer())) {
            CorePlugin.getInstance().getModModeHandler().deactivateVanish(target.getPlayer());
            target.getPlayer().sendMessage(CC.translate("&aYour vanish is no longer active."));
            return;
        }

        CorePlugin.getInstance().getModModeHandler().activateVanish(target.getPlayer());
        target.getPlayer().sendMessage(CC.translate("&aYour vanish is now activated."));
    }

    @CommandAlias("freeze|ss")
    @CommandPermission(CoreConstants.STAFF_PERM)
    public void freeze(CommandSender sender, @Name("player") OnlinePlayer target) {
        if (target.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            target.getPlayer().setGameMode(GameMode.CREATIVE);
            return;
        }

        if (CorePlugin.getInstance().getModModeHandler().isFrozen(target.getPlayer())) {
            CorePlugin.getInstance().getModModeHandler().unfreeze(target.getPlayer());
            sender.sendMessage(CC.translate("<blend:&2;&a>Successfully unfroze " + target.getPlayer().getName() + ".</>"));
            return;
        }

        CorePlugin.getInstance().getModModeHandler().freeze(target.getPlayer());
        sender.sendMessage(CC.translate("<blend:&2;&a>Successfully froze " + target.getPlayer().getName() + ".</>"));
    }

    @CommandAlias("onlinestaff")
    @CommandPermission(CoreConstants.STAFF_PERM)
    public void onlinestaff(Player sender) {
        new ViewOnlineStaffMenu().openMenu(sender);
    }

}
