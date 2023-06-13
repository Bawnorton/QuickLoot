package com.bawnorton.quickloot.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Networking {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.HANDSHAKE_PACKET, (server, player, handler, buf, responseSender) -> sendHandshakePacket(player));
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.REQUEST_INVENTORY_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean isBlock = buf.readBoolean();
            if(isBlock) {
                BlockPos pos = buf.readBlockPos();
                server.execute(() -> {
                    BlockEntity blockEntity = player.world.getBlockEntity(pos);
                    if(!(blockEntity instanceof Inventory inventory)) return;
                    sendInventoryPacket(player, inventory);
                });
            } else {
                int id = buf.readInt();
                server.execute(() -> {
                    Entity entity = player.world.getEntityById(id);
                    if(!(entity instanceof Inventory inventory)) return;
                    sendInventoryPacket(player, inventory);
                });
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.REQUEST_ITEM_PACKET, ((server, player, handler, buf, responseSender) -> {
            int slot = buf.readInt();
            server.execute(() -> player.currentScreenHandler.quickMove(player, slot));
        }));
    }

    private static void sendInventoryPacket(ServerPlayerEntity player, Inventory inventory) {
        PacketByteBuf buf = PacketByteBufs.create();
        Collection<Pair<Integer, ItemStack>> stacks = new ArrayList<>();
        for(int i = 0; i < inventory.size(); i++) {
            stacks.add(new Pair<>(i, inventory.getStack(i)));
        }
        buf.writeCollection(stacks, (byteBuf, pair) -> {
            byteBuf.writeInt(pair.getLeft());
            byteBuf.writeItemStack(pair.getRight());
        });
        ServerPlayNetworking.send(player, NetworkingConstants.INVENTORY_PACKET, buf);
    }

    public static void sendEnabledPacket(ServerPlayerEntity player, boolean enabled) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(enabled);
        ServerPlayNetworking.send(player, NetworkingConstants.ENABLED_PACKET, buf);
    }

    public static void sendHandshakePacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, NetworkingConstants.HANDSHAKE_PACKET, PacketByteBufs.empty());
    }
}
