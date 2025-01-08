package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.command.CommandSender;

@CommandAlias("whitelist|wl|wlist")
public class WhitelistCommand extends BaseCommand {

    @Subcommand("on|enable")
    @CommandCompletion("@players")
    public void on(CommandSender sender) {
        CorePlugin.getInstance().getConfig().set("whitelist.enabled", true);
        CorePlugin.getInstance().saveConfig();

        sender.sendMessage(CC.translate("&aSuccessfully turned on whitelist."));
    }

    @Subcommand("off|disable")
    @CommandCompletion("@players")
    public void off(CommandSender sender) {
        CorePlugin.getInstance().getConfig().set("whitelist.enabled", false);
        CorePlugin.getInstance().saveConfig();

        sender.sendMessage(CC.translate("&aSuccessfully turned off whitelist."));
    }

    @Subcommand("add")
    @CommandCompletion("@players")
    public void add(CommandSender sender, @Name("playerName") String playerName) {
        if (CorePlugin.getInstance().getWhitelistHandler().isNameBypassed(playerName)) {
            sender.sendMessage(CC.translate("&c" + playerName + " is already on the bypass list."));
            return;
        }

        CorePlugin.getInstance().getWhitelistHandler().addToBypassList(playerName);
        sender.sendMessage(CC.translate("&aSuccessfully added " + playerName + " to the bypass list."));
    }

    @Subcommand("remove")
    @CommandCompletion("@players")
    public void remove(CommandSender sender, @Name("playerName") String playerName) {
        if (!CorePlugin.getInstance().getWhitelistHandler().isNameBypassed(playerName)) {
            sender.sendMessage(CC.translate("&c" + playerName + " is not on the bypass list."));
            return;
        }

        CorePlugin.getInstance().getWhitelistHandler().removeFromBypassList(playerName);
        sender.sendMessage(CC.translate("&aSuccessfully removed " + playerName + " from the bypass list."));
    }

    @Subcommand("list|info")
    @CommandCompletion("@ranks")
    public void list(CommandSender sender) {
        boolean showRank = CorePlugin.getInstance().getWhitelistHandler().isEnforcingRank() || CorePlugin.getInstance().getWhitelistHandler().isEnforcingBoth();
        boolean showName = CorePlugin.getInstance().getWhitelistHandler().isEnforcingName() || CorePlugin.getInstance().getWhitelistHandler().isEnforcingBoth();

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&eWhitelisted&7: &f" + (CorePlugin.getInstance().getWhitelistHandler().isWhitelisted() ? "&aYes" : "&cNo")));
        sender.sendMessage(CC.translate("&eEnforcement&7: &f" + CorePlugin.getInstance().getConfig().getString("whitelist.enforce")));

        if (showRank) {
            Rank rank = CorePlugin.getInstance().getWhitelistHandler().getRankRequired();
            sender.sendMessage(CC.translate("&eRank&7: &f" + (rank == null ? "&cNone" : rank.getName())));
        }

        if (showName) {
            sender.sendMessage(CC.translate("&ePlayers&7: &f"));
            for (String name : CorePlugin.getInstance().getWhitelistHandler().getBypassList()) {
                sender.sendMessage(CC.translate("&7- &f" + name));
            }
        }
    }

    @Subcommand("setrank")
    @CommandCompletion("@ranks")
    public void ranks(CommandSender sender, @Name("rank") Rank rank) {
        CorePlugin.getInstance().getWhitelistHandler().updateRank(rank);
        sender.sendMessage(CC.translate("&aSuccessfully set " + rank.getName() + " as the whitelist rank."));
    }

    @Subcommand("enforce")
    @CommandCompletion("player,rank,both")
    public void enforce(CommandSender sender, @Name("type") String enforce) {
        if (!enforce.equalsIgnoreCase("player") && !enforce.equalsIgnoreCase("rank") && !enforce.equalsIgnoreCase("both")) {
            sender.sendMessage(CC.translate("&cInvalid enforcement provided."));
            return;
        }

        CorePlugin.getInstance().getWhitelistHandler().setEnforce(enforce);
        sender.sendMessage(CC.translate("&aSuccessfully set " + enforce + " as the whitelist enforcement."));
    }

}
