package net.god123.cultivationmod.item.cultivationart;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;

public record StageRule(
        Map<Holder<Attribute>, Double> perSubLevelFlat,      // 每小層增加的固定值 (ADD_VALUE)
        Map<Holder<Attribute>, Double> perSubLevelPercent,   // 每小層增加的百分比倍率 (ADD_MULTIPLIED_BASE, 例如 0.05 = +5%)
        Map<Holder<Attribute>, Double> breakthroughBonus     // 剛踏入此大境界時的一次性額外質變加成
) {
    public static class Builder {
        private final Map<Holder<Attribute>, Double> flat = new HashMap<>();
        private final Map<Holder<Attribute>, Double> percent = new HashMap<>();
        private final Map<Holder<Attribute>, Double> breakthrough = new HashMap<>();

        // 設定該境界「每一小層」加多少固定數值
        public Builder addPerLayer(Holder<Attribute> attr, double value) {
            flat.put(attr, value);
            return this;
        }

        // 設定該境界「每一小層」加多少百分比倍率 (如 0.02 代表每層 +2%)
        public Builder addPercentPerLayer(Holder<Attribute> attr, double percentValue) {
            percent.put(attr, percentValue);
            return this;
        }

        // 設定「突破到此大境界」時的一次性爆發加成
        public Builder addBreakthroughBonus(Holder<Attribute> attr, double value) {
            breakthrough.put(attr, value);
            return this;
        }

        public StageRule build() {
            return new StageRule(Map.copyOf(flat), Map.copyOf(percent), Map.copyOf(breakthrough));
        }
    }
}