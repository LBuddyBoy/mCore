package dev.minechase.core.api.log.model;

import dev.minechase.core.api.log.model.impl.*;
import dev.minechase.core.api.log.model.impl.permission.PermissionCreationLog;
import dev.minechase.core.api.log.model.impl.permission.PermissionRemoveLog;
import dev.minechase.core.api.log.model.impl.rank.RankCreationLog;
import dev.minechase.core.api.log.model.impl.rank.RankDeletionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum CoreLogType {

    IP_CHANGED("&d&lUSER IP CHANGED", IPChangedLog::new),
    RANK_CREATED("&a&lRANK CREATED", RankCreationLog::new),
    RANK_DELETED("&c&lRANK DELETED", RankDeletionLog::new),
    GRANT_CREATED("&b&lGRANT CREATED", GrantCreationLog::new),
    GRANT_REMOVED("&b&lGRANT REMOVED", GrantRemoveLog::new),
    PERMISSION_CREATED("&6&lPERMISSION ADDED", PermissionCreationLog::new),
    PERMISSION_REMOVED("&6&lPERMISSION REMOVED", PermissionRemoveLog::new),
    PUNISHMENT_CREATED("&c&lPUNISHMENT CREATED", PunishmentCreationLog::new),
    PUNISHMENT_REMOVED("&c&lPUNISHMENT REMOVED", PunishmentRemoveLog::new),
    NEW_USER("&e&lNEW USER", NewUserLog::new);

    private final String displayName;
    private final Function<Document, CoreLog> creationConsumer;

}
