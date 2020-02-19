package com.nguyenquyhy.discordbridge.discordcommands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import de.btobastian.javacord.entities.message.Message;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.whitelist.WhitelistService;

import java.util.concurrent.ExecutionException;

public class DiscordWhiteListAddCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        final boolean[] wasThere = { false };
        message.getAuthor().getRoles(message.getChannelReceiver().getServer()).forEach(r -> {
            if(r.getId().equals(config.adminTags) && !wasThere[0]) {
                try {
                    String u = message.getContent().split(" ")[1];
                    GameProfile g = Sponge.getServer().getGameProfileManager().get(u).get();
                    Task.builder().execute( () ->
                            Sponge.getGame().getServiceManager().provideUnchecked(WhitelistService.class).addProfile(g)
                    ).submit(mod);
                    message.reply("Added!");
                    wasThere[0] = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return;
            }
        });
        if(!wasThere[0]) message.reply("You're Not Admin!");
    }
}
