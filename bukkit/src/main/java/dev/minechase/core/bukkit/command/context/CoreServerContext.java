package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.server.model.CoreServer;

import java.util.Arrays;
import java.util.Collection;

public class CoreServerContext extends CommonCommandContext<CoreServer> {

    public CoreServerContext() {
        super("servers", CoreServer.class);
    }

    @Override
    public CoreServer getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        CoreServer server = CoreAPI.getInstance().getServerHandler().getServer(source);

        if (server != null) return server;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No server with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CoreAPI.getInstance().getServerHandler().getServers().keySet();
    }
}
