import com.google.gson.JsonArray;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.api.sync.model.GlobalChatMessage;

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
                "LBuddyBoy",
                serverName,
                "Lorem ipsum dolor sit amet consectetur adipisicing elit. Saepe, facere architecto ea totam molestiae recusandae eum excepturi eos vero a ipsum laboriosam mollitia commodi ipsa quisquam. Hic, id. Quisquam, rerum.",
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
