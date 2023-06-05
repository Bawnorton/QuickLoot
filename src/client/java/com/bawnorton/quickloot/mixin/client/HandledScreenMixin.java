package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.util.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandledScreen.class, priority = 2000)
public abstract class HandledScreenMixin {
    @Shadow public abstract void close();

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return;

        if(player.getStatus().isIdle()) {
            player.setStatus(Status.PAUSED);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return;

        if(player.getStatus().doesReadContainer()) {
            close();
            ci.cancel();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return;

        player.setStatus(Status.IDLE);
        player.getQuickLootContainer().ifPresent(container -> container.open(Status.PREVIEWING));
    }
}
