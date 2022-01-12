package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sun.jdi.connect.Connector;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.mixin.entity.EntityMixin;
import net.fabricmc.morgan.world.entity.Bounciness;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class BouncyCommand {
    public BouncyCommand() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bouncy").requires((source) -> {
                    return source.hasPermissionLevel(2);})
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("true")
                                .executes(context -> execute((ServerCommandSource)context.getSource(), Collections.singleton(EntityArgumentType.getEntities(context, "targets")),true)))
                )
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("false")
                                .executes(context -> execute((ServerCommandSource)context.getSource(), Collections.singleton(EntityArgumentType.getEntities(context, "targets")),false)))
                )
        );

    }

    private static int execute(ServerCommandSource source, Collection<? super Entity> targets, boolean bool) throws CommandSyntaxException {
        Iterator var6 = targets.iterator();

        label44:
        while(var6.hasNext()) {
            EntityMixin entity = (EntityMixin) (Object)var6.next();
            entity.setBouncy(bool);
        }
        //source.sendFeedback(new TranslatableText("commands.bounciness.success.single", new Object[]{bounciness}),false);
        return 1;
    }
 }
