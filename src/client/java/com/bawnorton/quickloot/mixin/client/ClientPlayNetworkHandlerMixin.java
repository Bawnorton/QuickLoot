package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.util.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
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
        ClientPlayerEntity player = client.player;
        if(player == null) return;

        PlayerEntityExtender playerExtender = (PlayerEntityExtender) player;
        Optional<ContainerExtender> optional = playerExtender.getQuickLootContainer();
        ContainerExtender container = optional.orElse(null);
        if(container == null) return;

        switch (playerExtender.getStatus()) {
            case PREVIEWING -> {
                List<ItemStack> stacks = packet.getContents();
                stacks = stacks.subList(0, stacks.size() - PlayerInventory.MAIN_SIZE);
                Map<ItemStack, Integer> stackSlotMap = new HashMap<>();
                for(int i = 0; i < stacks.size(); i++) {
                    stackSlotMap.put(stacks.get(i), i);
                }
                QuickLootClient.getPreviewWidget().updateItems(stackSlotMap);
                container.close();
            }
            case LOOTING -> {
                Pair<ItemStack, Integer> target = QuickLootClient.getPreviewWidget().getSelectedItem();
                if(target == null) return;

                ItemStack stack = target.getLeft();
                int slot = target.getRight();
                if(stack.isEmpty()) return;

                container.requestStack(stack, slot);
                container.close();
                container.open(Status.PREVIEWING);
            }
            case IDLE -> container.close();
        }
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        if((player.getStatus().isLooting() || player.getStatus().isPreviewing()) && packet.getCategory().equals(SoundCategory.BLOCKS)) {
            ci.cancel();
        }
    }

    @Inject(method = "onWorldEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void onWorldEvent(WorldEventS2CPacket packet, CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        player.getQuickLootContainer().ifPresent(container -> container.open(Status.PREVIEWING));
    }

    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void onBlockUpdate(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        player.getQuickLootContainer().ifPresent(container -> {
            if(container.canOpen() && QuickLootClient.getPreviewWidget().isBlocked()) {
                QuickLootClient.getPreviewWidget().setBlocked(false);
                container.open(Status.PREVIEWING);
            }
        });
    }
}
