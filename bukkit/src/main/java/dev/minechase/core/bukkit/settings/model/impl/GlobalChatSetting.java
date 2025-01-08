package dev.minechase.core.bukkit.settings.model.impl;

import dev.minechase.core.bukkit.settings.model.ISetting;

public class GlobalChatSetting implements ISetting {

    @Override
    public String getId() {
        return "global_chat";
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public boolean getDefault() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Global Chat";
    }

    @Override
    public String getPrimaryColor() {
        return "&6";
    }

    @Override
    public String getSecondaryColor() {
        return "&e";
    }

    @Override
    public String getDisplayMaterial() {
        return "BEACON";
    }

    @Override
    public String getDescription() {
        return "Toggles your ability to view messages from players talking in public chat.";
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
