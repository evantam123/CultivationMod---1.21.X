package net.god123.cultivationmod.attackevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CombatAttribute {
    public static final Codec<CombatAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("criticalChance").forGetter(CombatAttribute::getCriticalChance)
        ).apply(instance, CombatAttribute::new)
    );

    private int criticalChance;

    public CombatAttribute() {
        this.criticalChance = 0;
    }

    public CombatAttribute(int criticalChance) {
        this.criticalChance = criticalChance;
    }

    public int getCriticalChance() {return this.criticalChance;}
    public void setCriticalChance(int criticalChance) {this.criticalChance = criticalChance;}

}
