package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.log.model.impl.rank.RankCreationLog;
import dev.minechase.core.api.log.model.impl.rank.RankDeletionLog;
import dev.minechase.core.api.permission.packet.PlayerUpdatePermissionPacket;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.rank.packet.RankDeletePacket;
import dev.minechase.core.api.rank.packet.RankUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;

@CommandAlias("rank")
@CommandPermission("core.command.rank")
public class RankCommand extends BaseCommand {

    private final RankHandler rankHandler = CorePlugin.getInstance().getRankHandler();

    @Subcommand("create")
    public void create(CommandSender sender, @Name("name") @Single String rankName) {
        if (this.rankHandler.getRank(rankName) != null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Rank Error] A rank with the name '" + rankName + "' already exists.</>"));
            return;
        }

        Rank rank = new Rank(rankName);
        String senderName = CommandUtil.getSenderName(sender);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate("<blend:&6;&e>[Rank Handler]</>&a " + senderName + " created the '" + rank.getName() + "' rank!")).send();
        new RankCreationLog(CommandUtil.getSender(sender), rank).createLog();
    }

    @Subcommand("delete")
    @CommandCompletion("@ranks")
    public void delete(CommandSender sender, @Name("rank") Rank rank) {
        String senderName = CommandUtil.getSenderName(sender);

        new RankDeletePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&c " + senderName + " deleted the '" + rank.getName() + "' rank!"
        )).send();
        this.rankHandler.deleteRank(rank);
        new RankDeletionLog(CommandUtil.getSender(sender), rank).createLog();
    }

    @Subcommand("displayname")
    @CommandCompletion("@ranks <text>")
    public void displayName(CommandSender sender, @Name("rank") Rank rank, @Name("displayName") String displayName) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setDisplayName(displayName);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank display name! &7(" + rank.getDisplayName() + "&7)"
        )).send();
    }

    @Subcommand("disguiseRank")
    @CommandCompletion("@ranks")
    public void disguiseRank(CommandSender sender, @Name("rank") Rank rank) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setDisguiseRank(!rank.isDisguiseRank());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank disguise rank! &7(" + (rank.isDisguiseRank() ? "&a&lYES" : "&c&lNO") + "&7)"
        )).send();
    }

    @Subcommand("staffRank")
    @CommandCompletion("@ranks")
    public void staffRank(CommandSender sender, @Name("rank") Rank rank) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setStaffRank(!rank.isStaffRank());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank staff rank! &7(" + (rank.isStaffRank() ? "&a&lYES" : "&c&lNO") + "&7)"
        )).send();
    }

    @Subcommand("defaultRank")
    @CommandCompletion("@ranks")
    public void defaultRank(CommandSender sender, @Name("rank") Rank rank) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setDefaultRank(!rank.isDefaultRank());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank default rank! &7(" + (rank.isDefaultRank() ? "&a&lYES" : "&c&lNO") + "&7)"
        )).send();
    }

    @Subcommand("discordrole")
    @CommandCompletion("@ranks <role-id>")
    public void discordid(CommandSender sender, @Name("rank") Rank rank, @Name("discordId") String discordId) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setDiscordRoleId(discordId);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank discord id! &7(" + rank.getDiscordRoleId() + "&7)"
        )).send();
    }

    @Subcommand("prefix")
    @CommandCompletion("@ranks <text>")
    public void prefix(CommandSender sender, @Name("rank") Rank rank, @Name("prefix") String prefix) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setPrefix(prefix);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank prefix! &7(" + rank.getPrefix() + "&7)"
        )).send();
    }

    @Subcommand("suffix")
    @CommandCompletion("@ranks <text>")
    public void suffix(CommandSender sender, @Name("rank") Rank rank, @Name("suffix") String suffix) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setSuffix(suffix);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank suffix! &7(" + rank.getSuffix() + "&7)"
        )).send();
    }

    @Subcommand("primary")
    @CommandCompletion("@ranks <color>")
    public void primary(CommandSender sender, @Name("rank") Rank rank, @Name("displayName") String primary) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setPrimaryColor(primary.toLowerCase());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank primary color!"
        )).send();
    }

    @Subcommand("secondary")
    @CommandCompletion("@ranks <text>")
    public void secondary(CommandSender sender, @Name("rank") Rank rank, @Name("displayName") String secondary) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setSecondaryColor(secondary.toLowerCase());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank secondary color!"
        )).send();
    }

    @Subcommand("weight")
    @CommandCompletion("@ranks <number>")
    public void weight(CommandSender sender, @Name("rank") Rank rank, @Name("weight") int weight) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.setWeight(weight);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank weight! &7(&b" + rank.getWeight() + "&7)"
        )).send();
    }

    @Subcommand("permission add")
    @CommandCompletion("@ranks <permissionNode> @durations @scopes")
    public void permissionAdd(CommandSender sender, @Name("rank") Rank rank, @Name("permissionNode") String permissionNode, @Name("duration") @Optional TimeDuration duration, @Name("scope") @Optional MultiScope scope) {
        if (rank.hasPermission(permissionNode)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Rank Error] The '" + rank.getName() + "' rank already has that permission.</>"));
            return;
        }

        if (duration == null) duration = new TimeDuration("perm");
        if (scope == null) scope = new MultiScope("GLOBAL");

        String senderName = CommandUtil.getSenderName(sender);

        rank.getPermissions().add(new ScopedPermission(CommandUtil.getSender(sender), null, permissionNode, duration.transform(), CorePlugin.getInstance().getServerName(), "Rank Edit", scope));

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank permissions! &7(&a+" + permissionNode + "&7)"
        )).send();

        CorePlugin.getInstance().getUserHandler().fetchUsersAsync().whenCompleteAsync((users, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            users.stream().filter(user -> user.getRank() != null && (user.getRank().equals(rank) || rank.getValidInheritedRanks().contains(rank.getId()))).forEach(user -> {
                new PlayerUpdatePermissionPacket(user.getUniqueId()).send();
            });
        });
    }

    @Subcommand("permission remove")
    @CommandCompletion("@ranks @rankPermissions")
    public void permissionRemove(CommandSender sender, @Name("rank") Rank rank, @Name("permissionNode") String permissionNode) {
        ScopedPermission permission = rank.getPermission(permissionNode);

        if (permission == null) {
            sender.sendMessage(CC.translate("<blend:&4;&c>[Rank Error] The '" + rank.getName() + "' rank doesn't have that permission.</>"));
            return;
        }

        String senderName = CommandUtil.getSenderName(sender);

        rank.getPermissions().remove(permission);

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank permissions! &7(&c-" + permissionNode + "&7)"
        )).send();

        CorePlugin.getInstance().getUserHandler().fetchUsersAsync().whenCompleteAsync((users, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            users.stream().filter(user -> user.getRank() != null && (user.getRank().equals(rank) || rank.getValidInheritedRanks().contains(rank.getId()))).forEach(user -> {
                new PlayerUpdatePermissionPacket(user.getUniqueId()).send();
            });
        });
    }

    @Subcommand("scope set")
    @CommandCompletion("@ranks @scopes")
    public void scopeSet(CommandSender sender, @Name("rank") Rank rank, @Name("scope") MultiScope scope) {
        String senderName = CommandUtil.getSenderName(sender);

        rank.getScopes().clear();
        rank.getScopes().addAll(scope.getScopes());

        new RankUpdatePacket(rank).send();
        new StaffMessagePacket(CC.translate(
                "<blend:&6;&e>[Rank Handler]</>&a " + senderName + " updated the '" + rank.getName() + "' rank scopes! &7(&d" + StringUtils.join(scope.getScopes(), ", ") + "&7)"
        )).send();
    }

}
