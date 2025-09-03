import com.google.gson.JsonArray;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.sync.model.GlobalChatMessage;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RedisMultiTest {

    public static TestCommons commons;

    public static void main(String[] args) {
        commons = new TestCommons();

        commons.getModules().forEach(IModule::load);

        String serverName = "Sims";
        List<String> randomNames = Arrays.asList(
                "LBuddyBoy",
                "Premieres",
                "Woofless",
                "Dream",
                "hahasike"
        );

        for (int i = 0; i < 20; i++) {
            GlobalChatMessage message = new GlobalChatMessage(
                    UUID.randomUUID(),
                    randomNames.get(ThreadLocalRandom.current().nextInt(randomNames.size())),
                    serverName,
                    "Random Multi Message #" + ThreadLocalRandom.current().nextInt(1000),
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

}
