package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.ContainerExtender;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBlockEntityMixin extends LootableContainerBlockEntity implements ContainerExtender {
    protected ShulkerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public boolean canOpen() {
        BlockState state = getCachedState();
        World world = getWorld();
        if(world == null) return false;

        return ShulkerBoxBlock.canOpen(state, world, getPos(), (ShulkerBoxBlockEntity) (Object) this);
    }
}
