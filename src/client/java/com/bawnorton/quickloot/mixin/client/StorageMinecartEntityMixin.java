package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootEntityContainer;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartEntityMixin implements QuickLootEntityContainer {
}
