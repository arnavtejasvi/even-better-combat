package com.arnav.evenbettercombat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@Environment(EnvType.CLIENT)
public final class HudOverlay {
    private static final int MARGIN = 8;
    private static final int PAD_X = 5;
    private static final int PAD_Y = 3;
    private static final int LINE_H = 10;
    private static final int BG = 0x99000000;
    private static final int WHITE = 0xFFFFFF;
    private static final int DIM = 0x888888;

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        if (!TrackerState.hudVisible) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        TextRenderer tr = client.textRenderer;
        String deathLine = "☠ Deaths: " + TrackerState.deaths;
        String streakLine = TrackerState.streak > 0
            ? "⚔ Streak: " + TrackerState.streak + "  (best: " + TrackerState.bestStreak + ")"
            : null;

        int lineCount = streakLine != null ? 2 : 1;
        int width = tr.getWidth(deathLine);
        if (streakLine != null) width = Math.max(width, tr.getWidth(streakLine));

        int boxW = width + PAD_X * 2;
        int boxH = lineCount * LINE_H + PAD_Y * 2 - 1;

        ctx.fill(MARGIN, MARGIN, MARGIN + boxW, MARGIN + boxH, BG);

        int textX = MARGIN + PAD_X;
        int textY = MARGIN + PAD_Y;
        ctx.drawTextWithShadow(tr, deathLine, textX, textY, TrackerState.streak == 0 ? DIM : WHITE);
        if (streakLine != null) {
            ctx.drawTextWithShadow(tr, streakLine, textX, textY + LINE_H, WHITE);
        }
    }
}
