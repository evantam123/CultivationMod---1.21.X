package net.god123.cultivationmod.item.cultivationart;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
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

public class CultivationArt extends Item implements ICurioItem {

    private final CultivationArtAttribute artAttribute;

    public CultivationArt(Rarity rarity, CultivationArtAttribute artAttribute) {
        super(new Properties()
                .stacksTo(1)
                .rarity(rarity)
        );
        this.artAttribute = artAttribute;
    }

    public CultivationArtAttribute getArtAttribute() {
        return artAttribute;
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
        // 保留你原本的基礎描述
        tooltipComponents.add(Component.translatable("tooltip.cultivationmod.basic_cultivation_art"));

        if (this.artAttribute != null && !this.artAttribute.getRules().isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.literal("")); // 空一行比較美觀
                tooltipComponents.add(Component.literal("【功法境界成長】").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                // 按照 RealmLevel 的順序遍歷（確保練氣 -> 築基 -> 金丹由低到高排列）
                for (CultivationRealmData.RealmLevel realm : CultivationRealmData.RealmLevel.values()) {
                    StageRule rule = this.artAttribute.getRules().get(realm);
                    if (rule == null) continue;

                    // 1. 顯示大境界標題（直接自動抓你 RealmLevel 裡的翻譯鍵！）
                    tooltipComponents.add(Component.literal("▸ ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.translatable(realm.getTranslateKey()).withStyle(ChatFormatting.AQUA)));

                    // 2. 顯示突破大境界的一次性獎勵 (breakthroughBonus)
                    rule.breakthroughBonus().forEach((attr, val) -> {
                        tooltipComponents.add(formatBonusLine("  突破質變: ", attr, val, false));
                    });

                    // 3. 顯示每小層固定數值加成 (perSubLevelFlat)
                    rule.perSubLevelFlat().forEach((attr, val) -> {
                        tooltipComponents.add(formatBonusLine("  每層固定: ", attr, val, false));
                    });

                    // 4. 顯示每小層百分比倍率加成 (perSubLevelPercent)
                    rule.perSubLevelPercent().forEach((attr, pct) -> {
                        tooltipComponents.add(formatBonusLine("  每層倍率: ", attr, pct * 100.0, true));
                    });
                }
            } else {
                tooltipComponents.add(Component.literal("按住 ")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal("[Shift]").withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(" 查看各境界屬性成長").withStyle(ChatFormatting.DARK_GRAY)));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    /**
     * 格式化單行屬性顯示：自動讀取原版/模組屬性的翻譯名稱，並漂亮地去掉多餘小數點 (.0)
     */
    private Component formatBonusLine(String prefix, Holder<Attribute> attr, double value, boolean isPercent) {
        String num = (value % 1.0 == 0) ? String.valueOf((long) value) : String.format("%.1f", value);
        String sign = value >= 0 ? "+" : "";
        String suffix = isPercent ? "%" : "";

        return Component.literal(prefix)
                .withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(attr.value().getDescriptionId()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" " + sign + num + suffix).withStyle(ChatFormatting.GREEN));
    }
}