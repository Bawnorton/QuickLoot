package com.bawnorton.quickloot.event;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.extend.QuickLootEntityContainer;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.render.screen.QuickLootPreviewWidget;
import com.bawnorton.quickloot.util.ContainerStatus;
import com.bawnorton.quickloot.util.Status;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EventManager {
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
        if(!(blockEntity instanceof QuickLootContainer container)) {
            player.setQuickLootContainer(null);
            if(blockEntity instanceof CampfireBlockEntity campfire) {
                handleCampfireTarget(matricies, campfire);
            }
            return;
        }

        boolean newContainer = container.setAsCurrent();
        if(player.getStatus().isPaused()) return;

        if (newContainer) {
            if(container.canOpen()) {
                QuickLootClient.getPreviewWidget().start();
                QuickLootClient.getPreviewWidget().resetStatus();
                container.open(Status.PREVIEWING);
            } else {
                QuickLootClient.getPreviewWidget().block();
            }
            return;
        }

        BlockState state = client.world.getBlockState(blockPos);
        String containerName = state.getBlock().getName().getString();
        QuickLootClient.getPreviewWidget().render(matricies, containerName);
    }

    private static void handleEntityTarget(PlayerEntityExtender player, MatrixStack matricies, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if(!(entity instanceof QuickLootEntityContainer container)) {
            player.setQuickLootContainer(null);
            return;
        }

        assert MinecraftClient.getInstance().player != null;
        boolean newContainer = container.setAsCurrent();
        boolean sneaking = MinecraftClient.getInstance().player.isSneaking();
        QuickLootPreviewWidget previewWidget = QuickLootClient.getPreviewWidget();
        if(entity instanceof ChestBoatEntity && !sneaking) previewWidget.setStatus(ContainerStatus.REQUIRES_SNEAKING);
        if(player.getStatus().isPaused()) return;

        if (newContainer || (previewWidget.requiresSneaking() && sneaking)) {
            previewWidget.start();
            previewWidget.resetStatus();
            container.open(Status.PREVIEWING);
            return;
        }

        String containerName = entity.getType().getName().getString();
        QuickLootClient.getPreviewWidget().render(matricies, containerName);
    }

    private static void handleCampfireTarget(MatrixStack matricies, CampfireBlockEntity campfire) {
        List<ItemStack> items = campfire.getItemsBeingCooked();
        Map<ItemStack, Integer> slotMap = new HashMap<>();
        for(ItemStack item : items) {
            slotMap.put(item, slotMap.getOrDefault(item, 0) + 1);
        }
        QuickLootPreviewWidget previewWidget = QuickLootClient.getPreviewWidget();
        previewWidget.setStatus(ContainerStatus.NO_HINT);
        previewWidget.updateItems(slotMap);
        previewWidget.start();
        previewWidget.render(matricies, "Campfire");
    }
}

