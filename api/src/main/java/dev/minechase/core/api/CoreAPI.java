package dev.minechase.core.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.Pair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    public static List<String> getGroups() {
        return instance.getServerHandler().getServers().values()
                .stream()
                .flatMap(server -> server.getGroups().stream())
                .distinct()
                .toList();
    }

    // Pair<Value, Signature>
    public static Map<String, Pair<String, String>> getDisguiseSkins() {
        return new HashMap<>() {{
            put("Knight", new Pair<>(
                    "ewogICJ0aW1lc3RhbXAiIDogMTczNjczMjY4NjI2NSwKICAicHJvZmlsZUlkIiA6ICJjY2MxNGM2ZDUwMDE0MjBmYmMxYjkyMTM2Y2JmOWU4MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJab25lX1gwODE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EyZDVlYzFiZWE2N2Q5ZDQzOTdmMzU1YzY0ZWU3OTcyNGU3ODBlZDIxZTcyMGFmYzg3NTdhYWQzOGNiN2I3ZDQiCiAgICB9CiAgfQp9",
                    "Cx9J5H2kD79J00LoFBs9kLvMMPsuWyMc6rpKO/E+qIa62w+vjnusvEMHFXzRxpW2MVClKSTOixYQuHCH3n38jL8VLMhyJmmIpA8rB5F9IrHnRERqGV5cs7hfP7m78yZn8esHagtIjK/+WEPQsou2lst5NgzqdM8SD0AsGqQOWnW6UqB8S5PwW7il2mMVcIqXUEwmiHEur2whEAcx30XF4vnRP3pspUK+kkGHwRrqFFiZYD25V8+icq3b655GHXt+6cGUxJ0ELXoQRmR2if1ia3oGwe8f4Z0eCPmNo/9uYb7e5aQ6X8c7aLTWRtoQxu4PiBLEIOLGW+BVwD+A4x2ySH3uNSNxLLAWgkrmdyy2Brhdkj1sGp2qTxbvlAusF4S9Q/81Y0LmTdvcckvJCwPl9byJyaHM0XgzfvZCeDuEE5N2ju2BuOZqV0QL/R0kmxlX4p8eHvVjlLRRz6mnGszgFTmq2wXgb916zf5gUg4mhO4nfq355CSrxuEXzNXCoqP3A3HWEcpHwO14Pv8i1j2SHVqHv0hRGVALG4WmkC6A6e4GJRS6lBNELQdiy/dpkutK6eS6TSTzPEWS/OCLXKCFSJ5NgJy3Q9vXDVSRKniSbP9vH2YIzIcRqxN9Sq5QeF/GRpGYT4RViuGOW+6+GGhMZ4B2usoSxsVDHQ9ZXT1zSww="
            ));
            put("Pencil", new Pair<>(
                    "eyJ0aW1lc3RhbXAiOjE1MzQ0OTg3MDMwOTIsInByb2ZpbGVJZCI6IjJlYTU0MjlkMzg2NzRiN2ZiMWM5MjM5YjhiNTFlZjRmIiwicHJvZmlsZU5hbWUiOiJQZW5jaWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlNjRlZDc5Y2ExOGZiMzYxOWJhNWI1MjdlYmNlOGY4YmUxZWZiN2JkMTlmZjNhYzUwMWI1YTFiMmI1OWE2ZGQifX19",
                    "PSaEC9sQBwGF+bF2Avd4JBf+vACpuktbqLglle0PLuiqvOLejfjmdUm9YYpTweGQu+TrUDwv/3uIr0wRuU/J0MEiG3xegAy1buhVUkrrU/q1gVM5+l7Kjd4cU577mvPI6KDiAMQUtMULYxE47UKyyh+8MaYF4ON6zBRUACSbTnF28GGv4x9j/6OhD89Vkyhj1JfOvwIBHrcATqWoZWv4IwBiR+aUBNkeqD4MGANapwWt02Yn70SKl0GLZZfNib1LNuccoa2AcgoWEh/9H4halAdjNtn3w+FpXgo47d2DNVdfU7/t59toaSTnp9HuloHmi83uAcrQ3oAIFQENyXonj4sHa/fsFleRTmhqZ9TyaO3giMHeUuzl4091n4nMZ/TZ6yYCZTXHVRpoqZ+IY5HMvbkrhZA6s66B7DSczRIPqpAEqhltnb3sVu+JM+PxMyMR+mh7x91sNTKQxx+YGpKPHC5W+Loa8+PdOZHHC0iZNs4MSB4xVdOEucChEfWOCi0+F3AIX7WQ3kiBlyPw7RJoGvK93Ayccx2v5+OBLUbaGNtsP3d5HgnOBy6pVwFMro1ogx4N86zj1eDOw+1fe3lMCw0EgGon/h3H5b9uRPgAbRjj3M7LN4xmGPjU5jDmPoJjnAlpShaG/Ds1MWf9gTrOWi8pXL97DGCQeBPvhc9Peyw="
            ));
            put("Slime", new Pair<>(
                    "ewogICJ0aW1lc3RhbXAiIDogMTcxNzEyMTQwNTg0OSwKICAicHJvZmlsZUlkIiA6ICJhYzA2Yjc3ZTY3YmE0NzFkYTQwNDllNmE5NGY3Nzg4YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGaWRpdm9jYXQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUzNDZjODg5OTdiYTQ4YWVmNmRkMDQ0ZDAxOTEzMmFhYzI5ZmZiMzIzYzQ4M2JjYmQ3Mzk5MjBiMjQ5OTYxMCIKICAgIH0KICB9Cn0=",
                    "h/abrCV1yGYj1qvqrCdBdovkxmbik2ZPS3im+s5RSOQoxzJevoo6i4CSq3DVf7dbKEg6JWuqKbyp+6q/xM1PP+1wWdR9vvjbYWnkJ2PRE2/8jKvFyS+76ihHU65RVwYx7q16Ta6kxGZVHW9UOpMQaWfu1tB8TB42MD3sWpH5UaPwVL9ETRmgqiDYVIth/lM17lEUmq/lU8OXP2u9DiafFJu2iDZLcLw6Xq4DivGPpfW17jCGtvK7WhJMwUhrFwBCwSrWyWqK/YU/TSEG/fZMFd8giBrq8gwDlFaumdUZaC/HTDN030Vr+HdZK72nWUGLk8cqM5fOcCAbIMJBz3EFiWJ/DWqcFC422r40+RMsTEcMVL4NMOVVjRuxrcjo1Soljzqj/8yRn6Pv4TxFXW6XPygfZqLGQhkit1CtVKG3TQblqVMvf6KTTvVOzlTneim4JdWz4MqrcHvUEcgABRQkGGW8ozEIiG44OKIRIvj9TJR6A4KwquM6k1rw/lXRjH1eev2y1ZAMFCUBuF9RyYbHqEtFwaD5JZHhDAXcAWaJCk8HHS/irSU4Y8HAbg2wjPMjckxOD8vnRvqWTiLNfoV7z5xyonPK2SfhMsUUowkJ2Or2L3rfmqMGcjLJvA58Zl3IKgluD35orgGxnayF+RRFDDFpRAl0MBs6CHwhTzk6dWQ="
            ));
        }};
    }

    public static List<String> getDisguiseNames() {
        return Arrays.asList(
                "Steve",
                "Alex",
                "Notch"
        );
    }

    protected static void start(ICoreAPI coreAPI) {
        if (initiated) {
            throw new IllegalStateException("Tried to start CoreAPI more than once.");
        }

        instance = coreAPI;
        initiated = true;
    }

}
