package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.packet.SyncCodeUpdatePacket;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("sync")
public class SyncCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        CorePlugin.getInstance().getSyncHandler().getSyncInformation(sender.getUniqueId()).whenCompleteAsync(((information, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (information != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>Your account is already synced to: '" + information.getDiscordMemberId() + "'</>"));
                return;
            }

            SyncCode syncCode = CorePlugin.getInstance().getSyncHandler().getSyncCode(sender.getUniqueId());

            if (syncCode != null) {
                sender.sendMessage(CC.translate("<blend:&4;&c>You already have a sync code: " + syncCode.getCode() + "</>"));
                return;
            }

            syncCode = new SyncCode(sender.getUniqueId(), generateCode());

            Arrays.asList(
                    " ",
                    "<blend:&6;&e>&lHow to Sync Account</>",
                    "&eStep #1 &fJoin discord.gg/minechase",
                    "&eStep #2 &fGo to #bot-commands",
                    "&eStep #3 &fType /sync " + syncCode.getCode(),
                    " ",
                    "&fAfter doing this your discord account will be synced",
                    "&fto your Minecraft Account!",
                    " "
            ).forEach(s -> sender.sendMessage(CC.translate(s)));

            new SyncCodeUpdatePacket(syncCode).send();
        }));
    }

    public int generateCode() {
        int random = ThreadLocalRandom.current().nextInt(99999);

        if (CorePlugin.getInstance().getSyncHandler().getSyncCode(random) != null) return generateCode();

        return random;
    }

}
