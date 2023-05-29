package com.bawnorton.quickloot.render.screen;

import com.bawnorton.quickloot.render.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.*;

public class QuickLootPreviewWidget {
    private static final double ASPECT_RATIO = 1.5;
    private static final int ROW_HEIGHT = 18;

    private int HEIGHT;
    private int WIDTH;
    private int X_OFFSET;
    private int x;
    private int y;

    private final Map<ItemStack, Integer> stacks;
    private final List<ItemStack> stackList = new ArrayList<>();
    private int syncId;

    private int selected;
    private int scrollOffset;

    public QuickLootPreviewWidget() {
        stacks = new HashMap<>();
        selected = -1;
    }

    public void updateItems(int syncId, Map<ItemStack, Integer> stackSlotMap) {
        if(this.syncId != syncId) {
            this.syncId = syncId;
            start();
        }
        stacks.clear();
        stackList.clear();
        for(Map.Entry<ItemStack, Integer> entry : stackSlotMap.entrySet()) {
            ItemStack stack = entry.getKey();
            if(stack.isEmpty()) continue;
            int slot = entry.getValue();
            stacks.put(stack, slot);
            stackList.add(stack);
        }
        stackList.sort(Comparator.comparing(stack -> stack.getItem().getName().getString()));
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

    public void render(MatrixStack matrixStack, String title) {
        refreshPositionAndScale();
        RenderHelper.drawBorderedArea(matrixStack, x + X_OFFSET, y, WIDTH, HEIGHT, 2,0xAA000000, 0xFF000000);
        RenderHelper.drawOutlinedText(matrixStack, title, x + X_OFFSET + 4, y - 10, 0xFFFFFFFF, 0xFF000000);

        RenderHelper.startScissor(x + X_OFFSET + 2, y + 2, WIDTH - 4, HEIGHT - 4);
        int row = 0;
        for(ItemStack stack : stackList) {
            int count = stack.getCount();
            String name = stack.getName().getString();
            int yOffset = (row - scrollOffset) * ROW_HEIGHT + 4;
            if(row == selected) {
                RenderHelper.drawArea(matrixStack, x + X_OFFSET + 4, y + yOffset, WIDTH - 8, ROW_HEIGHT - 2, 0x55FFFFFF);
            }
            RenderHelper.drawItem(matrixStack, stack, x + X_OFFSET + 4, y + yOffset);
            RenderHelper.drawText(matrixStack, name + " (" + count + ")", x + X_OFFSET + 25, y + yOffset + 4, 0xFFFFFFFF);
            row++;
        }
        RenderHelper.endScissor();
    }

    public Pair<ItemStack, Integer> getSelectedItem() {
        if(selected == -1) return null;
        if(stackList.isEmpty()) return null;
        ItemStack stack = stackList.get(selected);
        int slot = stacks.get(stack);
        return new Pair<>(stack, slot);
    }

    public void next() {
        if(selected == stackList.size() - 1) {
            start();
        } else {
            selected++;
            int shownCount = (HEIGHT - 4) / ROW_HEIGHT - 1;
            if(selected > (scrollOffset + shownCount)) {
                scrollOffset++;
            }
        }
    }

    public void previous() {
        if(selected == 0) {
            end();
        } else {
            selected--;
            if(selected < scrollOffset) {
                scrollOffset--;
            }
        }
    }

    public void end() {
        selected = stackList.size() - 1;
        int itemsShown = (HEIGHT - 4) / ROW_HEIGHT;
        scrollOffset = Math.max(0, stackList.size() - itemsShown);
    }

    public void start() {
        selected = 0;
        scrollOffset = 0;
    }
}
