package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.PlayerStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public interface QuickLootEntityContainer extends QuickLootContainer {
    /**
     * Handles the opening of this container.
     * Overridden to open the container as an entity.
     * @param playerStatus Controls the behavior of how the container is opened.
     *               PREVIEWING: Opens the container in preview mode.
     *               LOOTING: Opens the container in looting mode.
     *               See {@link PlayerStatus} for more information.
     */
    @Override
    default void open(PlayerStatus playerStatus) {
        if(!canOpen()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return;

        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof QuickLootEntityContainer container)) return;
        if (container.notCurrent()) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ((PlayerEntityExtender) player).setStatus(playerStatus);
        interactionManager.sendSequencedPacket(world, i -> PlayerInteractEntityC2SPacket.interact(entity, player.isSneaking(), Hand.MAIN_HAND));
    }
}
