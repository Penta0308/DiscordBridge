package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;

public class DiscordGetTimeCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        String day = String.valueOf((int)Math.floor(mod.getGame().getServer().getRunningTimeTicks() / 2400.0d));
        int min = mod.getGame().getServer().getRunningTimeTicks() % 2400;
        message.reply("Day " + day + " " + String.valueOf((int)Math.floor(min / 100.0d)) + ":" + String.valueOf((int)Math.floor(min % 100 * (60.0d / 100.0d) )));
    }
}
