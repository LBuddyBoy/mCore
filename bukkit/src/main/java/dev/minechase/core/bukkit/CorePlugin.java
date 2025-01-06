package dev.minechase.core.bukkit;

import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.log.LogHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.bukkit.listener.ChatListener;
import dev.minechase.core.bukkit.listener.PunishmentListener;
import dev.minechase.core.bukkit.settings.SettingsHandler;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.bukkit.api.BukkitServerHandler;
import dev.minechase.core.bukkit.command.CommandHandler;
import dev.minechase.core.bukkit.listener.UserListener;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.task.QueuePlayerTask;
import lombok.Getter;
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
    private SettingsHandler settingsHandler;
    private BukkitServerHandler serverHandler;
    private LogHandler logHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        CoreAPI.start(this);

        this.loadModules();
        this.loadListeners();
        this.loadTasks();

        new StaffMessagePacket(Arrays.asList(
                "&7&m------------------------",
                "&6" + getServerName() + " &eis now &aonline&e!",
                "&7&m------------------------"
        )).send();
    }

    @Override
    public void onDisable() {
        new StaffMessagePacket(Arrays.asList(
                "&7&m------------------------",
                "&6" + getServerName() + " &eis now &coffline&e!",
                "&7&m------------------------"
        )).send();

        this.modules.forEach(IModule::unload);
    }

    @Override
    public String getServerName() {
        return this.getConfig().getString("serverName");
    }

    @Override
    public List<String> getServerGroups() {
        return this.getConfig().getStringList("serverGroups");
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
        localServer.setStoppedAt(0L);

        new ServerUpdatePacket(localServer).send();
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
                this.serverHandler = new BukkitServerHandler(),
                this.settingsHandler = new SettingsHandler(),
                this.logHandler = new LogHandler()
        ));

        this.modules.forEach(IModule::load);
    }

    private void loadTasks() {
        new QueuePlayerTask();
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PunishmentListener(), this);
        this.getServer().getPluginManager().registerEvents(new UserListener(), this);
    }

}
