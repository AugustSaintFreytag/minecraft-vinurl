package com.vinurl.util;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Constants {
	// General

	public static final String MOD_ID = "vinurl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Path VINURLPATH = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);

	public static final Identifier PLACEHOLDER_SOUND_ID = Identifier.of(MOD_ID, "placeholder");

	public static SoundEvent SONG;

	public static Item CUSTOM_RECORD;
	public static Item CUSTOM_RECORD_REWRITABLE;

	// Networking

	public static final OwoNetChannel NETWORK_CHANNEL = OwoNetChannel.create(Identifier.of(MOD_ID, "main"));

	public static final String DISC_URL_NBT_KEY = "MusicUrl";
	public static final String DISC_REWRITABLE_NBT_KEY = "Rewritable";
	public static final String DISC_LOOP_NBT_KEY = "Loop";
	public static final String DISC_LOCKED_NBT_KEY = "Locked";
	public static final String DISC_DURATION_KEY = "Duration";
}
