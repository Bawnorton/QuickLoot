package com.bawnorton.quickloot.render.screen;

import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.render.RenderHelper;
import com.bawnorton.quickloot.util.OptionalStackSlot;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class QuickLootPreviewWidget {
    private static final double ASPECT_RATIO = 1.5;
    private static final int ROW_HEIGHT = 18;

    private int HEIGHT;
    private int WIDTH;
    private int X_OFFSET;
    private int x;
    private int y;
    private int shownHeight;
    private int shownRows;

    private final Map<ItemStack, Integer> stacks;
    private final List<ItemStack> stackList = new ArrayList<>();

    private int selected;
    private int scrollOffset;
    private boolean blocked;

    public QuickLootPreviewWidget() {
        stacks = new HashMap<>();
        selected = -1;
    }

    public void updateItems(Map<ItemStack, Integer> stackSlotMap) {
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
        selected = MathHelper.clamp(selected, 0, stackList.size() - 1);
        scrollOffset = MathHelper.clamp(scrollOffset, 0, stackList.size() - shownRows);
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
        if (stackList.size() >= (HEIGHT - 4) / ROW_HEIGHT) {
            shownHeight = HEIGHT;
        } else {
            shownHeight = stackList.size() * ROW_HEIGHT + 6;
        }
        shownRows = (shownHeight - 4) / ROW_HEIGHT;
    }

    public void render(MatrixStack matricies, String title) {
        refreshPositionAndScale();
        matricies.push();
        matricies.translate(0, 0, 500);
        RenderHelper.drawOutlinedText(matricies, title, x + X_OFFSET + 4, y - 10, 0xFFFFFFFF, 0xFF000000);
        if(stackList.isEmpty() || blocked) {
            RenderHelper.drawBorderedArea(matricies, x + X_OFFSET, y, WIDTH, ROW_HEIGHT + 6, 2,0xAA000000, 0xFF000000);
            RenderHelper.drawText(matricies, blocked ? "Blocked" : "Empty", x + X_OFFSET + 25, y + 8, 0xFFFFFFFF);
            RenderHelper.drawText(matricies, "♦", x + X_OFFSET + 10, y + 8, 0xFFFFFFFF);
            renderButtonHints(matricies, y + ROW_HEIGHT + 13, false, !blocked);
            matricies.pop();
            return;
        }

        RenderHelper.drawBorderedArea(matricies, x + X_OFFSET, y, WIDTH, shownHeight, 2,0xAA000000, 0xFF000000);
        RenderHelper.startScissor(x + X_OFFSET + 2, y + 2, WIDTH - 6, shownHeight - 4);
        int row = 0;
        for(ItemStack stack : stackList) {
            int count = stack.getCount();
            String name = stack.getName().getString();
            int yOffset = (row - scrollOffset) * ROW_HEIGHT + 4;
            if(row == selected) {
                RenderHelper.drawArea(matricies, x + X_OFFSET + 4, y + yOffset, WIDTH - 8, ROW_HEIGHT - 2, 0x55FFFFFF);
            }
            RenderHelper.drawItem(matricies, stack, x + X_OFFSET + 4, y + yOffset);
            RenderHelper.drawText(matricies, name + " (" + count + ")", x + X_OFFSET + 25, y + yOffset + 4, 0xFFFFFFFF);
            row++;
        }
        RenderHelper.endScissor();
        renderButtonHints(matricies, shownHeight + y + 7, true, true);
        matricies.pop();
    }

    private void renderButtonHints(MatrixStack matricies, int y, boolean showTake, boolean showOpen) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String lootKey = KeybindManager.getLootKey().toUpperCase();
        String openKey = KeybindManager.getOpenKey().toUpperCase();
        int lootKeyWidth = textRenderer.getWidth(lootKey);
        int openKeyWidth = textRenderer.getWidth(openKey);
        int fullWidth = Math.max(lootKeyWidth + textRenderer.getWidth("Take"), openKeyWidth + textRenderer.getWidth("Open")) + 9;
        int x = this.x + X_OFFSET + (WIDTH / 2) - (fullWidth / 2);

        if(showTake) {
            RenderHelper.drawBorderedAreaAroundText(matricies, lootKey, x, y, 5, 2, 0xAA000000, 0xFF000000, 0xFFFFFFFF);
            RenderHelper.drawOutlinedText(matricies, "Take", x + lootKeyWidth + 9, y, 0xFFFFFFFF, 0xFF000000);
        }
        if(showOpen) {
            RenderHelper.drawBorderedAreaAroundText(matricies, openKey, x, y + (showTake ? 20 : 0), 5, 2, 0xAA000000, 0xFF000000, 0xFFFFFFFF);
            RenderHelper.drawOutlinedText(matricies, "Open", x + openKeyWidth + 9, y + (showTake ? 20 : 0), 0xFFFFFFFF, 0xFF000000);
        }
    }

    public OptionalStackSlot getSelectedItem() {
        if(selected == -1) return OptionalStackSlot.empty();
        if(stackList.isEmpty()) return OptionalStackSlot.empty();
        ItemStack stack = stackList.get(selected);
        int slot = stacks.get(stack);
        return OptionalStackSlot.of(stack, slot);
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void next() {
        if(selected == stackList.size() - 1) {
            start();
        } else {
            selected++;
            if(selected > (scrollOffset + shownRows - 1)) {
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
        scrollOffset = Math.max(0, stackList.size() - shownRows);
    }

    public void start() {
        selected = 0;
        scrollOffset = 0;
    }
}
