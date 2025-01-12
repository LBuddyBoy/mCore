package dev.minechase.core.jda.api;

import dev.minechase.core.api.server.ServerHandler;
import dev.minechase.core.jda.CoreBot;

import java.util.Timer;
import java.util.TimerTask;

public class JDAServerHandler extends ServerHandler {

    @Override
    public void load() {
        super.load();

        if (getLocalServer() == null) return;

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                getLocalServer().setStartedAt(System.currentTimeMillis());
                CoreBot.getInstance().updateLocalServer();
            }
        }, 500L);
    }
}
