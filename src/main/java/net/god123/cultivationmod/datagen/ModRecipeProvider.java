package net.god123.cultivationmod.datagen;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.block.ModBlocks;
import net.god123.cultivationmod.item.ItemIngredients;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> MYSTIC_IRON_SMELTABLES = List.of(ItemIngredients.RAW_MYSTIC_IRON,
                ModBlocks.MYSTIC_IRON_ORE);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SPIRIT_STONE_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ItemIngredients.LOW_SPIRIT_STONE.get())
                .unlockedBy("has_low_spirit_stone", has(ItemIngredients.LOW_SPIRIT_STONE)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemIngredients.LOW_SPIRIT_STONE.get(), 9)
                .requires(ModBlocks.SPIRIT_STONE_BLOCK)
                .unlockedBy("has_spirit_stone_block", has(ModBlocks.SPIRIT_STONE_BLOCK)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MYSTIC_IRON_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ItemIngredients.MYSTIC_IRON_INGOT.get())
                .unlockedBy("has_mystic_iron_ingot", has(ItemIngredients.MYSTIC_IRON_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemIngredients.MYSTIC_IRON_INGOT.get(), 9)
                .requires(ModBlocks.MYSTIC_IRON_BLOCK)
                .unlockedBy("has_mystic_iron_block", has(ModBlocks.MYSTIC_IRON_BLOCK)).save(recipeOutput);

        oreSmelting(recipeOutput, MYSTIC_IRON_SMELTABLES, RecipeCategory.MISC, ItemIngredients.MYSTIC_IRON_INGOT.get(), 0.25f, 200, "mystic_iron_ingot");
        oreBlasting(recipeOutput, MYSTIC_IRON_SMELTABLES, RecipeCategory.MISC, ItemIngredients.MYSTIC_IRON_INGOT.get(), 0.25f, 100, "mystic_iron_ingot");
    }

    //addition
    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, CultivationMod.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
