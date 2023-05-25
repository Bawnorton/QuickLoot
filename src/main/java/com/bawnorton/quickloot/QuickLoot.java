package com.bawnorton.quickloot;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickLoot implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("quickloot");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing QuickLoot");
    }
}