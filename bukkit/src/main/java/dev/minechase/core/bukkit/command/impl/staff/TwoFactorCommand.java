package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.totp.DisclaimerPrompt;
import dev.minechase.core.bukkit.util.totp.TotpUtil;
import dev.minechase.core.bukkit.util.totp.TwoFactorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.security.GeneralSecurityException;

public class TwoFactorCommand extends BaseCommand {

    @CommandAlias("2fasetup|setup2fa")
    @CommandPermission(CoreConstants.STAFF_PERM)
    public void setup2fa(Player sender) {
        Tasks.runAsync(() -> {
            User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

            if (user.getPersistentMetadata().getBooleanOrDefault(CoreConstants.TOTP_SETUP_KEY, false)) {
                sender.sendMessage(CC.translate("&cYou already have 2FA setup!"));
                return;
            }

            ConversationFactory factory = new ConversationFactory(CorePlugin.getInstance())
                    .withFirstPrompt(new DisclaimerPrompt())
                    .withLocalEcho(false)
                    .thatExcludesNonPlayersWithMessage("No console!");

            sender.beginConversation(factory.buildConversation(sender));
        });
    }

    @CommandAlias("auth")
    @CommandPermission(CoreConstants.STAFF_PERM)
    public void auth(Player sender, @Name("code") int code) {
        if (!TwoFactorUtil.isLocked(sender)) {
            sender.sendMessage(CC.translate("&cYou don't need to authenticate yourself."));
            return;
        }

        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (!user.getPersistentMetadata().getBooleanOrDefault(CoreConstants.TOTP_SETUP_KEY, false)) {
            sender.sendMessage(CC.translate("&cCouldn't verify your identity. Check the code you entered and try again. Error Code: 10"));
            return;
        }

        if (!user.getPersistentMetadata().contains(CoreConstants.TOTP_SECRET_KEY)) {
            sender.sendMessage(CC.translate("&cCouldn't verify your identity. Check the code you entered and try again. Error Code: 20"));
            return;
        }

        if (!user.getPersistentMetadata().contains(CoreConstants.TOTP_CODE_KEY)) {
            sender.sendMessage(CC.translate("&cCouldn't verify your identity. Check the code you entered and try again. Error Code: 30"));
            return;
        }

        Tasks.runAsync(() -> {
            String secret = user.getPersistentMetadata().get(CoreConstants.TOTP_SECRET_KEY);
            boolean valid = false;

            try {
                valid = TotpUtil.validateCurrentNumber(secret, code, 250);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return;
            }

            if (valid) {
                TwoFactorUtil.release(sender);
                sender.sendMessage(CC.translate("&aYour identity has been verified."));
            } else {
                sender.sendMessage(CC.translate("&aCouldn't verify your identity. Check the code you entered and try again. Error Code: 40"));
            }
        });
    }

    @CommandAlias("reset2fa")
    @CommandPermission(CoreConstants.ADMIN_PERM)
    public void reset2fa(CommandSender sender, @Name("player") AsyncCorePlayer player) {
        player.getUser().whenCompleteAsyncExcept(user -> {
            user.getPersistentMetadata().remove(CoreConstants.TOTP_SECRET_KEY);
            user.getPersistentMetadata().remove(CoreConstants.TOTP_SETUP_KEY);
            user.getPersistentMetadata().remove(CoreConstants.TOTP_CODE_KEY);

            user.save(true);

            sender.sendMessage(CC.translate("&aReset " + user.getName() + "'s 2fa data."));
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
