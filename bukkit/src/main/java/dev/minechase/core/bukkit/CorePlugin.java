package dev.minechase.core.bukkit;

import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.bukkit.api.BukkitServerHandler;
import dev.minechase.core.bukkit.command.CommandHandler;
import dev.minechase.core.bukkit.listener.UserListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CorePlugin extends JavaPlugin implements ICoreAPI {

    @Getter
    private static CorePlugin instance;

    private final List<IModule> modules = new ArrayList<>();
    private CommandHandler commandHandler;
    private MongoHandler mongoHandler;
    private UserHandler userHandler;
    private PunishmentHandler punishmentHandler;
    private GrantHandler grantHandler;
    private RankHandler rankHandler;
    private BukkitServerHandler serverHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        CoreAPI.start(this);

        this.loadModules();
        this.loadListeners();
    }

    @Override
    public void onDisable() {
        this.modules.forEach(IModule::unload);
    }

    @Override
    public String getServerName() {
        return this.getConfig().getString("serverName");
    }

    @Override
    public String getServerGroup() {
        return this.getConfig().getString("serverGroup");
    }

    public RedisHandler getRedisHandler() {
        return CommonsAPI.getInstance().getRedisHandler();
    }

    @Override
    public void updateLocalServer() {
        CoreServer localServer = this.getServerHandler().getLocalServer();

        localServer.setHost("localhost");
        localServer.setPort(this.getServer().getPort());
        localServer.setPlayerCount(this.getServer().getOnlinePlayers().size());
        localServer.setMaxPlayers(this.getServer().getMaxPlayers());
        localServer.setStartedAt(System.currentTimeMillis());
        localServer.setStoppedAt(0L);
    }

    private void loadModules() {
        this.modules.addAll(Arrays.asList(
                this.commandHandler = new CommandHandler(),
                this.mongoHandler = new MongoHandler(
                        getConfig().getString("mongo.host"),
                        getConfig().getInt("mongo.port"),
                        getConfig().getBoolean("mongo.auth.enabled"),
                        getConfig().getString("mongo.auth.username"),
                        getConfig().getString("mongo.auth.password"),
                        getConfig().getString("mongo.auth.database"),
                        getConfig().getString("mongo.database")
                ),
                this.userHandler = new UserHandler(),
                this.rankHandler = new RankHandler(),
                this.grantHandler = new GrantHandler(),
                this.punishmentHandler = new PunishmentHandler(),
                this.serverHandler = new BukkitServerHandler()
        ));

        this.modules.forEach(IModule::load);
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new UserListener(), this);
    }

}
