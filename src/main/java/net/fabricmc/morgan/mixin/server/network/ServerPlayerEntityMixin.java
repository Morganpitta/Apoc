package net.fabricmc.morgan.mixin.server.network;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.mixin.entity.player.PlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public int ticks =10;
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method="tick",at=@At("HEAD"))
    public void tick(CallbackInfo info){
        ticks++;
        if (ticks>=10) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(((EntityExtension)this).getBouncy());
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.BOUNCY_PACKET_ID, buf);
            ticks = 0;
        }
    }

    @Inject(method = "copyFrom",at = @At("HEAD"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive,CallbackInfo info) {
        ((EntityExtension)this).setBouncy(((EntityExtension)oldPlayer).getBouncy());
        ((PlayerEntityMixin)(Object)this).SleepSheep=((PlayerEntityMixin)(Object)oldPlayer).SleepSheep;
        ((PlayerEntityExtension)this).setDeathPos((oldPlayer).getPos());
    }
}
