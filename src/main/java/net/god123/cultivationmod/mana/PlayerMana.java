package net.god123.cultivationmod.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerMana {
    public static final Codec<PlayerMana> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mana").forGetter(PlayerMana::getMana),
            Codec.INT.fieldOf("max_mana").forGetter(PlayerMana::getMaxMana)
        ).apply(instance, PlayerMana::new)
    );

    private int mana;
    private int maxMana;

    public PlayerMana() {
        this.mana = 100;
        this.maxMana = 100;
    }

    public PlayerMana(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public int getMana() {return this.mana;}
    public void setMana(int mana) {this.mana = Math.clamp(mana, 0, this.maxMana);}

    public int getMaxMana() {return  this.maxMana;}
    public void setMaxMana(int maxMana) {this.maxMana = maxMana;}
}
