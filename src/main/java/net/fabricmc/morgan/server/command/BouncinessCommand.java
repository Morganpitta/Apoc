package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.mixin.entity.EntityMixin;
import net.fabricmc.morgan.world.entity.Bounciness;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.naming.Context;
import java.util.Collection;
import java.util.Iterator;

public class BouncinessCommand{

    public BouncinessCommand() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bounciness").requires((source) -> {
            return source.hasPermissionLevel(2);})
                .then(CommandManager.argument("bounciness", DoubleArgumentType.doubleArg(0.0D,1000.0D))
                        .executes(context -> execute((ServerCommandSource)context.getSource(),DoubleArgumentType.getDouble(context, "bounciness")))));
    }

    private static int execute(ServerCommandSource source,double bounciness) throws CommandSyntaxException {
        Bounciness.Bounciness = bounciness;
        source.sendFeedback(new TranslatableText("commands.bounciness.success.single", new Object[]{bounciness}),false);
        return 1;
    }
}
