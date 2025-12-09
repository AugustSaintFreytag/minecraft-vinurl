package com.vinurl;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = Mod.MOD_ID)
public class ModConfig implements ConfigData {

	public boolean enableDownloads = true;

	public boolean checkForUpdatesOnStartup = true;

	public boolean addDescriptionToItemTooltip = true;

	public String sourceWhitelist = "https://www.youtube.com,https://soundcloud.com";

	public boolean degradeAudioQuality = true;

}
