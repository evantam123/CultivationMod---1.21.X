package net.god123.cultivationmod.mana;

import net.god123.cultivationmod.CultivationMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModPlayerMana {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CultivationMod.MODID);

    public static final Supplier<AttachmentType<PlayerMana>> MANA_ATTACHMENT = ATTACHMENT_TYPES.register(
            "player_mana",
            () -> AttachmentType.builder(PlayerMana::new) // 預設值
                    .serialize(PlayerMana.CODEC)               // 儲存機制
                    .copyOnDeath()                             // 死後不重設資料
                    .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}

