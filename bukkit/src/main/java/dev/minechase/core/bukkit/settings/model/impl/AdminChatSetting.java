package dev.minechase.core.bukkit.settings.model.impl;

import dev.minechase.core.bukkit.settings.model.ISetting;

public class AdminChatSetting implements ISetting {

    @Override
    public String getId() {
        return "admin_chat";
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean getDefault() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return "Admin Chat";
    }

    @Override
    public String getPrimaryColor() {
        return "&4";
    }

    @Override
    public String getSecondaryColor() {
        return "&c";
    }

    @Override
    public String getDisplayMaterial() {
        return "RED_DYE";
    }

    @Override
    public String getDescription() {
        return "Toggles your ability to speak in admin chat.";
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
        return "core.command.adminchat";
    }

}
