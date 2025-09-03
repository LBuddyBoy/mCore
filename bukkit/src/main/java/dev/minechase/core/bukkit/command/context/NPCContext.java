package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.npc.model.CustomNPC;
import dev.minechase.core.bukkit.npc.model.INPC;

import java.util.Collection;
import java.util.List;

public class NPCContext extends CommonCommandContext<CustomNPC> {

    public NPCContext() {
        super("npcs", CustomNPC.class);
    }

    @Override
    public CustomNPC getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        INPC npc = CorePlugin.getInstance().getNpcHandler().getNpcs().get(source.toLowerCase());

        if (npc instanceof CustomNPC) {
            return (CustomNPC) npc;
        }

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>A npc with the name '" + source + "' doesn't exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CorePlugin.getInstance().getNpcHandler().getNpcs().values().stream().filter(npc -> npc instanceof CustomNPC).map(INPC::getName).toList();
    }

    public static class NPCLinesCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            CustomNPC npc = context.getContextValue(CustomNPC.class);

            return npc.getHologram().getLines().stream().map(HologramLine::getIndex).map(String::valueOf).toList();
        }

    }

}