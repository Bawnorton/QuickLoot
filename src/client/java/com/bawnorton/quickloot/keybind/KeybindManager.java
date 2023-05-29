package com.bawnorton.quickloot.keybind;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.ContainerExtender;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.util.Status;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class KeybindManager {
    public static final KeyBinding LOOT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickloot.loot",
            GLFW.GLFW_KEY_C,
            "category.quickloot"
    ));
    public static final KeyBinding NEXT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickloot.next",
            GLFW.GLFW_KEY_DOWN,
            "category.quickloot"
    ));
    public static final KeyBinding PREVIOUS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickloot.previous",
            GLFW.GLFW_KEY_UP,
            "category.quickloot"
    ));
    public static final KeyBinding END = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickloot.end",
            GLFW.GLFW_KEY_RIGHT,
            "category.quickloot"
    ));
    public static final KeyBinding START = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quickloot.start",
            GLFW.GLFW_KEY_LEFT,
            "category.quickloot"
    ));

    public static void init() {
        QuickLootClient.LOGGER.debug("Initializing Keybinds");
    }

    public static void handleKeybinds() {
        if (LOOT.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            Optional<ContainerExtender> container = ((PlayerEntityExtender) player).getQuickLootContainer();
            if(QuickLootClient.getPreviewWidget().getSelectedItem() == null) return;
            container.ifPresent(containerExtender -> containerExtender.open(Status.LOOTING));
        }
        if (NEXT.wasPressed()) {
            QuickLootClient.getPreviewWidget().next();
        }
        if (PREVIOUS.wasPressed()) {
            QuickLootClient.getPreviewWidget().previous();
        }
        if (END.wasPressed()) {
            QuickLootClient.getPreviewWidget().end();
        }
        if (START.wasPressed()) {
            QuickLootClient.getPreviewWidget().start();
        }
    }
}