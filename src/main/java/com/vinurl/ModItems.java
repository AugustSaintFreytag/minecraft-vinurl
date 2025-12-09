package com.vinurl;

import com.vinurl.items.CustomMusicDiscItem;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {

	public static Item CUSTOM_RECORD;
	public static Item CUSTOM_RECORD_REWRITABLE;

	public static void initialize() {
		CUSTOM_RECORD = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "custom_record"), new CustomMusicDiscItem(false));
		CUSTOM_RECORD_REWRITABLE = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "custom_record_rewritable"),
				new CustomMusicDiscItem(true));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(ModItems.CUSTOM_RECORD);
			content.add(ModItems.CUSTOM_RECORD_REWRITABLE);
		});
	}

}
