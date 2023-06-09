package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootContainer;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LockableContainerBlockEntityMixin implements QuickLootContainer {
    @Override
    public boolean canOpen() {
        World world = getWorld();
        if(world == null) return false;

        return !ChestBlock.isChestBlocked(world, getPos());
    }
}
