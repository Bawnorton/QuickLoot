package com.bawnorton.quickloot.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
    @Shadow public abstract BlockPos getPos();
    @Shadow @Nullable public abstract World getWorld();
    @Shadow public abstract BlockState getCachedState();
}
