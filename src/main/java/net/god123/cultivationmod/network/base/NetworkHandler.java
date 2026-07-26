package net.god123.cultivationmod.network.base;

import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.network.request.CultivateRequestPacket;
import net.god123.cultivationmod.network.request.RealmUpRequestPacket;
import net.god123.cultivationmod.network.response.RealmUpResponsePacket;
import net.god123.cultivationmod.network.sync.CultivationDataSyncPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CultivationMod.MODID)
public class NetworkHandler {
    
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(CultivationMod.MODID);
        
        registrar.playToServer(
                RealmUpRequestPacket.TYPE,
                RealmUpRequestPacket.STREAM_CODEC,
                RealmUpRequestPacket::handle
        );

        registrar.playToServer(
                CultivateRequestPacket.TYPE,
                CultivateRequestPacket.STREAM_CODEC,
                CultivateRequestPacket::handle
        );

        registrar.playToClient(
                RealmUpResponsePacket.TYPE,
                RealmUpResponsePacket.STREAM_CODEC,
                RealmUpResponsePacket::handle
        );

        registrar.playToClient(
                CultivationDataSyncPacket.TYPE,
                CultivationDataSyncPacket.STREAM_CODEC,
                CultivationDataSyncPacket::handle
        );
    }
}
