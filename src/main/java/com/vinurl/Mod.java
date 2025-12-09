package com.vinurl;

import java.nio.file.Path;

import com.vinurl.net.ServerEvents;
import com.vinurl.util.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Mod implements ModInitializer {

	// Configuration

	public static final String MOD_ID = "vinurl";

	public static final String MOD_NAME = "VinURL";

	public static final Path PATH = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);

	// References

	public static final Logger LOGGER = Logger.create(MOD_NAME);

	public static ModConfig CONFIG;

	// Init

	@Override
	public void onInitialize() {
		// Config

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		// Init

		ModItems.initialize();
		ModSounds.initialize();
		ModRecipes.initialize();
		ModNetworking.initialize();

		ServerEvents.register();
	}
}
