package dev.minechase.core.bukkit.settings.model;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.model.UserMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ISetting {

    String getId();
    int getPriority();
    boolean getDefault();
    String getDisplayName();
    String getPrimaryColor();
    String getSecondaryColor();
    String getDisplayMaterial();
    String getDescription();
    String getEnabledText();
    String getDisabledText();
    String getPermission();

    default boolean isEnabled(UUID playerUUID) {
        User user = CoreAPI.getInstance().getUserHandler().getUser(playerUUID);
        UserMetadata metadata = user.getPersistentMetadata();

        return metadata.getBooleanOrDefault("settings_" + getId(), this.getDefault());
    }

    default boolean toggle(UUID playerUUID) {
        User user = CoreAPI.getInstance().getUserHandler().getUser(playerUUID);
        UserMetadata metadata = user.getPersistentMetadata();
        boolean toggle = !metadata.getBooleanOrDefault("settings_" + getId(), this.getDefault());
        Player player = Bukkit.getPlayer(playerUUID);

        metadata.setBoolean("settings_" + getId(), toggle);

        if (player != null) {
            if (toggle) {
                player.sendMessage(CC.translate("<blend:&2;&a>[Settings] You enabled your " + this.getDisplayName() + "</>"));
            } else {
                player.sendMessage(CC.translate("<blend:&4;&c>[Settings] You disabled your " + this.getDisplayName() + "</>"));
            }
        }

        return toggle;
    }

}
