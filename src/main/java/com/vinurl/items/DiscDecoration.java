package com.vinurl.items;

import net.minecraft.item.ItemStack;

public record DiscDecoration(int coreColor, int sideColor, int labelColor) {

	public static final int DEFAULT_CORE_COLOR = 0x383838;
	public static final int DEFAULT_SIDE_COLOR = 0x2d2d2d;
	public static final int DEFAULT_LABEL_COLOR = 0xdddddd;

	private static final String DECORATION_KEY = "Decoration";
	private static final String CORE_KEY = "Core";
	private static final String SIDE_KEY = "Side";
	private static final String LABEL_KEY = "Label";

	public static DiscDecoration defaults() {
		return new DiscDecoration(DEFAULT_CORE_COLOR, DEFAULT_SIDE_COLOR, DEFAULT_LABEL_COLOR);
	}

	public static DiscDecoration from(ItemStack stack) {
		var decorationNbt = stack.getSubNbt(DECORATION_KEY);
		if (decorationNbt == null) {
			return defaults();
		}

		var core = decorationNbt.contains(CORE_KEY) ? decorationNbt.getInt(CORE_KEY) : DEFAULT_CORE_COLOR;
		var side = decorationNbt.contains(SIDE_KEY) ? decorationNbt.getInt(SIDE_KEY) : DEFAULT_SIDE_COLOR;
		var label = decorationNbt.contains(LABEL_KEY) ? decorationNbt.getInt(LABEL_KEY) : DEFAULT_LABEL_COLOR;

		return new DiscDecoration(core, side, label);
	}

	public void writeTo(ItemStack stack) {
		var decorationNbt = stack.getOrCreateSubNbt(DECORATION_KEY);
		decorationNbt.putInt(CORE_KEY, coreColor);
		decorationNbt.putInt(SIDE_KEY, sideColor);
		decorationNbt.putInt(LABEL_KEY, labelColor);
	}
}
