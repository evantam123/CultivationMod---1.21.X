package net.god123.cultivationmod.cultivationrealm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.network.sync.CultivationDataSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.god123.cultivationmod.cultivationrealm.CultivationRealmData.*;

@EventBusSubscriber(modid = CultivationMod.MODID)
public class CultivationRealmCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("cultivation")
                        .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
                        .then(Commands.argument("target", EntityArgument.player())
                                // SET 指令
                                .then(Commands.literal("set")
                                        .then(Commands.argument("level", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                    int level = IntegerArgumentType.getInteger(context, "level");

                                                    // ✅ 使用 DataAttachment
                                                    CultivationRealm data = player.getData(CULTIVATION_REALM);

                                                    RealmLevel[] realmLevels = RealmLevel.values();
                                                    if (level < realmLevels.length && level >= 0) {
                                                        data.setRealm(realmLevels[level]);
                                                        PacketDistributor.sendToPlayer(player, new CultivationDataSyncPacket(data.getRealm().name(), data.getExp()));
                                                    } else {
                                                        context.getSource().sendFailure(
                                                                Component.translatable("command.cultivationmod.cultivation_set_fail")
                                                        );
                                                    }

                                                    Component commandMsg = Component.translatable(
                                                            "command.cultivationmod.cultivation_set_success",
                                                            Component.literal(player.getScoreboardName()).withStyle(ChatFormatting.YELLOW),
                                                            Component.translatable(data.getRealmTranslateKey())
                                                    );
                                                    context.getSource().sendSuccess(() -> commandMsg, true);
                                                    return 1;
                                                })
                                        )
                                )
                                // QUERY 指令
                                .then(Commands.literal("query")
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                            CultivationRealm data = player.getData(CULTIVATION_REALM);

                                            Component realmNameComponent = Component.translatable(data.getRealmTranslateKey());
                                            Component commandMsg = Component.translatable(
                                                    "command.cultivationmod.cultivation_query",
                                                    Component.literal(player.getScoreboardName()).withStyle(ChatFormatting.YELLOW),
                                                    realmNameComponent
                                            );
                                            context.getSource().sendSuccess(() -> commandMsg, true);

                                            return 1;
                                        })
                                )
                                //exp
                                .then(Commands.literal("exp")
                                    .then(Commands.literal("set")
                                        .then(Commands.argument("exp", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                CultivationRealm data = player.getData(CULTIVATION_REALM);
                                                int exp = IntegerArgumentType.getInteger(context, "exp");
                                                data.setExp(exp);
                                                PacketDistributor.sendToPlayer(player, new CultivationDataSyncPacket(data.getRealm().name(), data.getExp()));
                                                context.getSource().sendSuccess(() -> getExpTextComponent(player), false);

                                                return 1;
                                            })
                                        )
                                    )
                                    .then(Commands.literal("get")
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                            CultivationRealm data = player.getData(CULTIVATION_REALM);
                                            context.getSource().sendSuccess(() -> getExpTextComponent(player), false);

                                            return 1;
                                        })
                                    )
                                    .then(Commands.literal("add")
                                        .then(Commands.argument("exp", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                                CultivationRealm data = player.getData(CULTIVATION_REALM);
                                                int exp = IntegerArgumentType.getInteger(context, "exp");
                                                data.addExp(exp);
                                                PacketDistributor.sendToPlayer(player, new CultivationDataSyncPacket(data.getRealm().name(), data.getExp()));
                                                context.getSource().sendSuccess(() -> getExpTextComponent(player), false);

                                                return 1;
                                            })
                                        )
                                    )
                                )
                                //up
                                .then(Commands.literal("up")
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                            CultivationRealm data = player.getData(CULTIVATION_REALM);
                                            data.RealmUp();

                                            Component realmNameComponent = Component.translatable(data.getRealmTranslateKey());
                                            Component commandMsg = Component.translatable(
                                                    "command.cultivationmod.cultivation_query",
                                                    player.getScoreboardName(),
                                                    realmNameComponent
                                            );
                                            context.getSource().sendSuccess(() -> commandMsg, true);

                                            return 1;
                                        })
                                )
                        )
        );
    }

    public static Component getExpTextComponent(Player player) {
        CultivationRealm data = player.getData(CULTIVATION_REALM);
        return Component.translatable(
                "command.cultivationmod.get_exp",
                Component.literal(player.getScoreboardName()).withStyle(ChatFormatting.YELLOW),
                Component.literal(String.valueOf(data.getExp())).withStyle(ChatFormatting.AQUA)
        );
    }

}