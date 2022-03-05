package net.fabricmc.morgan.mixin.entity;

import com.google.common.base.Objects;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.LivingEntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityExtension {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public boolean upsideDown = false;
    public boolean getUpsideDown(){return this.getRenderUpsideDown()&&((EntityExtension)this).getGravity()!=0.08D&&this.upsideDown;}
    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
        this.setRenderUpsideDown(upsideDown);
        if(!(((EntityExtension)this).getGravity()==-0.08D)){
            ((EntityExtension)this).setGravity(-0.08D);
        }
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(upsideDown);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.UPSIDE_DOWN_PACKET_ID, buf);
        }
        this.calculateDimensions();
    }

    public boolean renderUpsideDown = false;
    public boolean getRenderUpsideDown(){return this.renderUpsideDown;}
    public void setRenderUpsideDown(boolean renderUpsideDown) {
        this.renderUpsideDown = renderUpsideDown;
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(renderUpsideDown);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ExampleMod.RENDER_UPSIDE_DOWN_PACKET_ID, buf);
        }
    }

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public float lastHandSwingProgress;
    @Shadow public float handSwingProgress;

    @Shadow abstract protected int computeFallDamage(float fallDistance, float damageMultiplier);

    @Shadow abstract public SoundEvent getFallSound(int distance);

    @Shadow abstract protected void playBlockFallSound();

    @Shadow abstract public Optional<BlockPos> getSleepingPosition();
    @Shadow abstract void setPositionInBed(BlockPos pos);
    @Shadow abstract public boolean shouldDisplaySoulSpeedEffects();
    @Shadow
    protected abstract void displaySoulSpeedEffects();
    @Shadow abstract public boolean canBreatheInWater();
    @Shadow
    protected abstract int getNextAirUnderwater(int air);
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

    @Shadow protected abstract float getJumpVelocity();

    @Shadow public abstract double getJumpBoostVelocityModifier();

    @Shadow public abstract boolean canMoveVoluntarily();
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);
    @Shadow protected abstract boolean shouldSwimInFluids();
    @Shadow public abstract boolean canWalkOnFluid(Fluid fluid);
    @Shadow protected abstract float getBaseMovementSpeedMultiplier();
    @Shadow public abstract float getMovementSpeed();
    @Shadow public abstract boolean isClimbing();
    @Shadow public abstract Vec3d method_26317(double d, boolean bl, Vec3d vec3d);
    @Shadow public abstract boolean isFallFlying();
    @Shadow public abstract Vec3d applyMovementInput(Vec3d movementInput, float slipperiness);
    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    @Shadow public abstract boolean hasNoDrag();
    @Shadow public abstract void updateLimbs(LivingEntity entity, boolean flutter);
    @Shadow public int jumpingCooldown;
    @Shadow public int bodyTrackingIncrements;
    @Shadow protected double serverX;
    @Shadow protected double serverY;
    @Shadow protected double serverZ;
    @Shadow protected double serverYaw;
    @Shadow protected double serverPitch;
    @Shadow protected double serverHeadYaw;
    @Shadow protected int headTrackingIncrements;
    @Shadow protected abstract boolean isImmobile();
    @Shadow protected boolean jumping;
    @Shadow public float sidewaysSpeed;
    @Shadow public float upwardSpeed;
    @Shadow public float forwardSpeed;
    @Shadow protected abstract void swimUpward(Tag<Fluid> fluid);
    @Shadow public abstract void tickFallFlying();
    @Shadow public abstract void removePowderSnowSlow();
    @Shadow public abstract void addPowderSnowSlowIfNeeded();
    @Shadow protected int riptideTicks;
    @Shadow public abstract void tickRiptide(Box a, Box b);
    @Shadow public abstract void tickCramming();
    @Shadow public abstract boolean hurtByWater();
    @Shadow public abstract void tickNewAi();

    @Shadow public abstract boolean isHoldingOntoLadder();

    @Shadow protected abstract void applyDamage(DamageSource source, float amount);

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    /**
     * @author Morgan
     * @reason yet again trying to make fall damage not
     */
    @Overwrite
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource){
        boolean bl = super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
        if(((EntityExtension)this).getBouncy()){
            return false;
        }
        else {
            //ExampleMod.LOGGER.info("doing fall damamge stufs");
            int i = this.computeFallDamage(fallDistance, damageMultiplier);
            //ExampleMod.LOGGER.info(i);
            if (i > 0) {
                //ExampleMod.LOGGER.info("yes");
                this.playSound(this.getFallSound(i), 1.0F, 1.0F);
                this.playBlockFallSound();
                ExampleMod.LOGGER.info(this.damage(damageSource, (float) i));
                return true;
            } else {
                return bl;
            }
        }
    }

    /**
     * @author Morgan
     * @reason Upside down agggh
     */
    @Overwrite
    private Vec3d applyClimbingSpeed(Vec3d motion) {
        if (this.isClimbing()) {
            this.onLanding();
            float f = 0.15f;
            double d = MathHelper.clamp(motion.x, (double)-0.15f, (double)0.15f);
            double e = MathHelper.clamp(motion.z, (double)-0.15f, (double)0.15f);
            double g = Math.max(motion.y, (double)-0.15f);
            if (g < 0.0 && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder() && (LivingEntity)(Object)this instanceof PlayerEntity) {
                g = 0.0;
            }
            if (((EntityExtension)this).upsideDownGravity()){
                motion = new Vec3d(d, -g, e);
            }
            else {
                motion = new Vec3d(d, g, e);
            }
        }
        return motion;
    }

    /**
     * @author Morgan
     * @reason Upside down agggh
     */
    @Overwrite
    public void takeKnockback(double strength, double x, double z) {
        if ((strength *= 1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) <= 0.0) {
            return;
        }
        this.velocityDirty = true;
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
        this.setVelocity(vec3d.x / 2.0 - vec3d2.x, (this.onGround ? Math.min(0.4, vec3d.y / 2.0 + strength) : vec3d.y)*(((EntityExtension)this).upsideDownGravity()?-1:1), vec3d.z / 2.0 - vec3d2.z);
    }

    /**
     * @author Morgan
     * @reason i cant remember why i did this but sure
     */
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

            if ((this.isSubmergedIn(FluidTags.WATER) ||this.isSubmergedIn(FluidTags.CORRUPTED_FLUIDS)) && !this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
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

    /**
     * @author Morgan
     * @reason trying to make can jump work, dont think i need this but shush
     */
    @Overwrite
    public void jump() {
        double d = (double)this.getJumpVelocity() + this.getJumpBoostVelocityModifier();
        Vec3d vec3d = this.getVelocity();
        if (((EntityExtension)this).getGravity()<0){d*=-1;}
        if (this.isPlayer()) {
            if (((PlayerEntityExtension) this).getJump()||((EntityExtension)this).getGravity()<0) {
                this.setVelocity(vec3d.x, d, vec3d.z);
                if (this.isSprinting()) {
                    float f = (180+this.getYaw()) * 0.017453292F;
                    this.setVelocity(this.getVelocity().add((double) (-MathHelper.sin(f) * 0.2F), 0.0D, (double) (MathHelper.cos(f) * 0.2F)));
                }
            }
        }
        else {
            this.setVelocity(vec3d.x, d, vec3d.z);
            if (this.isSprinting()) {
                float f = this.getYaw() * 0.017453292F;
                this.setVelocity(this.getVelocity().add((double) (-MathHelper.sin(f) * 0.2F), 0.0D, (double) (MathHelper.cos(f) * 0.2F)));
            }
        }

        this.velocityDirty = true;
    }

    /**
     * @author Morgan
     * @reason yet again trying
     */
    @Overwrite
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            double d = ((EntityExtension)this).getGravity();
            boolean bl = this.getVelocity().y <= 0.0D;
            if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                d /= 8;
                this.onLanding();
            }

            FluidState fluidState = this.world.getFluidState(this.getBlockPos());
            //float f;
            //double e;
            if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
                double e = this.getY();
                float f = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
                float g = 0.02F;
                float h = (float) EnchantmentHelper.getDepthStrider((LivingEntity)(Object) this);
                if (h > 3.0F) {
                    h = 3.0F;
                }

                if (!this.onGround) {
                    h *= 0.5F;
                }

                if (h > 0.0F) {
                    f += (0.54600006F - f) * h / 3.0F;
                    g += (this.getMovementSpeed() - g) * h / 3.0F;
                }

                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    f = 0.96F;
                }

                this.updateVelocity(g, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    vec3d = new Vec3d(vec3d.x, 0.2D, vec3d.z);
                }

                this.setVelocity(vec3d.multiply((double)f, 0.800000011920929D, (double)f));
                Vec3d vec3d2 = this.method_26317(d, bl, this.getVelocity());
                this.setVelocity(vec3d2);
                if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.getY() + e, vec3d2.z)) {
                    this.setVelocity(vec3d2.x, 0.30000001192092896D, vec3d2.z);
                }
            } else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
                double e = this.getY();
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d f;
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
                    this.setVelocity(this.getVelocity().multiply(0.5D, 0.800000011920929D, 0.5D));
                    f = this.method_26317(d, bl, this.getVelocity());
                    this.setVelocity(f);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.5D));
                }

                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0D, -d / 4.0D, 0.0D));
                }

                f = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(f.x, f.y + 0.6000000238418579D - this.getY() + e, f.z)) {
                    this.setVelocity(f.x, 0.30000001192092896D, f.z);
                }
            } else if (this.isFallFlying()) {
                Vec3d e = this.getVelocity();
                if (e.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3d vec3d3 = this.getRotationVector();
                float f = this.getPitch() * 0.017453292F;
                double g = Math.sqrt(vec3d3.x * vec3d3.x + vec3d3.z * vec3d3.z);
                double vec3d = e.horizontalLength();
                double i = vec3d3.length();
                float j = MathHelper.cos(f);
                j = (float)((double)j * (double)j * Math.min(1.0D, i / 0.4D));
                e = this.getVelocity().add(0.0D, d * (-1.0D + (double)j * 0.75D), 0.0D);
                double k;
                if (e.y < 0.0D && g > 0.0D) {
                    k = e.y * -0.1D * (double)j;
                    e = e.add(vec3d3.x * k / g, k, vec3d3.z * k / g);
                }

                if (f < 0.0F && g > 0.0D) {
                    k = vec3d * (double)(-MathHelper.sin(f)) * 0.04D;
                    e = e.add(-vec3d3.x * k / g, k * 3.2D, -vec3d3.z * k / g);
                }

                if (g > 0.0D) {
                    e = e.add((vec3d3.x / g * vec3d - e.x) * 0.1D, 0.0D, (vec3d3.z / g * vec3d - e.z) * 0.1D);
                }

                this.setVelocity(e.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(MovementType.SELF, this.getVelocity());
                if (this.horizontalCollision && !this.world.isClient) {
                    k = this.getVelocity().horizontalLength();
                    double l = vec3d - k;
                    float m = (float)(l * 10.0D - 3.0D);
                    if (m > 0.0F) {
                        if (!((EntityExtension)this).getBouncy()) {
                            this.playSound(this.getFallSound((int) m), 1.0F, 1.0F);
                            this.damage(DamageSource.FLY_INTO_WALL, m);
                        }
                    }
                }

                if (this.onGround && !this.world.isClient) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPos e = this.getVelocityAffectingPos();
                float vec3d3 = this.world.getBlockState(e).getBlock().getSlipperiness();
                float f = this.onGround ? vec3d3 * 0.91F : 0.91F;
                Vec3d g = this.applyMovementInput(movementInput, vec3d3);
                double h = g.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    h += (0.05D * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - g.y) * 0.2D;
                    this.onLanding();
                } else if (this.world.isClient && !this.world.isChunkLoaded(e)) {
                    if (this.getY() > (double)this.world.getBottomY()) {
                        h = -0.1D;
                    } else {
                        h = 0.0D;
                    }
                } else if (!this.hasNoGravity()) {
                    h -= d;
                }

                if (this.hasNoDrag()) {
                    this.setVelocity(g.x, h, g.z);
                } else {
                    this.setVelocity(g.x * (double)f, h * 0.9800000190734863D, g.z * (double)f);
                }
            }
        }

        this.updateLimbs((LivingEntity) (Object)this, this instanceof Flutterer);
    }

    /**
     * @author Morgan
     * @reason
     */
    @Overwrite
    public void tickMovement() {
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (this.isLogicalSideForUpdatingMovement()) {
            this.bodyTrackingIncrements = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        if (this.bodyTrackingIncrements > 0) {
            double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
            double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
            this.setYaw(this.getYaw() + (float)g / (float)this.bodyTrackingIncrements);
            this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
            --this.bodyTrackingIncrements;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98D));
        }

        if (this.headTrackingIncrements > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }

        Vec3d d = this.getVelocity();
        double h = d.x;
        double i = d.y;
        double j = d.z;
        if (Math.abs(d.x) < 0.003D) {
            h = 0.0D;
        }

        if (Math.abs(d.y) < 0.003D) {
            i = 0.0D;
        }

        if (Math.abs(d.z) < 0.003D) {
            j = 0.0D;
        }

        this.setVelocity(h, i, j);
        this.world.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0F;
            this.forwardSpeed = 0.0F;
        } else if (this.canMoveVoluntarily()) {
            this.world.getProfiler().push("newAi");
            this.tickNewAi();
            this.world.getProfiler().pop();
        }

        this.world.getProfiler().pop();
        this.world.getProfiler().push("jump");

        if (this.jumping && this.shouldSwimInFluids()) {
            double k;
            if (this.isInLava()) {
                k = this.getFluidHeight(FluidTags.LAVA);
            } else {
                k = this.getFluidHeight(FluidTags.WATER);
            }

            boolean bl = this.isTouchingWater() && k > 0.0D;
            double l = this.getSwimHeight();
            if (bl && (!this.onGround || k > l)) {
                this.swimUpward(FluidTags.WATER);
            } else if (!this.isInLava() || this.onGround && !(k > l)) {
                if ((this.onGround || bl && k <= l) && this.jumpingCooldown == 0) {
                    this.jump();
                    this.jumpingCooldown = 10;
                }
            } else {
                this.swimUpward(FluidTags.LAVA);
            }
        } else {
            this.jumpingCooldown = 0;
        }

        this.world.getProfiler().pop();
        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98F;
        this.forwardSpeed *= 0.98F;
        this.tickFallFlying();
        Box k = this.getBoundingBox();
        this.travel(new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed));

        this.world.getProfiler().pop();
        this.world.getProfiler().push("freezing");
        boolean bl2 = this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
        int bl;
        if (!this.world.isClient && !this.isDead()) {
            bl = this.getFrozenTicks();
            if (this.getY()>350){
                this.setFrozenTicks(Math.min(200, bl + 5));
            }
            else if (this.getY()>300){
                this.setFrozenTicks(Math.min(this.getMinFreezeDamageTicks(), bl + 2));
            }
            else if (this.inPowderSnow && this.canFreeze()) {
                this.setFrozenTicks(Math.min(this.getMinFreezeDamageTicks(), bl + 1));
            } else {
                this.setFrozenTicks(Math.max(0, bl - 2));
            }
        }

        this.removePowderSnowSlow();
        this.addPowderSnowSlowIfNeeded();

        if (!this.world.isClient &&  this.isFreezing() && !this.isSpectator()&&this.getFrozenTicks()>this.getMinFreezeDamageTicks()) {
            bl = bl2 ? 5 : 1;
            this.damage(DamageSource.FREEZE, bl*4);
        }
        else if (!this.world.isClient && this.age % 40 == 0 && this.isFreezing() && this.canFreeze()) {
            bl = bl2 ? 5 : 1;
            this.damage(DamageSource.FREEZE, (float)bl);
        }

        this.world.getProfiler().pop();
        this.world.getProfiler().push("push");
        if (this.riptideTicks > 0) {
            --this.riptideTicks;
            this.tickRiptide(k, this.getBoundingBox());
        }

        this.tickCramming();
        this.world.getProfiler().pop();
        if (!this.world.isClient && this.hurtByWater() && this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0F);
        }

    }
}
