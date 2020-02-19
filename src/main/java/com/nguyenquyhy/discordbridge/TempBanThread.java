package com.nguyenquyhy.discordbridge;

import com.nguyenquyhy.discordbridge.commands.TempBanCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TempBanThread {
    private final Map<String, Integer> tempbanlist = new HashMap<>();

    private final Map<String, GameProfile> gplist = new HashMap<>();

    final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    BanService service;

    TempBanThread() { service = Sponge.getServiceManager().provide(BanService.class).get(); }

    public void start() { exec.scheduleAtFixedRate(() -> {
            try {
                tempbanlist.forEach((g, t) -> {
                    if(t == 0) {
                        service.pardon(gplist.get(g));
                        DiscordBridge.getInstance().getLogger().info("Pardoning : " + g);
                        tempbanlist.put(g, -1);
                    } else if (t == -1) {}
                    else tempbanlist.put(g, t - 1);
                });
            } catch (Exception e) {
                e.printStackTrace();
            } }, 0, 1, TimeUnit.SECONDS); }

    public void stop() {
        exec.shutdown();
        tempbanlist.forEach((g, t) -> service.pardon(gplist.get(g)));
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
        gplist.put(gp.getName().orElse(""), gp);
    }
}