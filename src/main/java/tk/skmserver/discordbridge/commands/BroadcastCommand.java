package tk.skmserver.discordbridge.commands;

import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.GlobalConfig;
import tk.skmserver.discordbridge.utils.ChannelUtil;
import tk.skmserver.discordbridge.utils.ErrorMessages;
import tk.skmserver.discordbridge.utils.TextUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Hy on 1/11/2016.
 */
public class BroadcastCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        String message = commandContext.<String>getOne("message").get();
        boolean sent = broadcast(commandSource, message);
        return sent ? CommandResult.success() : CommandResult.empty();
    }

    private boolean broadcast(CommandSource commandSource, String message) {
        DiscordBridge mod = DiscordBridge.getInstance();
        GlobalConfig config = mod.getConfig();
        Logger logger = mod.getLogger();

        DiscordApi defaultClient = mod.getBotClient();
        if (defaultClient == null) {
            commandSource.sendMessage(Text.of(TextColors.RED, "You have to set up a Bot token first!"));
            return false;
        }

        // Send to Discord
        config.channels.stream().filter(channelConfig -> StringUtils.isNotBlank(channelConfig.discordId)
                && channelConfig.discord != null
                && StringUtils.isNotBlank(channelConfig.discord.broadcastTemplate)).forEach(channelConfig -> {
            Optional<Channel> channel = defaultClient.getChannelById(channelConfig.discordId);
            if (channel.isPresent()) {
                String content = String.format(channelConfig.discord.broadcastTemplate,
                        TextUtil.escapeForDiscord(message, channelConfig.discord.broadcastTemplate, "%s"));
                ChannelUtil.sendMessage(channel.get(), content);
                logger.info("[BROADCAST DISCORD] " + message);
            } else {
                ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
            }
        });

        // Send to Minecraft
        if (StringUtils.isNotBlank(config.minecraftBroadcastTemplate)) {
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                player.sendMessage(Text.join(TextUtil.formatUrl(String.format(config.minecraftBroadcastTemplate, message))));
            }
            logger.info("[BROADCAST MINECRAFT] " + message);
        }
        return true;
    }
}
