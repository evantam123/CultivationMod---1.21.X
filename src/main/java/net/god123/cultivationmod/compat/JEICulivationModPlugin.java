package net.god123.cultivationmod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.block.ModBlocks;
import net.god123.cultivationmod.recipe.CauldronRecipe;
import net.god123.cultivationmod.recipe.ModRecipes;
import net.god123.cultivationmod.screen.custom.CauldronScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEICulivationModPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CauldronRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CauldronRecipe> cauldronRecipes = recipeManager.getAllRecipesFor(ModRecipes.CAULDRON_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CauldronRecipeCategory.CAULDRON_RECIPE_RECIPE_TYPE, cauldronRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CauldronScreen.class, 74, 30, 22, 20, CauldronRecipeCategory.CAULDRON_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CAULDRON_BLOCK.asItem()),
                CauldronRecipeCategory.CAULDRON_RECIPE_RECIPE_TYPE);
    }
}
