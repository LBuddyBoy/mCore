package dev.minechase.core.api.api;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.minechase.core.api.util.UUIDUtils;

import java.util.Date;
import java.util.UUID;

public interface ISendable {

    String getReason();
    UUID getTargetUUID();
    UUID getSenderUUID();

    default String getSenderName() {
        return UUIDUtils.getName(getSenderUUID());
    }

    default String getTargetName() {
        return UUIDCache.getUuidToNames().getOrDefault(this.getTargetUUID(), "N/A");
    }

}
