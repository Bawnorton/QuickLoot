package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.util.Status;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityExtender {
    @Unique @Nullable
    private ContainerExtender quickLootContainer;

    @Unique
    private Status status = Status.PREVIEWING;

    @Override
    public Optional<ContainerExtender> getQuickLootContainer() {
        return Optional.ofNullable(quickLootContainer);
    }

    @Override
    public void setQuickLootContainer(@Nullable ContainerExtender quickLootContainer) {
        this.quickLootContainer = quickLootContainer;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
