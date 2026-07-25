package net.god123.Firstmod.cultivationrealm;

import net.god123.Firstmod.Firstmod;
import net.god123.Firstmod.component.ModDataComponents;
import net.god123.Firstmod.network.sync.CultivationDataSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = Firstmod.MODID)
public class CultivateHandler {
    private static final Set<UUID> CULTIVATING_PLAYERS = new HashSet<>();

    private static final Map<UUID, Vec3> LAST_POSITION = new HashMap<>();

    private static final int ONE_SEC = 20;

    private static final int EXP_INTERVAL = ONE_SEC;

    private static final int EXP_PER_INTERVAL = 5;

    private static final Set<UUID> TICK_TACKER = new HashSet<>();

    public static void toggleCultivate(ServerPlayer player) {
        UUID uuid = player.getUUID();

        if(CULTIVATING_PLAYERS.contains(uuid)) {
            CULTIVATING_PLAYERS.remove(uuid);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§e你停止了打坐"),
                    true
            );
        } else {
            CULTIVATING_PLAYERS.add(uuid);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§a你開始打坐修煉..."),
                    true
            );
        }
    }

    public static boolean isCulivating(Player player) {
        return CULTIVATING_PLAYERS.contains(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UUID uuid = player.getUUID();

        if(!isCulivating(player)) return;

        if(!canCulivating(serverPlayer)) {
            CULTIVATING_PLAYERS.remove(uuid);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§c打坐被打斷！"),
                    true
            );
            return;
        }

        if (player.tickCount % EXP_INTERVAL == 0) {
            CultivationRealmData.CultivationRealm data = serverPlayer.getData(CultivationRealmData.CULTIVATION_REALM);

            if (data.isRealmUpExpOk()) {
                // 經驗已滿，提示突破
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§e經驗已滿！請突破境界！"),
                        true
                );
                // 停止自動獲得經驗
                return;
            }

            data.addExp(getExpPerInterval(serverPlayer));

            // 發送粒子效果（客戶端可見）
            // 這裡可以加一些視覺效果

            if (player.tickCount % (EXP_INTERVAL * 5) == 0) {
                PacketDistributor.sendToPlayer(serverPlayer, new CultivationDataSyncPacket(data.getRealm().name(), data.getExp()));
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                String.format("§7修煉中... 經驗 +%d (§b%d§7/§b%d§7)",
                                        getExpPerInterval(serverPlayer)* 5,
                                        data.getExp(),
                                        data.getRealm().getMaxExp())
                        ),
                        true
                );
            }
        }
    }

    private static int getExpPerInterval(Player player) {

        int bonus = CuriosApi.getCuriosInventory(player)
                .map(curiosInventory -> curiosInventory.getCurios().get("cultivation_art"))
                .map(stacksHandler -> stacksHandler.getStacks().getStackInSlot(0))
                .filter(stackInSlot -> !stackInSlot.isEmpty())
                .map(stackInSlot -> {
                    // 2. 读取你的 Data Component 数据
                    // 假设你的 ModDataComponents.ART_NUM 注册类型是 Integer
                    Integer value = stackInSlot.get(ModDataComponents.ART_NUM.get());
                    return value != null ? value : 0; // 如果物品上没有这个数据，默认加成系数为 1
                })
                .orElse(0);

        return EXP_PER_INTERVAL * bonus;
    }

    private static boolean canCulivating(ServerPlayer player) {

        if(!player.onGround()) return false;

        if (player.isInWater()) return false;

        if (isMoving(player)) return false;

        if (player.getLastHurtByMob() != null) return false;

        return true;
    }

    private static boolean isMoving(ServerPlayer player) {
        double dx = player.getX() - player.xOld;
        double dz = player.getZ() - player.zOld;
        return Math.abs(dx) > 0.01 || Math.abs(dz) > 0.01;
    }
    private static boolean isMoving2(ServerPlayer player) {
        // 檢測玩家是否在移動
        UUID uuid = player.getUUID();
        Vec3 lastPos = LAST_POSITION.get(uuid);

        if (lastPos == null) {
            LAST_POSITION.put(uuid, player.position());
            return false;
        }

        double dx = player.getX() - lastPos.x;
        double dz = player.getZ() - lastPos.z;

        // 移動超過 0.01 格就算移動
        return Math.abs(dx) > 0.01 || Math.abs(dz) > 0.01;
    }

    // 玩家移動時打斷打坐
    @SubscribeEvent
    public static void onPlayerMove(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UUID uuid = player.getUUID();
        if (!CULTIVATING_PLAYERS.contains(uuid)) return;

        // 如果移動了，打斷打坐
        if (isMoving(serverPlayer)) {
            CULTIVATING_PLAYERS.remove(uuid);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§c移動打斷了打坐！"),
                    true
            );
        }
    }

    // 玩家受傷時打斷打坐
    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        UUID uuid = player.getUUID();
        if (CULTIVATING_PLAYERS.contains(uuid)) {
            CULTIVATING_PLAYERS.remove(uuid);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§c受傷打斷了打坐！"),
                    true
            );
        }
    }


}
