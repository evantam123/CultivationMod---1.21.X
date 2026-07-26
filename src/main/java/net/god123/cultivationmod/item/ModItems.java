package net.god123.cultivationmod.item;

import net.god123.cultivationmod.CultivationMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CultivationMod.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ItemIngredients.init();
        ItemPill.init();
        ItemCultivationArt.init();
    }
}
