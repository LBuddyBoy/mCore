package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.settings.model.impl.TipMessageSetting;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TipHandler implements IModule {

    private final List<TipMessage> tipMessages;

    private long lastSent, announceEvery;
    private int lastSentIndex = 0;

    public TipHandler() {
        this.tipMessages = new ArrayList<>();
    }

    @Override
    public void load() {
        reload();

        Tasks.runAsyncTimer(() -> {
            if (!CorePlugin.getInstance().getConfig().getBoolean("tips.enabled")) return;
            if (this.lastSent + this.announceEvery > System.currentTimeMillis()) return;

            TipMessageSetting messageSetting = CorePlugin.getInstance().getSettingsHandler().getSetting(TipMessageSetting.class);

            TipMessage tipMessage = this.tipMessages.get(this.lastSentIndex);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!messageSetting.isEnabled(player.getUniqueId())) continue;

                tipMessage.message().forEach(s -> player.sendMessage(CC.translate(s)));
            }

            this.lastSentIndex++;
            this.lastSent = System.currentTimeMillis();

            if (this.lastSentIndex >= this.tipMessages.size()) this.lastSentIndex = 0;
        }, 20, 20);
    }

    @Override
    public void unload() {

    }

    @Override
    public void reload() {
        this.tipMessages.clear();
        this.announceEvery = new TimeDuration(CorePlugin.getInstance().getConfig().getString("tips.announceEvery")).transform();

        ConfigurationSection section = CorePlugin.getInstance().getConfig().getConfigurationSection("tips.messages");

        if (section == null) {
            CorePlugin.getInstance().getLogger().warning("[Tip Handler] Error loading messages section.");
            return;
        }

        for (String key : section.getKeys(false)) {
            this.tipMessages.add(new TipMessage(key, section.getStringList(key)));
        }
    }

    public record TipMessage(String key, List<String> message) { }

}
