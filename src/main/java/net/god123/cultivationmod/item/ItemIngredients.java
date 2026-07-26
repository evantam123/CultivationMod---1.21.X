package net.god123.cultivationmod.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static net.god123.cultivationmod.item.ModItems.ITEMS;

public class ItemIngredients {
    public static final DeferredItem<Item> LOW_SPIRIT_STONE = ITEMS.register("low_spirit_stone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MID_SPIRIT_STONE = ITEMS.register("mid_spirit_stone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HIGH_SPIRIT_STONE = ITEMS.register("high_spirit_stone", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MYSTIC_IRON_INGOT = ITEMS.register("mystic_iron_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_MYSTIC_IRON = ITEMS.register("raw_mystic_iron", () -> new Item(new Item.Properties()));

    public static void init() {}
}
