package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.hologram.HologramHandler;
import dev.minechase.core.bukkit.hologram.model.SerializableHologram;
import org.bukkit.entity.Player;

import java.util.Collections;

@CommandAlias("hologram|hg|holo|holograms")
@CommandPermission("commons.command.hologram")
public class HologramCommand extends BaseCommand {

    private final HologramHandler hologramHandler = CorePlugin.getInstance().getHologramHandler();

    @Subcommand("create")
    @CommandCompletion("<name>")
    public void create(Player sender, @Name("name") String name) {
        if (this.hologramHandler.getHolograms().containsKey(name)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>A hologram with the name '" + name + "' already exists.</>"));
            return;
        }

        SerializableHologram hologram = new SerializableHologram(name, sender.getLocation(), Collections.singletonList("Example Line"));

        sender.sendMessage(CC.translate("<blend:&2;&a>Created a new hologram with the name '" + hologram.getId() + "'</>"));
    }

    @Subcommand("delete")
    @CommandCompletion("@holograms")
    public void delete(Player sender, @Name("name") SerializableHologram hologram) {
        hologram.delete();
        sender.sendMessage(CC.translate("<blend:&4;&c>Deleted the '" + hologram.getId() + "' hologram.</>"));
    }

    @Subcommand("info")
    @CommandCompletion("@holograms")
    public void info(Player sender, @Name("name") SerializableHologram hologram) {
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("<blend:&6;&e>&l" + hologram.getId() + " Info</>"));
        sender.sendMessage(CC.translate("&eLocation: &f" + LocationUtils.toString(hologram.getLocation())));
        sender.sendMessage(CC.translate("&eWorld: &f" + hologram.getLocation().getWorld().getName()));
        sender.sendMessage(CC.translate("&eLines:"));
        for (int i = 0; i < hologram.getLines().size(); i++) {
            sender.sendMessage(CC.translate("&e" + i + ") &f") + hologram.getLines().get(i).getText());
        }
        sender.sendMessage(" ");
    }

    @Subcommand("tp|teleport")
    @CommandCompletion("@holograms")
    public void teleport(Player sender, @Name("name") SerializableHologram hologram) {
        sender.teleport(hologram.getLocation());
    }

    @Subcommand("movehere|tphere")
    @CommandCompletion("@holograms")
    public void movehere(Player sender, @Name("name") SerializableHologram hologram) {
        hologram.move(sender.getLocation());
        sender.sendMessage(CC.translate("<blend:&5;&d>Moved the '" + hologram.getId() + "' hologram to your location.</>"));
    }

    @Subcommand("line add")
    @CommandCompletion("@holograms <text>")
    public void lineAdd(Player sender, @Name("name") SerializableHologram hologram, @Name("text") String text) {
        hologram.addLine(text);
        sender.sendMessage(CC.translate("<blend:&2;&a>Added a new line to the '" + hologram.getId() + "' hologram.</>"));
    }

    @Subcommand("line remove")
    @CommandCompletion("@holograms @hologramLines")
    public void lineRemove(Player sender, @Name("name") SerializableHologram hologram, @Name("index") int index) {
        boolean withinBounds = hologram.removeLine(index);

        if (!withinBounds) {
            sender.sendMessage(CC.translate("<blend:&4;&c>The '" + hologram.getId() + "' hologram doesn't have that line..</>"));
            return;
        }

        sender.sendMessage(CC.translate("<blend:&4;&c>Removed line #" + index + " for the '" + hologram.getId() + "' hologram.</>"));
    }

    @Subcommand("line set")
    @CommandCompletion("@holograms @hologramLines <text>")
    public void lineSet(Player sender, @Name("name") SerializableHologram hologram, @Name("index") int index, @Name("text") String text) {
        boolean withinBounds = hologram.setLine(index, text);

        if (!withinBounds) {
            sender.sendMessage(CC.translate("<blend:&4;&c>The '" + hologram.getId() + "' hologram doesn't have that line..</>"));
            return;
        }

        sender.sendMessage(CC.translate("<blend:&6;&e>Updated line #" + index + " for the '" + hologram.getId() + "' hologram.</>"));
    }

}