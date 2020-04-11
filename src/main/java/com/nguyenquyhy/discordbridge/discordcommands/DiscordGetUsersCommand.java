package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;

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
        if(c.get() == 0) { message.reply("0"); }
        else { message.reply(rm.toString()); }
    }
}
