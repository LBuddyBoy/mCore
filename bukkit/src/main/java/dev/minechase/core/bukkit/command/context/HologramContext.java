package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.hologram.model.IHologram;
import dev.minechase.core.bukkit.hologram.model.SerializableHologram;
import dev.minechase.core.bukkit.npc.model.CustomNPC;

import java.util.Collection;

public class HologramContext extends CommonCommandContext<SerializableHologram> {

    public HologramContext() {
        super("holograms", SerializableHologram.class);
    }

    @Override
    public SerializableHologram getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        IHologram hologram = CorePlugin.getInstance().getHologramHandler().getHolograms().get(source);

        if (hologram != null && hologram instanceof SerializableHologram serializableHologram) return serializableHologram;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>A hologram with the name '" + source + "' doesn't exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CorePlugin.getInstance().getHologramHandler().getHolograms().values().stream().filter(hologram -> hologram instanceof SerializableHologram).map(IHologram::getId).toList();
    }

    public static class HologramLinesCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            SerializableHologram hologram = context.getContextValue(SerializableHologram.class);

            return hologram.getLines().stream().map(HologramLine::getIndex).map(String::valueOf).toList();
        }

    }

}