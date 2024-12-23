package dev.minechase.core.api;

import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.user.UserHandler;

import java.util.logging.Logger;

public interface ICoreAPI {

    String getServerName();
    String getServerGroup();
    MongoHandler getMongoHandler();
    RedisHandler getRedisHandler();
    PunishmentHandler getPunishmentHandler();
    GrantHandler getGrantHandler();
    RankHandler getRankHandler();
    ServerHandler getServerHandler();
    UserHandler getUserHandler();
    void updateLocalServer();
    Logger getLogger();

}
