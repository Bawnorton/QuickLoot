package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityExtender {
    @Unique
    private ContainerExtender quickLootContainer;

    @Unique
    private boolean quickLooting;

    @Override
    public Optional<ContainerExtender> getQuickLootContainer() {
        return Optional.ofNullable(quickLootContainer);
    }

    @Override
    public void setQuickLootContainer(ContainerExtender quickLootContainer) {
        this.quickLootContainer = quickLootContainer;
    }

    @Override
    public boolean isQuickLooting() {
        return quickLooting;
    }

    @Override
    public void setQuickLooting(boolean quickLooting) {
        this.quickLooting = quickLooting;
    }
}
