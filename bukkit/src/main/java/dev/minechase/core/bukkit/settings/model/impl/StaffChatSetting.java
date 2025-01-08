package dev.minechase.core.bukkit.settings.model.impl;

import dev.minechase.core.bukkit.settings.model.ISetting;

public class StaffChatSetting implements ISetting {

    @Override
    public String getId() {
        return "staff_chat";
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
        return "Staff Chat";
    }

    @Override
    public String getPrimaryColor() {
        return "&9";
    }

    @Override
    public String getSecondaryColor() {
        return "&b";
    }

    @Override
    public String getDisplayMaterial() {
        return "LIGHT_BLUE_DYE";
    }

    @Override
    public String getDescription() {
        return "Toggles your ability to speak in staff chat.";
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
        return "core.command.staffchat";
    }

}
