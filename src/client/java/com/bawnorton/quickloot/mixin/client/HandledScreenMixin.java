package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.render.screen.QuickLootWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends ScreenMixin {
    @Override
    protected void onScreenInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if(openedViaQuickloot()) passEvents = false;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(openedViaQuickloot()) ci.cancel();
    }

    @Override
    protected void onRenderBackground(MatrixStack matrices, CallbackInfo ci) {
        if(openedViaQuickloot()) ci.cancel();
    }

    @Override
    protected void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(!openedViaQuickloot()) return;

        boolean handled = false;
        for(KeyBinding key: MinecraftClient.getInstance().options.allKeys) {
            if(key.matchesKey(keyCode, scanCode)) {
                key.setPressed(true);
                handled = true;
                break;
            }
        }
        cir.setReturnValue(handled);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(openedViaQuickloot()) {
            boolean handled = false;
            for(KeyBinding key: MinecraftClient.getInstance().options.allKeys) {
                if(key.matchesKey(keyCode, scanCode)) {
                    key.setPressed(false);
                    handled = true;
                    break;
                }
            }
            return handled;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        QuickLootWidget.getInstance().resetStatus();
    }

    private boolean openedViaQuickloot() {
        PlayerEntityExtender player = (PlayerEntityExtender) MinecraftClient.getInstance().player;
        if(player == null) return false;

        return !EventManager.isPaused() && player.getStatus().doesReadContainer();
    }
}
