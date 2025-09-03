package dev.minechase.core.rest;

import com.mongodb.MongoClient;
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
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@SpringBootApplication
@Getter
public class CoreApplication implements ICoreAPI {

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

	public CoreApplication() {
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
				this.serverHandler = new ServerHandler(),
				this.logHandler = new LogHandler(),
				this.ipHistoryHandler = new IPHistoryHandler(),
				this.permissionHandler = new PermissionHandler(),
				this.prefixHandler = new PrefixHandler(),
				this.reportHandler = new ReportHandler(),
				this.tagHandler = new TagHandler(),
				this.noteHandler = new NoteHandler(),
				this.discordSyncHandler = new DiscordSyncHandler(),
				this.websiteSyncHandler = new WebsiteSyncHandler(),
				this.chatHandler = new ChatHandler()
		));
		CommonsAPI.getInstance().getModules().forEach(IModule::load);

		this.modules.forEach(IModule::load);

		this.userHandler.getOrCreateAsync("LBuddyBoy");

		UUID playerUUID = UUID.fromString("2732a2e3-2641-4888-81e7-de4282debeea");
		this.websiteSyncHandler.getSyncInformation(playerUUID).whenCompleteAsync(((information, throwable) -> {
			if (throwable != null) {
				throwable.printStackTrace();
				return;
			}

			if (information != null) {
				System.out.println("Your account is already synced to: " + information.getWebsiteUserId() + "'");
				return;
			}

			SyncCode syncCode = this.websiteSyncHandler.getSyncCode(playerUUID);

			if (syncCode != null) {
				System.out.println("You already have a sync code: " + syncCode.getCode());
				return;
			}

			syncCode = new SyncCode(playerUUID, this.generateWebsiteCode());

			Arrays.asList(
					" ",
					"<blend:&6;&e>&lHow to Sync Account</>",
					"&eStep #1 &fHead over to https://mcore.com/sync",
					"&eStep #2 &fCreate an account if you haven't",
					"&eStep #3 &fEnter this code: " + syncCode.getCode(),
					" ",
					"&fAfter doing this your website account will be synced",
					"&fto your Minecraft Account!",
					" "
			).forEach(s -> System.out.println(s));

			new WebsiteSyncCodeUpdatePacket(syncCode).send();
		}));
	}

	public int generateWebsiteCode() {
		int random = ThreadLocalRandom.current().nextInt(99999);

		if (this.websiteSyncHandler.getSyncCode(random) != null) return generateWebsiteCode();

		return random;
	}

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	// curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NjAzNDc2M30.QeC4A1ofq0Ri_eoBIN_IcAzFFnrW48wzf3lHHDcms9vu1wO5oQ28XgFn9-ONKvloiFTnYyXZcvhTJKv-jMoTFg" http://localhost:8080/api/secure

	public InputStream getResourceAsStream(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	@Override
	public String getServerName() {
		return "API";
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

}
