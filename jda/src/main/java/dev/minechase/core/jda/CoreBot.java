package dev.minechase.core.jda;

import com.mongodb.MongoClient;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.iphistory.IPHistoryHandler;
import dev.minechase.core.api.log.LogHandler;
import dev.minechase.core.api.note.NoteHandler;
import dev.minechase.core.api.permission.PermissionHandler;
import dev.minechase.core.api.prefix.PrefixHandler;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.rank.RankHandler;
import dev.minechase.core.api.report.ReportHandler;
import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.api.sync.SyncHandler;
import dev.minechase.core.api.tag.TagHandler;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.jda.api.JDAServerHandler;
import dev.minechase.core.jda.command.CommandEvent;
import dev.minechase.core.jda.command.CommandHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Getter
public class CoreBot implements ICoreAPI {

    @Getter
    private static CoreBot instance;

    private final JDA jda;
    private final List<IModule> modules;

    private final File dataDirectory;

    private final CommandHandler commandHandler;
    private final MongoHandler mongoHandler;
    private final UserHandler userHandler;
    private final PunishmentHandler punishmentHandler;
    private final GrantHandler grantHandler;
    private final RankHandler rankHandler;
    private final ServerHandler serverHandler;
    private final LogHandler logHandler;
    private final IPHistoryHandler ipHistoryHandler;
    private final PermissionHandler permissionHandler;
    private final ReportHandler reportHandler;
    private final PrefixHandler prefixHandler;
    private final TagHandler tagHandler;
    private final NoteHandler noteHandler;
    private final SyncHandler syncHandler;

    public CoreBot() {
        instance = this;
        this.modules = new ArrayList<>();
        this.dataDirectory = new File(System.getProperty("user.dir"));

        try {
            this.jda = JDABuilder.createDefault("MTEwMTYwOTAxNjMzOTIxMDM3MA.GcczWB.5E-3Bo8olpqWJ5aqabyH2gZ6G5sQ3T85AtJWpg")
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .setBulkDeleteSplittingEnabled(false)
                    .setCompression(Compression.NONE)
                    .addEventListeners(new CommandEvent())
                    .setActivity(Activity.of(Activity.ActivityType.WATCHING, "play.minechase.net"))
                    .setStatus(OnlineStatus.ONLINE) // Set the bot's status
                    .build();

            this.jda.awaitReady();

            start();

            CommonsAPI.setInstance(new CommonsAPI(
                    new UUIDCache(),
                    new RedisHandler(
                            "Commons|Global",
                            0,
                            0,
                            "localhost",
                            6379,
                            ""
                    )) {

                @Override
                public MongoClient getMongoClient() {
                    return mongoHandler.getClient();
                }

            });

            this.modules.addAll(Arrays.asList(
                    this.commandHandler = new CommandHandler(),
                    this.mongoHandler = new MongoHandler(
                            "localhost",
                            27017,
                            false,
                            "",
                            "",
                            "admin",
                            "mCore"
                    ),
                    this.userHandler = new UserHandler(),
                    this.rankHandler = new RankHandler(),
                    this.grantHandler = new GrantHandler(),
                    this.punishmentHandler = new PunishmentHandler(),
                    this.serverHandler = new JDAServerHandler(),
                    this.logHandler = new LogHandler(),
                    this.ipHistoryHandler = new IPHistoryHandler(),
                    this.permissionHandler = new PermissionHandler(),
                    this.prefixHandler = new PrefixHandler(),
                    this.reportHandler = new ReportHandler(),
                    this.tagHandler = new TagHandler(),
                    this.noteHandler = new NoteHandler(),
                    this.syncHandler = new SyncHandler()
            ));
            CommonsAPI.getInstance().getModules().forEach(IModule::load);

            this.modules.forEach(IModule::load);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(IModule::unload)));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Guild getGuild() {
        return CoreBot.getInstance().getJda().getGuildById("1104585785581240411");
    }

    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    @Override
    public String getServerName() {
        return "JDA";
    }

    @Override
    public List<String> getLocalServerGroups() {
        return Arrays.asList("GLOBAL");
    }

    @Override
    public RedisHandler getRedisHandler() {
        return CommonsAPI.getInstance().getRedisHandler();
    }

    @Override
    public void updateLocalServer() {
        CoreServer localServer = this.getServerHandler().getLocalServer();

        localServer.setHost("localhost");
        localServer.setPort(-1);
        localServer.setPlayerCount(getGuild().getMemberCount());
        localServer.setMaxPlayers(getGuild().getMaxMembers());
        localServer.setStoppedAt(0L);
        localServer.setGroups(this.getLocalServerGroups());
        localServer.setPlayers(new ArrayList<>());

        new ServerUpdatePacket(localServer).send();
    }

    @Override
    public Logger getLogger() {
        return Logger.getAnonymousLogger();
    }
}