package com.bawnorton.quickloot.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class RenderHelper {

    public static void drawBorderedArea(MatrixStack matrices, int x, int y, int width, int height, int borderThickness, int colour, int borderColour) {
        drawArea(matrices, x, y, width, height, colour);
        drawBorder(matrices, x, y, width, height, borderThickness, borderColour);
    }

    public static void drawArea(MatrixStack matrices, int x, int y, int width, int height, int colour) {
        fill(matrices, x, y, x + width, y + height, colour);
    }

    public static void drawBorder(MatrixStack matrices, int x, int y, int width, int height, int thickness, int colour) {
        fill(matrices, x, y, x + width, y + thickness, colour);
        fill(matrices, x, y + height - thickness, x + width, y + height, colour);
        fill(matrices, x, y + thickness, x + thickness, y + height - thickness, colour);
        fill(matrices, x + width - thickness, y + thickness, x + width, y + height - thickness, colour);
    }

    public static void drawText(MatrixStack matrixStack, String title, int x, int y, int colour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrixStack, title, x, y, colour);
    }
}
