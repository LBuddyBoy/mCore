package dev.minechase.core.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.user.model.User;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class CoreAPI {

    public static boolean initiated = false;
    @Getter
    private static ICoreAPI instance;

    public static final Executor POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("%d - mCore API")
                    .build()
    );

    /**
     *
     * Fetches a core user & will return a null if they aren't online/cached.
     *
     * @param playerUUID uuid of the player
     * @return a user from the user cache
     */

    public static @Nullable User getUser(UUID playerUUID) {
        return instance.getUserHandler().getUser(playerUUID);
    }

    /**
     *
     * Returns a future user from the database or if they are online it'll pull their profile.
     * If the playerUUID is null or doesn't exist in the mojang database it won't process.
     *
     * @param playerUUID uuid of the player
     * @return future user
     */

    public static @NotNull CompletableFuture<User> getUserAsync(UUID playerUUID) {
        return instance.getUserHandler().getOrCreateAsync(playerUUID);
    }

    public static List<String> getScopes() {
        List<String> scopes = new ArrayList<>(Arrays.asList("GLOBAL", "HUBS"));

        for (CoreServer server : instance.getServerHandler().getServers().values()) {
            scopes.add(server.getName());
        }

        return scopes;
    }

    protected static void start(ICoreAPI coreAPI) {
        if (initiated) {
            throw new IllegalStateException("Tried to start CoreAPI more than once.");
        }

        instance = coreAPI;
        initiated = true;
    }

}
