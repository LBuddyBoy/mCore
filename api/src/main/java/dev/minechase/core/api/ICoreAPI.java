package dev.minechase.core.api;

import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.iphistory.IPHistoryHandler;
import dev.minechase.core.api.log.LogHandler;
import dev.minechase.core.api.permission.PermissionHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.tag.TagHandler;
import dev.minechase.core.api.user.UserHandler;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public interface ICoreAPI {

    String getServerName();
    List<String> getLocalServerGroups();
    MongoHandler getMongoHandler();
    RedisHandler getRedisHandler();
    PunishmentHandler getPunishmentHandler();
    GrantHandler getGrantHandler();
    RankHandler getRankHandler();
    PermissionHandler getPermissionHandler();
    IPHistoryHandler getIpHistoryHandler();
    ServerHandler<?> getServerHandler();
    TagHandler getTagHandler();
    LogHandler getLogHandler();
    UserHandler getUserHandler();
    void updateLocalServer();
    Logger getLogger();

    default List<CoreServer> getHubs() {
        return this.getServerHandler().getServers().values().stream().filter(CoreServer::isHub).sorted(Comparator.comparingInt(CoreServer::getPlayerCount)).toList();
    }

    default void start() {
        CoreAPI.start(this);
    }

}
