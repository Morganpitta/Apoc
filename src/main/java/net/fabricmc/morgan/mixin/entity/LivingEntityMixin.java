package net.fabricmc.morgan.mixin.entity;

import com.google.common.base.Objects;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtension {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public float lastHandSwingProgress;
    @Shadow public float handSwingProgress;

    @Shadow abstract protected int computeFallDamage(float fallDistance, float damageMultiplier);

    @Shadow abstract public SoundEvent getFallSound(int distance);

    @Shadow abstract protected void playBlockFallSound();

    @Shadow abstract public Optional<BlockPos> getSleepingPosition();
    @Shadow abstract void setPositionInBed(BlockPos pos);
    @Shadow abstract public boolean shouldDisplaySoulSpeedEffects();
    @Shadow abstract void displaySoulSpeedEffects();
    @Shadow abstract public boolean canBreatheInWater();
    @Shadow abstract int getNextAirUnderwater(int air);
    @Shadow abstract int getNextAirOnLand(int air);
    @Shadow BlockPos lastBlockPos;
    @Shadow abstract void applyMovementEffects(BlockPos pos);
    @Shadow public int hurtTime;
    @Shadow abstract public boolean isDead();
    @Shadow abstract void updatePostDeath();
    @Shadow int playerHitTimer;
    @Shadow PlayerEntity attackingPlayer;
    @Shadow LivingEntity attacking;
    @Shadow LivingEntity attacker;
    @Shadow int lastAttackedTime;
    @Shadow abstract public void setAttacker(@Nullable LivingEntity attacker);
    @Shadow abstract void tickStatusEffects();
    @Shadow float lookDirection;
    @Shadow float prevLookDirection;
    @Shadow float bodyYaw;
    @Shadow float prevBodyYaw;
    @Shadow float headYaw;
    @Shadow float prevHeadYaw;


    @Overwrite
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource){
        boolean bl = super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
        if(this.isBouncy()){
            return false;
        }
        else {
            int i = this.computeFallDamage(fallDistance, damageMultiplier);
            if (i > 0) {
                this.playSound(this.getFallSound(i), 1.0F, 1.0F);
                this.playBlockFallSound();
                this.damage(damageSource, (float) i);
                return true;
            } else {
                return bl;
            }
        }
    }

    @Overwrite
    public void baseTick() {
        this.lastHandSwingProgress = this.handSwingProgress;
        if (this.firstUpdate) {
            this.getSleepingPosition().ifPresent(this::setPositionInBed);
        }

        if (this.shouldDisplaySoulSpeedEffects()) {
            this.displaySoulSpeedEffects();
        }

        super.baseTick();
        this.world.getProfiler().push("livingEntityBaseTick");
        if (this.isFireImmune() || this.world.isClient) {
            this.extinguish();
        }

        if (this.isAlive()) {
            boolean bl = ((LivingEntity)(Entity)this) instanceof PlayerEntity;
            if (this.isInsideWall()) {
                this.damage(DamageSource.IN_WALL, 1.0F);
            } else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
                double d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getSafeZone();
                if (d < 0.0D) {
                    double e = this.world.getWorldBorder().getDamagePerBlock();
                    if (e > 0.0D) {
                        this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d * e)));
                    }
                }
            }

            if ((this.isSubmergedIn(FluidTags.WATER) ||this.isSubmergedIn(FluidTags.WITHERED_FLUIDS)) && !this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
                boolean d = !this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing((LivingEntity) (Entity)this) && (!bl || !((PlayerEntity)(Entity)this).getAbilities().invulnerable);
                if (d) {
                    this.setAir(this.getNextAirUnderwater(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        Vec3d vec3d = this.getVelocity();

                        for(int e = 0; e < 8; ++e) {
                            double f = this.random.nextDouble() - this.random.nextDouble();
                            double g = this.random.nextDouble() - this.random.nextDouble();
                            double h = this.random.nextDouble() - this.random.nextDouble();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + f, this.getY() + g, this.getZ() + h, vec3d.x, vec3d.y, vec3d.z);
                        }

                        this.damage(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
                    this.stopRiding();
                }
            } else if (this.getAir() < this.getMaxAir()) {
                this.setAir(this.getNextAirOnLand(this.getAir()));
            }

            if (!this.world.isClient) {
                BlockPos d = this.getBlockPos();
                if (!Objects.equal(this.lastBlockPos, d)) {
                    this.lastBlockPos = d;
                    this.applyMovementEffects(d);
                }
            }
        }

        if (this.isAlive() && (this.isWet() || this.inPowderSnow)) {
            if (!this.world.isClient && this.wasOnFire) {
                this.playExtinguishSound();
            }

            this.extinguish();
        }

        if (this.hurtTime > 0) {
            --this.hurtTime;
        }

        if (this.timeUntilRegen > 0 && !((LivingEntity)(Entity)this instanceof ServerPlayerEntity)) {
            --this.timeUntilRegen;
        }

        if (this.isDead() && this.world.shouldUpdatePostDeath(this)) {
            this.updatePostDeath();
        }

        if (this.playerHitTimer > 0) {
            --this.playerHitTimer;
        } else {
            this.attackingPlayer = null;
        }

        if (this.attacking != null && !this.attacking.isAlive()) {
            this.attacking = null;
        }

        if (this.attacker != null) {
            if (!this.attacker.isAlive()) {
                this.setAttacker((LivingEntity)null);
            } else if (this.age - this.lastAttackedTime > 100) {
                this.setAttacker((LivingEntity)null);
            }
        }

        this.tickStatusEffects();
        this.prevLookDirection = this.lookDirection;
        this.prevBodyYaw = this.bodyYaw;
        this.prevHeadYaw = this.headYaw;
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.world.getProfiler().pop();
    }

}
