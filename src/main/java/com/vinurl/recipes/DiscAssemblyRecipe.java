package com.vinurl.recipes;

import java.util.ArrayList;
import java.util.List;

import com.vinurl.ModItems;
import com.vinurl.ModRecipes;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.items.DiscComponentItem;
import com.vinurl.items.DiscDecoration;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DiscAssemblyRecipe extends SpecialCraftingRecipe {

	public DiscAssemblyRecipe(Identifier identifier, CraftingRecipeCategory category) {
		super(identifier, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		var cores = 0;
		var sides = 0;
		var labels = 0;

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);
			if (stack.isOf(ModItems.DISC_CORE)) {
				cores++;
				if (cores > 1) {
					return false;
				}
			} else if (stack.isOf(ModItems.DISC_SIDE)) {
				sides++;
				if (sides > 2) {
					return false;
				}
			} else if (stack.isOf(ModItems.DISC_LABEL)) {
				labels++;
				if (labels > 1) {
					return false;
				}
			} else if (!stack.isEmpty()) {
				return false;
			}
		}

		return cores == 1 && sides == 2;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		var decoration = DiscDecoration.defaults();
		var sideColors = new ArrayList<Integer>();
		var labelColor = DiscDecoration.DEFAULT_LABEL_COLOR;
		var hasLabel = false;

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.isOf(ModItems.DISC_CORE)) {
				var color = getComponentColor(stack, DiscDecoration.DEFAULT_CORE_COLOR);
				decoration = new DiscDecoration(color, decoration.sideColor(), decoration.labelColor(), hasLabel);
			} else if (stack.isOf(ModItems.DISC_SIDE)) {
				var color = getComponentColor(stack, DiscDecoration.DEFAULT_SIDE_COLOR);
				sideColors.add(color);
			} else if (stack.isOf(ModItems.DISC_LABEL)) {
				labelColor = getComponentColor(stack, DiscDecoration.DEFAULT_LABEL_COLOR);
				hasLabel = true;
			}
		}

		var sideColor = sideColors.isEmpty() ? DiscDecoration.DEFAULT_SIDE_COLOR : average(sideColors);
		var finalLabelColor = hasLabel ? labelColor : DiscDecoration.DEFAULT_LABEL_COLOR;

		var result = new ItemStack(ModItems.CUSTOM_RECORD);
		CustomMusicDiscItem.setDecoration(result, new DiscDecoration(decoration.coreColor(), sideColor, finalLabelColor, hasLabel));

		return result;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return new ItemStack(ModItems.CUSTOM_RECORD);
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		var list = DefaultedList.ofSize(3, Ingredient.EMPTY);
		list.set(0, Ingredient.ofItems(ModItems.DISC_CORE));
		list.set(1, Ingredient.ofItems(ModItems.DISC_SIDE));
		list.set(2, Ingredient.ofItems(ModItems.DISC_SIDE));
		return list;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 4;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Override
	public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
		return ModRecipes.DISC_ASSEMBLY;
	}

	private int getComponentColor(ItemStack stack, int fallback) {
		var item = stack.getItem();
		if (item instanceof DyeableItem dyeableItem && dyeableItem.hasColor(stack)) {
			return dyeableItem.getColor(stack);
		}

		if (item instanceof DiscComponentItem componentItem) {
			return componentItem.getDefaultColor();
		}

		return fallback;
	}

	private int average(List<Integer> colors) {
		var red = 0;
		var green = 0;
		var blue = 0;

		for (var color : colors) {
			red += (color >> 16) & 0xFF;
			green += (color >> 8) & 0xFF;
			blue += color & 0xFF;
		}

		var count = Math.max(colors.size(), 1);
		return (red / count << 16) | (green / count << 8) | blue / count;
	}
}
