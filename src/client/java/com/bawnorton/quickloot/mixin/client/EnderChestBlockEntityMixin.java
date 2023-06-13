package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.networking.client.Networking;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnderChestBlockEntity.class)
public abstract class EnderChestBlockEntityMixin extends BlockEntityMixin implements QuickLootContainer {
    @Override
    public boolean canOpen() {
        World world = getWorld();
        if(world == null) return false;

        return !ChestBlock.isChestBlocked(world, getPos());
    }

    @Override
    public void serverOpen() {
        Networking.requestInventory(getPos());
    }
}
