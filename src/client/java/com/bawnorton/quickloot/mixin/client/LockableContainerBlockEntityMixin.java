package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.networking.client.Networking;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin extends BlockEntityMixin implements QuickLootContainer {
    @Override
    public void serverOpen() {
        Networking.requestInventory(getPos());
    }
}
