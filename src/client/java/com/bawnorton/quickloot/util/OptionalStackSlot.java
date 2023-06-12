package com.bawnorton.quickloot.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class OptionalStackSlot {
    private final @Nullable Pair<Optional<ItemStack>, Optional<Integer>> stackSlot;

    public OptionalStackSlot(ItemStack stack, Integer slot) {
        this.stackSlot = new Pair<>(Optional.ofNullable(stack), Optional.ofNullable(slot));
    }

    public static OptionalStackSlot empty() {
        return new OptionalStackSlot(null, null);
    }

    public static OptionalStackSlot of(ItemStack stack, Integer slot) {
        return new OptionalStackSlot(stack, slot);
    }

    public boolean isPresent() {
        return stackSlot != null && stackSlot.getLeft().isPresent() && stackSlot.getRight().isPresent();
    }

    public boolean isEmpty() {
        return !isPresent();
    }

    public void ifPresent(BiConsumer<ItemStack, Integer> consumer) {
        if(isPresent()) {
            assert stackSlot != null : "Unreachable";
            assert stackSlot.getLeft().isPresent() : "Unreachable";
            assert stackSlot.getRight().isPresent() : "Unreachable";
            consumer.accept(stackSlot.getLeft().get(), stackSlot.getRight().get());
        }
    }

    public Integer getSlot() {
        if(isPresent()) {
            assert stackSlot != null : "Unreachable";
            assert stackSlot.getRight().isPresent() : "Unreachable";
            return stackSlot.getRight().get();
        }
        return null;
    }
}
