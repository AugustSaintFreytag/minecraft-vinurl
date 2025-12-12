package com.vinurl.recipes;

import java.util.ArrayList;
import java.util.List;

import com.vinurl.ModRecipes;
import com.vinurl.items.DiscComponentItem;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DyeComponentRecipe extends SpecialCraftingRecipe {

	public DyeComponentRecipe(Identifier identifier, CraftingRecipeCategory category) {
		super(identifier, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		var component = ItemStack.EMPTY;
		var dyes = 0;

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.getItem() instanceof DiscComponentItem) {
				if (!component.isEmpty()) {
					return false;
				}
				component = stack;
			} else if (stack.getItem() instanceof DyeItem) {
				dyes++;
			} else {
				return false;
			}
		}

		return !component.isEmpty() && dyes > 0;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		var component = ItemStack.EMPTY;
		List<DyeItem> dyes = new ArrayList<>();

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.getItem() instanceof DiscComponentItem) {
				component = stack.copyWithCount(1);
			} else if (stack.getItem() instanceof DyeItem dyeItem) {
				dyes.add(dyeItem);
			}
		}

		if (component.isEmpty() || dyes.isEmpty()) {
			return ItemStack.EMPTY;
		}

		var blendedColor = blendColors(component, dyes);
		((DyeableItem) component.getItem()).setColor(component, blendedColor);
		return component;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
		return ModRecipes.DYE_COMPONENT;
	}

	private int blendColors(ItemStack base, List<DyeItem> dyes) {
		var dyeableItem = (DyeableItem) base.getItem();
		var redTotal = 0;
		var greenTotal = 0;
		var blueTotal = 0;
		var colorContributors = 0;

		if (dyeableItem.hasColor(base)) {
			var baseColor = dyeableItem.getColor(base);
			redTotal += (baseColor >> 16) & 0xFF;
			greenTotal += (baseColor >> 8) & 0xFF;
			blueTotal += baseColor & 0xFF;
			colorContributors++;
		}

		for (var dye : dyes) {
			var dyeColor = dye.getColor();
			var components = dyeColor.getColorComponents();
			redTotal += (int) (components[0] * 255.0F);
			greenTotal += (int) (components[1] * 255.0F);
			blueTotal += (int) (components[2] * 255.0F);
			colorContributors++;
		}

		if (colorContributors == 0) {
			return ((DiscComponentItem) base.getItem()).getDefaultColor();
		}

		var red = redTotal / colorContributors;
		var green = greenTotal / colorContributors;
		var blue = blueTotal / colorContributors;
		return (red << 16) | (green << 8) | blue;
	}
}
