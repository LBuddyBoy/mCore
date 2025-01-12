package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.note.model.Note;
import dev.minechase.core.api.note.packet.NoteUpdatePacket;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewGrantsMenu;
import dev.minechase.core.bukkit.menu.ViewNotesMenu;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("note|notes")
@CommandPermission("core.command.note")
public class NoteCommand extends BaseCommand {

    @Default
    @Subcommand("check")
    @CommandCompletion("@players")
    public void check(Player sender, @Name("player") AsyncCorePlayer player) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s notes, this may take a few seconds..."));

            CorePlugin.getInstance().getNoteHandler().getSortedNotes(uuid).whenCompleteAsync(((notes, throwable) -> {
                new ViewNotesMenu(uuid, notes).openMenu(sender);
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

    @Subcommand("add")
    @CommandCompletion("@players <message>")
    public void add(CommandSender sender, @Name("player") AsyncCorePlayer player, @Name("message") String message) {
        player.getUUID().whenCompleteAsyncExcept(uuid -> {
            sender.sendMessage(CC.translate("&aAdding a note to " + player.getName() + ", this may take a few seconds..."));

            Note note = new Note(
                    CommandUtil.getSender(sender),
                    uuid,
                    message
            );

            new NoteUpdatePacket(note).send();
        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
