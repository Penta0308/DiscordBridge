package com.nguyenquyhy.discordbridge;

import org.spongepowered.api.Sponge;

public class ServerState {
    public long getTick() { return Sponge.getServer().getDefaultWorld().orElse(null).getWorldTime(); }
    public long getDay() { return (long)Math.floor(getTick() / 24000.0d); }
    public long getHour() { return (long)((Math.floor((getTick() % 24000) / 1000.0d) + 6) % 24); }
    public long getMin() { return (long)(Math.floor(getTick() % 24000) % 1000); }
    public double getTPS() { return Sponge.getServer().getTicksPerSecond(); }
}
