package net.god123.cultivationmod.mana;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ManaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mana")
                        .requires(source -> source.hasPermission(2)) // 需要管理員權限 (OP等級2)
                        .then(Commands.literal("get")
                                .executes(context -> getMana(context.getSource())))
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)) // 只能輸入正整數
                                        .executes(context -> setMana(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "amount")
                                        ))
                                )
                        )
        );
    }

    private static int getMana(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            // 讀取玩家身上的 Mana 資料
            PlayerMana manaData = player.getData(ModPlayerMana.MANA_ATTACHMENT.get());

            // 在聊天欄回傳訊息給執行指令的玩家
            source.sendSuccess(() -> Component.literal(
                    "§b[Mana] §7目前魔法值: §e" + manaData.getMana() + "§7 / §e" + manaData.getMaxMana()
            ), false);

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("只能由線上玩家執行此指令"));
            return 0;
        }
    }

    private static int setMana(CommandSourceStack source, int amount) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            PlayerMana manaData = player.getData(ModPlayerMana.MANA_ATTACHMENT.get());

            // 修改數值
            manaData.setMana(amount);

            // 重要：修改後必須重新 setData，系統才會知道資料變動並在退出遊戲時存檔
            player.setData(ModPlayerMana.MANA_ATTACHMENT.get(), manaData);

            source.sendSuccess(() -> Component.literal(
                    "§b[Mana] §7已將你的魔法值設定為: §a" + amount
            ), false);

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("設定失敗"));
            return 0;
        }
    }
}

