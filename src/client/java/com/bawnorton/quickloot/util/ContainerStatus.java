package com.bawnorton.quickloot.util;

/**
 * Represents the status of a container.
 * Used to determine how the quick loot ui should be displayed.
 */
public class ContainerStatus {
    private boolean isBlocked;
    private boolean isEmpty;
    private boolean requiresSneaking;
    private boolean hasOpenHint;
    private boolean hasTakeHint;

    public ContainerStatus(boolean isBlocked, boolean isEmpty, boolean requiresSneaking, boolean hasOpenHint, boolean hasTakeHint) {
        this.isBlocked = isBlocked;
        this.isEmpty = isEmpty;
        this.requiresSneaking = requiresSneaking;
        this.hasOpenHint = hasOpenHint;
        this.hasTakeHint = hasTakeHint;
    }

    public static ContainerStatus normal() {
        return new ContainerStatus(false, false, false, true, true);
    }

    public void setBlocked(boolean blocked) {
        if(blocked) disableHints();
        isBlocked = blocked;
    }

    public void setEmpty(boolean empty) {
        if(empty) hasTakeHint = false;
        isEmpty = empty;
    }

    public void setRequiresSneaking(boolean requiresSneaking) {
        if(requiresSneaking) disableHints();
        this.requiresSneaking = requiresSneaking;
    }

    public void disableHints() {
        hasOpenHint = false;
        hasTakeHint = false;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean requiresSneaking() {
        return requiresSneaking;
    }

    public boolean hasOpenHint() {
        return hasOpenHint;
    }

    public boolean hasTakeHint() {
        return hasTakeHint;
    }

    public boolean hasHint() {
        return hasOpenHint || hasTakeHint;
    }

    public boolean isNormal() {
        return !(isBlocked || isEmpty || requiresSneaking);
    }

    public String getText() {
        if(isBlocked) {
            return "Blocked";
        } else if(requiresSneaking) {
            return "Requires Sneaking";
        } else if(isEmpty) {
            return "Empty";
        } else {
            return "";
        }
    }
}
