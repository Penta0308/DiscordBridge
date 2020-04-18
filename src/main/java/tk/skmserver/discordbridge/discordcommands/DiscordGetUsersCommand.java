package tk.skmserver.discordbridge.discordcommands;

import org.javacord.api.entity.channel.ServerTextChannel;
import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscordGetUsersCommand implements DiscordCommand {
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        AtomicInteger c = new AtomicInteger();
        StringBuilder rm = new StringBuilder();
        mod.getGame().getServer().getOnlinePlayers().forEach(p -> {
            rm.append(p.getName());
            rm.append(System.lineSeparator());
            c.getAndIncrement();
        });
        Optional<ServerTextChannel> textChannel = message.getServerTextChannel();
        if(!textChannel.isPresent()) return;
        if(c.get() == 0) textChannel.get().sendMessage("0");
        else textChannel.get().sendMessage(rm.toString());
    }
}
