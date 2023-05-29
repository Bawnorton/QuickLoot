package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.Status;

import java.util.Optional;

public interface PlayerEntityExtender {
    Optional<ContainerExtender> getQuickLootContainer();
    void setQuickLootContainer(ContainerExtender quickLootContainer);
    Status getStatus();
    void setStatus(Status status);
}
