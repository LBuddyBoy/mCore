package dev.minechase.core.api.util;

import com.google.gson.JsonObject;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.cache.UUIDCache;
import dev.lbuddyboy.commons.api.util.HTTPUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class UUIDUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");

    public static String getName(UUID uuid) {
        return CommonsAPI.getInstance().getUUIDCache().getName(uuid);
    }

    /**
     * Fetches an uuid asynchronously based on the UUID from the Mojang API
     *
     * @param name the existing name to query
     * @return A completable future with error handling
     */

    public static ExceptedFuture<UUID> fetchUUID(String name) {
        if (UUIDCache.getNamesToUuids().containsKey(name.toLowerCase())) return new ExceptedFuture<>(CompletableFuture.completedFuture(UUIDCache.getNamesToUuids().get(name.toLowerCase())));

        return new ExceptedFuture<>(HTTPUtils.requestAsync("https://api.mojang.com/users/profiles/minecraft/" + name).thenApplyAsync(
                (jsonResponse) -> {
                    if (jsonResponse == null) {
                        return null;
                    }

                    JsonObject json = APIConstants.PARSER.parse(jsonResponse).getAsJsonObject();
                    String uuidString = UUID_PATTERN.matcher(json.get("id").getAsString()).replaceAll("$1-$2-$3-$4-$5");
                    UUID uuid = UUID.fromString(uuidString);

                    CommonsAPI.getInstance().getUUIDCache().cache(uuid, name, true);

                    return UUID.fromString(uuidString);
                }
        ).exceptionally(throwable -> {
            if (throwable != null) throwable.printStackTrace();

            return null;
        }));
    }

    /**
     * Fetches a name asynchronously based on the UUID from the Mojang API
     *
     * @param uuid the existing uuid to query
     * @return A completable future with error handling
     */

    public static ExceptedFuture<String> fetchName(UUID uuid) {
        if (UUIDCache.getUuidToNames().containsKey(uuid)) return new ExceptedFuture<>(CompletableFuture.completedFuture(UUIDCache.getUuidToNames().get(uuid)));

        return new ExceptedFuture<>(HTTPUtils.requestAsync("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false").thenApplyAsync(
                (jsonResponse) -> {
                    JsonObject json = APIConstants.PARSER.parse(jsonResponse).getAsJsonObject();

                    return json.get("name").getAsString();
                }
        ).exceptionally(throwable -> {
            if (throwable != null) throwable.printStackTrace();

            return null;
        }));
    }

}
