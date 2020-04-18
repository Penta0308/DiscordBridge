package tk.skmserver.discordbridge.listeners;

import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.models.ChannelConfig;
import tk.skmserver.discordbridge.models.GlobalConfig;
import tk.skmserver.discordbridge.utils.ChannelUtil;
import tk.skmserver.discordbridge.utils.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;

public class DeathListener {
    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        DiscordBridge mod = DiscordBridge.getInstance();
        GlobalConfig config = mod.getConfig();
        DiscordApi client = mod.getBotClient();

        if (!(event.getTargetEntity() instanceof Player) || event.isMessageCancelled() || StringUtils.isBlank(event.getMessage().toPlain())) return;
        Player player = (Player) event.getTargetEntity();

        DiscordBridge.TempBan.setTempBanList(player.getProfile(), config.banDuration);

        if (client != null) {
            for (ChannelConfig channelConfig : config.channels) {
                if (StringUtils.isNotBlank(channelConfig.discordId) && channelConfig.discord != null) {
                    String template = ConfigUtil.get(channelConfig.discord.deathTemplate, null);
                    if (StringUtils.isNotBlank(template)) {
                        Optional<Channel> channel = client.getChannelById(channelConfig.discordId);
                        if(!channel.isPresent()) return;
                        ChannelUtil.sendMessage(channel.get(), template.replace("%s", event.getMessage().toPlain()));
                    }
                }
            }
        }
    }

}
