package net.fabricmc.morgan.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class DeathCommand{
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teleport.invalidPosition"));


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
        Vec3d pos =((PlayerEntityExtension)entity).getDeathPos();
        BlockPos blockPos = new BlockPos(pos);
        if (pos.getY()==-255){
            source.sendFeedback(new TranslatableText("commands.death.failure", new Object[]{entity.getDisplayName()}), false);
        }
        else
        {
            if (!World.isValid(blockPos)) {
                throw INVALID_POSITION_EXCEPTION.create();
            } else if (entity instanceof ServerPlayerEntity) {
                entity.stopRiding();
                if (((ServerPlayerEntity) entity).isSleeping()) {
                    ((ServerPlayerEntity) entity).wakeUp(true, true);
                }

                ((ServerPlayerEntity) entity).networkHandler.requestTeleport(pos.x, pos.y, pos.z, entity.getYaw(), entity.getPitch(), EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
                source.sendFeedback(new TranslatableText("commands.death.success", new Object[]{entity.getDisplayName()}), false);
            }
        }
        return 1;
    }


}

