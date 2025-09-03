package dev.minechase.core.api.util;

import org.bson.Document;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/19/2025
 */
public class NumberUtils {

    public static long getAsLong(Document d, String key, long def) {
        Object v = d.get(key);
        if (v == null) return def;
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) {
            try { return Long.parseLong((String) v); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

}
