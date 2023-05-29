package com.bawnorton.quickloot;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.keybind.KeybindManager;
import com.bawnorton.quickloot.render.screen.QuickLootPreviewWidget;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickLootClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("quickloot");
    public static QuickLootPreviewWidget previewWidget;

    @Override
    public void onInitializeClient() {
        previewWidget = new QuickLootPreviewWidget();
        EventManager.init();
        KeybindManager.init();
    }

    public static QuickLootPreviewWidget getPreviewWidget() {
        return previewWidget;
    }
}