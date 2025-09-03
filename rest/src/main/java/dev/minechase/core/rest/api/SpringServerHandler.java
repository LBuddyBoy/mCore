package dev.minechase.core.rest.api;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.server.ServerHandler;

import java.util.Timer;
import java.util.TimerTask;

public class SpringServerHandler extends ServerHandler {

    @Override
    public void load() {
        super.load();

        if (getLocalServer() == null) return;

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                getLocalServer().setStartedAt(System.currentTimeMillis());
                CoreAPI.getInstance().updateLocalServer();
            }
        }, 500L);
    }
}
