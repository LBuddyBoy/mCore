package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.rank.packet.RankUpdatePacket;
import dev.minechase.core.api.tag.model.Tag;
import dev.minechase.core.api.tag.packet.TagDeletePacket;
import dev.minechase.core.api.tag.packet.TagUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewTagsMenu;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("tags|tag|suffixes|suffix")
public class TagCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new ViewTagsMenu().openMenu(sender);
    }

    @Subcommand("create")
    @CommandPermission("core.command.tag")
    @CommandCompletion("<name>")
    public void create(CommandSender sender, @Name("name") String name) {
        if (CorePlugin.getInstance().getTagHandler().getLocalTag(name) != null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>A tag with the name '" + name + "' already exists.</>"));
            return;
        }

        String senderName = CommandUtil.getSenderName(sender);
        Tag tag = new Tag(name);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " created the '" + tag.getName() + "' tag!"
        )).send();
        new TagUpdatePacket(tag).send();
    }

    @Subcommand("delete")
    @CommandPermission("core.command.tag")
    @CommandCompletion("@tags")
    public void delete(CommandSender sender, @Name("tag") Tag tag) {
        String senderName = CommandUtil.getSenderName(sender);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " deleted the '" + tag.getName() + "' tag!"
        )).send();
        new TagDeletePacket(tag).send();
    }

    @Subcommand("name")
    @CommandPermission("core.command.tag")
    @CommandCompletion("@tags <name>")
    public void name(CommandSender sender, @Name("tag") Tag tag, @Name("name") String name) {
        String senderName = CommandUtil.getSenderName(sender);

        tag.setName(name);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " updated the '" + tag.getName() + "' tag name! &7(&b" + name + "&7)"
        )).send();
        new TagUpdatePacket(tag).send();
    }

    @Subcommand("displayname")
    @CommandPermission("core.command.tag")
    @CommandCompletion("@tags <display>")
    public void display(CommandSender sender, @Name("tag") Tag tag, @Name("displayName") String displayName) {
        String senderName = CommandUtil.getSenderName(sender);

        tag.setDisplayName(displayName);

        new TagUpdatePacket(tag).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " updated the '" + tag.getName() + "' tag display! &7(" + displayName + "&7)"
        )).send();
    }

    @Subcommand("suffix")
    @CommandPermission("core.command.tag")
    @CommandCompletion("@tags <display>")
    public void suffix(CommandSender sender, @Name("tag") Tag tag, @Name("suffix") String suffix) {
        String senderName = CommandUtil.getSenderName(sender);

        tag.setSuffix(suffix);

        new TagUpdatePacket(tag).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " updated the '" + tag.getName() + "' tag suffix! &7(" + suffix + "&7)"
        )).send();
    }

    @Subcommand("weight")
    @CommandPermission("core.command.tag")
    @CommandCompletion("@tags <weight>")
    public void weight(CommandSender sender, @Name("tag") Tag tag, @Name("weight") int weight) {
        String senderName = CommandUtil.getSenderName(sender);

        tag.setWeight(weight);

        new TagUpdatePacket(tag).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " updated the '" + tag.getName() + "' tag weight! &7(&b" + weight + "&7)"
        )).send();
    }

    @Subcommand("scope set")
    @CommandCompletion("@tags @scopes")
    @CommandPermission("core.command.tag")
    public void scopeSet(CommandSender sender, @Name("tag") Tag tag, @Name("scope") MultiScope scope) {
        String senderName = CommandUtil.getSenderName(sender);

        tag.getScopes().clear();
        tag.getScopes().addAll(scope.getScopes());

        new TagUpdatePacket(tag).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Tag Handler]</>&a " + senderName + " updated the '" + tag.getName() + "' tag scopes! &7(&d" + StringUtils.join(scope.getScopes(), ", ") + "&7)"
        )).send();
    }

}
