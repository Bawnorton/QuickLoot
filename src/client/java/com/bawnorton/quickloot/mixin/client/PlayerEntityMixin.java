package com.bawnorton.quickloot.mixin.client;

import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.util.Status;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityExtender {
    @Unique @Nullable
    private QuickLootContainer quickLootContainer;

    @Unique
    private Status status = Status.IDLE;

    @Override
    public Optional<QuickLootContainer> getQuickLootContainer() {
        return Optional.ofNullable(quickLootContainer);
    }

    @Override
    public void setQuickLootContainer(@Nullable QuickLootContainer container) {
        this.quickLootContainer = container;
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
