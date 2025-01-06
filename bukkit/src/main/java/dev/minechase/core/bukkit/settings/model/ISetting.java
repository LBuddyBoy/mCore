package dev.minechase.core.bukkit.settings.model;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.user.model.UserMetadata;

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

    default boolean isEnabled(UUID playerUUID) {
        User user = CoreAPI.getInstance().getUserHandler().getUser(playerUUID);
        UserMetadata metadata = user.getPersistentMetadata();

        return metadata.getBooleanOrDefault("settings_" + getId(), this.getDefault());
    }

    default boolean toggle(UUID playerUUID) {
        User user = CoreAPI.getInstance().getUserHandler().getUser(playerUUID);
        UserMetadata metadata = user.getPersistentMetadata();
        boolean toggle = !metadata.getBooleanOrDefault("settings_" + getId(), this.getDefault());

        metadata.setBoolean("settings_" + getId(), toggle);

        return toggle;
    }

}
