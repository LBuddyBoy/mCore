package dev.minechase.core.api.api;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.util.UUIDUtils;

import java.util.Date;
import java.util.UUID;

public interface IRemovable {

    String getRemovedReason();
    String getRemovedOn();
    UUID getRemovedBy();
    long getRemovedAt();
    default boolean isRemovable() {
        return true;
    }

    default boolean isRemoved() {
        return getRemovedReason() != null || getRemovedAt() > 0;
    }

    default String getRemovedAtDate() {
        return APIConstants.SDF.format(new Date(getRemovedAt()));
    }

    default String getRemovedByName() {
        return UUIDUtils.getName(getRemovedBy());
    }

}
