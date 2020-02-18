package com.nguyenquyhy.discordbridge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TempBanThread {
    private final Map<GameProfile, Integer> tempbanlist = new HashMap<>();

    final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    BanService service;

    TempBanThread() { service = Sponge.getServiceManager().provide(BanService.class).get(); }

    public void start() { exec.scheduleAtFixedRate(() -> {
            try {
                tempbanlist.forEach((g, t) -> {
                    if(t < 1) {
                        service.pardon(g);
                        tempbanlist.remove(g);
                    } else tempbanlist.put(g, t - 1);
                });
            } catch (Exception e) {
                e.printStackTrace();
            } }, 0, 1, TimeUnit.SECONDS); }

    public void stop() {
        exec.shutdown();
        tempbanlist.forEach((g, t) -> service.pardon(g));
    }

    public Map<String, Integer> getBanned() {
        Map<String, Integer> result = new HashMap<>();
        tempbanlist.forEach((g, t) -> result.put(g.getName().get(), t));
        return result;
    }

    public void setTempBanList(GameProfile gp, int secs) { tempbanlist.put(gp, secs); }
}