package tk.skmserver.discordbridge.logics;

import com.google.common.util.concurrent.FutureCallback;
/*import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;*/
import org.javacord.api.AccountType;
import org.javacord.api.DiscordApiBuilder;
import tk.skmserver.discordbridge.DiscordBridge;
import tk.skmserver.discordbridge.database.IStorage;
import tk.skmserver.discordbridge.models.ChannelConfig;
import tk.skmserver.discordbridge.models.GlobalConfig;
import tk.skmserver.discordbridge.utils.ChannelUtil;
import tk.skmserver.discordbridge.utils.ErrorMessages;
import org.javacord.api.DiscordApi;
import org.javacord.api.Javacord;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.MessageCreateListener;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hy on 8/6/2016.
 */
public class LoginHandler {
    private static DiscordBridge mod = DiscordBridge.getInstance();
    private static Logger logger = mod.getLogger();
    private static boolean loggedin = false;

    public static boolean loginBotAccount() {
        if(loggedin) return true;
        GlobalConfig config = mod.getConfig();

        if (StringUtils.isBlank(config.botToken)) {
            logger.warn("No Bot token is available! Messages can only get from and to authenticated players.");
            return false;
        }

        DiscordApi defaultClient = mod.getBotClient();
        if (defaultClient != null && defaultClient.getToken().equals(config.botToken)) {
            return true;
        }

        if (defaultClient != null) {
            defaultClient.disconnect();
        }

        logger.info("Logging in to bot Discord account...");

        DiscordApiBuilder clientBuilder = new DiscordApiBuilder();
        clientBuilder = clientBuilder.setAccountType(AccountType.BOT).setToken(config.botToken);
        CompletableFuture<DiscordApi> discordApiCompletableFuture = clientBuilder.login();
        /*discordApiCompletableFuture.join();
        DiscordApi client = null;
        try {
            client = discordApiCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        prepareBotClient(discordApiCompletableFuture, null);
        loggedin = true;
        return true;
    }

    /**
     * @param player
     * @return
     */
    /*public static boolean loginHumanAccount(Player player) {
        IStorage storage = mod.getStorage();

        if (storage != null) {
            String cachedToken = mod.getStorage().getToken(player.getUniqueId());
            if (StringUtils.isNotBlank(cachedToken)) {
                player.sendMessage(Text.of(TextColors.GRAY, "Logging in to Discord..."));

                DiscordApi client = mod.getHumanClients().get(player.getUniqueId());
                if (client != null) {
                    client.disconnect();
                } else {
                    client = Javacord.getApi(cachedToken, false);
                }

                prepareHumanClient(client, player);
                return true;
            }
        }
        return false;
    }*/

    private static Map<CommandSource, String> MFA_TICKETS = new HashMap<>();

    /*public static CommandResult login(CommandSource commandSource, String email, String password) {
        logout(commandSource, true);

        try {
            HttpResponse<JsonNode> response = Unirest.post("https://discordapp.com/api/v6/auth/login")
                    .header("content-type", "application/json")
                    .body(new JSONObject().put("email", email).put("password", password))
                    .asJson();
            if (response.getStatus() != 200) {
                DiscordBridge.getInstance().getLogger().info("Auth response {} code with: {}", response.getStatus(), response.getBody());
                commandSource.sendMessage(Text.of(TextColors.RED, "Wrong email or password!"));
                return CommandResult.empty();
            }
            JSONObject result = response.getBody().getObject();
            if (result.has("mfa") && result.getBoolean("mfa")) {
                MFA_TICKETS.put(commandSource, result.getString("ticket"));
                commandSource.sendMessage(Text.of(TextColors.GREEN, "Additional authorization required! Please type '/discord otp <code>' within a code from your authorization app"));
            } else if (result.has("token")) {
                String token = result.getString("token");
                prepareHumanClient(Javacord.getApi(token, false), commandSource);
            } else {
                commandSource.sendMessage(Text.of(TextColors.RED, "Unexpected error!"));
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            commandSource.sendMessage(Text.of(TextColors.RED, "Unexpected error!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }*/

    /*public static CommandResult otp(CommandSource commandSource, int code) {
        String ticket = MFA_TICKETS.remove(commandSource);
        if (ticket == null) {
            commandSource.sendMessage(Text.of(TextColors.RED, "No OTP auth queued!"));
            return CommandResult.empty();
        }
        try {
            HttpResponse<JsonNode> response = Unirest.post("https://discordapp.com/api/v6/auth/mfa/totp")
                    .header("content-type", "application/json")
                    .body(new JSONObject().put("code", String.format("%06d", code)).put("ticket", ticket))
                    .asJson();
            if (response.getStatus() != 200) {
                commandSource.sendMessage(Text.of(TextColors.RED, "Wrong auth code! Retry with '/discord loginconfirm <email> <password>'"));
                return CommandResult.empty();
            }
            String token = response.getBody().getObject().getString("token");
            prepareHumanClient(Javacord.getApi(token, false), commandSource);
        } catch (UnirestException e) {
            e.printStackTrace();
            commandSource.sendMessage(Text.of(TextColors.RED, "Unexpected error!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }*/

    public static CommandResult logout(CommandSource commandSource, boolean isSilence) {

        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            UUID playerId = player.getUniqueId();
            try {
                DiscordBridge.getInstance().getStorage().removeToken(playerId);
            } catch (IOException e) {
                e.printStackTrace();
                commandSource.sendMessage(Text.of(TextColors.RED, "Cannot remove cached token!"));
            }
            mod.removeAndLogoutClient(playerId);
            mod.getUnauthenticatedPlayers().add(player.getUniqueId());

            if (!isSilence)
                commandSource.sendMessage(Text.of(TextColors.YELLOW, "Logged out of Discord!"));
            return CommandResult.success();
        } else if (commandSource instanceof ConsoleSource) {
            mod.removeAndLogoutClient(null);
            commandSource.sendMessage(Text.of("Logged out of Discord!"));
            return CommandResult.success();
        } else if (commandSource instanceof CommandBlockSource) {
            commandSource.sendMessage(Text.of(TextColors.YELLOW, "Cannot log out from command blocks!"));
            return CommandResult.empty();
        }
        return CommandResult.empty();
    }

    private static void prepareBotClient(CompletableFuture<DiscordApi> clientfuture, CommandSource commandSource) {
        GlobalConfig config = mod.getConfig();

        if (commandSource != null)
            commandSource.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "Logging in..."));

        clientfuture.join();
        if(clientfuture.isCompletedExceptionally()) {
            logger.error("Cannot connect to Discord!");
        } else {
            DiscordApi client = null;
            try {
                client = clientfuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }
            client.addMessageCreateListener(MessageHandler::discordMessageReceived);

            String name = "unknown";
            User user = client.getYourself();
            if (user != null)
                name = user.getName();
            String text = "Bot account " + name + " will be used for all unauthenticated users!";
            if (StringUtils.isNotBlank(config.botDiscordGame)) {
                client.updateActivity(config.botDiscordGame);
            }
            if (commandSource != null)
                commandSource.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, text));
            else
                logger.info(text);

            mod.setBotClient(client);

            for (ChannelConfig channelConfig : config.channels) {
                if (StringUtils.isNotBlank(channelConfig.discordId)) {
                    Optional<Channel> channel = client.getChannelById(channelConfig.discordId);
                    if (channel.isPresent()) {
                        channelJoined(client, config, channelConfig, channel.get(), commandSource);
                    } else {
                        ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
                    }
                } else {
                    logger.warn("Channel with empty ID!");
                }
            }
        }
    }

    /*private static void prepareHumanClient(DiscordApi client, CommandSource commandSource) {
        GlobalConfig config = mod.getConfig();

        client.connect(new FutureCallback<DiscordApi>() {
            @Override
            public void onSuccess(@Nullable DiscordApi DiscordApi) {
                try {
                    String name = client.getYourself().getName();
                    commandSource.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "You have logged in to Discord account " + name + "!"));

                    if (commandSource instanceof Player) {
                        Player player = (Player) commandSource;
                        UUID playerId = player.getUniqueId();
                        mod.getUnauthenticatedPlayers().remove(playerId);
                        mod.addClient(playerId, client);
                        mod.getStorage().putToken(playerId, client.getToken());
                    } else if (commandSource instanceof ConsoleSource) {
                        commandSource.sendMessage(Text.of("WARNING: This Discord account will be used only for this console session!"));
                        mod.addClient(null, client);
                    } else if (commandSource instanceof CommandBlockSource) {
                        commandSource.sendMessage(Text.of(TextColors.GREEN, "Account is valid!"));
                        return;
                    }

                    for (ChannelConfig channelConfig : config.channels) {
                        if (StringUtils.isNotBlank(channelConfig.discordId)) {
                            Channel channel = client.getChannelById(channelConfig.discordId);
                            if (channel != null) {
                                channelJoined(client, config, channelConfig, channel, commandSource);
                            } else {
                                ErrorMessages.CHANNEL_NOT_FOUND_HUMAN.log(channelConfig.discordId);
                            }
                        } else {
                            logger.warn("Channel with empty ID!");
                        }
                    }
                } catch (IOException e) {
                    logger.error("Cannot connect to Discord!", e);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.error("Cannot connect to Discord!", throwable);
                if (commandSource != null) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Unable to login! Please check your login details or your email for login verification."));
                }
            }
        });
    }*/

//    private static Channel acceptInvite(DiscordApi client, ChannelConfig channelConfig, CommandSource src) {
//        DiscordBridge mod = DiscordBridge.getInstance();
//        Logger logger = mod.getLogger();
//        GlobalConfig config = mod.getConfig();
//
//        if (StringUtils.isNotBlank(channelConfig.discordInviteCode)) {
//            client.acceptInvite(channelConfig.discordInviteCode, new FutureCallback<Server>() {
//                @Override
//                public void onSuccess(@Nullable Server server) {
//                    Channel channel = client.getChannelById(channelConfig.discordId);
//                    channelJoined(client, channelConfig, channel, src);
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//                    logger.error("Cannot accept invite", throwable);
//                }
//            });
//        }
//        return null;
//    }

    private static void channelJoined(DiscordApi client, GlobalConfig config, ChannelConfig channelConfig, Channel channel, CommandSource src) {

        if (channel != null && StringUtils.isNotBlank(channelConfig.discordId) && channelConfig.discord != null) {
            if (client != mod.getBotClient()) {
                String playerName = "console";
                if (src instanceof Player) {
                    Player player = (Player) src;
                    playerName = player.getName();
                }
                if (StringUtils.isNotBlank(channelConfig.discord.joinedTemplate)) {
                    String content = String.format(channelConfig.discord.joinedTemplate, playerName);
                    ChannelUtil.sendMessage(channel, content);
                }
                logger.info(playerName + " connected to Discord channel " + channelConfig.discordId + ".");
            } else {
                logger.info("Bot account has connected to Discord channel " + channelConfig.discordId + ".");
                if (StringUtils.isNotBlank(channelConfig.discord.serverUpMessage)) {
                    ChannelUtil.sendMessage(channel, channelConfig.discord.serverUpMessage);
                }
            }
        }
    }
}
