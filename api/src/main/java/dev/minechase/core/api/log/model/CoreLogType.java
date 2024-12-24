package dev.minechase.core.api.log.model;

import dev.minechase.core.api.log.model.impl.NewUserLog;
import dev.minechase.core.api.log.model.impl.RankCreationLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum CoreLogType {

    RANK_CREATED("&a&lRANK CREATED", RankCreationLog::new),
    NEW_USER("&e&lNEW USER", NewUserLog::new);

    private final String displayName;
    private final Function<Document, CoreLog> creationConsumer;

}
