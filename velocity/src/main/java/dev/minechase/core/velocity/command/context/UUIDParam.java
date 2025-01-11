package dev.minechase.core.velocity.command.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.VelocityCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import com.velocitypowered.api.proxy.Player;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.util.MojangUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDParam implements ContextResolver<UUID, VelocityCommandExecutionContext> {

    @Override
    public UUID getContext(VelocityCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.popFirstArg();

        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return sender.getUniqueId();
        }

        UUID uuid = CommonsAPI.getInstance().getUUIDCache().getUUID(source.toLowerCase());
        if (uuid != null) return uuid;

        CompletableFuture<MojangUser> uuidFuture = MojangUser.fetchAsync(source);
        MojangUser user = uuidFuture.thenApply(futureUser -> {
            CommonsAPI.getInstance().getUUIDCache().cache(futureUser.getUuid(), futureUser.getName(), true);
            return futureUser;
        }).exceptionally(e -> null).join();

        if (user != null) {
            return user.getUuid();
        }

        throw new InvalidCommandArgument("No player with the name " + source + " could be found.");
    }

}