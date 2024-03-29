//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.fabricmc.morgan.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.LivingEntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.entity.player.PlayerInventoryExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.tag.MorganItemTags;
import net.minecraft.entity.*;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInitialise(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci){
        ((LivingEntityExtension)this).setUpsideDown(true);
        checkPlayerDisabilities();
    }

    public int tick = 0;
    public int onFireForTicks=0;
    public int fuse=-100;

    public int nextDropItem = this.random.nextInt(6000) + 6000;

    public boolean isForgetful=false;
    public boolean getForgetful() {return this.isForgetful;}
    public void setForgetful(boolean bool) {
        this.isForgetful=bool;
        if (!this.world.isClient()&& ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.FORGETFUL_PACKET_ID, buf);
        }
    }

    public Vec3d deathPos= new Vec3d(0,-255,0);
    public Vec3d getDeathPos(){return this.deathPos;}
    public void setDeathPos(Vec3d pos) {
        this.deathPos=pos;
        if (!this.world.isClient() &&((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(new BlockPos((double) pos.x,(double)pos.y,(double)pos.z));
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.DEATH_PACKET_ID, buf);
        }
    }

    public int SleepSheep = 0;
    public int getSleepSheep(){return this.SleepSheep;}
    public void setSleepSheep(int sheep) {
        this.SleepSheep=sheep;
        if (!this.world.isClient()&& ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(sheep);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.BLIND_PACKET_ID, buf);
        }
    }

    public boolean CanJump=true;
    public boolean getJump() {return this.CanJump;}
    public void setJump(boolean bool) {
        this.CanJump=bool;
        if (!this.world.isClient()&&((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.CAN_JUMP_PACKET_ID, buf);
        }
    }

    public boolean isBlind=false;
    public boolean getBlind() {return this.isBlind;}
    public void setBlind(boolean bool) {
        this.isBlind=bool;
        if (!this.world.isClient()&&((ServerPlayerEntity) (Object) this).networkHandler != null) {
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
        if (!this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
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

    /**
     * @author Morgan
     * @reason make drop item have 0 delay
     */
    @Overwrite
    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        if (stack.isEmpty()) {
            return null;
        } else {
            if (this.world.isClient) {
                this.swingHand(Hand.MAIN_HAND);
            }

            double d = this.getEyeY() - 0.30000001192092896D;
            ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), d, this.getZ(), stack);
            itemEntity.setPickupDelay(0);
            if (retainOwnership) {
                itemEntity.setThrower(this.getUuid());
            }

            float f;
            float g;
            if (throwRandomly) {
                f = this.random.nextFloat() * 0.5F;
                g = this.random.nextFloat() * 6.2831855F;
                itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.20000000298023224D, (double)(MathHelper.cos(g) * f));
            } else {
                f = 0.3F;
                g = MathHelper.sin(this.getPitch() * 0.017453292F);
                float h = MathHelper.cos(this.getPitch() * 0.017453292F);
                float i = MathHelper.sin(this.getYaw() * 0.017453292F);
                float j = MathHelper.cos(this.getYaw() * 0.017453292F);
                float k = this.random.nextFloat() * 6.2831855F;
                float l = 0.02F * this.random.nextFloat();
                itemEntity.setVelocity((double)(-i * h * 0.3F) + Math.cos((double)k) * (double)l, (double)(-g * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(j * h * 0.3F) + Math.sin((double)k) * (double)l);
            }

            return itemEntity;
        }
    }

    @Shadow @Final protected static TrackedData<Byte> PLAYER_MODEL_PARTS;

    @Shadow protected abstract boolean clipAtLedge();

    @Shadow protected abstract boolean method_30263();

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract ActionResult interact(Entity entity, Hand hand);

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
        //if (!((LivingEntityExtension)this).getUpsideDown()){
        //    ((LivingEntityExtension)this).setUpsideDown(true);
        //}
        if (this.getForgetful()&&--this.nextDropItem<0) {
            ((PlayerInventoryExtension)this.getInventory()).dropRandomUsedSlot();
            this.nextDropItem = this.random.nextInt(6000) + 6000;
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
        //this.checkPlayerDisabilities();
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

    public void checkPlayerDisabilities(){
        if ((Objects.equals(this.getEntityName(), "Zenxuss") || Objects.equals(this.getEntityName(), "alex_2772"))&&!this.getBlind()) {
            this.setBlind(true);
        }

        if ((Objects.equals(this.getEntityName(), "Slic_e"))&&!this.getBlind()) {
            this.setAffectedByWeight(true);
        }

        if ((Objects.equals(this.getEntityName(), "MagmaStan"))&&!((LivingEntityExtension)this).getUpsideDown()) {
            ((LivingEntityExtension)this).setUpsideDown(true);
        }

        ((EntityExtension)this).setStandingEyeHeight(this.getEyeHeight(EntityPose.STANDING, this.getDimensions(EntityPose.STANDING)));
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

    /**
     * @author Morgan
     * @reason sneaking upside down
     */
    @Overwrite
    public Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        if (!this.abilities.flying && (type == MovementType.SELF || type == MovementType.PLAYER) && this.clipAtLedge() && this.method_30263()) {
            double d = movement.x;
            double e = movement.z;
            double y = ((EntityExtension)this).upsideDownGravity() ?(double)(this.getBoundingBox().maxY-this.getBoundingBox().minY+this.stepHeight):(double)(-this.stepHeight);
            double var7 = 0.05D;

            while(true) {
                while(d != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(d, y, 0.0D))) {
                    if (d < 0.05D && d >= -0.05D) {
                        d = 0.0D;
                    } else if (d > 0.0D) {
                        d -= 0.05D;
                    } else {
                        d += 0.05D;
                    }
                }

                while(true) {
                    while(e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(0.0D, y, e))) {
                        if (e < 0.05D && e >= -0.05D) {
                            e = 0.0D;
                        } else if (e > 0.0D) {
                            e -= 0.05D;
                        } else {
                            e += 0.05D;
                        }
                    }

                    while(true) {
                        while(d != 0.0D && e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(d, y, e))) {
                            if (d < 0.05D && d >= -0.05D) {
                                d = 0.0D;
                            } else if (d > 0.0D) {
                                d -= 0.05D;
                            } else {
                                d += 0.05D;
                            }

                            if (e < 0.05D && e >= -0.05D) {
                                e = 0.0D;
                            } else if (e > 0.0D) {
                                e -= 0.05D;
                            } else {
                                e += 0.05D;
                            }
                        }

                        movement = new Vec3d(d, movement.y, e);
                        return movement;
                    }
                }
            }
        } else {
            return movement;
        }
    }
}
