package com.nguyenquyhy.discordbridge;

import com.nguyenquyhy.discordbridge.commands.TempBanCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ban.BanService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TempBanThread {
    private Map<String, Integer> tempbanlist = new ConcurrentHashMap<>();

    AtomicInteger ai = new AtomicInteger();
    AtomicBoolean ab = new AtomicBoolean();

    TimerTask exec = new TimerTask() {
        @Override
        public void run() {
            if(ab.get()) {
                ai.getAndIncrement();
                return;
            } else ai.set(1);

            ab.set(true);
            tempbanlist.forEach((g, t) -> {
                if (t < 1) {
                    Pardon pd = new Pardon(g);
                    pd.start();
                    try {
                        pd.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mod.getLogger().info("Pardoned : " + g);
                    tempbanlist.remove(g);
                } else tempbanlist.replace(g, t - ai.get());
            });
            ab.set(false);
        }
    };

    Timer timer = new Timer();

    BanService service;

    DiscordBridge mod;

    TempBanThread() {
        mod = DiscordBridge.getInstance();
        service = DiscordBridge.getInstance().getGame().getServiceManager().provide(BanService.class).get();
    }

    protected class Pardon extends Thread {
        private String u; public Pardon(String _u) { u = _u; }
        @Override
        public void run() {
            super.run();
            try {
                final GameProfile gp = Sponge.getServer().getGameProfileManager().get(UUID.fromString(u)).get();
                Task.builder().execute( () -> service.pardon(gp) ).submit(mod);
            }
            catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        }
    }

    public void start() {
        ai.set(1);
        ab.set(false);
        timer.scheduleAtFixedRate(exec, 0, 1000);
    }

    public void stop() {
        timer.cancel();
        AtomicReference<Pardon> k = new AtomicReference<>();
        tempbanlist.forEach( (g, t) -> { k.set(new Pardon(g)); k.get().start(); });
        if (k.get() != null) {
            try {
                k.get().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Integer> getBanned() {
        Map<String, Integer> result = new HashMap<>();
        tempbanlist.forEach((g, t) -> {
            if (t > 0) {
                try {
                    result.put(Sponge.getServer().getGameProfileManager().get(UUID.fromString(g)).get().getName().get(), t);
                }
                catch (InterruptedException e) { e.printStackTrace(); }
                catch (ExecutionException e) { e.printStackTrace(); }
            }
        });
        return result;
    }

    public void setTempBanList(GameProfile gp, int secs) {
        new TempBanCommand().execute(gp);
        tempbanlist.put(gp.getUniqueId().toString(), secs);
    }
}