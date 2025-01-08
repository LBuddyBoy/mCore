package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class UUIDContext extends CommonCommandContext<UUID> {

    public UUIDContext() {
        super("uuids", UUID.class);
    }

    @Override
    public UUID getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        UUID uuid = UUIDCache.getNamesToUuids().get(source.toLowerCase());

        if (uuid != null) return uuid;

        throw new InvalidCommandArgument(CoreConstants.INVALID_NAME(new AsyncCorePlayer(source)));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return UUIDCache.getUuidToNames().values();
    }
}
