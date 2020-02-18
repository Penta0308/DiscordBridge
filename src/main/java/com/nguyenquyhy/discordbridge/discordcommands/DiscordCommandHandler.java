package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
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
