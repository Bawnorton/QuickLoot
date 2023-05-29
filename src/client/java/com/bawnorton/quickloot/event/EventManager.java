package com.bawnorton.quickloot.event;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.util.Status;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class EventManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            PlayerEntityExtender player = (PlayerEntityExtender) client.player;
            if(player == null) return;

            if(!(client.crosshairTarget instanceof BlockHitResult hitResult)) return;
            if(client.world == null) return;

            BlockPos blockPos = hitResult.getBlockPos();
            BlockEntity blockEntity = client.world.getBlockEntity(blockPos);
            if(!(blockEntity instanceof ContainerExtender containerExtender)) {
                player.setQuickLootContainer(null);
                return;
            }

            if (containerExtender.setAsCurrent() && !player.getStatus().isPaused()) {
                if(client.player.isSneaking()) return;
                if(containerExtender.canOpen()) {
                    containerExtender.open(Status.PREVIEWING);
                    QuickLootClient.getPreviewWidget().start();
                } else {
                    QuickLootClient.getPreviewWidget().setBlocked(true);
                }
                return;
            }
            if(player.getStatus().isPaused()) return;

            BlockState state = client.world.getBlockState(blockPos);
            String containerName = state.getBlock().getName().getString();
            QuickLootClient.getPreviewWidget().render(matrixStack, containerName);
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> KeybindManager.handleKeybinds());
    }
}
