package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;

@CommandAlias("undisguise")
public class UnDisguiseCommand extends BaseCommand {

    @Default
    public void undisguise(Player sender) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(sender.getUniqueId());

        if (!user.isDisguised()) {
            sender.sendMessage(CC.translate("<blend:&4;&c>You are not disguised.</>"));
            return;
        }

        CorePlugin.getInstance().getUserHandler().undisguise(sender);
        sender.sendMessage(CC.translate("<blend:&2;&a>You are no longer disguised.</>"));
    }


}
