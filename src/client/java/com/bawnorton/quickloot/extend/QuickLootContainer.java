package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.mixin.client.ChestBlockEntityMixin;
import com.bawnorton.quickloot.util.PlayerStatus;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;

public interface QuickLootContainer {
    /**
     * Sets this container as the current quick loot container.
     * @return true if the container was not already current, false otherwise.
     */
    default boolean setAsCurrent() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if (player == null) return false;

        Optional<QuickLootContainer> quickLootContainer = player.getQuickLootContainer();
        if (quickLootContainer.isPresent()) {
            QuickLootContainer container = quickLootContainer.get();
            if (container == this) return false;
        }
        player.setQuickLootContainer(this);
        return true;
    }

    /**
     * Handles the opening of this container.
     * @param playerStatus Controls the behavior of how the container is opened.
     *               PREVIEWING: Opens the container in preview mode.
     *               LOOTING: Opens the container in looting mode.
     *               See {@link PlayerStatus} for more information.
     */
    default void open(PlayerStatus playerStatus) {
        if(!canOpen()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        ClientWorld world = client.world;
        if (world == null) return;

        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
        if (!(blockEntity instanceof QuickLootContainer container)) return;
        if (container.notCurrent()) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ((PlayerEntityExtender) player).setStatus(playerStatus);

        boolean reSneak = false;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        assert networkHandler != null;
        if(player.isSneaking()) {
            KeybindManager.unpressSneak();
            networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            reSneak = true;
        }
        interactionManager.sendSequencedPacket(world, i -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, i));
        if(reSneak) {
            KeybindManager.pressSneak();
            networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
    }

    /**
     * @return true if this container is the current quick loot container, false otherwise.
     */
    default boolean notCurrent() {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if (player == null) return true;

        Optional<QuickLootContainer> quickLootContainer = player.getQuickLootContainer();
        if (quickLootContainer.isPresent()) {
            QuickLootContainer container = quickLootContainer.get();
            return container != this;
        }
        return true;
    }

    /**
     * Handles the closing of this container.
     */
    default void close() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.closeHandledScreen();
        ((PlayerEntityExtender) player).setStatus(PlayerStatus.IDLE);
    }

    /**
     * Used to determine if the client should send an open request to the server.
     * See {@link ChestBlockEntityMixin#canOpen} for an example implementation.
     * @return true if this container can be opened, false otherwise.
     */
    default boolean canOpen() {
        return true;
    }

    /**
     * Requests the server to move the item in the specified slot to the player's inventory.
     * This is done via a click slot packet with {@link SlotActionType#QUICK_MOVE}.
     * @param slot The slot to move the item from.
     */
    default void requestStack(int slot) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        if(interactionManager == null) return;

        interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, player);
    }
}
