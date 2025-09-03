package dev.minechase.core.rest;

import com.mongodb.client.MongoClient;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.chat.ChatHandler;
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
import dev.minechase.core.api.sync.DiscordSyncHandler;
import dev.minechase.core.api.sync.WebsiteSyncHandler;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeUpdatePacket;
import dev.minechase.core.api.tag.TagHandler;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.rest.api.SpringServerHandler;
import dev.minechase.core.rest.api.SpringSyncHandler;
import dev.minechase.core.rest.service.AccountService;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@Getter
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class CoreApplication implements ICoreAPI {

	private final AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	@Getter private static CoreApplication instance;

	private final List<IModule> modules;

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
	private final DiscordSyncHandler discordSyncHandler;
	private final WebsiteSyncHandler websiteSyncHandler;
	private final ChatHandler chatHandler;

	public CoreApplication(AccountService accountService) {
        this.accountService = accountService;

        instance = this;
		this.modules = new ArrayList<>();

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
				this.serverHandler = new SpringServerHandler(),
				this.logHandler = new LogHandler(),
				this.ipHistoryHandler = new IPHistoryHandler(),
				this.permissionHandler = new PermissionHandler(),
				this.prefixHandler = new PrefixHandler(),
				this.reportHandler = new ReportHandler(),
				this.tagHandler = new TagHandler(),
				this.noteHandler = new NoteHandler(),
				this.discordSyncHandler = new DiscordSyncHandler(),
				this.websiteSyncHandler = new SpringSyncHandler(this.accountService),
				this.chatHandler = new ChatHandler()
		));

		CommonsAPI.getInstance().getModules().forEach(IModule::load);

		this.modules.forEach(IModule::load);
	}

	public InputStream getResourceAsStream(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	@Override
	public String getServerName() {
		return "Website";
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
		localServer.setPlayerCount(0);
		localServer.setMaxPlayers(0);
		localServer.setStoppedAt(0L);
		localServer.setGroups(this.getLocalServerGroups());
		localServer.setPlayers(new ArrayList<>());

		new ServerUpdatePacket(localServer).send();
	}

	@Override
	public Logger getLogger() {
		return Logger.getAnonymousLogger();
	}

	@Override
	public boolean isProxy() {
		return false;
	}

}
