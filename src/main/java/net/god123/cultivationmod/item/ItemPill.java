package net.god123.cultivationmod.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static net.god123.cultivationmod.item.ModItems.ITEMS;

public class ItemPill {
    public static final DeferredItem<Item> HEALING_PILL = ITEMS.register("healing_pill", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> QIRESTORATION_PILL = ITEMS.register("qi_restoration_pill", () -> new Item(new Item.Properties()));

    public static void init() {}
}
