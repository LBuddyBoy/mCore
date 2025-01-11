package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.prefix.packet.PrefixDeletePacket;
import dev.minechase.core.api.prefix.packet.PrefixUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.menu.ViewPrefixesMenu;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("prefix|prefixes")
public class PrefixCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new ViewPrefixesMenu().openMenu(sender);
    }

    @Subcommand("create")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("<name>")
    public void create(CommandSender sender, @Name("name") String name) {
        if (CorePlugin.getInstance().getPrefixHandler().getLocalPrefix(name) != null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>A prefix with the name '" + name + "' already exists.</>"));
            return;
        }

        String senderName = CommandUtil.getSenderName(sender);
        Prefix prefix = new Prefix(name);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " created the '" + prefix.getName() + "' prefix!"
        )).send();
        new PrefixUpdatePacket(prefix).send();
    }

    @Subcommand("delete")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("@prefixes")
    public void delete(CommandSender sender, @Name("prefix") Prefix prefix) {
        String senderName = CommandUtil.getSenderName(sender);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " deleted the '" + prefix.getName() + "' prefix!"
        )).send();
        new PrefixDeletePacket(prefix).send();
    }

    @Subcommand("name")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("@prefixes <name>")
    public void name(CommandSender sender, @Name("prefix") Prefix prefix, @Name("name") String name) {
        String senderName = CommandUtil.getSenderName(sender);

        prefix.setName(name);

        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " updated the '" + prefix.getName() + "' prefix name! &7(&b" + name + "&7)"
        )).send();
        new PrefixUpdatePacket(prefix).send();
    }

    @Subcommand("displayname")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("@prefixes <display>")
    public void display(CommandSender sender, @Name("prefix") Prefix prefix, @Name("displayName") String displayName) {
        String senderName = CommandUtil.getSenderName(sender);

        prefix.setDisplayName(displayName);

        new PrefixUpdatePacket(prefix).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " updated the '" + prefix.getName() + "' prefix display! &7(" + displayName + "&7)"
        )).send();
    }

    @Subcommand("prefix")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("@prefixes <text>")
    public void suffix(CommandSender sender, @Name("prefix") Prefix prefix, @Name("text") String prefixString) {
        String senderName = CommandUtil.getSenderName(sender);

        prefix.setPrefix(prefixString);

        new PrefixUpdatePacket(prefix).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " updated the '" + prefix.getName() + "' prefix prefix! &7(" + prefixString + "&7)"
        )).send();
    }

    @Subcommand("weight")
    @CommandPermission("core.command.prefix")
    @CommandCompletion("@prefixes <weight>")
    public void weight(CommandSender sender, @Name("prefix") Prefix prefix, @Name("weight") int weight) {
        String senderName = CommandUtil.getSenderName(sender);

        prefix.setWeight(weight);

        new PrefixUpdatePacket(prefix).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " updated the '" + prefix.getName() + "' prefix weight! &7(&b" + weight + "&7)"
        )).send();
    }

    @Subcommand("scope set")
    @CommandCompletion("@prefixes @scopes")
    @CommandPermission("core.command.prefix")
    public void scopeSet(CommandSender sender, @Name("prefix") Prefix prefix, @Name("scope") MultiScope scope) {
        String senderName = CommandUtil.getSenderName(sender);

        prefix.getScopes().clear();
        prefix.getScopes().addAll(scope.getScopes());

        new PrefixUpdatePacket(prefix).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&3;&b>[Prefix Handler]</>&a " + senderName + " updated the '" + prefix.getName() + "' prefix scopes! &7(&d" + StringUtils.join(scope.getScopes(), ", ") + "&7)"
        )).send();
    }

}
