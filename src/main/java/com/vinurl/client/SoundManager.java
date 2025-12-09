package com.vinurl.client;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SoundManager {

	// State

	private static final ConcurrentHashMap<Vec3d, CustomRecordSound> playingSounds = new ConcurrentHashMap<>();

	// Playback

	public static void playSound(Vec3d position) {
		var client = MinecraftClient.getInstance();
		var fileSound = playingSounds.get(position);

		if (fileSound != null) {
			client.getSoundManager().play(fileSound);
			client.inGameHud.setRecordPlayingOverlay(Text.literal(SoundDescriptionManager.getDescription(fileSound.fileName)));
		}
	}

	public static void stopSound(Vec3d position) {
		var client = MinecraftClient.getInstance();
		client.getSoundManager().stop(playingSounds.remove(position));
	}

	public static void addSound(String fileName, Vec3d position) {
		playingSounds.put(position, new CustomRecordSound(fileName, position));
	}
}
