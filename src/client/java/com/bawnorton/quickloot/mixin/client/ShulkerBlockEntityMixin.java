package com.bawnorton.quickloot.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBlockEntityMixin extends LockableContainerBlockEntityMixin {

    @Override
    public boolean canOpen() {
        BlockState state = getCachedState();
        World world = getWorld();
        if(world == null) return false;

        return ShulkerBoxBlock.canOpen(state, world, getPos(), (ShulkerBoxBlockEntity) (Object) this);
    }
}
