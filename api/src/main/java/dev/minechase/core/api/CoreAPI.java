package dev.minechase.core.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class CoreAPI {

    public static boolean initiated = false;
    public static final Executor POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("%d - mCore API").build());

    @Getter private static ICoreAPI instance;

    public static void start(ICoreAPI coreAPI) {
        if (initiated) {
            throw new IllegalStateException("Tried to start CoreAPI more than once.");
        }

        instance = coreAPI;
        initiated = true;
    }

    public static List<String> getScopes() {
        return Arrays.asList(
                "GLOBAL"
        );
    }

}
