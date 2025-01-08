package dev.minechase.core.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.iphistory.IPHistoryHandler;
import dev.minechase.core.api.log.LogHandler;
import dev.minechase.core.api.permission.PermissionHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.velocity.api.ProxyServerHandler;
import dev.minechase.core.velocity.listener.HubListener;
import dev.minechase.core.velocity.listener.PermissionListener;
import dev.minechase.core.velocity.listener.UserListener;
import dev.minechase.core.velocity.util.config.Config;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Plugin(
        id = "mcore-velocity",
        name = "mcore-velocity",
        version = "1.0",
        authors = {"LBuddyBoy"},
        dependencies = {
                @Dependency(id = "commons")
        }
)

@Getter
public class CoreVelocity implements ICoreAPI {

    @Getter private static CoreVelocity instance;

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    private final List<IModule> modules = new ArrayList<>();
    private Config configFile;

    private MongoHandler mongoHandler;
    private UserHandler userHandler;
    private PunishmentHandler punishmentHandler;
    private GrantHandler grantHandler;
    private RankHandler rankHandler;
    private ProxyServerHandler serverHandler;
    private LogHandler logHandler;
    private IPHistoryHandler ipHistoryHandler;
    private PermissionHandler permissionHandler;

    @Inject
    public CoreVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        this.configFile = new Config("config");

        this.start();
        this.loadModules();
        this.loadListeners();
    }

    @Subscribe
    public void onProxyDisable(ProxyShutdownEvent event) {
        this.modules.forEach(IModule::unload);
    }

    @Override
    public String getServerName() {
        return "Proxy";
    }

    @Override
    public List<String> getLocalServerGroups() {
        return Arrays.asList("GLOBAL");
    }

    public RedisHandler getRedisHandler() {
        return CommonsAPI.getInstance().getRedisHandler();
    }

    @Override
    public void updateLocalServer() {

    }

    private void loadModules() {
        this.modules.addAll(Arrays.asList(
//                this.commandHandler = new CommandHandler(),
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
                this.serverHandler = new ProxyServerHandler(),
                this.logHandler = new LogHandler(),
                this.ipHistoryHandler = new IPHistoryHandler(),
                this.permissionHandler = new PermissionHandler()
        ));

        this.modules.forEach(IModule::load);
    }

    private void loadListeners() {
        this.getProxy().getEventManager().register(this, new HubListener());
        this.getProxy().getEventManager().register(this, new PermissionListener());
        this.getProxy().getEventManager().register(this, new UserListener());
    }

    public void reload() {
        this.configFile.loadConfig();
        this.modules.forEach(IModule::reload);
    }

    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    public Configuration getConfig() {
        return this.configFile.getConfiguration();
    }

}
