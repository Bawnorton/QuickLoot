package com.bawnorton.quickloot.util;

public enum Status {
    LOOTING,
    PREVIEWING,
    IDLE,
    PAUSED;

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
