package com.bawnorton.quickloot;

import com.bawnorton.quickloot.event.EventManager;
import com.bawnorton.quickloot.render.screen.QuickLootPreviewWidget;
import net.fabricmc.api.ClientModInitializer;

public class QuickLootClient implements ClientModInitializer {
    public static QuickLootPreviewWidget previewWidget;

    @Override
    public void onInitializeClient() {
        previewWidget = new QuickLootPreviewWidget();
        EventManager.init();
    }

    public static QuickLootPreviewWidget getPreviewWidget() {
        return previewWidget;
    }
}