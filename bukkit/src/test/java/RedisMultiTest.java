import com.google.gson.JsonArray;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.CommonsAPI;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.model.GlobalChatMessage;

import java.util.UUID;

public class RedisTest {

    public static TestCommons commons;

    public static void main(String[] args) {
        commons = new TestCommons();

        commons.getModules().forEach(IModule::load);

        for (int i = 0; i < 20; i++) {

        }

        String serverName = "Test";
        GlobalChatMessage message = new GlobalChatMessage(
                UUID.randomUUID(),
                "hahasike",
                serverName,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU5Njg4Yjk1MGQ4ODBiNTViN2FhMmNmY2Q3NmU1YTBmYTk0YWFjNmQxNmY3OGU4MzNmNzQ0M2VhMjlmZWQzIn19fQ==",
                "Fat Message",
                System.currentTimeMillis()
        );

        commons.getRedisHandler().executeCommand(redis -> {
            JsonArray messages = new JsonArray();

            if (redis.hexists("mCoreMessages", serverName)) {
                String messagesString = redis.hget("mCoreMessages", serverName);
                messages = APIConstants.PARSER.parse(messagesString).getAsJsonArray();
            }

            messages.add(message.toJSON());
            redis.hset("mCoreMessages", serverName, messages.toString());

            return null;
        });

        commons.getRedisHandler().publish(
                "mCoreMessages",
                message.toJSON()
        );
    }

}
