package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.whitelist.WhitelistService;

public class DiscordWhiteListAddCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        message.getAuthor().getRoles(message.getChannelReceiver().getServer()).forEach(r -> {
            if(r.getMentionTag().equals(config.adminTags)) Sponge.getCommandManager().process(DiscordBridge.getInstance().getGame().getServer().getConsole(), "whitelist add " + message.getContent().split(" ")[1]);
        });
    }
}
