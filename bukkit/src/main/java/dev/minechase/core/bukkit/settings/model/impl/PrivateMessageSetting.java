package dev.minechase.core.bukkit.settings.model.impl;

import dev.minechase.core.bukkit.settings.model.ISetting;

public class PrivateMessageSetting implements ISetting {

    @Override
    public String getId() {
        return "private_messages";
    }

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public boolean getDefault() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Private Messages";
    }

    @Override
    public String getPrimaryColor() {
        return "&2";
    }

    @Override
    public String getSecondaryColor() {
        return "&a";
    }

    @Override
    public String getDisplayMaterial() {
        return "PAINTING";
    }

    @Override
    public String getDescription() {
        return "Toggles your ability to be privately messaged from players.";
    }

    @Override
    public String getEnabledText() {
        return "&a&lON";
    }

    @Override
    public String getDisabledText() {
        return "&c&lOFF";
    }

}
