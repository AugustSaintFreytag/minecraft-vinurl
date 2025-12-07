package com.vinurl;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vinurl.net.ServerEvents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class VinURL implements ModInitializer {

	// Configuration

	public static final String MOD_ID = "vinurl";
	public static final Path PATH = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);

	// References

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Init

	@Override
	public void onInitialize() {
		VinURLItems.initialize();
		VinURLSounds.initialize();
		VinURLNetwork.initialize();

		ServerEvents.register();
	}
}
