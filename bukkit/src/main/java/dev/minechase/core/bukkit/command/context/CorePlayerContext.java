package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.bukkit.model.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class CorePlayerContext extends CommonCommandContext<CorePlayer> {

    public CorePlayerContext() {
        super("players", CorePlayer.class);
    }

    @Override
    public CorePlayer getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();

        if (UUIDCache.getNamesToUuids().containsKey(source.toLowerCase())) {
            UUID uuid = UUIDCache.getNamesToUuids().get(source.toLowerCase());

            return new CorePlayer(uuid, UUIDCache.getUuidToNames().get(uuid));
        }

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No player with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
