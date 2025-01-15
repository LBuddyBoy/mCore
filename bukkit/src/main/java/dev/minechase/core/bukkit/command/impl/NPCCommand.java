package dev.minechase.core.bukkit.command.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.npc.model.CustomNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("npc")
@CommandPermission("commons.command.npc")
public class NPCCommand extends BaseCommand {

    @Subcommand("create")
    @CommandCompletion("<name>")
    public void create(Player sender, @Name("name") String name) {
        name = name.toLowerCase();

        if (CorePlugin.getInstance().getNpcHandler().getNpcs().containsKey(name)) {
            sender.sendMessage(CC.translate("&cThere's already a npc under the name '" + name + "'"));
            return;
        }

        new CustomNPC(name, sender.getLocation());
        sender.sendMessage(CC.translate("<blend:&2;&a>Created a new npc with the name '" + name + "'</>"));
    }

    @Subcommand("info")
    @CommandCompletion("@npcs")
    public void info(Player sender, @Name("npc") CustomNPC npc) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("<blend:&6;&e>&l" + npc.getName() + " NPC Info</>"));
        sender.sendMessage(CC.translate("&eLocation: &f" + LocationUtils.toString(npc.getSpawnLocation())));
        sender.sendMessage(CC.translate("&eWorld: &f" + npc.getWorld()));
        sender.sendMessage(CC.translate("&eHologram:"));
        for (int i = 0; i < npc.getHologramLines().size(); i++) {
            sender.sendMessage(CC.translate("&e" + i + ") &f") + npc.getHologramLines().get(i));
        }
        sender.sendMessage(" ");
    }

    @Subcommand("delete")
    @CommandCompletion("@npcs")
    public void delete(Player sender, @Name("npc") CustomNPC npc) {
        npc.delete();
        sender.sendMessage(CC.translate("<blend:&4;&c>Deleted the '" + npc.getName() + "' npc.</>"));
    }

    @Subcommand("tp|teleport")
    @CommandCompletion("@npcs")
    public void teleport(Player sender, @Name("name") CustomNPC npc) {
        sender.teleport(npc.getSpawnLocation());
    }

    @Subcommand("movehere|tphere")
    @CommandCompletion("@npcs")
    public void movehere(Player sender, @Name("name") CustomNPC npc) {
        npc.teleport(sender.getLocation());
        sender.sendMessage(CC.translate("<blend:&5;&d>Moved the '" + npc.getName() + "' npc to your location.</>"));
    }

    @Subcommand("center")
    @CommandCompletion("@npcs")
    public void center(Player sender, @Name("name") CustomNPC npc) {
        Location location = npc.getSpawnLocation().clone();

        location.setX(location.getBlockX() + 0.5D);
        location.setZ(location.getBlockZ() + 0.5D);

        npc.teleport(location);
        sender.sendMessage(CC.translate("<blend:&2;&a>Centered the '" + npc.getName() + "' npc to the center of the block.</>"));
    }

    @Subcommand("command")
    @CommandCompletion("@npcs <command>")
    public void command(Player sender, @Name("name") CustomNPC npc, @Name("command") String command) {
        npc.setRightClickCommand(command.equalsIgnoreCase("none") ? "" : command);
        sender.sendMessage(CC.translate("<blend:&2;&a>Updated the '" + npc.getName() + "' npc right click command.</>"));
    }

    @Subcommand("line add")
    @CommandCompletion("@npcs <text>")
    public void lineAdd(Player sender, @Name("name") CustomNPC npc, @Name("text") String text) {
        npc.getHologram().addLine(text);
        sender.sendMessage(CC.translate("<blend:&2;&a>Added a new line to the '" + npc.getName() + "' npc hologram.</>"));
    }

    @Subcommand("line remove")
    @CommandCompletion("@npcs @npcLines")
    public void lineRemove(Player sender, @Name("name") CustomNPC npc, @Name("index") int index) {
        boolean withinBounds = npc.getHologram().removeLine(index);

        if (!withinBounds) {
            sender.sendMessage(CC.translate("<blend:&4;&c>The '" + npc.getName() + "' npc hologram doesn't have that line..</>"));
            return;
        }

        sender.sendMessage(CC.translate("<blend:&4;&c>Removed line #" + index + " for the '" + npc.getName() + "' npc hologram.</>"));
    }

    @Subcommand("line set")
    @CommandCompletion("@npcs @npcLines <text>")
    public void lineSet(Player sender, @Name("name") CustomNPC npc, @Name("index") int index, @Name("text") String text) {
        boolean withinBounds = npc.getHologram().setLine(index, text);

        if (!withinBounds) {
            sender.sendMessage(CC.translate("<blend:&4;&c>The '" + npc.getName() + "' npc hologram doesn't have that line..</>"));
            return;
        }

        sender.sendMessage(CC.translate("<blend:&6;&e>Updated line #" + index + " for the '" + npc.getName() + "' npc hologram.</>"));
    }


}