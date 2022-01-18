package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.mixin.entity.EntityMixin;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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
        dispatcher.register( CommandManager.literal("bouncy")
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("true")
                                .executes(context -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets"),true)))
                )
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("false")
                                .executes(context -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets"),false)))
                )
        );

    }
    private static int execute(ServerCommandSource source, Collection<? extends net.minecraft.entity.Entity> targets, boolean bool) {
        Iterator var2 = targets.iterator();

        while(var2.hasNext()) {
            net.minecraft.entity.Entity entity = (net.minecraft.entity.Entity)var2.next();

                ((EntityExtension) entity).setBouncy(bool);
        }

        String temp;

        if (bool){
            temp = "bouncy";
        }
        else {
            temp = "not bouncy";
        }

        Object feedback = temp;

        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.bouncy.success.single", new Object[]{targets.iterator().next().getDisplayName(),feedback}), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.bouncy.success.multiple",new Object[]{targets.size(),feedback}), false);
        }

        return targets.size();
    }
}
