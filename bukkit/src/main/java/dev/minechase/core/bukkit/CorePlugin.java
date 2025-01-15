package dev.minechase.core.bukkit;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.iphistory.IPHistoryHandler;
import dev.minechase.core.api.log.LogHandler;
import dev.minechase.core.api.note.NoteHandler;
import dev.minechase.core.api.prefix.PrefixHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.api.sync.SyncHandler;
import dev.minechase.core.api.tag.TagHandler;
import dev.minechase.core.bukkit.api.*;
import dev.minechase.core.bukkit.hologram.HologramHandler;
import dev.minechase.core.bukkit.listener.CoreListener;
import dev.minechase.core.bukkit.listener.PunishmentListener;
import dev.minechase.core.bukkit.listener.TotpListener;
import dev.minechase.core.bukkit.mod.ModModeHandler;
import dev.minechase.core.bukkit.npc.NPCHandler;
import dev.minechase.core.bukkit.settings.SettingsHandler;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.bukkit.command.CommandHandler;
import dev.minechase.core.bukkit.listener.UserListener;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import dev.minechase.core.bukkit.task.QueuePlayerTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
    private BukkitUserHandler userHandler;
    private BukkitPunishmentHandler punishmentHandler;
    private BukkitGrantHandler grantHandler;
    private RankHandler rankHandler;
    private SettingsHandler settingsHandler;
    private BukkitServerHandler serverHandler;
    private LogHandler logHandler;
    private IPHistoryHandler ipHistoryHandler;
    private BukkitPermissionHandler permissionHandler;
    private PrefixHandler prefixHandler;
    private TagHandler tagHandler;
    private NoteHandler noteHandler;
    private SyncHandler syncHandler;
    private BukkitReportHandler reportHandler;
    private ChatHandler chatHandler;
    private ModModeHandler modModeHandler;
    private RebootHandler rebootHandler;
    private WhitelistHandler whitelistHandler;
    private TipHandler tipHandler;
    private NPCHandler npcHandler;
    private HologramHandler hologramHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.start();

        this.loadModules();
        this.loadListeners();
        this.loadTasks();

        new StaffMessagePacket(CC.translate(Arrays.asList(
                "&7&m------------------------",
                "&6" + getServerName() + " &eis now &aonline&e!",
                "&7&m------------------------"
        ))).send();
    }

    @Override
    public void onDisable() {
        new StaffMessagePacket(CC.translate(Arrays.asList(
                "&7&m------------------------",
                "&6" + getServerName() + " &eis now &coffline&e!",
                "&7&m------------------------"
        ))).send();

        this.modules.forEach(IModule::unload);
    }

    @Override
    public String getServerName() {
        return this.getConfig().getString("serverName");
    }

    @Override
    public List<String> getLocalServerGroups() {
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
        localServer.setGroups(this.getLocalServerGroups());
        localServer.setPlayers(Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).toList());

        new ServerUpdatePacket(localServer).send();
    }

    private void loadModules() {
        DisguiseManager.initialize(this, true);
        DisguiseManager.getProvider().allowOverrideChat(false);

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
                this.userHandler = new BukkitUserHandler(),
                this.rankHandler = new RankHandler(),
                this.grantHandler = new BukkitGrantHandler(),
                this.punishmentHandler = new BukkitPunishmentHandler(),
                this.serverHandler = new BukkitServerHandler(),
                this.settingsHandler = new SettingsHandler(),
                this.logHandler = new LogHandler(),
                this.ipHistoryHandler = new IPHistoryHandler(),
                this.permissionHandler = new BukkitPermissionHandler(),
                this.prefixHandler = new PrefixHandler(),
                this.tagHandler = new TagHandler(),
                this.reportHandler = new BukkitReportHandler(),
                this.noteHandler = new NoteHandler(),
                this.syncHandler = new SyncHandler(),
                this.chatHandler = new ChatHandler(),
                this.modModeHandler = new ModModeHandler(),
                this.rebootHandler = new RebootHandler(),
                this.whitelistHandler = new WhitelistHandler(),
                this.tipHandler = new TipHandler(),
                this.npcHandler = new NPCHandler(),
                this.hologramHandler = new HologramHandler()
        ));

        this.modules.forEach(IModule::load);
    }

    private void loadTasks() {
        new QueuePlayerTask();
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new CoreListener(), this);
        this.getServer().getPluginManager().registerEvents(new PunishmentListener(), this);
        this.getServer().getPluginManager().registerEvents(new UserListener(), this);
        this.getServer().getPluginManager().registerEvents(new TotpListener(), this);
    }

}
