package dev.minechase.core.bukkit.settings;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.bukkit.settings.model.ISetting;
import dev.minechase.core.bukkit.settings.model.impl.AdminChatSetting;
import dev.minechase.core.bukkit.settings.model.impl.GlobalChatSetting;
import dev.minechase.core.bukkit.settings.model.impl.PrivateMessageSetting;
import dev.minechase.core.bukkit.settings.model.impl.StaffChatSetting;
import lombok.Getter;

import java.util.*;

@Getter
public class SettingsHandler implements IModule {

    private final Map<String, ISetting> settings;

    public SettingsHandler() {
        this.settings = new HashMap<>();
    }

    @Override
    public void load() {
        this.registerSetting(new GlobalChatSetting());
        this.registerSetting(new PrivateMessageSetting());
        this.registerSetting(new StaffChatSetting());
        this.registerSetting(new AdminChatSetting());
    }

    @Override
    public void unload() {

    }

    public List<ISetting> getSortedSettings() {
        return this.settings.values().stream().sorted(Comparator.comparingInt(ISetting::getPriority)).toList();
    }

    public <T extends ISetting> T getSetting(Class<T> clazz) {
        return (T) this.settings.values().stream().filter(setting -> setting.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public void registerSetting(ISetting setting) {
        if (this.settings.containsKey(setting.getId())) return;

        this.settings.put(setting.getId(), setting);
        CoreAPI.getInstance().getLogger().info("[Settings Handler] Loading " + setting.getId() + " setting.");
    }

}
