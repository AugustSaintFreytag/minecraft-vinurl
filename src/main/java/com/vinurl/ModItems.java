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
	public static Item DISC_CORE_REWRITABLE;

	public static Item DISC_SIDE;
	public static Item DISC_LABEL;

	public static void initialize() {
		DISC_CORE = registerItem("disc_core", new DiscComponentItem(DiscDecoration.DEFAULT_CORE_COLOR, new Item.Settings()));
		DISC_CORE_REWRITABLE = registerItem("disc_core_rewritable", new Item(new Item.Settings()));

		DISC_SIDE = registerItem("disc_side", new DiscComponentItem(DiscDecoration.DEFAULT_SIDE_COLOR, new Item.Settings()));
		DISC_LABEL = registerItem("disc_label", new DiscComponentItem(DiscDecoration.DEFAULT_LABEL_COLOR, new Item.Settings()));

		CUSTOM_RECORD = registerItem("custom_record", new CustomMusicDiscItem(false));
		CUSTOM_RECORD_REWRITABLE = registerItem("custom_record_rewritable", new CustomMusicDiscItem(true));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(DISC_CORE);
			content.add(DISC_CORE_REWRITABLE);
			content.add(DISC_SIDE);
			content.add(DISC_LABEL);
			content.add(ModItems.CUSTOM_RECORD);
			content.add(ModItems.CUSTOM_RECORD_REWRITABLE);
		});
	}

	private static <T extends Item> T registerItem(String name, T item) {
		Registry.register(Registries.ITEM, new Identifier(Mod.MOD_ID, name), item);
		return item;
	}

}
