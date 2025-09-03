import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.redis.RedisHandler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class TestCommons extends CommonsAPI {

    private final RedisHandler redisHandler;
    private final UUIDCache uuidCache;

    public TestCommons() {
        this.redisHandler = new RedisHandler("Commons|Global",
                0,
                0,
                "localhost",
                6379,
                ""
        );
        this.uuidCache = new UUIDCache();

        setModules(new ArrayList<>(Arrays.asList(
                getRedisHandler(),
                getUUIDCache()
        )));

        CommonsAPI.init(this);
    }

    @Override
    public RedisHandler getRedisHandler() {
        return this.redisHandler;
    }

    @Override
    public UUIDCache getUUIDCache() {
        return this.uuidCache;
    }

}
