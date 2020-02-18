package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

public class DiscordGetUsersCommand implements DiscordCommand {
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        StringBuffer rm = new StringBuffer();
        mod.getGame().getServer().getOnlinePlayers().forEach(p -> {
            rm.append(p.getName());
            rm.append(System.lineSeparator());
        });
        rm.setLength(rm.length() - System.lineSeparator().length());
        message.reply(rm.toString());
    }
}
