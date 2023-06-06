package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.Status;

import java.util.Optional;

public interface PlayerEntityExtender {
    Optional<QuickLootContainer> getQuickLootContainer();
    void setQuickLootContainer(QuickLootContainer container);
    Status getStatus();
    void setStatus(Status status);
}
