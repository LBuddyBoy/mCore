package dev.minechase.core.bukkit.settings.model.impl;

import dev.minechase.core.bukkit.settings.model.ISetting;

public class TipMessageSetting implements ISetting {

    @Override
    public String getId() {
        return "tip_messages";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean getDefault() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Tip Messages";
    }

    @Override
    public String getPrimaryColor() {
        return "&5";
    }

    @Override
    public String getSecondaryColor() {
        return "&d";
    }

    @Override
    public String getDisplayMaterial() {
        return "GOLD_NUGGET";
    }

    @Override
    public String getDescription() {
        return "Toggles your ability to see tips in chat.";
    }

    @Override
    public String getEnabledText() {
        return "&a&lON";
    }

    @Override
    public String getDisabledText() {
        return "&c&lOFF";
    }

    @Override
    public String getPermission() {
        return "";
    }

}
