package dev.minechase.core.api.user.model;

import lombok.NonNull;

import java.util.*;

public class UserMetadata {

    private final Map<String, String> metadata = new HashMap<>();

    public boolean toggle(String key) {
        return toggle(key, true);
    }

    public boolean toggle(String key, boolean def) {
        if (contains(key)) {
            setBoolean(key, !getBoolean(key));
        } else {
            setBoolean(key, !def);
        }

        return getBoolean(key);
    }
    
    public boolean getBooleanOrDefault(String key, boolean def) {
        try {
            return Boolean.parseBoolean(getOrDefault(key, String.valueOf(def)));
        } catch (Exception e) {
            e.printStackTrace();
            return def;
        }
    }

    public boolean getBoolean(String key) {
        return getBooleanOrDefault(key, false);
    }

    public void setBoolean(String key, Boolean value) {
        set(key, value.toString());
    }

    public int getInteger(String key) {
        if (!contains(key)) return 0;

        try {
            return Integer.parseInt(getOrDefault(key, "0"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setInteger(String key, Integer value) {
        set(key, value.toString());
    }

    public long getLong(String key) {
        if (!contains(key)) return 0L;

        try {
            return Long.parseLong(getOrDefault(key, "0"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public void setLong(String key, Long value) {
        set(key, value.toString());
    }

    public double getDouble(String key) {
        if (!contains(key)) return 0D;

        try {
            return Double.parseDouble(getOrDefault(key, "0.0"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0D;
        }
    }

    public void setDouble(String key, Double value) {
        set(key, value.toString());
    }

    public float getFloat(String key) {
        if (!contains(key)) return 0f;

        try {
            return Float.parseFloat(getOrDefault(key, "0.0"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public void setFloat(String key, Float value) {
        set(key, value.toString());
    }

    public void setUUID(String key, UUID value) {
        set(key, value.toString());
    }

    public UUID getUUID(String key) {
        if (!contains(key)) return null;

        return UUID.fromString(get(key));
    }

    public void set(String key, String value) {
        this.metadata.put(key, value);
    }

    public String get(String key) {
        return this.metadata.get(key);
    }

    @NonNull
    public String getOrDefault(String key, String def) {
        return contains(key) ? get(key) : def;
    }

    public void remove(String key) {
        this.metadata.remove(key);
    }

    public boolean contains(String key) {
        return this.metadata.containsKey(key);
    }
    
    public Set<String> getKeys() {
        return this.metadata.keySet();
    }
    
    public Collection<String> getValues() {
        return this.metadata.values();
    }

}
