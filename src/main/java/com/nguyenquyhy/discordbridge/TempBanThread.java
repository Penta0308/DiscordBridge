package com.nguyenquyhy.discordbridge;

import com.nguyenquyhy.discordbridge.commands.TempBanCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ban.BanService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TempBanThread {
    private Map<String, Integer> tempbanlist = new HashMap<>();

    final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    BanService service;

    TempBanThread() { service = Sponge.getServiceManager().provide(BanService.class).get(); }

    protected class Pardon extends Thread {
        private String u; public Pardon(String _u) { u = _u; }
        @Override
        public void run() {
            super.run();
            try {
                final GameProfile gp = Sponge.getServer().getGameProfileManager().get(u).get();
                Task.builder().execute( () -> service.pardon(gp) ).submit(DiscordBridge.getInstance());
            }
            catch (InterruptedException e) { e.printStackTrace(); }
            catch (ExecutionException e) { e.printStackTrace(); }
        }
    }

    public void start() {
        exec.scheduleAtFixedRate(() -> tempbanlist.forEach((g, t) -> {
            if (t < 1) {
                new Pardon(g).start();
                DiscordBridge.getInstance().getLogger().info("Pardoned : " + g);
                tempbanlist.remove(g);
            } else tempbanlist.replace(g, t - 1);
        }), 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        exec.shutdown();
        tempbanlist.forEach( (g, t) -> new Pardon(g).start() );
    }

    public Map<String, Integer> getBanned() {
        Map<String, Integer> result = new HashMap<>();
        tempbanlist.forEach((g, t) -> {
            if (t > 0) result.put(g, t);
        });
        return result;
    }

    public void setTempBanList(GameProfile gp, int secs) {
        new TempBanCommand().execute(gp);
        tempbanlist.put(gp.getName().orElse(""), secs);
    }
}