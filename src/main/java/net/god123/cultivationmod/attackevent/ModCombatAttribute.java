package net.god123.cultivationmod.attackevent;

import net.god123.cultivationmod.CultivationMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCombatAttribute {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, CultivationMod.MODID);

    public static final Holder<Attribute> CRITICAL_CHANCE = ATTRIBUTES.register(
            "critical_chance",
            () -> new RangedAttribute("attribute.name.cultivationmod.critical_chance", 0.0, 0.0, 10000.0)
                    .setSyncable(true) // 自動同步到客戶端！連 Client 端飄字/粒子都不用自己寫封包！
    );

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, CRITICAL_CHANCE)) {
            event.add(EntityType.PLAYER, CRITICAL_CHANCE);
        }
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
        eventBus.addListener(ModCombatAttribute::onEntityAttributeModification);
    }
}

