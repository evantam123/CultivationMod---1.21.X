package net.god123.cultivationmod.network.request;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.god123.cultivationmod.network.response.RealmUpResponsePacket;
import net.god123.cultivationmod.network.sync.CultivationDataSyncPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RealmUpRequestPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RealmUpRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "realm_up_request"));

    public static final StreamCodec<FriendlyByteBuf, RealmUpRequestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {

            },
            buf -> new RealmUpRequestPacket()
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) iPayloadContext.player();
            CultivationRealmData.CultivationRealm data = player.getData(CultivationRealmData.CULTIVATION_REALM);
            boolean success = data.RealmUp();
            String realmId = data.getRealm().name();
            int exp = data.getExp();
            iPayloadContext.reply(new CultivationDataSyncPacket(realmId, exp));
            iPayloadContext.reply(new RealmUpResponsePacket(success));
        });
    }
}
