package com.vinurl.recipes;

import com.vinurl.ModItems;
import com.vinurl.ModRecipes;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.items.DiscComponentItem;
import com.vinurl.items.DiscDecoration;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ApplyLabelRecipe extends SpecialCraftingRecipe {

	public ApplyLabelRecipe(Identifier identifier, CraftingRecipeCategory category) {
		super(identifier, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		var discCount = 0;
		var labelCount = 0;

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);

			if (stack.isEmpty()) {
				continue;
			}

			if (isDisc(stack)) {
				discCount++;
			} else if (stack.isOf(ModItems.DISC_LABEL)) {
				labelCount++;
			} else {
				return false;
			}

			if (discCount > 1 || labelCount > 1) {
				return false;
			}
		}

		return discCount == 1 && labelCount == 1;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		var disc = ItemStack.EMPTY;
		var label = ItemStack.EMPTY;

		for (var slot = 0; slot < inventory.size(); slot++) {
			var stack = inventory.getStack(slot);

			if (stack.isEmpty()) {
				continue;
			}

			if (isDisc(stack)) {
				disc = stack.copyWithCount(1);
			} else if (stack.isOf(ModItems.DISC_LABEL)) {
				label = stack;
			}
		}

		if (disc.isEmpty() || label.isEmpty()) {
			return ItemStack.EMPTY;
		}

		var decoration = CustomMusicDiscItem.getDecoration(disc);
		var labelColor = getLabelColor(label);

		CustomMusicDiscItem.setDecoration(disc, new DiscDecoration(decoration.coreColor(), decoration.sideColor(), labelColor, true));
		return disc;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
		return ModRecipes.APPLY_LABEL;
	}

	private boolean isDisc(ItemStack stack) {
		return stack.isOf(ModItems.CUSTOM_RECORD) || stack.isOf(ModItems.CUSTOM_RECORD_REWRITABLE);
	}

	private int getLabelColor(ItemStack label) {
		var item = (DyeableItem) label.getItem();
		if (item.hasColor(label)) {
			var color = item.getColor(label);
			if (color != 0) {
				return color;
			}
		}

		return ((DiscComponentItem) label.getItem()).getDefaultColor();
	}
}
