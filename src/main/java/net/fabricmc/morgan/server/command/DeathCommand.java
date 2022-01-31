package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class DeathCommand{

    public DeathCommand() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("death")
                        .executes(context -> execute((ServerCommandSource)context.getSource(),((ServerCommandSource)context.getSource()).getEntityOrThrow())));
    }

    private static int execute(ServerCommandSource source, Entity entity) throws CommandSyntaxException {
        entity.setPosition(((PlayerEntityExtension)entity).getDeathPos());
        //source.sendFeedback(new TranslatableText("commands.bounciness.success.single", new Object[]{bounciness}),false);
        return 1;
    }
}

