import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SyncTest {

    public static void main(String[] args) {
        UUID playerUUID = UUID.fromString("2732a2e3-2641-4888-81e7-de4282debeea");
        CorePlugin.getInstance().getWebsiteSyncHandler().getSyncInformation(playerUUID).whenCompleteAsync(((information, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (information != null) {
                System.out.println("Your account is already synced to: '\" + information.getWebsiteUserId() + \"'");
                return;
            }

            SyncCode syncCode = CorePlugin.getInstance().getWebsiteSyncHandler().getSyncCode(playerUUID);

            if (syncCode != null) {
                System.out.println("You already have a sync code: " + syncCode.getCode());
                return;
            }

            syncCode = new SyncCode(playerUUID, generateWebsiteCode());

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


    public static int generateWebsiteCode() {
        int random = ThreadLocalRandom.current().nextInt(99999);

        if (CorePlugin.getInstance().getWebsiteSyncHandler().getSyncCode(random) != null) return generateWebsiteCode();

        return random;
    }

}
