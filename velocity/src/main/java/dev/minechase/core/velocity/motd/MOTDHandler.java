package dev.minechase.core.velocity.motd;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.velocity.CoreLocale;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.motd.impl.DefaultMOTD;
import dev.minechase.core.velocity.motd.impl.LockdownMOTD;
import dev.minechase.core.velocity.motd.model.MOTD;
import dev.minechase.core.velocity.motd.model.MOTDTimer;
import dev.lbuddyboy.commons.api.util.IModule;
import dev.minechase.core.velocity.util.CC;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd
 * @since 2/16/2024
 */

public class MOTDHandler implements IModule {

    private final List<MOTD> motds;
    private final List<IMOTD> motdImpls;
    @Getter private final Map<String, MOTDTimer> motdTimers;
    private Favicon favicon;
    @Getter private MOTD activeMOTD;
    @Getter private MOTDTimer activeMOTDTimer;
    private MongoCollection<Document> collection;

    public MOTDHandler() {
        this.motds = new ArrayList<>();
        this.motdTimers = new HashMap<>();
        this.motdImpls = Arrays.asList(
                new LockdownMOTD(),
                new DefaultMOTD()
        );
    }

    @Override
    public void load() {
        reload();

        CoreVelocity.getInstance().getProxy().getScheduler().buildTask(CoreVelocity.getInstance(), () -> {
            rotateMOTD();
            rotateMOTDTimer();
        }).repeat(5, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void unload() {
        this.motdTimers.values().forEach(this::saveMOTDTimer);
    }

    @Override
    public void reload() {
        this.collection = CoreVelocity.getInstance().getMongoHandler().getDatabase().getCollection("MOTDTimers");
        this.motdTimers.clear();
        this.motds.clear();

        for (Document document : this.collection.find()) {
            MOTDTimer timer = new MOTDTimer(document);

            this.motdTimers.put(timer.getName(), timer);
        }

        try {
            final File file = new File("server-icon.png");
            if (file.exists()) {
                favicon = Favicon.create(ImageIO.read(file));
            }
        } catch (final IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        this.loadMOTDs();
    }

    private void rotateMOTD() {
        if (this.motds.isEmpty()) return;

        if (this.activeMOTD == null) {
            this.activeMOTD = this.motds.get(0);
            return;
        }

        int motds = this.motds.size();
        int nextIndex = this.motds.indexOf(this.activeMOTD) + 1;

        if (nextIndex >= motds) nextIndex = 0;

        this.activeMOTD = this.motds.get(nextIndex);
    }

    private void rotateMOTDTimer() {
        List<MOTDTimer> activeMOTDTimers = this.motdTimers.values().stream().filter(MOTDTimer::isActive).toList();

        if (activeMOTDTimers.isEmpty()) {
            this.activeMOTDTimer = null;
            return;
        }

        if (this.activeMOTDTimer == null) {
            this.activeMOTDTimer = activeMOTDTimers.get(0);
            return;
        }

        int timersSize = activeMOTDTimers.size();
        int nextIndex = activeMOTDTimers.indexOf(this.activeMOTDTimer) + 1;

        if (nextIndex >= timersSize) nextIndex = 0;

        this.activeMOTDTimer = activeMOTDTimers.get(nextIndex);
    }

    private void loadMOTDs() {
        Configuration configuration = CoreVelocity.getInstance().getConfig();

        if (!configuration.contains("motds")) {
            this.loadDefaultMOTDs();
            loadMOTDs();
            return;
        }

        try {
            for (String key : configuration.getSection("motds").getKeys()) {
                motds.add(new MOTD(
                        configuration.getString("motds." + key + ".normal.line-one"),
                        configuration.getString("motds." + key + ".normal.line-two"),
                        configuration.getString("motds." + key + ".legacy.line-one"),
                        configuration.getString("motds." + key + ".legacy.line-two"),
                        Favicon.create(ImageIO.read(new File(configuration.getString("motds." + key + ".favicon"))))
                ));
                System.out.println("Loaded the " + key + " MOTD!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.loadDefaultMOTDs();
        }

        if (this.activeMOTD == null && !this.motds.isEmpty()) this.activeMOTD = this.motds.get(0);

    }

    private void loadDefaultMOTDs() {
        Configuration configuration = CoreVelocity.getInstance().getConfig();

        configuration.set("motds.example.normal.line-one", "<blend:#327637;#c9ff99e;false>→ Arrow MOTD Line One</>");
        configuration.set("motds.example.normal.line-two", "<blend:#327637;#c9ff99e;false>→ Arrow MOTD Line Two</>");
        configuration.set("motds.example.legacy.line-one", "&2→ &aArrow MOTD Line One");
        configuration.set("motds.example.legacy.line-two", "&2→ &aArrow MOTD Line Two");

        CoreVelocity.getInstance().getConfigFile().saveConfig();
    }

    public Optional<IMOTD> getAvailableMOTD() {
        return this.motdImpls.stream().filter(IMOTD::isActive).min(Comparator.comparingInt(IMOTD::getWeight));
    }

    public Optional<MOTDTimer> getMOTDTimer(String name) {
        return Optional.ofNullable(this.motdTimers.get(name));
    }

    public void createMOTDTimer(MOTDTimer timer) {
        this.motdTimers.put(timer.getName(), timer);
        this.saveMOTDTimer(timer);
    }

    public void deleteMOTDTimer(MOTDTimer timer) {
        this.motdTimers.remove(timer.getName());
        this.collection.deleteOne(Filters.eq("name", timer.getName()));
    }

    public void saveMOTDTimer(MOTDTimer timer) {
        Bson bson = Filters.eq("name", timer.getName());
        Document document = timer.toDocument();

        this.collection.replaceOne(bson, document, new ReplaceOptions().upsert(true));
        CoreVelocity.getInstance().getLogger().info("[MOTD Timer] Saving " + timer.getName() + "!");
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onPing(ProxyPingEvent event) {
        ServerPing.Builder pong = event.getPing().asBuilder();
        Optional<IMOTD> motdOpt = getAvailableMOTD();
        boolean activeTimer = this.activeMOTDTimer != null && this.activeMOTDTimer.isActive(), centeredText = CoreLocale.MOTD_TIMER_HEADER_CENTER_CONTEXT.getBoolean();

        if (motdOpt.isPresent()) {
            pong = motdOpt.get().generate(event);
        }

        if (CoreVelocity.getInstance().getLockdownHandler().isActive() ? activeTimer && CoreLocale.MOTD_TIMER_HEADER_OVERRIDE_LOCKDOWN.getBoolean() : activeTimer) {
            String header = CoreLocale.MOTD_TIMER_HEADER.getString(), footer = this.activeMOTDTimer.getContextFormatted();

            if (centeredText) footer = center(footer);

            pong.description(
                    CC.translate(header)
                    .appendNewline()
                    .append(CC.translate(footer))
            );
        }

        int onlinePlayers = 0;

        for (CoreServer server : CoreVelocity.getInstance().getServerHandler().getServers().values()) {
            if (server.getName().contains("Proxy")) continue;
            if (server.getName().equalsIgnoreCase("JDA")) continue;

            onlinePlayers += server.getPlayerCount();
        }

        pong.onlinePlayers(onlinePlayers);
        pong.maximumPlayers(1000);

        event.setPing(pong.build());
    }

    public String center(String text) {
        String stripped = ChatColor.stripColor(dev.lbuddyboy.commons.util.CC.translate(text));
        int messageWidth = stripped.length();
        int totalWidth = 53;
        int spacesNeeded = (totalWidth - messageWidth) / 2;

        return " ".repeat(spacesNeeded) +
                text;
    }

}
