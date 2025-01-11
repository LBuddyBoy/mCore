package dev.minechase.core.bukkit.util.totp;

import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TwoFactorUtil {

    private static final String METADATA_KEY = "TWO_FACTOR_LOCKED";

    public static void lock(Player player, String message) {
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(CorePlugin.getInstance(), message));
    }

    public static void release(Player player) {
        player.removeMetadata(METADATA_KEY, CorePlugin.getInstance());
    }

    public static boolean isLocked(Player player) {
        return player.hasMetadata(METADATA_KEY);
    }

    public static String getMessage(Player player) {
        return player.getMetadata(METADATA_KEY).get(0).asString();
    }

}
