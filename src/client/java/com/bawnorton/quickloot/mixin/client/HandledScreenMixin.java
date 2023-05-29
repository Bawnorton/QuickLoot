package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.util.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return;

        if(player.getStatus().isIdle()) {
            player.setStatus(Status.PAUSED);
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return;

        if(player.getStatus().isPaused()) {
            player.getQuickLootContainer().ifPresent(container -> container.open(Status.PREVIEWING));
        }
    }
}
