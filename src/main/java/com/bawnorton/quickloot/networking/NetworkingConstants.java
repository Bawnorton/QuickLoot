package com.bawnorton.quickloot.networking;

import com.bawnorton.quickloot.QuickLoot;
import net.minecraft.util.Identifier;

public abstract class NetworkingConstants {
    public static final Identifier HANDSHAKE_PACKET = QuickLoot.id("handshake");
    public static final Identifier ENABLED_PACKET = QuickLoot.id("enabled");
}
