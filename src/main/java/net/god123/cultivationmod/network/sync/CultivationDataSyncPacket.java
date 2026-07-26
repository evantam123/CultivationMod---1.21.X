package net.god123.cultivationmod.network.sync;

import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CultivationDataSyncPacket(String realmId, int exp) implements CustomPacketPayload {

    public static final Type<CultivationDataSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("god123awsomemod", "cultivation_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, CultivationDataSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.realmId());
                buf.writeInt(payload.exp());
            },
            buf -> new CultivationDataSyncPacket(
                    buf.readUtf(),
                    buf.readInt()
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
            CultivationRealmData.RealmLevel realm = CultivationRealmData.RealmLevel.valueOf(realmId);
            if (player != null) {
                CultivationRealmData.CultivationRealm data = player.getData(CultivationRealmData.CULTIVATION_REALM);
                data.setRealm(realm);
                data.setExp(exp);
            }
        });
    }
}
