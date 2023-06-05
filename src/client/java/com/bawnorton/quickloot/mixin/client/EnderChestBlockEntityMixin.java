package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.ContainerExtender;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnderChestBlockEntity.class)
public abstract class EnderChestBlockEntityMixin extends BlockEntity implements ContainerExtender {
    protected EnderChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean canOpen() {
        World world = getWorld();
        if(world == null) return false;

        return !ChestBlock.isChestBlocked(world, getPos());
    }
}
