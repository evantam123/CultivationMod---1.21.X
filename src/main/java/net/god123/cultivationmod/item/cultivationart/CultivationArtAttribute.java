package net.god123.cultivationmod.item.cultivationart;

import net.god123.cultivationmod.cultivationrealm.CultivationRealmData.RealmLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CultivationArtAttribute {
    private final Map<RealmLevel, StageRule> rules;

    private CultivationArtAttribute(Map<RealmLevel, StageRule> rules) {
        this.rules = Map.copyOf(rules);
    }

    /**
     * 計算從凡人一路修煉到「當前大境界 + 當前小層數」的總加成
     */
    public CalculatedBonus calculate(RealmLevel currentRealm, int currentSubLayer) {
        Map<Holder<Attribute>, Double> totalFlat = new HashMap<>();
        Map<Holder<Attribute>, Double> totalPercent = new HashMap<>();

        for (RealmLevel stage : RealmLevel.values()) {
            if (stage.ordinal() > currentRealm.ordinal()) break;

            StageRule rule = rules.get(stage);
            if (rule == null) continue;

            // 已經過去的大境界算滿 10 層，當前所在大境界算到 currentSubLayer 層
            int layers = (stage == currentRealm) ? currentSubLayer : stage.getMaxSubLayers();

            if (layers > 0) {
                // 1. 加上踏入該大境界的「一次性突破質變加成」
                rule.breakthroughBonus().forEach((attr, bonus) ->
                        totalFlat.merge(attr, bonus, Double::sum));

                // 2. 加上每一小層的「固定數值加成」
                rule.perSubLevelFlat().forEach((attr, val) ->
                        totalFlat.merge(attr, val * layers, Double::sum));

                // 3. 加上每一小層的「百分比倍率加成」
                rule.perSubLevelPercent().forEach((attr, pct) ->
                        totalPercent.merge(attr, pct * layers, Double::sum));
            }
        }

        return new CalculatedBonus(totalFlat, totalPercent);
    }

    public record CalculatedBonus(Map<Holder<Attribute>, Double> flat, Map<Holder<Attribute>, Double> percent) {}

    public static class Builder {
        private final Map<RealmLevel, StageRule> rules = new EnumMap<>(RealmLevel.class);

        public Builder stage(RealmLevel realm, StageRule rule) {
            rules.put(realm, rule);
            return this;
        }

        public CultivationArtAttribute build() {
            return new CultivationArtAttribute(rules);
        }
    }

    public Map<RealmLevel, StageRule> getRules() {
        return rules;
    }
}
