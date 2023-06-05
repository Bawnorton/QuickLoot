package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.Status;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;

public interface ContainerExtender {
    default boolean setAsCurrent() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if (player == null) return false;

        Optional<ContainerExtender> quickLootContainer = player.getQuickLootContainer();
        if (quickLootContainer.isPresent()) {
            ContainerExtender container = quickLootContainer.get();
            if (container == this) return false;
        }
        player.setQuickLootContainer(this);
        return true;
    }

    default void open(Status status) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
        if (!(blockEntity instanceof ContainerExtender containerExtender)) return;
        if (containerExtender.notCurrent()) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ((PlayerEntityExtender) player).setStatus(status);
        interactionManager.sendSequencedPacket(world, i -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, i));
    }

    default boolean notCurrent() {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if (player == null) return true;

        Optional<ContainerExtender> quickLootContainer = player.getQuickLootContainer();
        if (quickLootContainer.isPresent()) {
            ContainerExtender container = quickLootContainer.get();
            return container != this;
        }
        return true;
    }

    default void close() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.closeHandledScreen();
        ((PlayerEntityExtender) player).setStatus(Status.IDLE);
    }

    default boolean canOpen() {
        return true;
    }

    default void requestStack(int slot) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        if(interactionManager == null) return;

        interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, player);
    }
}
