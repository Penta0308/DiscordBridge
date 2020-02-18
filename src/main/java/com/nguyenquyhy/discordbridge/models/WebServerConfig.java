package com.nguyenquyhy.discordbridge.models;

import ninja.leaping.configurate.objectmapping.Setting;

public class WebServerConfig  {
    /**
     * This is called only when the config file is first created.
     */
    void initializeDefault() {
        port = 0;
    }

    @Setting
    public Integer port;

    void migrate() {
        /*
        if (StringUtils.isNotBlank(anonymousChatTemplate)) {
            if (publicChat == null) publicChat = new SpongeChannelConfig();
            publicChat.anonymousChatTemplate = anonymousChatTemplate;
            anonymousChatTemplate = null;
        }
        if (StringUtils.isNotBlank(authenticatedChatTemplate)) {
            if (publicChat == null) publicChat = new SpongeChannelConfig();
            publicChat.authenticatedChatTemplate = authenticatedChatTemplate;
            authenticatedChatTemplate = null;
        }
        */
    }
}