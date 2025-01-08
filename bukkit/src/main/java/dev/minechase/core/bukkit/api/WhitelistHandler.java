package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WhitelistHandler implements IModule, Listener {

    @Override
    public void load() {
        CorePlugin.getInstance().getServer().getPluginManager().registerEvents(this, CorePlugin.getInstance());
    }

    @Override
    public void unload() {

    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (isWhitelisted()) {
            if (isEnforcingBoth()) {
                boolean allowed = this.checkName(event);

                if (allowed) return;

                this.checkRank(event);
            } else if (isEnforcingName()) {
                this.checkName(event);
            } else if (isEnforcingRank()) {
                this.checkRank(event);
            }
        }
    }

    public boolean checkName(AsyncPlayerPreLoginEvent event) {
        if (isNameBypassed(event.getName())) return true;

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
        event.setKickMessage(CC.translate(StringUtils.join(Arrays.asList(
                "&cYou are not whitelisted on " + CoreAPI.getInstance().getServerName() + ".",
                "&cIt is only available to " + this.getRankRequired().getDisplayName() + "&c ranks."
        ), "\n")));

        return false;
    }

    public boolean checkRank(AsyncPlayerPreLoginEvent event) {
        User user = CorePlugin.getInstance().getUserHandler().getUser(event.getUniqueId());

        if (user.getRank() != null && this.getRankRequired() != null && (user.getRank().getWeight() <= this.getRankRequired().getWeight())) return true;

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
        event.setKickMessage(CC.translate(StringUtils.join(Arrays.asList(
                "&cYou are not whitelisted on " + CoreAPI.getInstance().getServerName() + ".",
                "&cIt is only available to " + this.getRankRequired().getDisplayName() + "&c ranks."
        ), "\n")));

        return false;
    }

    public void setEnforce(String enforce) {
        CorePlugin.getInstance().getConfig().set("whitelist.enforce", enforce.toUpperCase());
        CorePlugin.getInstance().saveConfig();
    }

    public void updateRank(Rank rank) {
        CorePlugin.getInstance().getConfig().set("whitelist.rank", rank.getName());
        CorePlugin.getInstance().saveConfig();
    }

    public void addToBypassList(String name) {
        List<String> names = new ArrayList<>(this.getBypassList());

        if (isNameBypassed(name)) return;

        names.add(name);
        CorePlugin.getInstance().getConfig().set("whitelist.players", names);
        CorePlugin.getInstance().saveConfig();
    }

    public void removeFromBypassList(String name) {
        List<String> names = new ArrayList<>(this.getBypassList());

        if (!isNameBypassed(name)) return;

        names.remove(name);
        CorePlugin.getInstance().getConfig().set("whitelist.players", names);
        CorePlugin.getInstance().saveConfig();
    }

    public boolean isNameBypassed(String name) {
        return this.getBypassList().stream().anyMatch(s -> s.equalsIgnoreCase(name));
    }

    public boolean isWhitelisted() {
        return CorePlugin.getInstance().getConfig().getBoolean("whitelist.enabled");
    }

    public Rank getRankRequired() {
        return CorePlugin.getInstance().getRankHandler().getRank(CorePlugin.getInstance().getConfig().getString("whitelist.rank"));
    }

    public List<String> getBypassList() {
        return CorePlugin.getInstance().getConfig().getStringList("whitelist.players");
    }

    public boolean isEnforcingName() {
        return CorePlugin.getInstance().getConfig().getString("whitelist.enforce").equalsIgnoreCase("player");
    }

    public boolean isEnforcingRank() {
        return CorePlugin.getInstance().getConfig().getString("whitelist.enforce").equalsIgnoreCase("rank");
    }

    public boolean isEnforcingBoth() {
        return CorePlugin.getInstance().getConfig().getString("whitelist.enforce").equalsIgnoreCase("both");
    }

}
