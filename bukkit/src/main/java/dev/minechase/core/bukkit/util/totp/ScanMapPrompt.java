package dev.minechase.core.bukkit.util.totp;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class ScanMapPrompt extends StringPrompt {

    private static final SecureRandom SECURE_RANDOM;
    private static final Base32 BASE_32_ENCODER = new Base32();

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("This should never happen");
        }
    }

    private int failures = 0;

    @Override
    public String getPromptText(ConversationContext context) {
        Player player = (Player) context.getForWhom();

        if (this.failures != 0) {
            return CC.translate("&cOn your 2FA device, scan the map given to you. Once you've scanned the map, type the code displayed on your device in chat.");
        }

        Tasks.runAsync(() -> {
            String secret = generateSecret();
            BufferedImage image = generateImage(player, secret);

            if (image != null) {
                MapView mapView = Bukkit.getServer().createMap(player.getWorld());
                mapView.getRenderers().forEach(mapView::removeRenderer);
                mapView.addRenderer(new QCodeMapRenderer(player.getUniqueId(), image));

                ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
                MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

                mapMeta.setMapView(mapView);
                mapMeta.setLore(Collections.singletonList("QR Code Map"));
                mapItem.setItemMeta(mapMeta);

                context.setSessionData("secret", secret);

                player.sendMap(mapView);
                player.getInventory().setItemInOffHand(mapItem);
            }
        });

        return CC.translate("&cOn your 2FA device, scan the map given to you. Once you've scanned the map, type the code displayed on your device in chat.");
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String secret = (String) context.getSessionData("secret");

        Player player = (Player) context.getForWhom();
        player.getInventory().setItemInOffHand(null);

        int code;
        try {
            code = Integer.parseInt(input.replace(" ", ""));
        } catch (NumberFormatException e) {
            if (this.failures++ >= 3) {
                for (String message : Arrays.asList(
                        "&cCancelling 2FA setup due to too many incorrect codes.",
                        "&cContact the staff team for any questions you have about 2FA."
                )) {
                    context.getForWhom().sendRawMessage(CC.translate(message));
                }

                return Prompt.END_OF_CONVERSATION;
            }

            context.getForWhom().sendRawMessage("");
            context.getForWhom().sendRawMessage(ChatColor.RED + "'" + input + "' isn't a valid code. Let's try that again.");
            return this;
        }

        context.getForWhom().sendRawMessage(CC.translate("&aYour 2FA is now set up."));
        TwoFactorUtil.release(player);

        Tasks.runAsync(() -> {
            User user = CorePlugin.getInstance().getUserHandler().getUser(player.getUniqueId());

            user.getPersistentMetadata().setInteger(CoreConstants.TOTP_CODE_KEY, code);
            user.getPersistentMetadata().set(CoreConstants.TOTP_SECRET_KEY, secret);
            user.getPersistentMetadata().setBoolean(CoreConstants.TOTP_SETUP_KEY, true);

            user.save(true);
        });

        return Prompt.END_OF_CONVERSATION;
    }

    private BufferedImage generateQRCodeImage(String data) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 130, 130);

        BufferedImage image = new BufferedImage(130, 130, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 130; x++) {
            for (int y = 0; y < 130; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
    }

    private BufferedImage generateImage(Player player, String secret) {
        String issuer = "MineChase Network";
        String url = "otpauth://totp/" + player.getName() + "?secret=" + secret + "&issuer=" + issuer;

        try {
            return generateQRCodeImage(url);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String generateSecret() {
        byte[] secretKey = new byte[10];
        SECURE_RANDOM.nextBytes(secretKey);
        return BASE_32_ENCODER.encodeToString(secretKey);
    }

}