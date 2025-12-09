package com.vinurl;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {

	public static SoundEvent CUSTOM_MUSIC;
	public static SoundEvent FAST_FORWARD_RECORD;

	public static void initialize() {
		CUSTOM_MUSIC = registerSound(Identifier.of(Mod.MOD_ID, "placeholder"));
	}

	private static SoundEvent registerSound(Identifier identifier) {
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}
}
