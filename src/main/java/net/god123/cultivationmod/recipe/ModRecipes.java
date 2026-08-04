package net.god123.cultivationmod.recipe;

import net.god123.cultivationmod.CultivationMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CultivationMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, CultivationMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronRecipe>> CAULDRON_SERIALIZER = SERIALIZERS.register("cauldron", CauldronRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronRecipe>> CAULDRON_TYPE = TYPES.register("cauldron", () -> new RecipeType<CauldronRecipe>() {
        @Override
        public String toString() {
            return "cauldron";
        }
    });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
