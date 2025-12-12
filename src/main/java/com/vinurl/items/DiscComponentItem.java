package com.vinurl.items;

import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public class DiscComponentItem extends Item implements DyeableItem {

	private final int defaultColor;

	public DiscComponentItem(int defaultColor, Settings settings) {
		super(settings);
		this.defaultColor = defaultColor;
	}

	@Override
	public int getColor(ItemStack stack) {
		var displayNbt = stack.getSubNbt("display");
		if (displayNbt != null && displayNbt.contains("color", NbtElement.INT_TYPE)) {
			return displayNbt.getInt("color");
		}

		return defaultColor;
	}

	public int getDefaultColor() {
		return defaultColor;
	}

	@Override
	public Text getName(ItemStack stack) {
		if (!hasColor(stack)) {
			return super.getName(stack);
		}

		return Text.translatable(getTranslationKey() + ".dyed", super.getName(stack));
	}
}
