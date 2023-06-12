package com.bawnorton.quickloot;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.networking.client.Networking;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickLootClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("quickloot");
    public static boolean enabled = true;
    public static boolean installedOnServer = false;

    @Override
    public void onInitializeClient() {
        EventManager.init();
        KeybindManager.init();
        Networking.init();
    }
}