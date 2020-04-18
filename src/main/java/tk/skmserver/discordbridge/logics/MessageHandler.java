package tk.skmserver.discordbridge.logics;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.ChannelConfig;
import tk.skmserver.discordbridge.models.ChannelMinecraftConfigCore;
import tk.skmserver.discordbridge.models.GlobalConfig;
import tk.skmserver.discordbridge.utils.TextUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.permission.Role;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Hy on 8/6/2016.
 */
public class MessageHandler {
    /**
     * Forward Discord messages to Minecraft
     */
    public static void discordMessageReceived(MessageCreateEvent messageCreateEvent) {
        Message message = messageCreateEvent.getMessage();
        DiscordBridge mod = DiscordBridge.getInstance();
        Logger logger = mod.getLogger();
        GlobalConfig config = mod.getConfig();

        for (ChannelConfig channelConfig : config.channels) {
            if (config.prefixBlacklist != null) {
                for (String prefix : config.prefixBlacklist) {
                    if (StringUtils.isNotBlank(prefix) && message.getContent().startsWith(prefix)) {
                        return;
                    }
                }
            }
            if (config.ignoreBots && message.getAuthor().isBotUser()) {
                return;
            }
            //if (message.getNonce() != null && message.getNonce().equals(ChannelUtil.SPECIAL_CHAR + ChannelUtil.BOT_RANDOM)) {
            //    return;
            //}
            if (StringUtils.isNotBlank(channelConfig.discordId)
                    && channelConfig.minecraft != null
                    && message.getChannel() != null
                    && message.getChannel().getIdAsString().equals(channelConfig.discordId)) {

                mod.discordCommandHandler.discordMessageReceived(message, mod, logger, config);

                // Role base configuration
                ChannelMinecraftConfigCore minecraftConfig = channelConfig.minecraft;
                if (channelConfig.minecraft.roles != null) {
                    Optional<User> userAuthor = message.getUserAuthor();
                    Optional<Server> server = message.getServer();
                    if(!userAuthor.isPresent() || !server.isPresent()) return;
                    Collection<Role> roles = userAuthor.get().getRoles(server.get());
                    for (String roleName : channelConfig.minecraft.roles.keySet()) {
                        if (roles.stream().anyMatch(r -> r.getName().equals(roleName))) {
                            ChannelMinecraftConfigCore roleConfig = channelConfig.minecraft.roles.get(roleName);
                            roleConfig.inherit(channelConfig.minecraft);
                            minecraftConfig = roleConfig;
                            break;
                        }
                    }
                }

                if (StringUtils.isNotBlank(minecraftConfig.chatTemplate)) {
                    Text messageText = TextUtil.formatForMinecraft(minecraftConfig, message);

                    // Format attachments
                    if (minecraftConfig.attachment != null
                            && StringUtils.isNotBlank(minecraftConfig.attachment.template)
                            && message.getAttachments() != null) {
                        for (MessageAttachment attachment : message.getAttachments()) {
                            String spacing = StringUtils.isBlank(message.getContent()) ? "" : " ";
                            Text.Builder builder = Text.builder()
                                    .append(TextSerializers.FORMATTING_CODE.deserialize(spacing + minecraftConfig.attachment.template));
                            if (minecraftConfig.attachment.allowLink)
                                builder = builder.onClick(TextActions.openUrl(attachment.getUrl()));
                            if (StringUtils.isNotBlank(minecraftConfig.attachment.hoverTemplate))
                                builder = builder.onHover(TextActions.showText(Text.of(minecraftConfig.attachment.hoverTemplate)));
                            messageText = Text.join(messageText, builder.build());
                        }
                    }

                    Text formattedMessage = messageText;
                    // This case is used for default account
                    logger.info(formattedMessage.toPlain());
                    Sponge.getServer().getOnlinePlayers().forEach(p -> p.sendMessage(formattedMessage));
                }
            }
        }
    }
}
