package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindningMixin {

    @Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
    private static void onUnpressAll(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        if(((PlayerEntityExtender) player).isQuickLooting()) {
            ci.cancel();
        }
    }
}
