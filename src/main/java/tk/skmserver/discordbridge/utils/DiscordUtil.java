package tk.skmserver.discordbridge.utils;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.permission.Role;
//import org.apache.http.util.TextUtils;

import java.util.Optional;

public class DiscordUtil {

    /**
     * @param name   The name to search the server for valid a User
     * @param server The server to search through Users
     * @return The User, if any, that matches the name supplied
     */
    static Optional<User> getUserByName(String name, Server server) {
        for (User user : server.getMembers()) {
            if (user.getName().equalsIgnoreCase(name) || (user.getNickname(server).isPresent() && user.getNickname(server).get().equalsIgnoreCase(name))) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * @param name   The name to search the server for valid a Role
     * @param server The server to search through Roles
     * @return The Role, if any, that matches the name supplied
     */
    static Optional<Role> getRoleByName(String name, Server server) {
        for (Role role : server.getRoles()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }

    /**
     * @param name   The name to search the server for valid a Channel
     * @param server The server to search through Roles
     * @return The Channel, if any, that matches the name supplied
     */
    static Optional<Channel> getChannelByName(String name, Server server) {
        for (Channel channel : server.getChannels()) {
            Optional<ServerTextChannel> textChannel = channel.asServerTextChannel();
            if (!textChannel.isPresent()) continue;
            if (textChannel.get().getName().equalsIgnoreCase(name)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }
}
