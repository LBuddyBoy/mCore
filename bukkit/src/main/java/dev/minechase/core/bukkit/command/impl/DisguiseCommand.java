package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.SkinAPI;
import dev.lbuddyboy.commons.Commons;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("disguise")
public class DisguiseCommand extends BaseCommand {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    @Subcommand("player")
    public void asPlayer(Player sender, @Name("name") String name, @Name("skin") @Optional AsyncCorePlayer other) {
        long time = System.currentTimeMillis();
        Disguise.Builder builder = Disguise.builder().setName(name);

        if (other != null) {
            other.getUUID().whenCompleteExcept(uuid -> {
                builder.setSkin(SkinAPI.MOJANG, uuid);

                provider.disguise(sender, builder.build());
                sender.sendMessage(CC.translate("&a(done in " + (System.currentTimeMillis() - time) + "ms)"));
            }, (throwable -> sender.sendMessage(CC.translate("<blend:&4;&c>No skin with the name '" + other.getName() + "' exists.</>"))));
            return;
        }

        provider.disguise(sender, builder.build());
        sender.sendMessage(CC.translate("&a(done in " + (System.currentTimeMillis() - time) + "ms)"));
    }
}
