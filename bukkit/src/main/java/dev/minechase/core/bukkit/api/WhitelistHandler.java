package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.bukkit.CorePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhitelistHandler implements IModule {

    @Override
    public void load() {

    }

    @Override
    public void unload() {

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
        return !isEnforcingName();
    }

}
