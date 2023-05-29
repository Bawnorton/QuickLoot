package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.ContainerExtender;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements ContainerExtender {
    @Override
    public void requestStack(ItemStack stack, int slot) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        if(interactionManager == null) return;

        interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, player);
    }
}
