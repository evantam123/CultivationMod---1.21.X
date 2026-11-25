package net.god123.cultivationmod.cultivationrealm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CultivationRealmData{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "cultivationmod");

    public enum RealmLevel  implements StringRepresentable{
        MORTAL("mortal", 100),
        QI_REFINING("qi_refining", 1000),
        FOUNDATION("foundation", 10000),
        GOLDEN_CORE("golden_core", 100000),
        NASCENT_SOUL("nascent_soul", 1000000),
        SPIRIT_SHEDDING("spirit_shedding", 10000000);

        private final String translate_key;
        private final int max_exp;

        RealmLevel(String translate_key, int max_exp) {
            this.translate_key = translate_key;
            this.max_exp = max_exp;
        }

        public int getMaxSubLayers() {
            return this == MORTAL ? 0 : 10;
        }

        public String getTranslateKey() {
            return "realm.cultivationmod." + translate_key;
        }

        public int getMaxExp() {
            return max_exp;
        }

        @Override
        public String getSerializedName() {
            return getTranslateKey();
       }

        public static final Codec<RealmLevel> CODEC = StringRepresentable.fromValues(RealmLevel::values);

    }

    public static class CultivationRealm {
        private RealmLevel realm;
        private int mathLevel;
        private int exp;

        public CultivationRealm() {
            this.realm = RealmLevel.MORTAL;
            this.mathLevel = 0;
            this.exp = 0;
        }

        public CultivationRealm(RealmLevel realm,int mathLevel, int exp) {
            this.realm = realm;
            this.mathLevel = mathLevel;
            this.exp = exp;
        }

        public RealmLevel getRealm() {
            return realm;
        }

        public void setRealm(RealmLevel realm) {
            this.realm = realm;
        }

        public int getMathLevel() {
            return this.mathLevel;
        }

        public void setMathLevel(int mathLevel) {
            this.mathLevel = mathLevel;
        }

        public int getExp() {
            return exp;
        }

        public void setExp(int exp) {
            this.exp = Math.min(exp, realm.getMaxExp());
        }

        public void addExp(int exp) {
            this.exp = Math.min(this.exp + exp, realm.getMaxExp());
        }

        public boolean isRealmUpExpOk() {
            return this.exp == realm.getMaxExp();
        }

        public void refreshRealm() {
            RealmLevel[] realmlevels = RealmLevel.values();
            if (this.mathLevel <= 0) {
                this.realm = RealmLevel.MORTAL;
            } else {
                int index = (this.mathLevel - 1) / 10 + 1;
                this.realm = realmlevels[Math.min(index, realmlevels.length - 1)];
            }
        }

        public boolean RealmUp() {
            RealmLevel[] realmlevels = RealmLevel.values();
            if (isRealmUpExpOk()) {
                int maxMathLevel = (realmlevels.length - 1) * 10;
                if(mathLevel < maxMathLevel) {
                    this.exp = 0;
                    this.mathLevel += 1;
                    refreshRealm();
                    return true;
                }
            }
            return false;
        }

         public String getRealmTranslateKey() {
            return realm.getTranslateKey();
         }

         public String getSublayerTranslateKey(int subLayer) {
            if(subLayer > 0) {
                return "word.cultivationmod." + subLayer;
            } else {
                return "realm.cultivationmod.full";
            }
         }

         public Component getRealmNameComponent() {
            int sublayer = mathLevel % 10;
            MutableComponent realmNameComponent = Component.translatable(realm.getTranslateKey());
            if (mathLevel > 0) {
                realmNameComponent = realmNameComponent.append(Component.translatable(getSublayerTranslateKey(sublayer)));
            }
            if (sublayer > 0) {
                realmNameComponent = realmNameComponent.append(Component.translatable("realm.cultivationmod.tier"));
            }
            return realmNameComponent;
         }

         public static final Codec<CultivationRealm> CODEC = RecordCodecBuilder.create(instance ->
                 instance.group(
                         RealmLevel.CODEC.fieldOf("realm").forGetter(CultivationRealm::getRealm),
                         Codec.INT.fieldOf("mathLevel").forGetter(CultivationRealm::getMathLevel),
                         Codec.INT.fieldOf("exp").forGetter(CultivationRealm::getExp)
                 ).apply(instance, CultivationRealm::new)
         );
    }

    public static final Supplier<AttachmentType<CultivationRealm>> CULTIVATION_REALM = ATTACHMENT_TYPES.register(
            "cultivation_realm",
            () -> AttachmentType.builder(() -> new CultivationRealm())
                    .serialize(CultivationRealm.CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
