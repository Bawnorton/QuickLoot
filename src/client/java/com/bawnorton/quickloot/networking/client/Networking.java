package com.bawnorton.quickloot.networking.client;

import com.bawnorton.quickloot.QuickLootClient;
import com.bawnorton.quickloot.extend.PlayerEntityExtender;
import com.bawnorton.quickloot.networking.NetworkingConstants;
import com.bawnorton.quickloot.render.screen.QuickLootWidget;
import com.bawnorton.quickloot.util.PlayerStatus;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.INVENTORY_PACKET, (client, handler, buf, responseSender) -> {
            Collection<Pair<Integer, ItemStack>> stacks = buf.readCollection(ArrayList::new, (byteBuf) -> new Pair<>(byteBuf.readInt(), byteBuf.readItemStack()));
            client.execute(() -> {
                Map<ItemStack, Integer> map = new HashMap<>();
                for(Pair<Integer, ItemStack> pair : stacks) {
                    map.put(pair.getRight(), pair.getLeft());
                }
                QuickLootWidget.getInstance().updateItems(map);
            });
        });
    }

    public static void sendHandshakePacket() {
        ClientPlayNetworking.send(NetworkingConstants.HANDSHAKE_PACKET, PacketByteBufs.empty());
    }

    public static void requestInventory(BlockPos pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(NetworkingConstants.REQUEST_INVENTORY_PACKET, buf);
    }

    public static void requestEntityInventory(Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(NetworkingConstants.REQUEST_INVENTORY_PACKET, buf);
    }

    public static void requestItem(int slot) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(slot);
        ClientPlayNetworking.send(NetworkingConstants.REQUEST_ITEM_PACKET, buf);
    }
}
