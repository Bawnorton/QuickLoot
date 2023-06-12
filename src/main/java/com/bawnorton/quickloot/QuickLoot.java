package com.bawnorton.quickloot;

import com.bawnorton.quickloot.command.CommandHandler;
import com.bawnorton.quickloot.networking.Networking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickLoot implements ModInitializer {
    public static final String MOD_ID = "quickloot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing QuickLoot");
        Networking.init();
        CommandHandler.init();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}