package tk.skmserver.discordbridge.discordcommands;

import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;

public interface DiscordCommand {
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config);
}
