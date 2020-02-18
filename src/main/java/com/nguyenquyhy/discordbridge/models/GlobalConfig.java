package com.nguyenquyhy.discordbridge.models;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hy on 10/13/2016.
 */
@ConfigSerializable
public class GlobalConfig {
    /**
     * Configs initialized in constructor will be restored automatically if deleted.
     */
    public GlobalConfig() {
        channels = new ArrayList<>();
        prefixBlacklist = new ArrayList<>();
        ignoreBots = false;
        botDiscordGame = "";
        minecraftBroadcastTemplate = "&2<BROADCAST> %s";
        botToken = "";
        tokenStore = TokenStore.JSON;
        jcsConfig = new JCSConfig();
        jcsConfig.initializeDefault();
    }

    @Setting
    public String botToken;
    @Setting
    public TokenStore tokenStore;
    @Setting
    public List<String> prefixBlacklist;
    @Setting
    public Boolean ignoreBots;
    @Setting
    public String botDiscordGame;
    @Setting
    public String minecraftBroadcastTemplate;
    @Setting
    public List<ChannelConfig> channels;
    @Setting
    public JCSConfig jcsConfig;

    public void migrate() {
        if (channels != null) {
            channels.forEach(ChannelConfig::migrate);
        }
        if (jcsConfig != null) jcsConfig.migrate();
    }
}
