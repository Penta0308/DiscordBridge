package tk.skmserver.discordbridge;

import org.spongepowered.api.Sponge;

public class ServerState {
    public long getTick() { return Sponge.getServer().getDefaultWorld().get().getWorldTime(); }
    public long getDay() { return (long)Math.floor(getTick() / 24000.0d); }
    public long getHour() { return (long)((Math.floor((getTick() % 24000) / 1000.0d) + 6) % 24); }
    public long getMin() { return (long)Math.floor((getTick() % 1000) * 60.0d / 1000.0d); }
    public double getTPS() { return Sponge.getServer().getTicksPerSecond(); }
}
