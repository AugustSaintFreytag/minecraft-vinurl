package com.vinurl;

import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.items.DiscComponentItem;
import com.vinurl.items.DiscDecoration;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {

	public static Item CUSTOM_RECORD;
	public static Item CUSTOM_RECORD_REWRITABLE;
	public static Item DISC_CORE;
	public static Item DISC_SIDE;
	public static Item DISC_LABEL;

	public static void initialize() {
		DISC_CORE = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "disc_core"),
				new DiscComponentItem(DiscDecoration.DEFAULT_CORE_COLOR, new Item.Settings()));
		DISC_SIDE = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "disc_side"),
				new DiscComponentItem(DiscDecoration.DEFAULT_SIDE_COLOR, new Item.Settings()));
		DISC_LABEL = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "disc_label"),
				new DiscComponentItem(DiscDecoration.DEFAULT_LABEL_COLOR, new Item.Settings()));

		CUSTOM_RECORD = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "custom_record"), new CustomMusicDiscItem(false));
		CUSTOM_RECORD_REWRITABLE = Registry.register(Registries.ITEM, Identifier.of(Mod.MOD_ID, "custom_record_rewritable"),
				new CustomMusicDiscItem(true));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(DISC_CORE);
			content.add(DISC_SIDE);
			content.add(DISC_LABEL);
			content.add(ModItems.CUSTOM_RECORD);
			content.add(ModItems.CUSTOM_RECORD_REWRITABLE);
		});
	}

}
