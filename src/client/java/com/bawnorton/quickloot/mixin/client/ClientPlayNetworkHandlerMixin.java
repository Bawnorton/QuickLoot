package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;updateSlotStacks(ILjava/util/List;Lnet/minecraft/item/ItemStack;)V"))
    private void readInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        List<ItemStack> stacks = packet.getContents();
        stacks = stacks.subList(0, stacks.size() - PlayerInventory.MAIN_SIZE);
        Map<ItemStack, Integer> stackSlotMap = new HashMap<>();
        for(int i = 0; i < stacks.size(); i++) {
            stackSlotMap.put(stacks.get(i), i);
        }
        QuickLootClient.getPreviewWidget().updateItems(packet.getSyncId(), stackSlotMap);

        ClientPlayerEntity player = client.player;
        if(player == null) return;

        if(((PlayerEntityExtender) player).isQuickLooting()) {
            Optional<ContainerExtender> container = ((PlayerEntityExtender) player).getQuickLootContainer();
            container.ifPresent(ContainerExtender::closeContainer);
        }
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        if(player.isQuickLooting() && packet.getCategory().equals(SoundCategory.BLOCKS)) {
            ci.cancel();
        }
    }
}
