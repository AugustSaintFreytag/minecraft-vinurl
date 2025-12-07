package com.vinurl;

import com.vinurl.net.ServerEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import com.vinurl.util.Constants;

import static com.vinurl.util.Constants.MOD_ID;
import static com.vinurl.util.Constants.PLACEHOLDER_SOUND_ID;

import com.vinurl.items.VinURLDisc;

public class VinURL implements ModInitializer {

	@Override
	public void onInitialize() {
		// Register Sound Events

		Constants.SONG = Registry.register(Registries.SOUND_EVENT, PLACEHOLDER_SOUND_ID, SoundEvent.of(PLACEHOLDER_SOUND_ID));

		// Register Items
		
		Constants.CUSTOM_RECORD = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "custom_record"), new VinURLDisc(false));
		Constants.CUSTOM_RECORD_REWRITABLE = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "custom_record_rewritable"), new VinURLDisc(true));

		// Register Creative Tab Entries
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(Constants.CUSTOM_RECORD);
			content.add(Constants.CUSTOM_RECORD_REWRITABLE);
		});

		// Register Network Events

		ServerEvent.register();
	}
}
