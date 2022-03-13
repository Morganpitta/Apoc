package net.fabricmc.morgan.mixin.entity;


import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.block.BlockExtension;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.world.entity.Bounciness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin  implements Nameable, EntityLike, CommandOutput, EntityExtension {

    @Inject(method="<init>",at = @At("TAIL"))
    public void Entity(EntityType<?> type, World world, CallbackInfo info){
        //this.gravity = 0.08D;
        this.standingEyeHeight = this.getEyeHeight(EntityPose.STANDING, this.dimensions);
        //ExampleMod.LOGGER.info(standingEyeHeight);
    }


    public boolean upsideDownGravity(){return this.getGravity()<0;}

    public double gravity=0.08;
    public double getGravity(){return this.gravity;}
    public void setGravity(double gravity) {
        this.gravity = gravity;
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeDouble(gravity);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.GRAVITY_PACKET_ID, buf);
        }
        this.calculateDimensions();
    }

    public boolean isBouncy=false;
    public void setBouncy(boolean bool){
        this.isBouncy=bool;
        if (this.isPlayer()&&!this.world.isClient()&&((ServerPlayerEntity)(Object)this).networkHandler!=null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(bool);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.BOUNCY_PACKET_ID, buf);
        }
    }
    public boolean getBouncy(){return this.isBouncy;}
    @Shadow abstract public void onLanding();
    @Shadow public float fallDistance;
    @Shadow public boolean noClip;
    @Shadow public final void setPosition(Vec3d pos) {
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }
    @Shadow abstract void setPosition(double x, double y, double z);
    @Shadow abstract void setVelocity(Vec3d velocity);

    @Shadow abstract void setVelocity(double x, double y, double z);

    @Shadow private Vec3d pos;
    @Shadow private BlockPos blockPos;
    @Shadow public World world;
    @Shadow protected Vec3d movementMultiplier;
    @Shadow public boolean horizontalCollision;
    @Shadow public boolean verticalCollision;
    @Shadow public boolean collidedSoftly;
    @Shadow abstract boolean hasCollidedSoftly(Vec3d adjustedMovement);
    @Shadow public boolean onGround;
    @Shadow public float speed;
    @Shadow public float horizontalSpeed;
    @Shadow public float distanceTraveled;
    @Shadow private float nextStepSoundDistance;

    @Shadow public boolean wasOnFire;
    @Shadow public abstract boolean isOnFire();


    @Shadow public final double getX() {
        return this.pos.x;
    }
    @Shadow public final double getY() {
        return this.pos.y;
    }
    @Shadow public final double getZ() {
        return this.pos.z;
    }

    @Shadow public abstract void setPos(double x, double y, double z) ;
    @Shadow public abstract boolean isRemoved();

    @Shadow  protected abstract Vec3d adjustMovementForPiston(Vec3d movement) ;
    @Shadow protected abstract Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type);
    @Shadow public abstract Vec3d getVelocity();

    @Shadow abstract public boolean bypassesSteppingEffects();

    @Shadow abstract protected Entity.MoveEffect getMoveEffect();

    @Shadow abstract public boolean hasVehicle();

    @Shadow abstract protected float calculateNextStepSoundDistance();

    @Shadow abstract public boolean isTouchingWater();

    @Shadow abstract public boolean hasPassengers();
    @Shadow @Nullable abstract public Entity getPrimaryPassenger();

    @Shadow abstract protected void playSwimSound(float volume);

    @Shadow abstract public void emitGameEvent(GameEvent event, @Nullable Entity entity, BlockPos pos);

    @Shadow abstract public void emitGameEvent(GameEvent event, @Nullable Entity entity);

    @Shadow abstract public void emitGameEvent(GameEvent event, BlockPos pos);

    @Shadow abstract public void emitGameEvent(GameEvent event);

    @Shadow abstract public void playAmethystChimeSound(BlockState state);

    @Shadow abstract public void playStepSound(BlockPos pos, BlockState state);

    @Shadow abstract public void addAirTravelEffects();

    @Shadow abstract protected void tryCheckBlockCollision();

    @Shadow abstract protected float getVelocityMultiplier();

    @Shadow private int fireTicks;

    @Shadow abstract public void setFireTicks(int ticks);

    @Shadow protected abstract int getBurningDuration();

    @Shadow public boolean inPowderSnow;

    @Shadow abstract public boolean isWet();

    @Shadow protected abstract void playExtinguishSound();

    @Shadow abstract public List<Entity> getPassengerList();

    @Shadow protected Object2DoubleMap<Tag<Fluid>> fluidHeight;

    @Shadow protected boolean firstUpdate;

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract Box getBoundingBox();

    @Shadow public double prevY;

    @Shadow public abstract float getStandingEyeHeight();

    @Shadow public double prevZ;

    @Shadow public double prevX;

    @Shadow public abstract Vec3d getRotationVector();

    @Shadow public abstract float getYaw();

    @Shadow protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Shadow public abstract float getYaw(float tickDelta);

    @Shadow public abstract void calculateDimensions();

    @Shadow private float standingEyeHeight;

    @Shadow private EntityDimensions dimensions;

    @Shadow private float pitch;

    @Shadow public abstract float getPitch();

    @Shadow public float prevPitch;

    @Shadow protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

    @Shadow public float stepHeight;

    public EntityMixin(EntityType<?> type, World world) {

    }

    @Inject(method = "writeNbt",at = @At("HEAD"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable info ) {
        nbt.putBoolean("IsBouncy", this.isBouncy);
    }
    @Inject(method = "readNbt",at = @At("HEAD"))
    public void readNbt(NbtCompound nbt, CallbackInfo info ) {
        this.setBouncy(nbt.getBoolean("IsBouncy"));
    }

    /**
     * @author Morgan
     * @reason gravity stuff
     */
    @Overwrite
    public BlockPos getLandingPos() {
        BlockPos blockPos2;
        BlockState blockState;
        int k= MathHelper.floor(this.pos.z);
        int j= MathHelper.floor((double)(this.upsideDownGravity()?this.getBoundingBox().maxY+.2f:this.pos.y -.2f));
        //ExampleMod.LOGGER.info(String.valueOf(j)+"  "+String.valueOf(this.pos.y));
        int i = MathHelper.floor(this.pos.x);
        BlockPos blockPos = new BlockPos(i, j , k );
        if (this.world.getBlockState(blockPos).isAir() && ((blockState = this.world.getBlockState(blockPos2 = blockPos.down())).isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock)) {
            return blockPos2;
        }
        return blockPos;
    }


    /**
     * @author Morgan
     * @reason gravity stuff
     */
    @Overwrite
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(fallDistance, damageMultiplier, damageSource);
            }
        }
        return false;
    }

    /**
     * @author Morgan
     * @reason gravity stuff
     */
    @Overwrite
    public BlockPos getVelocityAffectingPos() {
        double y = (double) ( this.upsideDownGravity()? this.getBoundingBox().maxY + 0.5000001:this.getBoundingBox().minY - 0.5000001);
        return new BlockPos(this.pos.x, y, this.pos.z);
    }

    /**
     * @author Morgan
     * @reason gravity stuff
     */
    @Overwrite
    public final Vec3d getCameraPosVec(float tickDelta) {
        double d = MathHelper.lerp((double)tickDelta, this.prevX, this.getX());
        double e = MathHelper.lerp((double)tickDelta, this.prevY, this.getY()) + ((double) this.getStandingEyeHeight());
        //if ((Entity)(Object)this instanceof PlayerEntity) {((PlayerEntity) (Object)this).sendMessage(Text.of(String.valueOf(this.getStandingEyeHeight())),false);};
        double f = MathHelper.lerp((double)tickDelta, this.prevZ, this.getZ());
        return new Vec3d(d, e, f);
    }

    /**
    @Overwrite
    public float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        ExampleMod.LOGGER.info(String.valueOf(upsideDownGravity())+" "+String.valueOf(getGravity()));
        if (upsideDownGravity()){

            float orange = dimensions.height * 0.85f;
            //height+=0.1;
            float brown = (dimensions.height/2);
            float grey = (orange-brown);
            float height = orange-2*grey;
            //(dimensions.height/2)
            //ExampleMod.LOGGER.info(String.valueOf(orange)+" "+String.valueOf(brown)+" "+String.valueOf(grey)+" "+String.valueOf(height));
            ExampleMod.LOGGER.info("are you there?"+" "+String.valueOf(height));
            return height;
        } else {
            ExampleMod.LOGGER.info("help me!"+" "+String.valueOf(dimensions.height * 0.85f));

            return dimensions.height * 0.85f;
        }
    }
    **/


    public void setStandingEyeHeight(float eyeHeight){
        this.standingEyeHeight = eyeHeight;
        //ExampleMod.LOGGER.info("checking player disabilities stuff"+String.valueOf(standingEyeHeight));
    }

    /**
     * @author Morgan
     * @reason slabs and stairs upsidedown
     */
    @Overwrite
    public Vec3d adjustMovementForCollisions(Vec3d movement) {
        //ExampleMod.LOGGER.info(this.upsideDownGravity());
        if (this.upsideDownGravity()) {
            boolean bl4;
            Box box = this.getBoundingBox();
            List<VoxelShape> list = this.world.getEntityCollisions((Entity) (Object) this, box.stretch(movement));
            Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions((Entity) (Object) this, movement, box, this.world, list);
            boolean bl = movement.x != vec3d.x;
            boolean bl2 = movement.y != vec3d.y;
            boolean bl3 = movement.z != vec3d.z;
            boolean bl5 = bl4 = this.onGround || bl2 && movement.y > 0.0;
            if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
                Vec3d vec3d4;
                Vec3d vec3d2 = Entity.adjustMovementForCollisions((Entity) (Object) this, new Vec3d(movement.x, -this.stepHeight, movement.z), box, this.world, list);
                Vec3d vec3d3 = Entity.adjustMovementForCollisions((Entity) (Object) this, new Vec3d(0.0, -this.stepHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, list);
                if (vec3d3.y > (double) this.stepHeight && (vec3d4 = Entity.adjustMovementForCollisions((Entity) (Object) this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, list).add(vec3d3)).horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                    vec3d2 = vec3d4;
                }
                if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                    return ( vec3d2.add(Entity.adjustMovementForCollisions((Entity) (Object) this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, list)));

                }
            }
            return ( vec3d);
        }
        else {
            boolean bl4;
            Box box = this.getBoundingBox();
            List<VoxelShape> list = this.world.getEntityCollisions((Entity) (Object)this, box.stretch(movement));
            Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions((Entity) (Object)this, movement, box, this.world, list);
            boolean bl = movement.x != vec3d.x;
            boolean bl2 = movement.y != vec3d.y;
            boolean bl3 = movement.z != vec3d.z;
            boolean bl5 = bl4 = this.onGround || bl2 && movement.y < 0.0;
            if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
                Vec3d vec3d4;
                Vec3d vec3d2 = Entity.adjustMovementForCollisions((Entity) (Object)this, new Vec3d(movement.x, this.stepHeight, movement.z), box, this.world, list);
                Vec3d vec3d3 = Entity.adjustMovementForCollisions((Entity) (Object)this, new Vec3d(0.0, this.stepHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, list);
                if (vec3d3.y < (double) this.stepHeight && (vec3d4 = Entity.adjustMovementForCollisions((Entity) (Object)this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, list).add(vec3d3)).horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                    vec3d2 = vec3d4;
                }
                if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                    return vec3d2.add(Entity.adjustMovementForCollisions((Entity) (Object)this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, list));
                }
            }
            return vec3d;
        }
        //ExampleMod.LOGGER.info("continued");
    }



    /**
     * @author Morgan
     * @reason gravity stuff
     */
    /**
    @Overwrite
    public final Vec3d getRotationVec(float tickDelta) {
        return this.getRotationVector(this.getPitch(tickDelta)+(((EntityExtension)this).upsideDownGravity()?180:0), this.getYaw(tickDelta));
    }
    **/

    /**
     * @author Morgan
     * @reason gravity stuff
     */
    @Inject(method = "getPitch(F)F",at = @At("HEAD"),cancellable = true)
    public void getPitch(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (this.upsideDownGravity()) {
            //ExampleMod.LOGGER.info("getting pitch"+String.valueOf(upsideDownGravity()));
            //ExampleMod.LOGGER.info(String.valueOf(getPitch()) + " " + String.valueOf(getPitch() + ((this).upsideDownGravity() ? 180 : 0)));
            if (tickDelta == 1.0f) {
                //ExampleMod.LOGGER.info(this.getPitch()+((this).upsideDownGravity()?180:0));
                cir.setReturnValue(this.getPitch() + 180);
            }
            //ExampleMod.LOGGER.info(MathHelper.lerp(tickDelta, this.prevPitch, this.getPitch())+((this).upsideDownGravity()?180:0));
            cir.setReturnValue(MathHelper.lerp(tickDelta, this.prevPitch, this.getPitch()) + 180);
        }
    }


    /**
     * @author Morgan
     * @reason change stuff to make bouncy
     */
    @Overwrite
    public void move(MovementType movementType, Vec3d movement) {
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        } else {
            this.wasOnFire = this.isOnFire();
            if (movementType == MovementType.PISTON) {
                movement = this.adjustMovementForPiston(movement);
                if (movement.equals(Vec3d.ZERO)) {
                    return;
                }
            }

            this.world.getProfiler().push("move");
            if (this.movementMultiplier.lengthSquared() > 1.0E-7D) {
                movement = movement.multiply(this.movementMultiplier);
                this.movementMultiplier = Vec3d.ZERO;
                this.setVelocity(Vec3d.ZERO);
            }
            this.onGround = this.getGravity()>=0 ?this.verticalCollision && movement.y < 0.0D:this.verticalCollision && movement.y > 0.0D;
            movement = this.adjustMovementForSneaking(movement, movementType);
            Vec3d vec3d = this.adjustMovementForCollisions(movement);
            if (vec3d.lengthSquared() > 1.0E-7D) {
                this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
            }

            this.world.getProfiler().pop();
            this.world.getProfiler().push("rest");
            this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, vec3d.x) || !MathHelper.approximatelyEquals(movement.z, vec3d.z);
            this.verticalCollision = movement.y != vec3d.y;
            if (this.horizontalCollision) {
                this.collidedSoftly = this.hasCollidedSoftly(vec3d);
            } else {
                this.collidedSoftly = false;
            }

            this.onGround = this.getGravity()>=0 ?this.verticalCollision && movement.y < 0.0D:this.verticalCollision && movement.y > 0.0D;
            //if ((Entity)(Object)this instanceof PlayerEntity){ExampleMod.LOGGER.info(String.valueOf(this)+ " "+ String.valueOf(this.standingEyeHeight));}
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            this.fall(vec3d.y, this.onGround, blockState, blockPos);
            if (this.isRemoved()) {
                this.world.getProfiler().pop();
            } else {
                Vec3d vec3d2 = this.getVelocity();
                if (movement.x != vec3d.x) {
                    if (getBouncy())
                    {
                        this.setVelocity(-vec3d.x*Bounciness.Bounciness, vec3d2.y, vec3d2.z);
                    }
                    else{
                        this.setVelocity(0.0D, vec3d2.y, vec3d2.z);
                    }
                }

                if (movement.z != vec3d.z) {
                    if (getBouncy())
                    {
                        this.setVelocity(vec3d.x, vec3d2.y, -vec3d2.z*Bounciness.Bounciness);
                    }
                    else{
                        this.setVelocity(vec3d.x, vec3d2.y, 0.0D);
                    }
                }

                Block block = blockState.getBlock();
                if (movement.y != vec3d.y) {
                    if (getBouncy()) {
                        //this.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
                        this.setVelocity(vec3d.x, -vec3d2.y * Bounciness.Bounciness, vec3d.z);
                    }
                    else {
                        block.onEntityLand(this.world, (Entity) (Object) this);
                    }
                }

                if (this.onGround) {
                    ((BlockExtension)block).onSteppedOnIgnoringCrouching(this.world, blockPos, blockState, (Entity) (Object) this);
                    if(!this.bypassesSteppingEffects()) {
                        block.onSteppedOn(this.world, blockPos, blockState, (Entity) (Object) this);
                    }
                }

                Entity.MoveEffect moveEffect = this.getMoveEffect();
                if (moveEffect.hasAny() && !this.hasVehicle()) {
                    double d = vec3d.x;
                    double e = vec3d.y;
                    double f = vec3d.z;
                    this.speed = (float)((double)this.speed + vec3d.length() * 0.6D);
                    if (!blockState.isIn(BlockTags.CLIMBABLE) && !blockState.isOf(Blocks.POWDER_SNOW)) {
                        e = 0.0D;
                    }

                    this.horizontalSpeed += (float)vec3d.horizontalLength() * 0.6F;
                    this.distanceTraveled += (float)Math.sqrt(d * d + e * e + f * f) * 0.6F;
                    if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                        this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                        if (this.isTouchingWater()) {
                            if (moveEffect.playsSounds()) {
                                Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : (Entity) (Object)this;
                                float g = entity == (Entity) (Object)this ? 0.35F : 0.4F;
                                Vec3d vec3d3 = entity.getVelocity();
                                float h = Math.min(1.0F, (float)Math.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224D + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224D) * g);
                                this.playSwimSound(h);
                            }

                            if (moveEffect.emitsGameEvents()) {
                                this.emitGameEvent(GameEvent.SWIM);
                            }
                        } else {
                            if (moveEffect.playsSounds()) {
                                this.playAmethystChimeSound(blockState);
                                this.playStepSound(blockPos, blockState);
                            }

                            if (moveEffect.emitsGameEvents() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                                this.emitGameEvent(GameEvent.STEP);
                            }
                        }
                    } else if (blockState.isAir()) {
                        this.addAirTravelEffects();
                    }
                }

                this.tryCheckBlockCollision();
                float d = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply((double)d, 1.0D, (double)d));
                if (this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6D)).noneMatch((state) -> {
                    return state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA);
                })) {
                    if (this.fireTicks <= 0) {
                        this.setFireTicks(-this.getBurningDuration());
                    }

                    if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
                        this.playExtinguishSound();
                    }
                }

                if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
                    this.setFireTicks(-this.getBurningDuration());
                }

                this.world.getProfiler().pop();
            }
        }
    }


    /**
     * @author Morgan
     * @reason trying to not make fall damage when bouncy, idk if actually works
     */
    @Overwrite
    public void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        heightDifference *=MathHelper.sqrt((float) MathHelper.square(gravity/0.08));
        //ExampleMod.LOGGER.info(heightDifference);
        if (onGround) {
            if (this.fallDistance > 0.0F) {
                //ExampleMod.LOGGER.info(landedState.getBlock());
                //ExampleMod.LOGGER.info(fallDistance);
                landedState.getBlock().onLandedUpon(this.world, landedState, landedPosition, (Entity) (Object) this, this.fallDistance);
                if (!landedState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                    this.emitGameEvent(GameEvent.HIT_GROUND);
                }
            }

            this.onLanding();
        } else if (heightDifference < 0.0D&&!this.upsideDownGravity()) {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }
        else if (this.upsideDownGravity()&&heightDifference>0.0D){
            this.fallDistance = (float)((double)this.fallDistance + heightDifference);
        }
    }

    @Inject(method = "baseTick",at = @At("HEAD"))
    public void baseTick(CallbackInfo info){
        if (!this.firstUpdate && this.fluidHeight.getDouble(FluidTags.CORRUPTED_FLUIDS) > 0.0D){
            this.damage(DamageSource.MAGIC,1);
        }
    }

    /**
     * @author Morgan
     * @reason uncapped movement speed so i can make weight thingy
     */
    @Overwrite
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        //ExampleMod.LOGGER.info(d);
        if (d < 1.0E-7D) {
            return Vec3d.ZERO;
        } else {
            //ExampleMod.LOGGER.info(movementInput);
            Vec3d vec3d = (d > 1000000.0D ? movementInput.normalize() : movementInput).multiply((double)speed);
            //ExampleMod.LOGGER.info(vec3d);
            float f = MathHelper.sin(yaw * 0.017453292F);
            float g = MathHelper.cos(yaw * 0.017453292F);
            return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }

}
