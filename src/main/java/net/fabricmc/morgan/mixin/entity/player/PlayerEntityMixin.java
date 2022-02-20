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
import net.fabricmc.morgan.entity.player.PlayerInventoryExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.tag.MorganItemTags;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension, EntityExtension {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private static final UUID WEIGHT_SPEED_ID = UUID.fromString("2f9a3e00-7c2b-4916-9a5b-56804fa6cc91");

    public int tick = 0;
    public int onFireForTicks=0;
    public int fuse=-100;

    public int nextDropItem = this.random.nextInt(10) + 5;

    public boolean isForgetful=true;
    public boolean getForgetful() {return this.isForgetful;}
    public void setForgetful(boolean bool) {
        this.isForgetful=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.FORGETFUL_PACKET_ID, buf);
        }
    }

    public Vec3d deathPos= new Vec3d(0,-255,0);
    public Vec3d getDeathPos(){return this.deathPos;}
    public void setDeathPos(Vec3d pos) {
        this.deathPos=pos;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(new BlockPos((double) pos.x,(double)pos.y,(double)pos.z));
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.DEATH_PACKET_ID, buf);
        }
    }

    public int SleepSheep = 0;
    public int getSleepSheep(){return this.SleepSheep;}
    public void setSleepSheep(int sheep) {
        this.SleepSheep=sheep;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(sheep);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.BLIND_PACKET_ID, buf);
        }
    }

    public boolean CanJump=true;
    public boolean getJump() {return this.CanJump;}
    public void setJump(boolean bool) {
        this.CanJump=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.CAN_JUMP_PACKET_ID, buf);
        }
    }

    public boolean isBlind=false;
    public boolean getBlind() {return this.isBlind;}
    public void setBlind(boolean bool) {
        this.isBlind=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.WEIGHT_PACKET_ID, buf);
        }
    }

    public boolean isSad = false;

    public boolean isAffectedByWeight = false;
    public boolean getAffectedByWeight(){return this.isAffectedByWeight;}
    public void setAffectedByWeight(boolean bool) {
        this.isAffectedByWeight=bool;
        if (!this.world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.DEATH_PACKET_ID, buf);
        }
    }

    @Shadow public abstract HungerManager getHungerManager();
    @Shadow public abstract boolean checkFallFlying();
    @Shadow protected int abilityResyncCountdown;
    @Shadow public abstract void sendAbilitiesUpdate();
    @Shadow public abstract PlayerAbilities getAbilities();
    @Shadow protected HungerManager hungerManager = new HungerManager();
    @Final
    @Shadow private final PlayerAbilities abilities = new PlayerAbilities();
    @Shadow public void incrementStat(Identifier stat) {this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));}
    @Shadow  public void incrementStat(Stat<?> stat) {
        this.increaseStat(stat, 1);
    }
    @Shadow public void increaseStat(Stat<?> stat, int amount) {}
    @Shadow public abstract void addExhaustion(float exhaustion);
    @Shadow public abstract PlayerInventory getInventory();

    @Shadow @Nullable public abstract ItemEntity dropItem(ItemStack stack, boolean retainOwnership);

    @Shadow @Final protected static TrackedData<Byte> PLAYER_MODEL_PARTS;

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
        if (--this.nextDropItem<0) {
            this.dropItem(this.getInventory().getStack(((PlayerInventoryExtension)this.getInventory()).getRandomUsedSlot()),false);
            this.nextDropItem = this.random.nextInt(10) + 5;
        }
        if (this.isOnFire()){
            onFireForTicks++;
            if (onFireForTicks>20&&(this.getInventory().contains(MorganItemTags.EXPLOSIVE))&&fuse<0) {
                world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                fuse = 20;
            }
            if (fuse < 0&&fuse >-10){
                this.world.createExplosion(this,this.getX(), this.getY(),this.getZ(),30, true, Explosion.DestructionType.BREAK);
                fuse = -100;
            }
            fuse--;
        }
        else {
            onFireForTicks--;
        }
        if (this.isSad){
            giveUpAndDie();
        }
        if ((Objects.equals(this.getEntityName(), "Zenxuss") || Objects.equals(this.getEntityName(), "alex_2772"))&&!this.getBlind()) {
            this.setBlind(true);
        }

        if ((Objects.equals(this.getEntityName(), "Slic_e"))&&!this.getBlind()) {
            this.setAffectedByWeight(true);
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
            /*
            else if (this.isSneaking() && this.jumping)
            {
            }
             */
        }
        if ((this.getStackInHand(Hand.MAIN_HAND).isOf(MorganItems.ITEM_MAGNET)||this.getStackInHand(Hand.OFF_HAND).isOf(MorganItems.ITEM_MAGNET))){
            List list = this.world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),this.getBoundingBox().expand(30D),Entity::isAlive);
            Iterator var1 = list.iterator();

            while(var1.hasNext()) {
                ItemEntity item = (ItemEntity) var1.next();
                if (((PlayerInventoryExtension)(this.getInventory())).canInsertStack(item.getStack())) {
                    Vec3d vec3d = new Vec3d(this.getX() - item.getX(), this.getY() + (double) this.getStandingEyeHeight() / 2.0D - item.getY(), this.getZ() - item.getZ());
                    double d = vec3d.lengthSquared();
                    if (d < 256) {
                        double e = 1.0D - Math.sqrt(d) / 8.0D;
                        e = e * 4;
                        item.setVelocity(item.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1D)));
                    }
                }
            }
            list = this.world.getEntitiesByType(TypeFilter.instanceOf(ExperienceOrbEntity.class),this.getBoundingBox().expand(30D),Entity::isAlive);
            var1 = list.iterator();

            while(var1.hasNext()) {
                ExperienceOrbEntity item = (ExperienceOrbEntity) var1.next();
                Vec3d vec3d = new Vec3d(this.getX() - item.getX(), this.getY() + (double) this.getStandingEyeHeight() / 2.0D - item.getY(), this.getZ() - item.getZ());
                double d = vec3d.lengthSquared();
                if (d < 256) {
                    double e = 1.0D - Math.sqrt(d) / 8.0D;
                    e = e * 4;
                    item.setVelocity(item.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1D)));
                }

            }
        }
        tick++;
        if (tick >20){
            tick = 0;
        }
    }

    @Inject(method = "tickMovement",at = @At("HEAD"))
    public void tickMovement(CallbackInfo info) {
        /**
        float weight = ((PlayerInventoryExtension) this.getInventory()).getWeight();
        weight += 0.01;
        //ExampleMod.LOGGER.info("doing suff"+weight);
        weight = 1 / weight;
        //ExampleMod.LOGGER.info("doing more suff"+weight);
        weight *= 128;
        ExampleMod.LOGGER.info("doing more suff" + weight);
        this.setMovementSpeed(this.getMovementSpeed() * weight);
         **/
    }

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
