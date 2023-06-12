package com.bawnorton.quickloot.command;

import com.bawnorton.quickloot.networking.Networking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public abstract class CommandHandler {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            registerEnableCommand(dispatcher);
        }));
    }

    private static void registerEnableCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("quickloot").requires((source) -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.players())
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            targets.forEach(target -> Networking.sendEnabledPacket(target, enabled));
                            Text text = Text.literal("§bQuickLoot§r for " + targets.size() + " players is now " + (enabled ? "§a[Enabled]" : "§c[Disabled]"));
                            if(targets.size() == 1) text = Text.literal("§bQuickLoot§r for " + targets.iterator().next().getName().getString() + " is now " + (enabled ? "§a[Enabled]" : "§c[Disabled]"));
                            context.getSource().sendFeedback(text, true);
                            return 0;
                        })
                    )
                )
        );
    }
}
