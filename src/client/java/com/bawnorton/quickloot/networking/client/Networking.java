package com.bawnorton.quickloot.networking.client;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.networking.NetworkingConstants;
import com.bawnorton.quickloot.util.PlayerStatus;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public abstract class Networking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ENABLED_PACKET, (client, handler, buf, responseSender) -> {
            boolean enabled = buf.readBoolean();
            client.execute(() -> {
                QuickLootClient.enabled = enabled;
                if(!enabled && client.player instanceof PlayerEntityExtender playerExtender) {
                    playerExtender.setQuickLootContainer(null);
                    playerExtender.setStatus(PlayerStatus.IDLE);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.HANDSHAKE_PACKET, (client, handler, buf, responseSender) -> client.execute(() -> QuickLootClient.installedOnServer = true));
    }

    public static void sendHandshakePacket() {
        ClientPlayNetworking.send(NetworkingConstants.HANDSHAKE_PACKET, PacketByteBufs.empty());
    }
}
