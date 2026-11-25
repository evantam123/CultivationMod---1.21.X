package net.god123.cultivationmod.item.cultivationart;

import net.god123.cultivationmod.attackevent.ModCombatAttribute;
import net.god123.cultivationmod.component.ModDataComponents;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static net.god123.cultivationmod.cultivationrealm.CultivationRealmData.CULTIVATION_REALM;

public class BasicCultivationArt extends CultivationArt {

    public BasicCultivationArt() {
        super(
                Rarity.COMMON,
                new CultivationArtAttribute.Builder()
                .stage(CultivationRealmData.RealmLevel.QI_REFINING, new StageRule.Builder()
                        .addPerLayer(Attributes.ATTACK_DAMAGE, 1.0)
                        .addPerLayer(ModCombatAttribute.CRITICAL_CHANCE, 5)
                        .build())
                .stage(CultivationRealmData.RealmLevel.FOUNDATION, new StageRule.Builder()
                        .addBreakthroughBonus(Attributes.ATTACK_DAMAGE, 10)
                        .addPerLayer(Attributes.ATTACK_DAMAGE, 2.0)
                        .addPercentPerLayer(Attributes.MAX_HEALTH, 10)
                        .build())
                .build());
    }
}