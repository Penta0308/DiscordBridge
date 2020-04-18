package tk.skmserver.discordbridge.utils;

import org.javacord.api.entity.channel.Channel;

import java.io.File;
import java.util.Random;

/**
 * Created by Hy on 12/4/2016.
 */
public class ChannelUtil {
    public static final String SPECIAL_CHAR = "\u2062";
    public static final String BOT_RANDOM = String.valueOf(new Random().nextInt(100000));

    public static void sendMessage(Channel channel, String content) {
        channel.asServerTextChannel().get().sendMessage(content, null, false, SPECIAL_CHAR + BOT_RANDOM, (File) null);
    }
}
