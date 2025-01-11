package dev.minechase.core.velocity;

import dev.lbuddyboy.commons.api.util.TimeDuration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum CoreLocale {
    
    SERVER_NAME("server-info.name", "Proxy"),
    LOCK_DOWN_ACTIVE("lock-down.active", false),
    LOCK_DOWN_MESSAGE("lock-down.message", "&cExample Network is currently locked down\nOnly Default ranks and up can join.\n Check out our website for more info www.example.net"),
    LOCK_DOWN_RANK("lock-down.rank", "Default"),
    LOCK_DOWN_MOTD_1("lock-down.motd.normal.line-one", "<blend:#327637;#c9ff99;false>→ Lockdown MOTD Line One</>"),
    LOCK_DOWN_MOTD_2("lock-down.motd.normal.line-two", "<blend:#327637;#c9ff99;false>→ Lockdown MOTD Line Two</>"),
    LOCK_DOWN_LEGACY_MOTD_1("lock-down.motd.legacy.line-one", "&2→ &aLockdown MOTD Line One"),
    LOCK_DOWN_LEGACY_MOTD_2("lock-down.motd.legacy.line-two", "&2→ &aLockdown MOTD Line Two"),
    LOCK_DOWN_FAVICON("lock-down.motd.favicon", "server-icon.png"),
    LOCK_DOWN_BYPASS("lock-down.bypass", Arrays.asList(
            ""
    )),
    LOCK_DOWN_IP_BYPASS("lock-down.ip-bypass", Arrays.asList(
            ""
    )),
    LOCK_DOWN_DURATION("lock-down.duration", -1), // -1 means it'll just be on until toggled off
    LOCK_DOWN_STARTED_AT("lock-down.started-at", -1),
    MOTD_TIMER_HEADER_OVERRIDE_LOCKDOWN("motd-timer.override-lockdown", false),
    MOTD_TIMER_HEADER_CENTER_CONTEXT("motd-timer.center-context", true),
    MOTD_TIMER_HEADER("motd-timer.header", "<blend:#327637;#c9ff99;false>→ MOTD Timer Header</>");

    final String path;
    final Object value;

    public String getString() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path))
            return CoreVelocity.getInstance().getConfig().getString(this.path);

        loadDefault();

        return String.valueOf(this.value);
    }

    public boolean getBoolean() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path)) {
            return CoreVelocity.getInstance().getConfig().getBoolean(this.path);
        }

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public int getInt() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path))
            return CoreVelocity.getInstance().getConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public long getLong() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path))
            return CoreVelocity.getInstance().getConfig().getLong(this.path);

        loadDefault();

        return Long.parseLong(String.valueOf(this.value));
    }

    public double getDouble() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path))
            return CoreVelocity.getInstance().getConfig().getDouble(this.path);

        loadDefault();

        return Double.parseDouble(String.valueOf(this.value));
    }

    public TimeDuration getTimeDuration() {
        return new TimeDuration(getString());
    }

    public List<String> getStringList() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path))
            return CoreVelocity.getInstance().getConfig().getStringList(this.path);

        loadDefault();

        return (List<String>) this.value;
    }

    public void update(Object value) {
        CoreVelocity.getInstance().getConfig().set(this.path, value);
        CoreVelocity.getInstance().getConfigFile().saveConfig();
    }

    public void loadDefault() {
        if (CoreVelocity.getInstance().getConfig().contains(this.path)) return;

        CoreVelocity.getInstance().getConfig().set(this.path, this.value);
        CoreVelocity.getInstance().getConfigFile().saveConfig();
    }

}
