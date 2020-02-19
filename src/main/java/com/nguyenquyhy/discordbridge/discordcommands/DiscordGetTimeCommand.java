package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;

public class DiscordGetTimeCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        message.reply("Day " + mod.serverState.getDay() + " " + mod.serverState.getHour() + ":" + mod.serverState.getMin());
    }
}
