package net.fabricmc.morgan.mixin.entity;


import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.block.BlockExtension;
import net.fabricmc.morgan.block.MeatBlock;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.mixin.block.BlockMixin;
import net.fabricmc.morgan.world.entity.Bounciness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.*;
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
    @Shadow public abstract BlockPos getLandingPos();
    @Shadow public abstract boolean isRemoved();

    @Shadow  protected abstract Vec3d adjustMovementForPiston(Vec3d movement) ;
    @Shadow protected abstract Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type);
    @Shadow abstract public Vec3d adjustMovementForCollisions(Vec3d movement);
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

    @Shadow abstract public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource);

    public EntityMixin(EntityType<?> type, World world) {

    }

    @Inject(method = "writeNbt",at = @At("HEAD"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable info ) {
        nbt.putBoolean("IsBouncy", this.isBouncy);
    }
    @Inject(method = "readNbt",at = @At("HEAD"))
    public void readNbt(NbtCompound nbt,CallbackInfo info ) {
        this.setBouncy(nbt.getBoolean("IsBouncy"));
    }

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

            this.onGround = this.verticalCollision && movement.y < 0.0D;
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
                    block.onEntityLand(this.world, (Entity) (Object) this);
                }

                if (this.onGround) {
                    ((BlockExtension)block).onSteppedOnIgnoringCrouching(this.world, blockPos, blockState, (Entity) (Object) this);
                    if(!this.bypassesSteppingEffects()) {
                        if (getBouncy() && vec3d.y < 0.0D) {
                            this.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
                            if (this.isPlayer()) {
                                this.setVelocity(vec3d.x, -vec3d2.y * Bounciness.Bounciness, vec3d.z);
                            } else {
                                this.setVelocity(vec3d.x, -vec3d2.y * Bounciness.Bounciness, vec3d.z);
                            }
                        }
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


    @Overwrite
    public void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        if (onGround) {
            if (this.fallDistance > 0.0F) {
                    landedState.getBlock().onLandedUpon(this.world, landedState, landedPosition, (Entity) (Object) this, this.fallDistance);
                    if (!landedState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                        this.emitGameEvent(GameEvent.HIT_GROUND);
                    }
            }

            this.onLanding();
        } else if (heightDifference < 0.0D) {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }

    }

}
