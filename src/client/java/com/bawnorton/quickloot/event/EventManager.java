package com.bawnorton.quickloot.event;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.extend.QuickLootEntityContainer;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.networking.client.Networking;
import com.bawnorton.quickloot.render.screen.QuickLootWidget;
import com.bawnorton.quickloot.util.PlayerStatus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
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
    private static boolean quicklootPaused = false;

    public static void init() {
        HudRenderCallback.EVENT.register((matricies, tickDelta) -> {
            if(isPaused() || !QuickLootClient.enabled) return;
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

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            QuickLootClient.installedOnServer = false;
            Networking.sendHandshakePacket();
        });
    }

    private static void handleBlockTarget(PlayerEntityExtender player, MatrixStack matricies, BlockHitResult hitResult) {
        assert client.world != null;
        BlockPos blockPos = hitResult.getBlockPos();
        BlockEntity blockEntity = client.world.getBlockEntity(blockPos);
        if(!(blockEntity instanceof QuickLootContainer container)) {
            player.getQuickLootContainer().ifPresent(QuickLootContainer::close);
            player.setQuickLootContainer(null);
            if(blockEntity instanceof CampfireBlockEntity campfire) {
                handleCampfireTarget(matricies, campfire);
            }
            return;
        }

        if (container.setAsCurrent()) {
            if(container.canOpen()) {
                QuickLootWidget.getInstance().start();
                QuickLootWidget.getInstance().resetStatus();
                container.open(PlayerStatus.PREVIEWING);
            } else {
                QuickLootWidget.getInstance().block();
            }
            return;
        }

        BlockState state = client.world.getBlockState(blockPos);
        String containerName = state.getBlock().getName().getString();
        QuickLootWidget.getInstance().render(matricies, containerName);
    }

    private static void handleEntityTarget(PlayerEntityExtender player, MatrixStack matricies, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if(!(entity instanceof QuickLootEntityContainer container)) {
            player.getQuickLootContainer().ifPresent(QuickLootContainer::close);
            player.setQuickLootContainer(null);
            return;
        }

        assert MinecraftClient.getInstance().player != null;
        boolean newContainer = container.setAsCurrent();
        boolean sneaking = MinecraftClient.getInstance().player.isSneaking();
        QuickLootWidget previewWidget = QuickLootWidget.getInstance();
        if(entity instanceof ChestBoatEntity && !sneaking) previewWidget.getStatus().setRequiresSneaking(true);
        if (newContainer || (previewWidget.requiresSneaking() && sneaking)) {
            previewWidget.start();
            previewWidget.resetStatus();
            container.open(PlayerStatus.PREVIEWING);
            return;
        }

        String containerName = entity.getType().getName().getString();
        QuickLootWidget.getInstance().render(matricies, containerName);
    }
    private static void handleCampfireTarget(MatrixStack matricies, CampfireBlockEntity campfire) {
        List<ItemStack> items = campfire.getItemsBeingCooked();
        Map<ItemStack, Integer> slotMap = new HashMap<>();
        for(ItemStack item : items) {
            slotMap.put(item, slotMap.getOrDefault(item, 0) + 1);
        }
        QuickLootWidget previewWidget = QuickLootWidget.getInstance();
        previewWidget.resetStatus();
        previewWidget.getStatus().disableHints();
        previewWidget.updateItems(slotMap);
        previewWidget.start();
        previewWidget.render(matricies, "Campfire");
    }


    public static boolean isPaused() {
        return quicklootPaused;
    }

    public static void resume() {
        quicklootPaused = false;
    }

    public static void pause() {
        quicklootPaused = true;
    }
}

