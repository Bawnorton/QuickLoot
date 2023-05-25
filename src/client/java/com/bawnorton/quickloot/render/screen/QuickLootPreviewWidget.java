package com.bawnorton.quickloot.render.screen;

import com.bawnorton.quickloot.render.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

import java.util.Map;

public class QuickLootPreviewWidget {
    private static final double ASPECT_RATIO = 1.5;
    private static final int ROW_HEIGHT = 20;

    private int HEIGHT;
    private int WIDTH;
    private int X_OFFSET;
    private int x;
    private int y;

    public QuickLootPreviewWidget() {
    }

    public void refreshPositionAndScale() {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        this.x = halfWidth - (WIDTH / 2);
        this.y = halfHeight - (HEIGHT / 2);
        scaleToFit(width, height, window.getScaleFactor());
    }

    public void scaleToFit(int winWidth, int winHeight, double winScale) {
        HEIGHT = (int) (600 / winScale);
        WIDTH = (int) (HEIGHT * ASPECT_RATIO);
        X_OFFSET = (int) (WIDTH * 0.7);
        if(WIDTH > (winWidth - X_OFFSET) / 2 - 10) {
            WIDTH = (winWidth - X_OFFSET) / 2 - 10;
            X_OFFSET = (int) (WIDTH * 0.7);
            HEIGHT = (int) (WIDTH / ASPECT_RATIO);
        }
        if(HEIGHT > winHeight / 2 - 10) {
            HEIGHT = winHeight / 2 - 10;
            WIDTH = (int) (HEIGHT * ASPECT_RATIO);
        }
    }

    public void render(MatrixStack matrixStack, String title, Map<Item, Integer> itemCounts) {
        refreshPositionAndScale();
        RenderHelper.drawBorderedArea(matrixStack, x + X_OFFSET, y, WIDTH, HEIGHT, 2,0xAA000000, 0xFF000000);
        RenderHelper.drawText(matrixStack, title, x + X_OFFSET + 4, y - 10, 0xFFFFFFFF);

        int row = 0;
        for(Map.Entry<Item, Integer> itemCount: itemCounts.entrySet()) {
            Item item = itemCount.getKey();
            int count = itemCount.getValue();
            String name = item.getName().getString();
            RenderHelper.drawText(matrixStack, name + " (" + count + ")", x + X_OFFSET + 20, y + row * ROW_HEIGHT + 4, 0xFFFFFFFF);
            row++;
        }
    }
}
