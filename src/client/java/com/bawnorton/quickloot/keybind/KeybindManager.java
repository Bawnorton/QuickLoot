package com.bawnorton.quickloot.keybind;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.QuickLootContainer;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.render.screen.QuickLootWidget;
import com.bawnorton.quickloot.util.Status;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@SuppressWarnings("unused")
public abstract class KeybindManager {
    public static final KeyBindingAction LOOT = KeyBindingAction.create(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.loot", GLFW.GLFW_KEY_R, "category.quickloot")), () -> {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        Optional<QuickLootContainer> container = ((PlayerEntityExtender) player).getQuickLootContainer();
        if(QuickLootWidget.getInstance().getSelectedItem().isEmpty()) return;
        container.ifPresent(containerExtender -> containerExtender.open(Status.LOOTING));
    });
    public static final KeyBindingAction NEXT = KeyBindingAction.create(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.next", GLFW.GLFW_KEY_DOWN, "category.quickloot")), QuickLootWidget.getInstance()::next);
    public static final KeyBindingAction PREVIOUS = KeyBindingAction.create(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.previous", GLFW.GLFW_KEY_UP, "category.quickloot")), QuickLootWidget.getInstance()::previous);
    public static final KeyBindingAction START = KeyBindingAction.create(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.start", GLFW.GLFW_KEY_LEFT, "category.quickloot")), QuickLootWidget.getInstance()::start);
    public static final KeyBindingAction END = KeyBindingAction.create(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quickloot.end", GLFW.GLFW_KEY_RIGHT, "category.quickloot")), QuickLootWidget.getInstance()::end);

    public static void init() {
        QuickLootClient.LOGGER.debug("Initializing Keybinds");
    }

    public static void handleKeybinds() {
        LOOT.handle();
        NEXT.handle();
        PREVIOUS.handle();
        START.handle();
        END.handle();
    }

    public static String getLootKeyString() {
        return LOOT.keybind.getBoundKeyLocalizedText().getString();
    }

    public static String getOpenKeyString() {
        return getOpenKeybind().getBoundKeyLocalizedText().getString();
    }

    private static KeyBinding getOpenKeybind() {
        return MinecraftClient.getInstance().options.useKey;
    }

    private record KeyBindingAction(KeyBinding keybind, KeybindAction action) {
        public static KeyBindingAction create(KeyBinding keybind, KeybindAction action) {
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