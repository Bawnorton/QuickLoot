package com.bawnorton.quickloot.util;

/**
 * Represents the status of a container.
 * Used to determine how the container should be displayed.
 * Use methods to check the status of the container instead of comparing directly.
 */
public enum ContainerStatus {
    NORMAL, // Normal
    EMPTY, // No items
    NO_HINT, // Don't display hint
    NO_HINT_EMPTY, // Don't display hint, no items
    NO_TAKE_HINT, // Don't display take hint, can still open
    BLOCKED, // Can't open, also don't display hint
    REQUIRES_SNEAKING; // Requires sneaking, also don't display hint

    /**
     * @return true if the container is in a state where it has items and can be opened.
     */
    public boolean isNormal() {
        return this == NORMAL || this == NO_HINT || this == NO_TAKE_HINT;
    }

    /**
     * @return true if the container is in a state where it can't be opened.
     */
    public boolean isBlocked() {
        return this == BLOCKED;
    }

    /**
     * @return true if the container is in a state where it can be opened but has no items.
     */
    public boolean isEmpty() {
        return this == EMPTY || this == NO_HINT_EMPTY;
    }

    /**
     * @return true if the container is in a state where it either is blocked or is forced to display no hint.
     * Note: This does not include the {@link #NO_TAKE_HINT} state.
     */
    public boolean isNoHint() {
        return this == NO_HINT || this == NO_HINT_EMPTY || this == REQUIRES_SNEAKING || this == BLOCKED;
    }


    /**
     * @return true if the container is in a state where it is forced to display no take hint.
     */
    public boolean isNoTakeHint() {
        return this == NO_TAKE_HINT || isEmpty();
    }

    public String getText() {
        return switch (this) {
            case EMPTY, NO_HINT_EMPTY -> "Empty";
            case BLOCKED -> "Blocked";
            case REQUIRES_SNEAKING -> "Requires Sneaking";
            default -> "-Error-";
        };
    }
}
