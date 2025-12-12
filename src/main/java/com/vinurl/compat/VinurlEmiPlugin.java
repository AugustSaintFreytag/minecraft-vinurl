package com.vinurl.compat;

import java.util.ArrayList;
import java.util.List;

import com.vinurl.Mod;
import com.vinurl.ModItems;
import com.vinurl.ModRecipes;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.items.DiscDecoration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

@EmiEntrypoint
public class VinurlEmiPlugin implements EmiPlugin {

	@Override
	public void register(EmiRegistry registry) {
		for (var entry : registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING)) {

			if (entry.getSerializer() != ModRecipes.DISC_ASSEMBLY) {
				continue;
			}

			registerDiscAssemblyRecipe(entry, registry);
		}
	}

	private void registerDiscAssemblyRecipe(CraftingRecipe entry, EmiRegistry registry) {
		var baseInputs = List.of(EmiIngredient.of(Ingredient.ofItems(ModItems.DISC_CORE)),
				EmiIngredient.of(Ingredient.ofItems(ModItems.DISC_SIDE)), EmiIngredient.of(Ingredient.ofItems(ModItems.DISC_SIDE)));

		var output = EmiStack.of(createDisc());
		registry.addRecipe(new EmiCraftingRecipe(baseInputs, output, entry.getId()));

		var labeledInputs = new ArrayList<EmiIngredient>(baseInputs);
		labeledInputs.add(EmiStack.of(ModItems.DISC_LABEL));

		var labeledId = Identifier.of(Mod.MOD_ID, entry.getId().getPath() + "_with_label");
		var labeledOutput = EmiStack.of(createLabeledDisc()).comparison(Comparison.compareData(stack -> stack.getItemStack().getItem()));

		registry.addRecipe(new EmiCraftingRecipe(labeledInputs, labeledOutput, labeledId));
	}

	private ItemStack createDisc() {
		var disc = new ItemStack(ModItems.CUSTOM_RECORD);
		CustomMusicDiscItem.setDecoration(disc, DiscDecoration.defaults());
		return disc;
	}

	private ItemStack createLabeledDisc() {
		var disc = new ItemStack(ModItems.CUSTOM_RECORD);
		CustomMusicDiscItem.setDecoration(disc, new DiscDecoration(DiscDecoration.DEFAULT_CORE_COLOR, DiscDecoration.DEFAULT_SIDE_COLOR,
				DiscDecoration.DEFAULT_LABEL_COLOR, true));
		return disc;
	}
}
