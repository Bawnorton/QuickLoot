package com.bawnorton.quickloot.extend;

import com.bawnorton.quickloot.util.PlayerStatus;

import java.util.Optional;

public interface PlayerEntityExtender {
    Optional<QuickLootContainer> getQuickLootContainer();
    void setQuickLootContainer(QuickLootContainer container);
    PlayerStatus getStatus();
    void setStatus(PlayerStatus playerStatus);
}
