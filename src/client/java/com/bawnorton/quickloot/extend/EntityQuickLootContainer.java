package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public interface EntityQuickLootContainer extends QuickLootContainer {
    @Override
    default void open(Status status) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return;

        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof EntityQuickLootContainer container)) return;
        if (container.notCurrent()) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ((PlayerEntityExtender) player).setStatus(status);
        interactionManager.sendSequencedPacket(world, i -> PlayerInteractEntityC2SPacket.interact(entity, player.isSneaking(), Hand.MAIN_HAND));
    }
}
