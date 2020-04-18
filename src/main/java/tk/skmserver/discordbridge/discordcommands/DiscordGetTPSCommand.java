package tk.skmserver.discordbridge.discordcommands;

import org.javacord.api.entity.channel.ServerTextChannel;
import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;

import java.util.Optional;

public class DiscordGetTPSCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        Optional<ServerTextChannel> textChannel = message.getServerTextChannel();
        textChannel.ifPresent(serverTextChannel -> serverTextChannel.sendMessage(String.valueOf(mod.serverState.getTPS())));
    }
}
