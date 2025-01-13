package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.punishment.model.PunishmentType;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameModeContext extends CommonCommandContext<GameMode> {

    private static Map<String, GameMode> gameModeIds = new HashMap<>(){{
        put("s", GameMode.SURVIVAL);
        put("survival", GameMode.SURVIVAL);
        put("c", GameMode.CREATIVE);
        put("creative", GameMode.CREATIVE);
        put("a", GameMode.ADVENTURE);
        put("adv", GameMode.ADVENTURE);
        put("adventure", GameMode.ADVENTURE);
        put("spec", GameMode.SPECTATOR);
        put("sp", GameMode.SPECTATOR);
        put("spectator", GameMode.SPECTATOR);
    }};

    public GameModeContext() {
        super("gameModes", GameMode.class);
    }

    @Override
    public GameMode getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        GameMode gameMode = gameModeIds.get(source.toLowerCase());

        if (gameMode != null) return gameMode;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No gamemode with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return gameModeIds.keySet();
    }
}