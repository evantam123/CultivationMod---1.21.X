package net.god123.cultivationmod.datagen;

import net.god123.cultivationmod.block.ModBlocks;
import net.god123.cultivationmod.item.ItemIngredients;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.MYSTIC_IRON_BLOCK.get());
        dropSelf(ModBlocks.SPIRIT_STONE_BLOCK.get());

        add(ModBlocks.MYSTIC_IRON_ORE.get(),
                block -> createOreDrop(ModBlocks.MYSTIC_IRON_ORE.get(), ItemIngredients.RAW_MYSTIC_IRON.get()));
        add(ModBlocks.SPIRIT_STONE_ORE.get(),
                block -> createOreDrop(ModBlocks.SPIRIT_STONE_ORE.get(), ItemIngredients.LOW_SPIRIT_STONE.get()));

        dropSelf(ModBlocks.CAULDRON_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
