//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.fabricmc.morgan.mixin.entity.player;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension, EntityExtension {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    public int SleepSheep = 0;
    public boolean CanJump=true;
    public boolean isBlind=false;
    public boolean isSad = false;
    public boolean getJump() {return this.CanJump;}
    public void setJump(boolean bool) {
        this.CanJump=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.CAN_JUMP_PACKET_ID, buf);
        }
    }
    public boolean getBlind() {return this.isBlind;}
    public void setBlind(boolean bool) {
        this.isBlind=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.BLIND_PACKET_ID, buf);
        }
    }
    @Shadow protected HungerManager hungerManager = new HungerManager();
    @Shadow private final PlayerAbilities abilities = new PlayerAbilities();
    @Shadow public void incrementStat(Identifier stat) {this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));}
    @Shadow  public void incrementStat(Stat<?> stat) {
        this.increaseStat((Stat)stat, 1);
    }
    @Shadow public void increaseStat(Stat<?> stat, int amount) {}
    @Shadow public abstract void addExhaustion(float exhaustion);
    @Shadow public abstract PlayerInventory getInventory();

    @Inject(method = "writeCustomDataToNbt",at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt,CallbackInfo info) {
        nbt.putInt("SleepSheep", this.SleepSheep);
    }
    @Inject(method = "readCustomDataFromNbt",at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt,CallbackInfo info) {
        this.SleepSheep = nbt.getInt("SleepSheep");
    }

    public void giveUpAndDie(){
        this.kill();
    }
    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this.isSad){
            giveUpAndDie();
        }
        if ((this.getEntityName()=="Zenxuss"||this.getEntityName()=="alex_2772")&&!this.getBlind()) {
            this.setBlind(true);
        }
        if (this.isSleeping()&&!this.world.isClient)
        {
            SleepSheep=SleepSheep+1;
            //ExampleMod.LOGGER.info(String.valueOf(SleepSheep));
            Entity Sheep = EntityType.SHEEP.spawn((ServerWorld) world, null, Text.of(String.valueOf(SleepSheep)), (PlayerEntity)(LivingEntity) this, new BlockPos(this.getX(),this.getY()+1,this.getZ()), SpawnReason.BREEDING,false,false);
            Sheep.setVelocity(0,5,0);
            Sheep.setGlowing(true);
            Sheep.setOnFireFor(1000);
            world.spawnEntity(Sheep);
        }
        if (this.isTouchingWater()&&!this.noClip)
        {
            if (this.isSneaking())
            {
                this.addVelocity(0,1,0);
            }
            else if (this.jumping)
            {
                this.addVelocity(0,-1,0);
            }
            else if (this.isSneaking() && this.jumping)
            {

            }
        }
        if ((this.getStackInHand(Hand.MAIN_HAND).isOf(MorganItems.ITEM_MAGNET)||this.getStackInHand(Hand.OFF_HAND).isOf(MorganItems.ITEM_MAGNET))){
            List list = this.world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),this.getBoundingBox().expand(30D),Entity::isAlive);
            Iterator var1 = list.iterator();

            while(var1.hasNext()) {
                ItemEntity item = (ItemEntity) var1.next();
                Vec3d vec3d = new Vec3d(this.getX() - item.getX(), this.getY() + (double) this.getStandingEyeHeight() / 2.0D - item.getY(), this.getZ() - item.getZ());
                double d = vec3d.lengthSquared();
                if (d < 256) {
                    double e = 1.0D - Math.sqrt(d) / 8.0D;
                    e = e * 4;
                    item.setVelocity(item.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1D)));
                }

            }
        }

    }

    @Overwrite
    public void jump() {
        super.jump();
        this.incrementStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2F);
        } else {
            this.addExhaustion(0.05F);
        }
    }

    @Override
    public void SwitchJump()
    {
        if (getJump()) {
            this.setJump(false);
        } else {
            this.setJump(true);
        }
        //ExampleMod.LOGGER.info(String.valueOf(Text.of("setting can jump to: "+this.CanJump+ " for: "+this)), false);
    }

}
