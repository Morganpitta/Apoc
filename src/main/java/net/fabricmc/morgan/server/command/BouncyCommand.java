package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.mixin.entity.EntityMixin;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
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
        dispatcher.register((LiteralArgumentBuilder) CommandManager.literal("bouncy").requires((source) -> {
                    return source.hasPermissionLevel(2);})
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("true")
                                .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"),true)))
                )
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("false")
                                .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"),false)))
                )
        );

    }

    private static int execute(ServerCommandSource source, Collection<? extends net.minecraft.entity.Entity> targets, boolean bool) throws CommandSyntaxException {
        Iterator var6 = targets.iterator();
        ExampleMod.LOGGER.info("trying to execute");

        while (var6.hasNext()) {
            ExampleMod.LOGGER.info("trying to cast");
            Entity entity = (Entity) var6.next();
            ExampleMod.LOGGER.info("trying to set bool");
            ((EntityMixin) (Object) entity).setBouncy(bool);
        }
        source.sendFeedback(new TranslatableText("commands.bouncy.success", new Object[]{targets},new boolean[]{bool}),false);
        return 1;
    }
 }
