package com.bawnorton.quickloot.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static net.minecraft.client.gui.DrawableHelper.fill;

@SuppressWarnings("DuplicatedCode")
public class RenderHelper {
    public static void drawArea(MatrixStack matrices, int x, int y, int width, int height, int colour) {
        fill(matrices, x, y, x + width, y + height, colour);
    }

    public static void drawBorder(MatrixStack matrices, int x, int y, int width, int height, int thickness, int colour) {
        fill(matrices, x, y, x + width, y + thickness, colour);
        fill(matrices, x, y + height - thickness, x + width, y + height, colour);
        fill(matrices, x, y + thickness, x + thickness, y + height - thickness, colour);
        fill(matrices, x + width - thickness, y + thickness, x + width, y + height - thickness, colour);
    }

    public static void drawBorderedArea(MatrixStack matrices, int x, int y, int width, int height, int borderThickness, int colour, int borderColour) {
        drawArea(matrices, x, y, width, height, colour);
        drawBorder(matrices, x, y, width, height, borderThickness, borderColour);
    }

    public static void drawText(MatrixStack matrixStack, String title, int x, int y, int colour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrixStack, title, x, y, colour);
    }

    public static void drawOutlinedText(MatrixStack matrixStack, String title, int x, int y, int colour, int outlineColour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        OrderedText text = OrderedText.styledForwardsVisitedString(title, Style.EMPTY);
        textRenderer.drawWithOutline(text, x, y, colour, outlineColour, matrixStack.peek().getPositionMatrix(), immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        immediate.draw();
    }

    public static void drawBorderedAreaAroundText(MatrixStack matrixStack, String title, int x, int y, int textPadding, int borderThickness, int colour, int borderColour, int textColour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = textRenderer.getWidth(title);
        int height = textRenderer.fontHeight - 2;
        drawBorderedArea(matrixStack, x - textPadding, y - textPadding, width + textPadding * 2, height + textPadding * 2, borderThickness, colour, borderColour);
        drawText(matrixStack, title, x, y, textColour);
    }

    public static void drawItem(MatrixStack matrixStack, ItemStack stack, int x, int y) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderInGui(matrixStack, stack, x, y);
    }

    public static void startScissor(int x, int y, int width, int height) {
        DrawableHelper.enableScissor(x, y, x + width, y + height);
    }

    public static void endScissor() {
        DrawableHelper.disableScissor();
    }
}
