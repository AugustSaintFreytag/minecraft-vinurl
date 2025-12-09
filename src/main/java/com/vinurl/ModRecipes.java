package com.vinurl;

import com.vinurl.recipes.ApplyLabelRecipe;
import com.vinurl.recipes.DiscAssemblyRecipe;
import com.vinurl.recipes.DyeComponentRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipes {

	public static RecipeSerializer<DyeComponentRecipe> DYE_COMPONENT;
	public static RecipeSerializer<DiscAssemblyRecipe> DISC_ASSEMBLY;
	public static RecipeSerializer<ApplyLabelRecipe> APPLY_LABEL;

	private ModRecipes() {
	}

	public static void initialize() {
		DYE_COMPONENT = register("dye_disc_component", new SpecialRecipeSerializer<>(DyeComponentRecipe::new));
		DISC_ASSEMBLY = register("assemble_disc", new SpecialRecipeSerializer<>(DiscAssemblyRecipe::new));
		APPLY_LABEL = register("disc_label_apply", new SpecialRecipeSerializer<>(ApplyLabelRecipe::new));
	}

	private static <T extends Recipe<?>> RecipeSerializer<T> register(String id, RecipeSerializer<T> serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(Mod.MOD_ID, id), serializer);
	}
}
