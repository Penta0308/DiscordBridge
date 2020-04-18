package tk.skmserver.discordbridge;

import com.google.inject.Inject;
import tk.skmserver.discordbridge.database.IStorage;
import tk.skmserver.discordbridge.discordcommands.*;
import tk.skmserver.discordbridge.listeners.ChatListener;
import tk.skmserver.discordbridge.listeners.ClientConnectionListener;
import tk.skmserver.discordbridge.listeners.DeathListener;
import tk.skmserver.discordbridge.logics.ConfigHandler;
import tk.skmserver.discordbridge.logics.LoginHandler;
import tk.skmserver.discordbridge.models.ChannelConfig;
import tk.skmserver.discordbridge.models.GlobalConfig;
import tk.skmserver.discordbridge.utils.ChannelUtil;
import tk.skmserver.discordbridge.utils.ErrorMessages;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Hy on 1/4/2016.
 */
@Plugin(id = "discordbridge", name = "Discord Bridge", version = "4.0.0",
        description = "A Sponge plugin to connect your Minecraft server with Discord", authors = {"Hy", "Mohron"})
public class DiscordBridge {

    private DiscordApi consoleClient = null;
    private final Map<UUID, DiscordApi> humanClients = new HashMap<>();
    private DiscordApi botClient = null;

    private final Set<UUID> unauthenticatedPlayers = new HashSet<>(100);

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private GlobalConfig config;

    @Inject
    private Game game;

    private IStorage storage;

    private static DiscordBridge instance;

    public static TempBanThread TempBan;

    public DiscordCommandHandler discordCommandHandler;

    public ServerState serverState;

    public WebServer server;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) throws IOException, ObjectMappingException {
        instance = this;
        config = ConfigHandler.loadConfiguration();

        Sponge.getEventManager().registerListeners(this, new ChatListener());
        Sponge.getEventManager().registerListeners(this, new ClientConnectionListener());
        Sponge.getEventManager().registerListeners(this, new DeathListener());

        TempBan = new TempBanThread();
        discordCommandHandler = new DiscordCommandHandler();
        server = new WebServer();
        serverState = new ServerState();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandRegistry.register();
        LoginHandler.loginBotAccount();

        TempBan.start();
        registerDiscordCommands();
        server.start();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        TempBan.stop();

        if (botClient != null) {
            for (ChannelConfig channelConfig : config.channels) {
                if (StringUtils.isNotBlank(channelConfig.discordId)
                        && channelConfig.discord != null
                        && StringUtils.isNotBlank(channelConfig.discord.serverDownMessage)) {
                    Optional<Channel> channel = botClient.getChannelById(channelConfig.discordId);
                    if (channel.isPresent()) {
                        ChannelUtil.sendMessage(channel.get(), channelConfig.discord.serverDownMessage);
                    } else {
                        ErrorMessages.CHANNEL_NOT_FOUND.log(channelConfig.discordId);
                    }
                }
            }
        }
    }

    public static DiscordBridge getInstance() {
        return instance;
    }

    public Game getGame() {
        return game;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public GlobalConfig getConfig() {
        return config;
    }

    public void setConfig(GlobalConfig config) {
        this.config = config;
    }

    public Logger getLogger() {
        return logger;
    }

    public IStorage getStorage() {
        return storage;
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }

    public DiscordApi getBotClient() {
        return botClient;
    }

    public void setBotClient(DiscordApi botClient) {
        this.botClient = botClient;
    }

    public Map<UUID, DiscordApi> getHumanClients() {
        return humanClients;
    }

    public Set<UUID> getUnauthenticatedPlayers() {
        return unauthenticatedPlayers;
    }

    public void addClient(UUID player, DiscordApi client) {
        if (player == null) {
            consoleClient = client;
        } else {
            humanClients.put(player, client);
        }
    }

    public void removeAndLogoutClient(UUID player) {
        if (player == null) {
            consoleClient.disconnect();
            consoleClient = null;
        } else {
            if (humanClients.containsKey(player)) {
                DiscordApi client = humanClients.get(player);
                client.disconnect();
                humanClients.remove(player);
            }
        }
    }

    private void registerDiscordCommands() {
        discordCommandHandler.registerDiscordCommand("time", new DiscordGetTimeCommand());
        discordCommandHandler.registerDiscordCommand("tick", new DiscordGetTPSCommand());
        discordCommandHandler.registerDiscordCommand("users", new DiscordGetUsersCommand());
        discordCommandHandler.registerDiscordCommand("whitelist", new DiscordWhiteListAddCommand());
    }
}
