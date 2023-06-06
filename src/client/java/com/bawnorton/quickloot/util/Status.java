package com.bawnorton.quickloot.util;

public enum Status {
    // Player Statuses (Used to determine how the player interacts with the container)
    LOOTING, // Loot the container's selected slot
    PREVIEWING, // Read the container's contents
    IDLE, // The player is not interacting with a container
    PAUSED; // The player is inside a container

    public boolean isIdle() {
        return this == IDLE;
    }

    public boolean isPaused() {
        return this == PAUSED;
    }

    public boolean isPreviewing() {
        return this == PREVIEWING;
    }

    public boolean isLooting() {
        return this == LOOTING;
    }

    public boolean doesReadContainer() {
        return isPreviewing() || isLooting();
    }
}
