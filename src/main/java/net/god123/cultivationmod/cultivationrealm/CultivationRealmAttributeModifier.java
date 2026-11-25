package net.god123.cultivationmod.cultivationrealm;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.attackevent.ModAttributeModifier;
import net.god123.cultivationmod.attackevent.ModCombatAttribute;
import net.god123.cultivationmod.item.cultivationart.CultivationArt;
import net.god123.cultivationmod.item.cultivationart.CultivationArtAttribute;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

public class CultivationRealmAttributeModifier {

    public static void refreshAttribute(Player player) {
        if (player.level().isClientSide()) return;

        CultivationRealmData.CultivationRealm playerData = player.getData(CultivationRealmData.CULTIVATION_REALM.get());

        // 1. 從玩家的 Curios 飾品欄取得當前裝備的功法
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var curioOpt = handler.findFirstCurio(stack -> stack.getItem() instanceof CultivationArt);
            CultivationRealmData.RealmLevel realm = playerData.getRealm();
            // 如果你的 CultivationRealmData 還沒加 getSubLayer()，先把下面這行改成 int subLayer = 1;
            int subLayer = playerData.getMathLevel() % 10;
            if (playerData.getMathLevel() > 0 && subLayer == 0) {
                subLayer += 1;
            }

            if (curioOpt.isPresent()) {
                CultivationArt artItem = (CultivationArt) curioOpt.get().stack().getItem();
                CultivationArtAttribute artAttr = artItem.getArtAttribute();

                if (artAttr != null) {
                    // 2. 根據「功法成長規則 + 當前大境界 + 小層數」算出總加成
                    CultivationArtAttribute.CalculatedBonus bonus = artAttr.calculate(realm, subLayer);

                    // 3A. 呼叫你原本寫好的 ModAttributeModifier 更新基礎三圍（沒設定的屬性預設為 0）
                    double bonusDamage = bonus.flat().getOrDefault(Attributes.ATTACK_DAMAGE, 0.0);
                    double bonusHealth = bonus.flat().getOrDefault(Attributes.MAX_HEALTH, 0.0);
                    double bonusCrit = bonus.flat().getOrDefault(ModCombatAttribute.CRITICAL_CHANCE, 0.0);

                    ModAttributeModifier.updatePlayerMeleeDamage(player, bonusDamage);
                    ModAttributeModifier.updatePlayerMaxHealth(player, bonusHealth);
                    ModAttributeModifier.updatePlayerCriticalChance(player, bonusCrit);

                    // 3B. 套用百分比倍率加成 (例如築基期每層 +5% 攻擊力)
                    applyPercentModifiers(player, bonus);
                }
            } else {
                // 沒裝備功法時，全部歸零並清除倍率
                ModAttributeModifier.updatePlayerMeleeDamage(player, 0);
                ModAttributeModifier.updatePlayerMaxHealth(player, 0);
                ModAttributeModifier.updatePlayerCriticalChance(player, 0);
                clearPercentModifiers(player);
            }
        });
    }

    // 處理百分比倍率 (ADD_MULTIPLIED_BASE)
    private static void applyPercentModifiers(Player player, CultivationArtAttribute.CalculatedBonus bonus) {
        clearPercentModifiers(player);
        bonus.percent().forEach((attr, pct) -> {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null && pct != 0) {
                ResourceLocation id = getPercentModifierId(attr);
                inst.addOrReplacePermanentModifier(new AttributeModifier(id, pct, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        });
    }

    private static void clearPercentModifiers(Player player) {
        for (AttributeInstance inst : player.getAttributes().getSyncableAttributes()) {
            inst.removeModifier(getPercentModifierId(inst.getAttribute()));
        }
    }

    private static ResourceLocation getPercentModifierId(Holder<Attribute> attr) {
        String path = attr.unwrapKey().map(k -> k.location().getPath().replace('.', '_')).orElse("unknown");
        return ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_pct_" + path);
    }
}


