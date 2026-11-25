package net.god123.cultivationmod.attackevent;

import net.god123.cultivationmod.CultivationMod;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ModAttributeModifier {
    // 定義一個專屬的屬性識別 ID（避免重複疊加）
    private static final ResourceLocation REALM_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_melee_damage");

    private static final ResourceLocation REALM_CRITICAL_CHANCE_ID =
            ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_critical_chance");

    private static final ResourceLocation REALM_HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_max_health");

    public static void updatePlayerMeleeDamage(Player player, double extraDamage) {
        applyModifier(player, Attributes.ATTACK_DAMAGE, REALM_DAMAGE_ID, extraDamage);
    }

    public static void updatePlayerMaxHealth(Player player, double extraHealth) {
        applyModifier(player, Attributes.MAX_HEALTH, REALM_HEALTH_ID, extraHealth);
    }

    public static void updatePlayerCriticalChance(Player player, double extraCriticalChance) {
        applyModifier(player, ModCombatAttribute.CRITICAL_CHANCE, REALM_CRITICAL_CHANCE_ID, extraCriticalChance);
    }

    private static void applyModifier(Player player, Holder<Attribute> attribute, ResourceLocation id, double value) {
        if (player.level().isClientSide()) return; // 確保只在伺服器端執行

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        instance.removeModifier(id);

        if (value > 0) {
            instance.addPermanentModifier(new AttributeModifier(
                    id,
                    value,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }
}
