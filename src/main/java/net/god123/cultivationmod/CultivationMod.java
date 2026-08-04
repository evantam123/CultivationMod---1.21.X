package net.god123.cultivationmod;

import net.god123.cultivationmod.block.ModBlocks;
import net.god123.cultivationmod.block.entity.ModBlockEntities;
import net.god123.cultivationmod.component.ModDataComponents;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.god123.cultivationmod.item.ModCreativeModeTabs;
import net.god123.cultivationmod.item.ModItems;
import net.god123.cultivationmod.mana.ManaCommand;
import net.god123.cultivationmod.mana.ManaHudOverlay;
import net.god123.cultivationmod.mana.ModPlayerMana;
import net.god123.cultivationmod.network.sync.CultivationDataSyncPacket;
import net.god123.cultivationmod.recipe.ModRecipes;
import net.god123.cultivationmod.screen.ModMenuTypes;
import net.god123.cultivationmod.screen.custom.CauldronScreen;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CultivationMod.MODID)
public class CultivationMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cultivationmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // 呼叫我們剛剛寫好的指令註冊方法
        ManaCommand.register(event.getDispatcher());
        LOGGER.info("Mana 指令註冊成功！");
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CultivationMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModPlayerMana.register(modEventBus);
        CultivationRealmData.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModDataComponents.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = CultivationMod.MODID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        static void onRegisterGuiLayers(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
            event.registerAbove(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "hotbar"),
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "mana_hud"),
                    (ManaHudOverlay::render)
            );
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                // 延遲 1 tick 發送，確保客戶端準備好
                serverPlayer.server.execute(() -> {
                    CultivationRealmData.CultivationRealm data = serverPlayer.getData(CultivationRealmData.CULTIVATION_REALM);
                    // 同步到客戶端
                    PacketDistributor.sendToPlayer(serverPlayer,
                            new CultivationDataSyncPacket(data.getRealm().name(), data.getExp()));
                });
            }
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.CAULDRON_MENU.get(), CauldronScreen::new);
        }

    }
}
