package net.god123.cultivationmod.attackevent;

import net.god123.cultivationmod.CultivationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = CultivationMod.MODID)
public class CriticalHit {

    private static final Map<Display.TextDisplay, Integer> ACTIVE_FLOATING_TEXTS = new ConcurrentHashMap<>();

    //private static final Map<UUID, Integer> PLAYER_HIT_COUNTS = new HashMap<>();

    /** 1. 每次玩家造成傷害時自動觸發（累積 hitCount）
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        // 確認攻擊者是玩家，且只在伺服器端執行計算
        if (event.getSource().getEntity() instanceof Player player && !player.level().isClientSide()) {
            UUID uuid = player.getUUID();
            int currentHits = PLAYER_HIT_COUNTS.getOrDefault(uuid, 0) + 1;
            PLAYER_HIT_COUNTS.put(uuid, currentHits);
            criticalChance += 100;
        }
    }
     */

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {

        boolean isVanillaCrit = event.isVanillaCritical();

        Player player = event.getEntity();
        Entity target = event.getTarget();
        boolean isFullCharge = player.getAttackStrengthScale(0.5F) > 0.5F;
        int criticalChance = (int) player.getAttributeValue(ModCombatAttribute.CRITICAL_CHANCE);
        int criticalLevel = criticalChance / 100;
        int criticalChanceOverflow = criticalChance % 100;

        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            if (player.getRandom().nextInt(100) < criticalChanceOverflow && isFullCharge) {
                criticalLevel += 1;
            }
            boolean customCritCondition = criticalLevel > 0;
            event.setCriticalHit(customCritCondition);

            // 4. 自訂暴擊傷害倍率（原版暴擊預設為 1.5F，非暴擊預設為 1.0F）
            if (event.isCriticalHit()) {
                float muti = getCritMultiplier(criticalLevel);
                event.setDamageMultiplier(muti); // 改為 2.0 倍傷害
                player.displayClientMessage(Component.literal(getCritTitle(criticalLevel) + " §7(" + muti + "倍)"), false);

                Component floatingText = Component.literal(getCritTitle(criticalLevel) + " §7(" + muti + "倍)");
                spawnFloatingText(serverLevel, target, floatingText);
            }
        }
    }

    /**
     * 透過標準 NBT (load) 設定 TextDisplay，避開 private 方法限制
     */
    private static void spawnFloatingText(ServerLevel level, Entity target, Component text) {
        Display.TextDisplay textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);

        double offsetX = (level.getRandom().nextDouble() - 0.5) * 0.8;
        double offsetZ = (level.getRandom().nextDouble() - 0.5) * 0.8;
        double startY = target.getBoundingBox().maxY + 0.3;

        // 使用 NBT 設定所有展示屬性（100% 公開 API，無存取權限問題）
        CompoundTag nbt = new CompoundTag();
        // 1. 轉換 Component 為 JSON 字串寫入 text 標籤
        nbt.putString("text", Component.Serializer.toJson(text, level.registryAccess()));
        // 2. 設定永遠面向玩家鏡頭 ("center")
        nbt.putString("billboard", "center");
        // 3. 設定背景全透明 (0) 與開啟文字陰影 (true)
        nbt.putInt("background", 0);
        nbt.putBoolean("shadow", true);

        // 載入 NBT 設定並定位
        textDisplay.load(nbt);
        textDisplay.setPos(target.getX() + offsetX, startY, target.getZ() + offsetZ);

        // 生成實體並設定存活 20 ticks（1 秒）
        level.addFreshEntity(textDisplay);
        ACTIVE_FLOATING_TEXTS.put(textDisplay, 20);
    }

    /**
     * 每 Tick 讓飄字往上升起，1 秒後自動清除實體
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ACTIVE_FLOATING_TEXTS.isEmpty()) return;

        Iterator<Map.Entry<Display.TextDisplay, Integer>> iterator = ACTIVE_FLOATING_TEXTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Display.TextDisplay, Integer> entry = iterator.next();
            Display.TextDisplay display = entry.getKey();
            int ticksLeft = entry.getValue();

            if (!display.isAlive() || ticksLeft <= 0) {
                display.discard();
                iterator.remove();
            } else {
                // 每 1/20 秒往上飄 0.035 格
                display.setPos(display.getX(), display.getY() + 0.035, display.getZ());
                entry.setValue(ticksLeft - 1);
            }
        }
    }

    // 會心 → 聚勢 → 凝真 → 破妄 → 天罡 → ... → 斬神 → 滅道
    private static String getCritTitle(int criticalLevel) {
        List<String> chineseNumber = List.of("十", "一", "二", "三", "四", "五", "六", "七", "八", "九");
        int showLevel = criticalLevel % 10;
         if (criticalLevel <= 1) {
            return "§7會心一擊！";
        } else if (criticalLevel <= 10) {
            return "§b會心" + chineseNumber.get(showLevel) + "重";
        } else if (criticalLevel <= 20) {
            return "§9聚勢" + chineseNumber.get(showLevel) + "重";
        } else if (criticalLevel <= 30) {
            return "§e凝真" + chineseNumber.get(showLevel) + "重";
        } else if (criticalLevel <= 40) {
            return "§5破妄" + chineseNumber.get(showLevel) + "重";
        } else if (criticalLevel <= 50) {
            return "§6天罡" + chineseNumber.get(showLevel) + "重";
        } else {
             return "§4斬神" + (criticalLevel - 50) + "重";
         }
    }

    private static float getCritMultiplier(int criticalLevel) {
        if (criticalLevel <= 0) {
            return 1.0F;
        }

        int fullTiers = criticalLevel / 10;    // 已經練滿幾個 10 級
        int currentLevel = criticalLevel % 10; // 當前階段的零頭等級

        // 滿階的乘區：每個滿 10 級都是 (1 + 0.5 * 10) = 6.0 倍
        float multiplier = (float) Math.pow(6.0, fullTiers);

        // 加上當前階段未滿 10 級的乘區
        if (currentLevel > 0) {
            multiplier *= (1.0F + 0.5F * currentLevel);
        }

        return multiplier;
    }
}
