package dev.minechase.core.velocity.instance.impl;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.minechase.core.api.server.model.CoreServer;
import dev.minechase.core.api.server.packet.ServerDeletePacket;
import dev.minechase.core.api.server.packet.ServerUpdatePacket;
import dev.minechase.core.velocity.CoreVelocity;
import dev.minechase.core.velocity.instance.model.InstanceType;
import dev.minechase.core.velocity.util.CC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ethan Toups (https://www.ethantoups.dev/)
 * @version 1.0
 * @since 8/23/2025
 */
public class SkyblockInstance implements InstanceType {

    @Override
    public String getId() {
        return "skyblock";
    }

    @Override
    public int createInstance(int amount) {
        int created = 0;

        for (int i = 0; i < amount; i++) {
            String name = createServerName(); // your method
            int port = CoreVelocity.getInstance().getInstanceHandler().findAvailablePort();
            ProcessBuilder pb = new ProcessBuilder(
                    "/bin/bash",
                    "/home/ubuntu/@upload/instance_create.sh",
                    name, "127.0.0.1", String.valueOf(port), this.getId()
            );

            pb.environment().put("DEBUG", "1");
            pb.environment().put("LOG_TO_FILE", "1");
            pb.redirectErrorStream(true);

            Process p = null;
            String jsonLine = null;
            try {
                p = pb.start();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        if (line.startsWith("{") && line.endsWith("}")) {
                            jsonLine = line;
                        }
                    }
                }

                if (!p.waitFor(2, TimeUnit.MINUTES)) {
                    p.destroyForcibly();
                    throw new IllegalStateException("Instance creation timed out");
                }

                int code = p.exitValue();
                if (code != 0) {
                    throw new IllegalStateException("Instance creation exited with code " + code);
                }

                if (jsonLine == null) {
                    throw new IllegalStateException("Instance creation produced no JSON line");
                }

                CoreServer server = new CoreServer(name);

                server.setPort(port);
                server.setHost("localhost");

                CoreVelocity.getInstance().getServerHandler().getServers().put(name, server);

                CoreVelocity.getInstance().getLogger().info("Instance created: " + name + " (" + ++created + " Total)");

            } catch (Exception e) {
                if (p != null) p.destroyForcibly();
                throw new RuntimeException("Failed to create instance " + name + " on port " + port, e);
            }
        }

        return created;
    }

    @Override
    public int deleteInstanceAmount(int amount) {
        List<CoreServer> candidates = new ArrayList<>(this.getInstances());

        candidates.sort(Comparator.comparing(CoreServer::getName).reversed());

        int deleted = 0;
        for (CoreServer s : candidates) {
            if (deleted >= amount) break;

            try {
                deleteInstanceByName(s.getName());
                deleted++;
            } catch (Exception e) {
                CoreVelocity.getInstance().getLogger().info("Failed to delete instance " + s.getName() + " on port " + s.getPort());
                CoreVelocity.getInstance().getLogger().log(Level.SEVERE, "Deleting failed", e);
            }
        }

        if (deleted < amount) {
            throw new IllegalStateException("Requested " + amount + " deletions, but only " + deleted +
                    " servers of type '" + getId() + "' were found.");
        }

        return deleted;
    }

    @Override
    public void deleteInstanceByName(String name) {
        CoreServer server = CoreVelocity.getInstance().getServerHandler().getServer(name);

        if (server == null) {
            throw new IllegalArgumentException("Server " + name + " not found");
        }

        ProxyServer proxy = CoreVelocity.getInstance().getProxy();
        Optional<RegisteredServer> reg = proxy.getServer(name);

        reg.ifPresent(rs -> {
            List<CoreServer> hubs = CoreVelocity.getInstance().getHubs();

            rs.getPlayersConnected().forEach(p -> {
                if (hubs.isEmpty()) {
                    p.sendMessage(CC.translate("<blend:&4;&c>[Fallback Error] We couldn't find a hub server to connect you to.</>"));
                    p.disconnect(CC.translate("<blend:&4;&c>[Fallback Error] We couldn't find a hub server to connect you to.</>"));
                    return;
                }

                CoreVelocity.getInstance().getServerHandler().sendPlayerToServer(p.getUniqueId(), hubs.getFirst().getName());
            });

            proxy.unregisterServer(rs.getServerInfo());
        });

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash",
                "/home/ubuntu/@upload/instance_delete.sh",
                name, this.getId()
        );
        pb.redirectErrorStream(true);

        Process p = null;
        String lastJson = null;
        try {
            p = pb.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    CoreVelocity.getInstance().getLogger().info("[delete:" + name + "] " + line);
                    if (line.startsWith("{") && line.endsWith("}")) {
                        lastJson = line;
                    }
                }
            }

            if (!p.waitFor(60, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                throw new IllegalStateException("Deletion timed out for " + name);
            }
            if (p.exitValue() != 0) {
                throw new IllegalStateException("Deletion script exit code " + p.exitValue() + " for " + name);
            }

            if (lastJson == null)
                return;

            if (!parseDeletedFlag(lastJson)) {
                throw new IllegalStateException(
                        "Deletion script reported failure for " + name + ": " + lastJson
                );
            }

        } catch (Exception e) {
            if (p != null) p.destroyForcibly();
            throw new RuntimeException("Failed to delete instance " + name, e);
        }

        new ServerDeletePacket(server).sendDelayed();
        CoreVelocity.getInstance().getLogger().info("Deleted instance and unregistered backend: " + name);
    }

    private static boolean parseDeletedFlag(String json) {
        Pattern p = Pattern.compile("\"deleted\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(json);

        return m.find() && Boolean.parseBoolean(m.group(1));
    }
}
