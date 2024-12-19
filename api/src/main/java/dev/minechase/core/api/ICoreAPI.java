package dev.minechase.core.api;

import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.user.UserHandler;

public interface ICoreAPI {

    String getServerName();
    MongoHandler getMongoHandler();
    RedisHandler getRedisHandler();
    PunishmentHandler getPunishmentHandler();
    UserHandler getUserHandler();

}
