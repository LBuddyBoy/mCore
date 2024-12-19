package dev.minechase.core.bukkit;

import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.mongo.MongoHandler;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.ICoreAPI;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.user.UserHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CorePlugin extends JavaPlugin implements ICoreAPI {

    @Getter private static CorePlugin instance;

    private final List<IModule> modules = new ArrayList<>();
    private MongoHandler mongoHandler;
    private UserHandler userHandler;
    private PunishmentHandler punishmentHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        CoreAPI.start(this);

        this.modules.addAll(Arrays.asList(
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
                this.punishmentHandler = new PunishmentHandler()
        ));

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getServerName() {
        return this.getConfig().getString("serverName");
    }

    public RedisHandler getRedisHandler() {
        return CommonsAPI.getInstance().getRedisHandler();
    }
}
