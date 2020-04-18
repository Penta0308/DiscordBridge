package tk.skmserver.discordbridge.discordcommands;

import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DiscordCommandHandler {
    protected Map<String, DiscordCommand> discordCommandList = new HashMap<>();

    public void registerDiscordCommand(String msg, DiscordCommand cmd) { discordCommandList.put(msg, cmd); }

    public void discordMessageReceived(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        String command = message.getContent().split(" ")[0];
        if(discordCommandList.containsKey(command)) discordCommandList.get(command).run(message, mod, logger, config);
    }
}
