package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootContainer;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements QuickLootContainer {
}
