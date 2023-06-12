package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.extend.QuickLootEntityContainer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted() && player.world.getBlockEntity(hitResult.getBlockPos()) instanceof QuickLootContainer container && container.canOpen()) {
            EventManager.resume();
        }
    }

    @Inject(method = "interactEntity", at = @At("RETURN"))
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted() && entity instanceof QuickLootEntityContainer container && container.canOpen()) {
            EventManager.resume();
        }
    }
}
