package com.bawnorton.quickloot.event;

import com.bawnorton.quickloot.QuickLootClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if(!(client.crosshairTarget instanceof BlockHitResult hitResult)) return;

            BlockPos blockPos = hitResult.getBlockPos();
            if(client.world == null) return;

            BlockEntity blockEntity = client.world.getBlockEntity(blockPos);
            if(!(blockEntity instanceof Inventory inventory)) return;

            Map<Item, Integer> items = new HashMap<>();
            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                System.out.println(i + ": " + stack);
                if(stack.isEmpty()) continue;
                items.put(stack.getItem(), items.getOrDefault(stack.getItem(), 0) + stack.getCount());
            }

            BlockState state = client.world.getBlockState(blockPos);
            String containerName = state.getBlock().getName().getString();
            QuickLootClient.getPreviewWidget().render(matrixStack, containerName, items);
        });
    }
}
