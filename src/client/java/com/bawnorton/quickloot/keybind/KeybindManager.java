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

@SuppressWarnings("unused")
public class KeybindManager {
    public static final KeyBindingAction LOOT = KeyBindingAction.add(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.loot", GLFW.GLFW_KEY_R, "category.quickloot")), () -> {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        Optional<ContainerExtender> container = ((PlayerEntityExtender) player).getQuickLootContainer();
        if(QuickLootClient.getPreviewWidget().getSelectedItem().isEmpty()) return;
        container.ifPresent(containerExtender -> containerExtender.open(Status.LOOTING));
    });
    public static final KeyBindingAction NEXT = KeyBindingAction.add(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.next", GLFW.GLFW_KEY_DOWN, "category.quickloot")), QuickLootClient.getPreviewWidget()::next);
    public static final KeyBindingAction PREVIOUS = KeyBindingAction.add(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.previous", GLFW.GLFW_KEY_UP, "category.quickloot")), QuickLootClient.getPreviewWidget()::previous);
    public static final KeyBindingAction START = KeyBindingAction.add(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.start", GLFW.GLFW_KEY_LEFT, "category.quickloot")), QuickLootClient.getPreviewWidget()::start);
    public static final KeyBindingAction END = KeyBindingAction.add(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.end", GLFW.GLFW_KEY_RIGHT, "category.quickloot")), QuickLootClient.getPreviewWidget()::end);

    private static boolean openContainerSync = false;

    public static void init() {
        QuickLootClient.LOGGER.debug("Initializing Keybinds");
    }

    public static void handleKeybinds() {
        LOOT.handle();
        NEXT.handle();
        PREVIOUS.handle();
        START.handle();
        END.handle();

        openContainerSync = getOpenKeybind().isPressed();
    }

    public static String getLootKeyString() {
        return LOOT.keybind.getBoundKeyLocalizedText().getString();
    }

    public static String getOpenKeyString() {
        return getOpenKeybind().getBoundKeyLocalizedText().getString();
    }

    public static boolean checkContainerSync() {
        return openContainerSync;
    }

    private static KeyBinding getOpenKeybind() {
        return MinecraftClient.getInstance().options.useKey;
    }

    private record KeyBindingAction(KeyBinding keybind, KeybindAction action) {
        public static KeyBindingAction add(KeyBinding keybind, KeybindAction action) {
            return new KeyBindingAction(keybind, action);
        }

        private void handle() {
            if (keybind.wasPressed()) action.run();
        }
    }

    @FunctionalInterface
    private interface KeybindAction {
        void run();
    }
}