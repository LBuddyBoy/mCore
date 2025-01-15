package dev.minechase.core.bukkit.npc;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Config;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.command.context.NPCContext;
import dev.minechase.core.bukkit.command.impl.NPCCommand;
import dev.minechase.core.bukkit.hologram.model.HologramLine;
import dev.minechase.core.bukkit.npc.listener.NPCListener;
import dev.minechase.core.bukkit.npc.model.CustomNPC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NPCHandler implements IModule {

    private final Map<String, CustomNPC> npcs;
    private final Map<Integer, CustomNPC> npcById;
    private final File directory;

    public NPCHandler() {
        this.npcs = new ConcurrentHashMap<>();
        this.npcById = new ConcurrentHashMap<>();
        this.directory = new File(CorePlugin.getInstance().getDataFolder(), "npcs");

        if (!this.directory.exists()) this.directory.mkdir();
    }

    @Override
    public void load() {
        if (!CorePlugin.getInstance().getConfig().getBoolean("npcs", false)) return;

        new NPCContext().register(CorePlugin.getInstance().getCommandHandler().getCommandManager());

        CorePlugin.getInstance().getCommandHandler().getCommandManager().getCommandCompletions().registerCompletion("npcLines", (ctx) -> {
            CustomNPC npc = ctx.getContextValue(CustomNPC.class);

            return npc.getHologram().getLines().stream().map(HologramLine::getIndex).map(String::valueOf).toList();
        });

        CorePlugin.getInstance().getCommandHandler().getCommandManager().registerCommand(new NPCCommand());
        CorePlugin.getInstance().getServer().getPluginManager().registerEvents(new NPCListener(), CorePlugin.getInstance());

        for (String fileName : this.directory.list()) {
            fileName = fileName.replaceAll(".yml", "");
            Config config = new Config(CorePlugin.getInstance(), fileName, this.directory);
            CustomNPC npc = new CustomNPC(config);
            World world = Bukkit.getWorld(npc.getWorld());

            this.npcs.put(fileName, npc);

            if (world != null) npc.spawnNPC();
        }
    }

    @Override
    public void unload() {
        this.npcs.values().forEach(CustomNPC::despawnNPC);
    }

}