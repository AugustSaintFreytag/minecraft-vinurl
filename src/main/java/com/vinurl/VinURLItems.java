package com.vinurl;

import com.vinurl.items.VinURLDisc;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class VinURLItems {

	public static Item CUSTOM_RECORD;
	public static Item CUSTOM_RECORD_REWRITABLE;

	public static void initialize() {
		CUSTOM_RECORD = Registry.register(Registries.ITEM, Identifier.of(VinURL.MOD_ID, "custom_record"), new VinURLDisc(false));
		CUSTOM_RECORD_REWRITABLE = Registry.register(Registries.ITEM, Identifier.of(VinURL.MOD_ID, "custom_record_rewritable"),
				new VinURLDisc(true));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(VinURLItems.CUSTOM_RECORD);
			content.add(VinURLItems.CUSTOM_RECORD_REWRITABLE);
		});
	}

}
