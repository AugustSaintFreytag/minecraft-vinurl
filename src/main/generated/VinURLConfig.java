package com.vinurl.client;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VinURLConfig extends ConfigWrapper<com.vinurl.client.ClientConfig> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> downloadEnabled = this.optionForKey(this.keys.downloadEnabled);
    private final Option<java.lang.Boolean> updatesOnStartup = this.optionForKey(this.keys.updatesOnStartup);
    private final Option<java.lang.Boolean> showDescription = this.optionForKey(this.keys.showDescription);
    private final Option<java.util.List<java.lang.String>> urlWhitelist = this.optionForKey(this.keys.urlWhitelist);
    private final Option<com.vinurl.client.ClientConfig.AudioQuality> audioBitrate = this.optionForKey(this.keys.audioBitrate);
    private final Option<java.lang.Boolean> degradeAudioQuality = this.optionForKey(this.keys.degradeAudioQuality);

    private VinURLConfig() {
        super(com.vinurl.client.ClientConfig.class);
    }

    private VinURLConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(com.vinurl.client.ClientConfig.class, janksonBuilder);
    }

    public static VinURLConfig createAndLoad() {
        var wrapper = new VinURLConfig();
        wrapper.load();
        return wrapper;
    }

    public static VinURLConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new VinURLConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public boolean downloadEnabled() {
        return downloadEnabled.value();
    }

    public void downloadEnabled(boolean value) {
        downloadEnabled.set(value);
    }

    public boolean updatesOnStartup() {
        return updatesOnStartup.value();
    }

    public void updatesOnStartup(boolean value) {
        updatesOnStartup.set(value);
    }

    public boolean showDescription() {
        return showDescription.value();
    }

    public void showDescription(boolean value) {
        showDescription.set(value);
    }

    public java.util.List<java.lang.String> urlWhitelist() {
        return urlWhitelist.value();
    }

    public void urlWhitelist(java.util.List<java.lang.String> value) {
        urlWhitelist.set(value);
    }

    public com.vinurl.client.ClientConfig.AudioQuality audioBitrate() {
        return audioBitrate.value();
    }

    public void audioBitrate(com.vinurl.client.ClientConfig.AudioQuality value) {
        audioBitrate.set(value);
    }

    public boolean degradeAudioQuality() {
        return degradeAudioQuality.value();
    }

    public void degradeAudioQuality(boolean value) {
        degradeAudioQuality.set(value);
    }


    public static class Keys {
        public final Option.Key downloadEnabled = new Option.Key("downloadEnabled");
        public final Option.Key updatesOnStartup = new Option.Key("updatesOnStartup");
        public final Option.Key showDescription = new Option.Key("showDescription");
        public final Option.Key urlWhitelist = new Option.Key("urlWhitelist");
        public final Option.Key audioBitrate = new Option.Key("audioBitrate");
        public final Option.Key degradeAudioQuality = new Option.Key("degradeAudioQuality");
    }
}

