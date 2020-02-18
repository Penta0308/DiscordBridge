package com.nguyenquyhy.discordbridge.models;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class JCSConfig {
    /**
     * Configs initialized in constructor will be restored automatically if deleted.
     */
    public JCSConfig() {

    }

    /**
     * This is called only when the config file is first created.
     */
    public void initializeDefault() {
        ban = new JCSBanConfig();
        ban.initializeDefault();
        wsc = new WebServerConfig();
        wsc.initializeDefault();
    }

    @Setting
    public JCSBanConfig ban;
    @Setting
    public WebServerConfig wsc;

    public void migrate() {
        if (ban != null)
            ban.migrate();
        if (wsc != null)
            wsc.migrate();
    }
}
