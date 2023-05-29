package com.bawnorton.quickloot.extend;

import java.util.Optional;

public interface PlayerEntityExtender {
    Optional<ContainerExtender> getQuickLootContainer();
    void setQuickLootContainer(ContainerExtender quickLootContainer);
    boolean isQuickLooting();
    void setQuickLooting(boolean quickLooting);
}
