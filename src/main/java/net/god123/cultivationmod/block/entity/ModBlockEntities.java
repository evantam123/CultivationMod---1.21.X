package net.god123.cultivationmod.block.entity;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CultivationMod.MODID);

    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_BE = BLOCK_ENTITIES.register("cauldron_be", () -> BlockEntityType.Builder.of(
            CauldronBlockEntity::new, ModBlocks.CAULDRON_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
