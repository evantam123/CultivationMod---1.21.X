package net.god123.cultivationmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.god123.cultivationmod.CultivationMod;
import net.god123.cultivationmod.cultivationrealm.CultivationRealmData;
import net.god123.cultivationmod.network.request.RealmUpRequestPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class CultivationScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CultivationMod.MODID, "textures/gui/cultivation.png");

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private int guiLeft;
    private int guiTop;

    private CultivationRealmData.CultivationRealm playerData;

    public CultivationScreen() {
        super(Component.literal("修仙境界"));
    }

    @Override
    protected void init() {
        super.init();

        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;

        // 获取玩家数据
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            // 注意：请确保你的 DataComponent 或 Capability 获取方式正确
            playerData = player.getData(CultivationRealmData.CULTIVATION_REALM);
        }

        // 添加突破按钮
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.cultivationmod.button.breakthrough"),
                        button -> handleRealmUp()
                ).bounds(
                        guiLeft + 20, // 调整了位置，避免与关闭按钮重叠或超出边框
                        guiTop + 130,
                        100,
                        20
                ).tooltip(Tooltip.create(Component.translatable("gui.cultivationmod.button.breakthrough.tooltip")))
                .build());

        // 添加关闭按钮
        this.addRenderableWidget(Button.builder(
                Component.literal("X"),
                button -> this.onClose()
        ).bounds(
                guiLeft + 135,
                guiTop + 130,
                20,
                20
        ).build());
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;  // ← 不暫停遊戲！
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. 渲染背景暗化效果（1.21 推荐使用此方法）
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 2. 设置渲染状态

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // 3. 绘制GUI背景（从纹理文件读取）
        guiGraphics.blit(GUI_TEXTURE, guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // 4. 绘制自定义GUI边框
        //drawGuiBorder(guiGraphics);

        // 5. 绘制所有文本和经验条
        drawGuiContent(guiGraphics);
        // 6. 渲染按钮以及按钮的 Tooltip（必须调用 super）
        for(Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        // 7. 恢复状态
        RenderSystem.disableBlend();
    }

    private void drawGuiBorder(GuiGraphics guiGraphics) {
        int borderColor = 0xFF8B8B8B;
        guiGraphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + 1, borderColor);
        guiGraphics.fill(guiLeft, guiTop + GUI_HEIGHT - 1, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, borderColor);
        guiGraphics.fill(guiLeft, guiTop, guiLeft + 1, guiTop + GUI_HEIGHT, borderColor);
        guiGraphics.fill(guiLeft + GUI_WIDTH - 1, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, borderColor);
    }

    private void drawGuiContent(GuiGraphics guiGraphics) {
        if (playerData == null) {
            guiGraphics.drawCenteredString(this.font, "无法加载玩家数据", guiLeft + GUI_WIDTH / 2, guiTop + GUI_HEIGHT / 2, 0xFF0000);
            return;
        }

        int textColor = 0xFFFFFF;

        // 标题
        drawTextWithBackground(guiGraphics, "§6§l修 仙 境 界",
                guiLeft + GUI_WIDTH / 2 - 40, guiTop + 10, 0xFF000000);

        Component realmNameComponent = Component.translatable(playerData.getRealmTranslateKey());
        Component realmText = Component.translatable("gui.cultivationmod.current_realm", realmNameComponent);
        guiGraphics.drawString(this.font, realmText, guiLeft + 20, guiTop + 35, textColor, false);

        // 经验值
        Component expText = Component.translatable(
                "gui.cultivationmod.exp_format",
                Component.literal(String.valueOf(playerData.getExp())).withStyle(ChatFormatting.AQUA),
                Component.literal(String.valueOf(playerData.getRealm().getMaxExp())).withStyle(ChatFormatting.AQUA)
        );
        guiGraphics.drawString(this.font, expText, guiLeft + 20, guiTop + 55, textColor, false);

        // 绘制经验条
        drawExpBar(guiGraphics);

        // 显示突破状态
        String statusKey;
        int statusColor;
        if (playerData.isRealmUpExpOk()) {
            statusKey = "gui.cultivationmod.breakthrough_status_ok";
            statusColor = 0x00FF00;
        } else {
            statusKey = "gui.cultivationmod.breakthrough_status_not_ok";
            statusColor = 0xFF0000;
        }
        guiGraphics.drawString(this.font, Component.translatable(statusKey), guiLeft + 20, guiTop + 100, statusColor, false);

        // 绘制境界列表
        drawRealmList(guiGraphics);
    }

    private void drawTextWithBackground(GuiGraphics guiGraphics, String text, int x, int y, int bgColor) {
        int width = this.font.width(text);
        guiGraphics.fill(x - 2, y - 1, x + width + 2, y + this.font.lineHeight + 1, bgColor);
        guiGraphics.drawString(this.font, text, x, y, 0xFFFFFF, false);
    }

    private void drawExpBar(GuiGraphics guiGraphics) {
        int barX = guiLeft + 20;
        int barY = guiTop + 72;
        int barWidth = 136;
        int barHeight = 12;

        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);

        guiGraphics.fill(barX, barY, barX + barWidth, barY + 1, 0xFF888888);
        guiGraphics.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF888888);
        guiGraphics.fill(barX, barY, barX + 1, barY + barHeight, 0xFF888888);
        guiGraphics.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF888888);

        float progress = (float) playerData.getExp() / playerData.getRealm().getMaxExp();
        int filledWidth = (int) (barWidth * Math.min(progress, 1.0f));

        int color;
        if (progress >= 1.0f) {
            color = 0xFF00FF00;
        } else if (progress > 0.7f) {
            color = 0xFFFFFF00;
        } else if (progress > 0.3f) {
            color = 0xFFFF8800;
        } else {
            color = 0xFFFF0000;
        }

        if (filledWidth > 0) {
            guiGraphics.fill(barX + 1, barY + 1, barX + filledWidth - 1, barY + barHeight - 1, color);
            guiGraphics.fill(barX + 1, barY + 1, barX + filledWidth - 1, barY + 4, 0x44FFFFFF);
        }

        String percentText = String.format("%.0f%%", progress * 100);
        int textX = barX + barWidth / 2 - this.font.width(percentText) / 2;
        int textY = barY + (barHeight - this.font.lineHeight) / 2;
        guiGraphics.drawString(this.font, percentText, textX, textY, 0xFFFFFF, true);
    }

    private void drawRealmList(GuiGraphics guiGraphics) {
        guiGraphics.drawString(this.font, Component.translatable("gui.cultivationmod.text"), guiLeft + 20, guiTop + 115, 0xCCCCCC, false);
    }

    private void handleRealmUp() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (playerData != null) {
            PacketDistributor.sendToServer(new RealmUpRequestPacket());
            player.sendSystemMessage(Component.translatable("chat.cultivationmod.breakthrough_trying"));
            // 关闭窗口
            this.onClose();
        }
    }
}
