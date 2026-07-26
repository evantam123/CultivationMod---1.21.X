package net.god123.cultivationmod.network.response;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RealmUpResponsePacket(boolean success) implements CustomPacketPayload {

    public static final Type<RealmUpResponsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_up_response"));

    public static final StreamCodec<FriendlyByteBuf, RealmUpResponsePacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.success());
            },
            buf -> new RealmUpResponsePacket(
                    buf.readBoolean()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player != null) {
                CultivationRealmData.CultivationRealm data = player.getData(CultivationRealmData.CULTIVATION_REALM);
                Component realmComponent = Component.translatable(data.getRealmTranslateKey());
                String translationKey = success ? "chat.cultivationmod.breakthrough_success" : "chat.cultivationmod.breakthrough_failure";
                Component breakText = Component.translatable(translationKey, realmComponent);
                player.sendSystemMessage(breakText);
            }
        });
    }
}
