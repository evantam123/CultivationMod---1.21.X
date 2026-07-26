package net.god123.cultivationmod.mana;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class ManaHudOverlay {
    public static void render(GuiGraphics guiGraphics, DeltaTracker partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.screen !=null) {
            return;
        }

        PlayerMana manaData = player.getData(ModPlayerMana.MANA_ATTACHMENT.get());
        String text = "Mana: " + manaData.getMana() + " / " + manaData.getMaxMana();

        int x = 10;
        int y = minecraft.getWindow().getGuiScaledHeight() - 20;

        int color = 0x00AAFF;

        guiGraphics.drawString(minecraft.font, text, x, y, color, true);
    }
}
