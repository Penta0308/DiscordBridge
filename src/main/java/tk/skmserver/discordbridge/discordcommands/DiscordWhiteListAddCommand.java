package tk.skmserver.discordbridge.discordcommands;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.user.User;
import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.whitelist.WhitelistService;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DiscordWhiteListAddCommand implements DiscordCommand {
    @Override
    public void run(Message message, DiscordBridge mod, Logger logger, GlobalConfig config) {
        Optional<ServerTextChannel> textChannel = message.getServerTextChannel();
        if(!textChannel.isPresent()) return;
        final boolean[] wasThere = { false };
        Optional<User> userAuthor = message.getUserAuthor();
        Optional<org.javacord.api.entity.server.Server> server = message.getServer();
        if(!userAuthor.isPresent() || !server.isPresent()) return;
        userAuthor.get().getRoles(server.get()).forEach(r -> {
            if(r.getIdAsString().equals(config.adminTags) && !wasThere[0]) {
                try {
                    String u = message.getContent().split(" ")[1];
                    GameProfile g = Sponge.getServer().getGameProfileManager().get(u).get();
                    Task.builder().execute( () ->
                            Sponge.getGame().getServiceManager().provideUnchecked(WhitelistService.class).addProfile(g)
                    ).submit(mod);
                    textChannel.get().sendMessage("Added!");
                    wasThere[0] = true;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        if(!wasThere[0]) textChannel.get().sendMessage("You're Not Admin!");
    }
}
