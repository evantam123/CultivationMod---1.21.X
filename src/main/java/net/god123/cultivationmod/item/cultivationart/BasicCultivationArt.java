package net.god123.cultivationmod.item.cultivationart;

import net.god123.cultivationmod.component.ModDataComponents;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static net.god123.cultivationmod.cultivationrealm.CultivationRealmData.CULTIVATION_REALM;

public class BasicCultivationArt extends Item implements ICurioItem {

    public BasicCultivationArt() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.COMMON)
                .component(ModDataComponents.ART_NUM.get(), 10)
        );
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 可以在这里添加装备条件，比如需要特定等级
        return true;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 装备时触发的逻辑
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 卸下时触发的逻辑
        LivingEntity wearer = slotContext.entity();
        if (wearer instanceof Player player && canUnequip(slotContext, stack)) {

        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 每个 tick 触发的逻辑（比如每秒恢复生命）
    }
    
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();

        // 检查是否是玩家
        if (wearer instanceof Player player) {
            CultivationRealmData.CultivationRealm data = wearer.getData(CULTIVATION_REALM);
            if (data.getExp() == 0 && data.getRealm() == CultivationRealmData.RealmLevel.MORTAL) {
                return true;
            }
            if (!player.level().isClientSide() && !(player instanceof net.neoforged.neoforge.common.util.FakePlayer)) {
                player.displayClientMessage(
                        Component.translatable("curios.cultivationmod.chat.cannot_equip")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        false // 设置为 false 显示在聊天栏，设置为 true 显示在物品栏上方（ActionBar）
                );
            }
            // 你也可以播放音效
            // player.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);
        }

        // 返回 false 阻止卸下
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.cultivationmod.basic_cultivation_art.shift_down"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.cultivationmod.basic_cultivation_art"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}