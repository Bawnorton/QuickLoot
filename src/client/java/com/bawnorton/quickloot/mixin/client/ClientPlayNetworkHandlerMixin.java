package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.render.screen.QuickLootWidget;
import com.bawnorton.quickloot.util.PlayerStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private final HashSet<String> toCancel = new HashSet<>();

    @Inject(method = "onInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;updateSlotStacks(ILjava/util/List;Lnet/minecraft/item/ItemStack;)V"))
    private void readInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if(EventManager.isPaused()) {
            EventManager.resume();
        }
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if (player == null) return;

        player.getQuickLootContainer().ifPresent(container -> {
            List<ItemStack> stacks = packet.getContents();
            stacks = stacks.subList(0, stacks.size() - PlayerInventory.MAIN_SIZE);
            Map<ItemStack, Integer> stackSlotMap = new HashMap<>();
            for (int i = 0; i < stacks.size(); i++) {
                stackSlotMap.put(stacks.get(i), i);
            }
            QuickLootWidget.getInstance().updateItems(stackSlotMap);
        });
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"))
    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        QuickLootClient.LOGGER.info("Slot update in screen handler " + packet.getItemStack());
    }

    @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"))
    private void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
        QuickLootClient.LOGGER.info("Slot update in screen handler " + packet.getValue());
    }

//    @Inject(method = "onPlaySound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"), cancellable = true)
//    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
//        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
//        if(player == null) return;
//
//        String idPath = packet.getSound().value().getId().getPath();
//        if(toCancel.contains(idPath)) {
//            toCancel.remove(idPath);
//            ci.cancel();
//        } else if(idPath.endsWith("open") && player.getStatus().doesReadContainer()) {
//            toCancel.add(idPath.replace("open", "close"));
//            ci.cancel();
//        }
//    }

    @Inject(method = "onWorldEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void onWorldEvent(WorldEventS2CPacket packet, CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        player.getQuickLootContainer().ifPresent(container -> container.open(PlayerStatus.PREVIEWING));
    }

    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void onBlockUpdate(CallbackInfo ci) {
        PlayerEntityExtender player = (PlayerEntityExtender) client.player;
        if(player == null) return;

        player.getQuickLootContainer().ifPresent(container -> {
            if(container.canOpen() && QuickLootWidget.getInstance().isBlocked()) {
                QuickLootWidget.getInstance().resetStatus();
                container.open(PlayerStatus.PREVIEWING);
            }
        });
    }
}
