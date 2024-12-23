package dev.minechase.core.api.api;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.TimeUtils;

import java.util.Date;
import java.util.UUID;

public interface IExpirable {

    long getSentAt();
    long getDuration();

    default long getTimeLeft() {
        if (isPermanent()) return Long.MAX_VALUE;

        return (getSentAt() + getDuration()) - System.currentTimeMillis();
    }

    default boolean isPermanent() {
        return getDuration() == -1L;
    }

    default boolean isExpired() {
        if (isPermanent()) return false;

        return getTimeLeft() <= 0;
    }

    default String getDurationString() {
        return (this.isPermanent() ? "&eForever" : TimeUtils.formatIntoDetailedString(this.getDuration())) + " (" + TimeUtils.formatIntoHHMMSS((int) (getTimeLeft() / 1000)) + ")";
    }

    default boolean isTemporary() {
        return !isPermanent();
    }

    default String getSentAtDate() {
        return APIConstants.SDF.format(new Date(getSentAt()));
    }

}
