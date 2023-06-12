package com.bawnorton.quickloot.mixin.client;

import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParentElement.class)
public interface ParentElementMixin {
//    @SuppressWarnings("CancellableInjectionUsage")
//    @Inject(method = "keyReleased", at = @At("HEAD"), cancellable = true)
//    default void onKeyReleased(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
//    }
}
