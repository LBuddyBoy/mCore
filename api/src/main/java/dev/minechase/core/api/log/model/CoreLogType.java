package dev.minechase.core.api.log.model;

import dev.minechase.core.api.log.model.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum CoreLogType {

    RANK_CREATED("&a&lRANK CREATED", RankCreationLog::new),
    GRANT_CREATED("&b&lGRANT CREATED", GrantCreationLog::new),
    GRANT_REMOVED("&b&lGRANT REMOVED", GrantRemoveLog::new),
    PUNISHMENT_CREATED("&c&lPUNISHMENT CREATED", PunishmentCreationLog::new),
    PUNISHMENT_REMOVED("&c&lPUNISHMENT REMOVED", PunishmentRemoveLog::new),
    NEW_USER("&e&lNEW USER", NewUserLog::new);

    private final String displayName;
    private final Function<Document, CoreLog> creationConsumer;

}
