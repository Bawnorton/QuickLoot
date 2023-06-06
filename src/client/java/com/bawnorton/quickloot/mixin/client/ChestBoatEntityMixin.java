package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootEntityContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestBoatEntity.class)
public abstract class ChestBoatEntityMixin implements QuickLootEntityContainer {
    @Override
    public boolean canOpen() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return false;

        return player.isSneaking();
    }
}
