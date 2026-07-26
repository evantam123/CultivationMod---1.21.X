package net.god123.cultivationmod.datagen;


import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CultivationMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.MYSTIC_IRON_BLOCK);
        blockWithItem(ModBlocks.SPIRIT_STONE_BLOCK);
        blockWithItem(ModBlocks.MYSTIC_IRON_ORE);
        blockWithItem(ModBlocks.SPIRIT_STONE_ORE);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
