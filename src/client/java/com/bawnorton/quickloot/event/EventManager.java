package com.bawnorton.quickloot.event;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.EntityContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.util.Status;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class EventManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        HudRenderCallback.EVENT.register((matricies, tickDelta) -> {
            PlayerEntityExtender player = (PlayerEntityExtender) client.player;
            if(player == null) return;
            if(client.world == null) return;

            HitResult hitResult = client.crosshairTarget;
            if(hitResult instanceof BlockHitResult blockHitResult) {
                handleBlockTarget(player, matricies, blockHitResult);
            } else if (hitResult instanceof EntityHitResult entityHitResult) {
                handleEntityTarget(player, matricies, entityHitResult);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> KeybindManager.handleKeybinds());
    }

    private static void handleBlockTarget(PlayerEntityExtender player, MatrixStack matricies, BlockHitResult hitResult) {
        assert client.world != null;
        BlockPos blockPos = hitResult.getBlockPos();
        BlockEntity blockEntity = client.world.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ContainerExtender containerExtender)) {
            player.setQuickLootContainer(null);
            return;
        }

        boolean newContainer = containerExtender.setAsCurrent();
        if(player.getStatus().isPaused()) return;

        if (newContainer) {
            if(containerExtender.canOpen()) {
                containerExtender.open(Status.PREVIEWING);
                QuickLootClient.getPreviewWidget().start();
            } else {
                QuickLootClient.getPreviewWidget().setBlocked(true);
            }
            return;
        }

        BlockState state = client.world.getBlockState(blockPos);
        String containerName = state.getBlock().getName().getString();
        QuickLootClient.getPreviewWidget().render(matricies, containerName);
    }

    private static void handleEntityTarget(PlayerEntityExtender player, MatrixStack matricies, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if(!(entity instanceof EntityContainerExtender containerExtender)) {
            player.setQuickLootContainer(null);
            return;
        }

        boolean newContainer = containerExtender.setAsCurrent();
        if(player.getStatus().isPaused()) return;

        if (newContainer) {
            containerExtender.open(Status.PREVIEWING);
            QuickLootClient.getPreviewWidget().start();
            return;
        }

        String containerName = entity.getType().getName().getString();
        QuickLootClient.getPreviewWidget().render(matricies, containerName);
    }
}

