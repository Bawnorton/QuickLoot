package com.bawnorton.quickloot.extend;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;

public interface ContainerExtender {
    default boolean setAsCurrentContainer() {
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

    default void openContainer() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if (player == null) return;

        player.setQuickLooting(true);
        interactionManager.sendSequencedPacket(world, i -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, i));
    }

    default void closeContainer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.closeHandledScreen();
        ((PlayerEntityExtender) player).setQuickLooting(false);
    }

    void requestStack(ItemStack stack, int slot);
}
