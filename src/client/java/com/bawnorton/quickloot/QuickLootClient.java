package com.bawnorton.quickloot;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickLootClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("quickloot");
    private static boolean isEnabled = true;

    @Override
    public void onInitializeClient() {
        EventManager.init();
        KeybindManager.init();
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }
}