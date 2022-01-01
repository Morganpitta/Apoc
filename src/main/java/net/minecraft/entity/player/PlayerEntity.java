//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEntity extends LivingEntity {
    public static final String OFFLINE_PLAYER_UUID_PREFIX = "OfflinePlayer:";
    public static final int field_30643 = 16;
    public static final int field_30644 = 20;
    public static final int field_30645 = 100;
    public static final int field_30646 = 10;
    public static final int field_30647 = 200;
    public static final float field_30648 = 1.5F;
    public static final float field_30649 = 0.6F;
    public static final float field_30650 = 0.6F;
    public static final float field_30651 = 1.62F;
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6F, 1.8F);
    private static final ImmutableMap<Object, Object> POSE_DIMENSIONS;
    private static final int field_30652 = 25;
    private static final TrackedData<Float> ABSORPTION_AMOUNT;
    private static final TrackedData<Integer> SCORE;
    protected static final TrackedData<Byte> PLAYER_MODEL_PARTS;
    protected static final TrackedData<Byte> MAIN_ARM;
    protected static final TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY;
    protected static final TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY;
    private long shoulderEntityAddedTime;
    private final PlayerInventory inventory = new PlayerInventory(this);
    protected EnderChestInventory enderChestInventory = new EnderChestInventory();
    public final PlayerScreenHandler playerScreenHandler;
    public ScreenHandler currentScreenHandler;
    protected HungerManager hungerManager = new HungerManager();
    protected int abilityResyncCountdown;
    public float prevStrideDistance;
    public float strideDistance;
    public int experiencePickUpDelay;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;
    private int sleepTimer;
    protected boolean isSubmergedInWater;
    private final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentTableSeed;
    protected final float field_7509 = 0.02F;
    private int lastPlayedLevelUpSoundTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack selectedItem;
    private final ItemCooldownManager itemCooldownManager;
    public int SleepSheep = 0;
    public boolean CanJump=true;
    @Nullable
    public FishingBobberEntity fishHook;

    public PlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(EntityType.PLAYER, world);
        this.selectedItem = ItemStack.EMPTY;
        this.itemCooldownManager = this.createCooldownManager();
        this.setUuid(getUuidFromProfile(profile));
        this.gameProfile = profile;
        this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !world.isClient, this);
        this.currentScreenHandler = this.playerScreenHandler;
        this.refreshPositionAndAngles((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D, yaw, 0.0F);
        this.field_6215 = 180.0F;
    }

    public boolean isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode) {
        if (!gameMode.isBlockBreakingRestricted()) {
            return false;
        } else if (gameMode == GameMode.SPECTATOR) {
            return true;
        } else if (this.canModifyBlocks()) {
            return false;
        } else {
            ItemStack itemStack = this.getMainHandStack();
            return itemStack.isEmpty() || !itemStack.canDestroy(world.getTagManager(), new CachedBlockPosition(world, pos, false));
        }
    }

    public static Builder createPlayerAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10000000149011612D).add(EntityAttributes.GENERIC_ATTACK_SPEED).add(EntityAttributes.GENERIC_LUCK);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ABSORPTION_AMOUNT, 0.0F);
        this.dataTracker.startTracking(SCORE, 0);
        this.dataTracker.startTracking(PLAYER_MODEL_PARTS, (byte)0);
        this.dataTracker.startTracking(MAIN_ARM, (byte)1);
        this.dataTracker.startTracking(LEFT_SHOULDER_ENTITY, new NbtCompound());
        this.dataTracker.startTracking(RIGHT_SHOULDER_ENTITY, new NbtCompound());
    }

    public void tick() {
        if (this.isSleeping()&&!this.world.isClient)
        {
            SleepSheep=SleepSheep+1;
            Entity Sheep = EntityType.SHEEP.spawn((ServerWorld) world, null, Text.of(String.valueOf(SleepSheep)), this, new BlockPos(this.getX(),this.getY()+1,this.getZ()), SpawnReason.BREEDING,false,false);
            Sheep.setVelocity(0,5,0);
            Sheep.setGlowing(true);
            Sheep.setOnFireFor(1000);
            world.spawnEntity(Sheep);
        }
        if (this.isTouchingWaterOrRain())
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
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.experiencePickUpDelay > 0) {
            --this.experiencePickUpDelay;
        }

        if (this.isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.world.isClient && this.world.isDay()) {
                this.wakeUp(false, true);
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        this.updateWaterSubmersionState();
        super.tick();
        if (!this.world.isClient && this.currentScreenHandler != null && !this.currentScreenHandler.canUse(this)) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }

        this.updateCapeAngles();
        if (!this.world.isClient) {
            this.hungerManager.update(this);
            this.incrementStat(Stats.PLAY_TIME);
            this.incrementStat(Stats.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.incrementStat(Stats.TIME_SINCE_DEATH);
            }

            if (this.isSneaky()) {
                this.incrementStat(Stats.SNEAK_TIME);
            }

            if (!this.isSleeping()) {
                this.incrementStat(Stats.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d = MathHelper.clamp(this.getX(), -2.9999999E7D, 2.9999999E7D);
        double e = MathHelper.clamp(this.getZ(), -2.9999999E7D, 2.9999999E7D);
        if (d != this.getX() || e != this.getZ()) {
            this.setPosition(d, this.getY(), e);
        }

        ++this.lastAttackedTicks;
        ItemStack itemStack = this.getMainHandStack();
        if (!ItemStack.areEqual(this.selectedItem, itemStack)) {
            if (!ItemStack.areItemsEqual(this.selectedItem, itemStack)) {
                this.resetLastAttackedTicks();
            }

            this.selectedItem = itemStack.copy();
        }

        this.updateTurtleHelmet();
        this.itemCooldownManager.update();
        this.updatePose();
    }

    public boolean shouldCancelInteraction() {
        return this.isSneaking();
    }

    protected boolean shouldDismount() {
        return this.isSneaking();
    }

    protected boolean clipAtLedge() {
        return this.isSneaking();
    }

    protected boolean updateWaterSubmersionState() {
        this.isSubmergedInWater = this.isSubmergedIn(FluidTags.WATER);
        return this.isSubmergedInWater;
    }

    private void updateTurtleHelmet() {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack.isOf(Items.TURTLE_HELMET) && !this.isSubmergedIn(FluidTags.WATER)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
        }

    }

    protected ItemCooldownManager createCooldownManager() {
        return new ItemCooldownManager();
    }

    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double d = this.getX() - this.capeX;
        double e = this.getY() - this.capeY;
        double f = this.getZ() - this.capeZ;
        double g = 10.0D;
        if (d > 10.0D) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (f > 10.0D) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (e > 10.0D) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        if (d < -10.0D) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (f < -10.0D) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (e < -10.0D) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        this.capeX += d * 0.25D;
        this.capeZ += f * 0.25D;
        this.capeY += e * 0.25D;
    }

    protected void updatePose() {
        if (this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
            EntityPose entityPose;
            if (this.isFallFlying()) {
                entityPose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entityPose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entityPose = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                entityPose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.flying) {
                entityPose = EntityPose.CROUCHING;
            } else {
                entityPose = EntityPose.STANDING;
            }

            EntityPose entityPose2;
            if (!this.isSpectator() && !this.hasVehicle() && !this.wouldPoseNotCollide(entityPose)) {
                if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
                    entityPose2 = EntityPose.CROUCHING;
                } else {
                    entityPose2 = EntityPose.SWIMMING;
                }
            } else {
                entityPose2 = entityPose;
            }

            this.setPose(entityPose2);
        }
    }

    public int getMaxNetherPortalTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    public int getDefaultNetherPortalCooldown() {
        return 10;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.world.playSound(this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
    }

    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    protected int getBurningDuration() {
        return 20;
    }

    public void handleStatus(byte status) {
        if (status == 9) {
            this.consumeItem();
        } else if (status == 23) {
            this.reducedDebugInfo = false;
        } else if (status == 22) {
            this.reducedDebugInfo = true;
        } else if (status == 43) {
            this.spawnParticles(ParticleTypes.CLOUD);
        } else {
            super.handleStatus(status);
        }

    }

    private void spawnParticles(ParticleEffect parameters) {
        for(int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02D;
            double e = this.random.nextGaussian() * 0.02D;
            double f = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(parameters, this.getParticleX(1.0D), this.getRandomBodyY() + 1.0D, this.getParticleZ(1.0D), d, e, f);
        }

    }

    protected void closeHandledScreen() {
        this.currentScreenHandler = this.playerScreenHandler;
    }

    public void tickRiding() {
        if (!this.world.isClient && this.shouldDismount() && this.hasVehicle()) {
            this.stopRiding();
            this.setSneaking(false);
        } else {
            double d = this.getX();
            double e = this.getY();
            double f = this.getZ();
            super.tickRiding();
            this.prevStrideDistance = this.strideDistance;
            this.strideDistance = 0.0F;
            this.increaseRidingMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
        }
    }

    protected void tickNewAi() {
        super.tickNewAi();
        this.tickHandSwing();
        this.headYaw = this.getYaw();
    }

    public void tickMovement() {
        if (this.abilityResyncCountdown > 0) {
            --this.abilityResyncCountdown;
        }

        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.age % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }

        this.inventory.updateItems();
        this.prevStrideDistance = this.strideDistance;
        super.tickMovement();
        this.airStrafingSpeed = 0.02F;
        if (this.isSprinting()) {
            this.airStrafingSpeed = (float)((double)this.airStrafingSpeed + 0.005999999865889549D);
        }

        this.setMovementSpeed((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        float f;
        if (this.onGround && !this.isDead() && !this.isSwimming()) {
            f = Math.min(0.1F, (float)this.getVelocity().horizontalLength());
        } else {
            f = 0.0F;
        }

        this.strideDistance += (f - this.strideDistance) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            Box box;
            if (this.hasVehicle() && !this.getVehicle().isRemoved()) {
                box = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0D, 0.0D, 1.0D);
            } else {
                box = this.getBoundingBox().expand(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.world.getOtherEntities(this, box);
            List<Entity> list2 = Lists.newArrayList();

            for(int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity)list.get(i);
                if (entity.getType() == EntityType.EXPERIENCE_ORB) {
                    list2.add(entity);
                } else if (!entity.isRemoved()) {
                    this.collideWithEntity(entity);
                }
            }

            if (!list2.isEmpty()) {
                this.collideWithEntity((Entity)Util.getRandom(list2, this.random));
            }
        }

        this.updateShoulderEntity(this.getShoulderEntityLeft());
        this.updateShoulderEntity(this.getShoulderEntityRight());
        if (!this.world.isClient && (this.fallDistance > 0.5F || this.isTouchingWater()) || this.abilities.flying || this.isSleeping() || this.inPowderSnow) {
            this.dropShoulderEntities();
        }
        if (this.hasPassengers()) {
            Iterator var1 = this.getPassengerList().iterator();

            while(var1.hasNext()) {
                Entity entity = (Entity) var1.next();
                this.updatePassengerPosition(entity);
            }

        }
    }

    private void updateShoulderEntity(@Nullable NbtCompound entityNbt) {
        if (entityNbt != null && (!entityNbt.contains("Silent") || !entityNbt.getBoolean("Silent")) && this.world.random.nextInt(200) == 0) {
            String string = entityNbt.getString("id");
            EntityType.get(string).filter((entityType) -> {
                return entityType == EntityType.PARROT;
            }).ifPresent((entityType) -> {
                if (!ParrotEntity.imitateNearbyMob(this.world, this)) {
                    this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), ParrotEntity.getRandomSound(this.world, this.world.random), this.getSoundCategory(), 1.0F, ParrotEntity.getSoundPitch(this.world.random));
                }

            });
        }

    }

    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        float f = MathHelper.sin(this.bodyYaw * 0.017453292F);
        float g = MathHelper.cos(this.bodyYaw * 0.017453292F);
        float h = 0.1F;
        float i = 0.0F;
        passenger.setPosition(this.getX() + (double)(0.1F * f), this.getBodyY(0.5D) + passenger.getHeightOffset() + 0.0D, this.getZ() - (double)(0.1F * g));
        if (passenger instanceof LivingEntity) {
            ((LivingEntity)passenger).bodyYaw = this.bodyYaw;
        }

    }

    private void collideWithEntity(Entity entity) {
        entity.onPlayerCollision(this);
    }

    public int getScore() {
        return (Integer)this.dataTracker.get(SCORE);
    }

    public void setScore(int score) {
        this.dataTracker.set(SCORE, score);
    }

    public void addScore(int score) {
        int i = this.getScore();
        this.dataTracker.set(SCORE, i + score);
    }

    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.refreshPosition();
        if (!this.isSpectator()) {
            this.drop(source);
        }

        if (source != null) {
            this.setVelocity((double)(-MathHelper.cos((this.knockbackVelocity + this.getYaw()) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double)(-MathHelper.sin((this.knockbackVelocity + this.getYaw()) * 0.017453292F) * 0.1F));
        } else {
            this.setVelocity(0.0D, 0.1D, 0.0D);
        }

        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setOnFire(false);
    }

    protected void dropInventory() {
        super.dropInventory();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.vanishCursedItems();
            this.inventory.dropAll();
        }

    }

    protected void vanishCursedItems() {
        for(int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
                this.inventory.removeStack(i);
            }
        }

    }

    protected SoundEvent getHurtSound(DamageSource source) {
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        } else if (source == DamageSource.DROWN) {
            return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
        } else if (source == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH;
        } else {
            return source == DamageSource.FREEZE ? SoundEvents.ENTITY_PLAYER_HURT_FREEZE : SoundEvents.ENTITY_PLAYER_HURT;
        }
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean retainOwnership) {
        return this.dropItem(stack, false, retainOwnership);
    }

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
            itemEntity.setPickupDelay(40);
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

    public float getBlockBreakingSpeed(BlockState block) {
        float f = this.inventory.getBlockBreakingSpeed(block);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getEfficiency(this);
            ItemStack itemStack = this.getMainHandStack();
            if (i > 0 && !itemStack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (StatusEffectUtil.hasHaste(this)) {
            f *= 1.0F + (float)(StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2F;
        }

        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float i;
            switch(this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    i = 0.3F;
                    break;
                case 1:
                    i = 0.09F;
                    break;
                case 2:
                    i = 0.0027F;
                    break;
                case 3:
                default:
                    i = 8.1E-4F;
            }

            f *= i;
        }

        if (this.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean canHarvest(BlockState state) {
        return !state.isToolRequired() || this.inventory.getMainHandStack().isSuitableFor(state);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setUuid(getUuidFromProfile(this.gameProfile));
        NbtList nbtList = nbt.getList("Inventory", 10);
        this.inventory.readNbt(nbtList);
        this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
        this.sleepTimer = nbt.getShort("SleepTimer");
        this.experienceProgress = nbt.getFloat("XpP");
        this.experienceLevel = nbt.getInt("XpLevel");
        this.totalExperience = nbt.getInt("XpTotal");
        this.enchantmentTableSeed = nbt.getInt("XpSeed");
        if (this.enchantmentTableSeed == 0) {
            this.enchantmentTableSeed = this.random.nextInt();
        }

        this.setScore(nbt.getInt("Score"));
        this.hungerManager.readNbt(nbt);
        this.abilities.readNbt(nbt);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkSpeed());
        if (nbt.contains("EnderItems", 9)) {
            this.enderChestInventory.readNbtList(nbt.getList("EnderItems", 10));
        }

        if (nbt.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(nbt.getCompound("ShoulderEntityLeft"));
        }

        if (nbt.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(nbt.getCompound("ShoulderEntityRight"));
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbt.put("Inventory", this.inventory.writeNbt(new NbtList()));
        nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
        nbt.putShort("SleepTimer", (short)this.sleepTimer);
        nbt.putFloat("XpP", this.experienceProgress);
        nbt.putInt("XpLevel", this.experienceLevel);
        nbt.putInt("XpTotal", this.totalExperience);
        nbt.putInt("XpSeed", this.enchantmentTableSeed);
        nbt.putInt("Score", this.getScore());
        this.hungerManager.writeNbt(nbt);
        this.abilities.writeNbt(nbt);
        nbt.put("EnderItems", this.enderChestInventory.toNbtList());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            nbt.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            nbt.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }

    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (super.isInvulnerableTo(damageSource)) {
            return true;
        } else if (damageSource == DamageSource.DROWN) {
            return !this.world.getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
        } else if (damageSource.isFromFalling()) {
            return !this.world.getGameRules().getBoolean(GameRules.FALL_DAMAGE);
        } else if (damageSource.isFire()) {
            return !this.world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE);
        } else if (damageSource == DamageSource.FREEZE) {
            return !this.world.getGameRules().getBoolean(GameRules.FREEZE_DAMAGE);
        } else {
            return false;
        }
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.abilities.invulnerable && !source.isOutOfWorld()) {
            return false;
        } else {
            this.despawnCounter = 0;
            if (this.isDead()) {
                return false;
            } else {
                this.dropShoulderEntities();
                if (source.isScaledWithDifficulty()) {
                    if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                        amount = 0.0F;
                    }

                    if (this.world.getDifficulty() == Difficulty.EASY) {
                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                    }

                    if (this.world.getDifficulty() == Difficulty.HARD) {
                        amount = amount * 3.0F / 2.0F;
                    }
                }

                return amount == 0.0F ? false : super.damage(source, amount);
            }
        }
    }

    protected void takeShieldHit(LivingEntity attacker) {
        super.takeShieldHit(attacker);
        if (attacker.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }

    }

    public boolean canTakeDamage() {
        return !this.getAbilities().invulnerable && super.canTakeDamage();
    }

    public boolean shouldDamagePlayer(PlayerEntity player) {
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        AbstractTeam abstractTeam2 = player.getScoreboardTeam();
        if (abstractTeam == null) {
            return true;
        } else {
            return !abstractTeam.isEqual(abstractTeam2) ? true : abstractTeam.isFriendlyFireAllowed();
        }
    }

    protected void damageArmor(DamageSource source, float amount) {
        this.inventory.damageArmor(source, amount, PlayerInventory.ARMOR_SLOTS);
    }

    protected void damageHelmet(DamageSource source, float amount) {
        this.inventory.damageArmor(source, amount, PlayerInventory.HELMET_SLOTS);
    }

    protected void damageShield(float amount) {
        if (this.activeItemStack.isOf(Items.SHIELD)) {
            if (!this.world.isClient) {
                this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
            }

            if (amount >= 3.0F) {
                int i = 1 + MathHelper.floor(amount);
                Hand hand = this.getActiveHand();
                this.activeItemStack.damage(i, this, (player) -> {
                    player.sendToolBreakStatus(hand);
                });
                if (this.activeItemStack.isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.activeItemStack = ItemStack.EMPTY;
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    protected void applyDamage(DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            amount = this.applyArmorToDamage(source, amount);
            amount = this.applyEnchantmentsToDamage(source, amount);
            float f = amount;
            amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - amount));
            float g = f - amount;
            if (g > 0.0F && g < 3.4028235E37F) {
                this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
            }

            if (amount != 0.0F) {
                this.addExhaustion(source.getExhaustion());
                float h = this.getHealth();
                this.setHealth(this.getHealth() - amount);
                this.getDamageTracker().onDamage(source, h, amount);
                if (amount < 3.4028235E37F) {
                    this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(amount * 10.0F));
                }

            }
        }
    }

    protected boolean isOnSoulSpeedBlock() {
        return !this.abilities.flying && super.isOnSoulSpeedBlock();
    }

    public void openEditSignScreen(SignBlockEntity sign) {
    }

    public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
    }

    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
    }

    public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
    }

    public void openJigsawScreen(JigsawBlockEntity jigsaw) {
    }

    public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
    }

    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
        return OptionalInt.empty();
    }

    public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
    }

    public void useBook(ItemStack book, Hand hand) {
    }

    public ActionResult interact(Entity entity, Hand hand) {
        if (this.isSpectator()) {
            if (entity instanceof NamedScreenHandlerFactory) {
                this.openHandledScreen((NamedScreenHandlerFactory)entity);
            }

            return ActionResult.PASS;
        } else {
            ItemStack itemStack = this.getStackInHand(hand);
            ItemStack itemStack2 = itemStack.copy();
            ActionResult actionResult = entity.interact(this, hand);
            if (actionResult.isAccepted()) {
                if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
                    itemStack.setCount(itemStack2.getCount());
                }

                return actionResult;
            } else {
                if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
                    if (this.abilities.creativeMode) {
                        itemStack = itemStack2;
                    }

                    ActionResult actionResult2 = itemStack.useOnEntity(this, (LivingEntity)entity, hand);
                    if (actionResult2.isAccepted()) {
                        if (itemStack.isEmpty() && !this.abilities.creativeMode) {
                            this.setStackInHand(hand, ItemStack.EMPTY);
                        }

                        return actionResult2;
                    }
                }

                return ActionResult.PASS;
            }
        }
    }

    public double getHeightOffset() {
        return -0.35D;
    }

    public void dismountVehicle() {
        super.dismountVehicle();
        this.ridingCooldown = 0;
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    public boolean shouldSwimInFluids() {
        return !this.abilities.flying;
    }

    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        if (!this.abilities.flying && (type == MovementType.SELF || type == MovementType.PLAYER) && this.clipAtLedge() && this.method_30263()) {
            double d = movement.x;
            double e = movement.z;
            double var7 = 0.05D;

            while(true) {
                while(d != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), 0.0D))) {
                    if (d < 0.05D && d >= -0.05D) {
                        d = 0.0D;
                    } else if (d > 0.0D) {
                        d -= 0.05D;
                    } else {
                        d += 0.05D;
                    }
                }

                while(true) {
                    while(e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(0.0D, (double)(-this.stepHeight), e))) {
                        if (e < 0.05D && e >= -0.05D) {
                            e = 0.0D;
                        } else if (e > 0.0D) {
                            e -= 0.05D;
                        } else {
                            e += 0.05D;
                        }
                    }

                    while(true) {
                        while(d != 0.0D && e != 0.0D && this.world.isSpaceEmpty(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), e))) {
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

    private boolean method_30263() {
        return this.onGround || this.fallDistance < this.stepHeight && !this.world.isSpaceEmpty(this, this.getBoundingBox().offset(0.0D, (double)(this.fallDistance - this.stepHeight), 0.0D));
    }

    public void attack(Entity target) {
        if (target.isAttackable()) {
            if (!target.handleAttack(this)) {
                float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float g;
                if (target instanceof LivingEntity) {
                    g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
                } else {
                    g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
                }

                float h = this.getAttackCooldownProgress(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                this.resetLastAttackedTicks();
                if (f > 0.0F || g > 0.0F) {
                    boolean bl = h > 0.9F;
                    boolean bl2 = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockback(this);
                    if (this.isSprinting() && bl) {
                        this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                        ++i;
                        bl2 = true;
                    }

                    boolean bl3 = bl && this.fallDistance > 0.0F && !this.onGround && !this.isClimbing() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && !this.hasVehicle() && target instanceof LivingEntity;
                    bl3 = bl3 && !this.isSprinting();
                    if (bl3) {
                        f *= 1.5F;
                    }

                    f += g;
                    boolean bl4 = false;
                    double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
                    if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed()) {
                        ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
                        if (itemStack.getItem() instanceof SwordItem) {
                            bl4 = true;
                        }
                    }

                    float itemStack = 0.0F;
                    boolean bl5 = false;
                    int j = EnchantmentHelper.getFireAspect(this);
                    if (target instanceof LivingEntity) {
                        itemStack = ((LivingEntity)target).getHealth();
                        if (j > 0 && !target.isOnFire()) {
                            bl5 = true;
                            target.setOnFireFor(1);
                        }
                    }

                    Vec3d vec3d = target.getVelocity();
                    boolean bl6 = target.damage(DamageSource.player(this), f);
                    if (bl6) {
                        if (i > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity)target).takeKnockback((double)((float)i * 0.5F), (double)MathHelper.sin(this.getYaw() * 0.017453292F), (double)(-MathHelper.cos(this.getYaw() * 0.017453292F)));
                            } else {
                                target.addVelocity((double)(-MathHelper.sin(this.getYaw() * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.getYaw() * 0.017453292F) * (float)i * 0.5F));
                            }

                            this.setVelocity(this.getVelocity().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (bl4) {
                            float k = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;
                            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
                            Iterator var19 = list.iterator();

                            label166:
                            while(true) {
                                LivingEntity livingEntity;
                                do {
                                    do {
                                        do {
                                            do {
                                                if (!var19.hasNext()) {
                                                    this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                                    this.spawnSweepAttackParticles();
                                                    break label166;
                                                }

                                                livingEntity = (LivingEntity)var19.next();
                                            } while(livingEntity == this);
                                        } while(livingEntity == target);
                                    } while(this.isTeammate(livingEntity));
                                } while(livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker());

                                if (this.squaredDistanceTo(livingEntity) < 9.0D) {
                                    livingEntity.takeKnockback(0.4000000059604645D, (double)MathHelper.sin(this.getYaw() * 0.017453292F), (double)(-MathHelper.cos(this.getYaw() * 0.017453292F)));
                                    livingEntity.damage(DamageSource.player(this), k);
                                }
                            }
                        }

                        if (target instanceof ServerPlayerEntity && target.velocityModified) {
                            ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                            target.velocityModified = false;
                            target.setVelocity(vec3d);
                        }

                        if (bl3) {
                            this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                            this.addCritParticles(target);
                        }

                        if (!bl3 && !bl4) {
                            if (bl) {
                                this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (g > 0.0F) {
                            this.addEnchantedHitParticles(target);
                        }

                        this.onAttacking(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.onUserDamaged((LivingEntity)target, this);
                        }

                        EnchantmentHelper.onTargetDamaged(this, target);
                        ItemStack k = this.getMainHandStack();
                        Entity list = target;
                        if (target instanceof EnderDragonPart) {
                            list = ((EnderDragonPart)target).owner;
                        }

                        if (!this.world.isClient && !k.isEmpty() && list instanceof LivingEntity) {
                            k.postHit((LivingEntity)list, this);
                            if (k.isEmpty()) {
                                this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (target instanceof LivingEntity) {
                            float l = itemStack - ((LivingEntity)target).getHealth();
                            this.increaseStat(Stats.DAMAGE_DEALT, Math.round(l * 10.0F));
                            if (j > 0) {
                                target.setOnFireFor(j * 4);
                            }

                            if (this.world instanceof ServerWorld && l > 2.0F) {
                                int livingEntity = (int)((double)l * 0.5D);
                                ((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5D), target.getZ(), livingEntity, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.addExhaustion(0.1F);
                    } else {
                        this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                        if (bl5) {
                            target.extinguish();
                        }
                    }
                }

            }
        }
    }

    protected void attackLivingEntity(LivingEntity target) {
        this.attack(target);
    }

    public void disableShield(boolean sprinting) {
        float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
        if (sprinting) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().set(Items.SHIELD, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte)30);
        }

    }

    public void addCritParticles(Entity target) {
    }

    public void addEnchantedHitParticles(Entity target) {
    }

    public void spawnSweepAttackParticles() {
        double d = (double)(-MathHelper.sin(this.getYaw() * 0.017453292F));
        double e = (double)MathHelper.cos(this.getYaw() * 0.017453292F);
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.getBodyY(0.5D), this.getZ() + e, 0, d, 0.0D, e, 0.0D);
        }

    }

    public void requestRespawn() {
    }

    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.playerScreenHandler.close(this);
        if (this.currentScreenHandler != null) {
            this.currentScreenHandler.close(this);
        }

    }

    public boolean isMainPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    public void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType) {
    }

    public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
        this.sleep(pos);
        this.sleepTimer = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {
        super.wakeUp();
        if (this.world instanceof ServerWorld && updateSleepingPlayers) {
            ((ServerWorld)this.world).updateSleepingPlayers();
        }

        this.sleepTimer = skipSleepTimer ? 0 : 100;
    }

    public void wakeUp() {
        this.wakeUp(true, true);
    }

    public static Optional<Vec3d> findRespawnPosition(ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof RespawnAnchorBlock && (Integer)blockState.get(RespawnAnchorBlock.CHARGES) > 0 && RespawnAnchorBlock.isNether(world)) {
            Optional<Vec3d> optional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
            if (!alive && optional.isPresent()) {
                world.setBlockState(pos, (BlockState)blockState.with(RespawnAnchorBlock.CHARGES, (Integer)blockState.get(RespawnAnchorBlock.CHARGES) - 1), 3);
            }

            return optional;
        } else if (block instanceof BedBlock && BedBlock.isBedWorking(world)) {
            return BedBlock.findWakeUpPosition(EntityType.PLAYER, world, pos, angle);
        } else if (!forced) {
            return Optional.empty();
        } else {
            boolean optional = block.canMobSpawnInside();
            boolean bl = world.getBlockState(pos.up()).getBlock().canMobSpawnInside();
            return optional && bl ? Optional.of(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D)) : Optional.empty();
        }
    }

    public boolean canResetTimeBySleeping() {
        return this.isSleeping() && this.sleepTimer >= 100;
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void sendMessage(Text message, boolean actionBar) {
    }

    public void incrementStat(Identifier stat) {
        this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));
    }

    public void increaseStat(Identifier stat, int amount) {
        this.increaseStat(Stats.CUSTOM.getOrCreateStat(stat), amount);
    }

    public void incrementStat(Stat<?> stat) {
        this.increaseStat((Stat)stat, 1);
    }

    public void increaseStat(Stat<?> stat, int amount) {
    }

    public void resetStat(Stat<?> stat) {
    }

    public int unlockRecipes(Collection<Recipe<?>> recipes) {
        return 0;
    }

    public void unlockRecipes(Identifier[] ids) {
    }

    public int lockRecipes(Collection<Recipe<?>> recipes) {
        return 0;
    }

    public void jump() {
        //this.sendMessage(Text.of(this+" is trying to jump. CanJump:"+this.CanJump), false);
        if (this.CanJump) {
            //this.sendMessage(Text.of("jumped"), false);
            super.jump();
            this.incrementStat(Stats.JUMP);
            if (this.isSprinting()) {
                this.addExhaustion(0.2F);
            } else {
                this.addExhaustion(0.05F);
            }
        }

    }
    public void SwitchJump()
    {
            if (this.CanJump) {
                this.CanJump = false;
            } else {
                this.CanJump = true;
            }
            //this.sendMessage(Text.of("setting can jump to: "+this.CanJump+ " for: "+this), false);
    }

    public void travel(Vec3d movementInput) {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        double g;
        if (this.isSwimming() && !this.hasVehicle()) {
            g = this.getRotationVector().y;
            double h = g < -0.2D ? 0.085D : 0.06D;
            if (g <= 0.0D || this.jumping || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
                Vec3d vec3d = this.getVelocity();
                this.setVelocity(vec3d.add(0.0D, (g - vec3d.y) * h, 0.0D));
            }
        }

        if (this.abilities.flying && !this.hasVehicle()) {
            g = this.getVelocity().y;
            float h = this.airStrafingSpeed;
            this.airStrafingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
            super.travel(movementInput);
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, g * 0.6D, vec3d2.z);
            this.airStrafingSpeed = h;
            this.onLanding();
            this.setFlag(7, false);
        } else {
            super.travel(movementInput);
        }

        this.increaseTravelMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
    }

    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }

    }

    protected boolean doesNotSuffocate(BlockPos pos) {
        return !this.world.getBlockState(pos).shouldSuffocate(this.world, pos);
    }

    public float getMovementSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    public void increaseTravelMotionStats(double dx, double dy, double dz) {
        if (!this.hasVehicle()) {
            int i;
            if (this.isSwimming()) {
                i = Math.round((float)Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
                if (i > 0) {
                    this.increaseStat(Stats.SWIM_ONE_CM, i);
                    this.addExhaustion(0.01F * (float)i * 0.01F);
                }
            } else if (this.isSubmergedIn(FluidTags.WATER)) {
                i = Math.round((float)Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
                if (i > 0) {
                    this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
                    this.addExhaustion(0.01F * (float)i * 0.01F);
                }
            } else if (this.isTouchingWater()) {
                i = Math.round((float)Math.sqrt(dx * dx + dz * dz) * 100.0F);
                if (i > 0) {
                    this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
                    this.addExhaustion(0.01F * (float)i * 0.01F);
                }
            } else if (this.isClimbing()) {
                if (dy > 0.0D) {
                    this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(dy * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round((float)Math.sqrt(dx * dx + dz * dz) * 100.0F);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.increaseStat(Stats.SPRINT_ONE_CM, i);
                        this.addExhaustion(0.1F * (float)i * 0.01F);
                    } else if (this.isInSneakingPose()) {
                        this.increaseStat(Stats.CROUCH_ONE_CM, i);
                        this.addExhaustion(0.0F * (float)i * 0.01F);
                    } else {
                        this.increaseStat(Stats.WALK_ONE_CM, i);
                        this.addExhaustion(0.0F * (float)i * 0.01F);
                    }
                }
            } else if (this.isFallFlying()) {
                i = Math.round((float)Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
                this.increaseStat(Stats.AVIATE_ONE_CM, i);
            } else {
                i = Math.round((float)Math.sqrt(dx * dx + dz * dz) * 100.0F);
                if (i > 25) {
                    this.increaseStat(Stats.FLY_ONE_CM, i);
                }
            }

        }
    }

    private void increaseRidingMotionStats(double dx, double dy, double dz) {
        if (this.hasVehicle()) {
            int i = Math.round((float)Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F);
            if (i > 0) {
                Entity entity = this.getVehicle();
                if (entity instanceof AbstractMinecartEntity) {
                    this.increaseStat(Stats.MINECART_ONE_CM, i);
                } else if (entity instanceof BoatEntity) {
                    this.increaseStat(Stats.BOAT_ONE_CM, i);
                } else if (entity instanceof PigEntity) {
                    this.increaseStat(Stats.PIG_ONE_CM, i);
                } else if (entity instanceof HorseBaseEntity) {
                    this.increaseStat(Stats.HORSE_ONE_CM, i);
                } else if (entity instanceof StriderEntity) {
                    this.increaseStat(Stats.STRIDER_ONE_CM, i);
                }
            }
        }

    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.abilities.allowFlying) {
            return false;
        } else {
            if (fallDistance >= 2.0F) {
                this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)fallDistance * 100.0D));
            }

            return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
        }
    }

    public boolean checkFallFlying() {
        if (!this.onGround && !this.isFallFlying() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
                this.startFallFlying();
                return true;
            }
        }

        return false;
    }

    public void startFallFlying() {
        this.setFlag(7, true);
    }

    public void stopFallFlying() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }

    protected void onSwimmingStart() {
        if (!this.isSpectator()) {
            super.onSwimmingStart();
        }

    }

    public FallSounds getFallSounds() {
        return new FallSounds(SoundEvents.ENTITY_PLAYER_SMALL_FALL, SoundEvents.ENTITY_PLAYER_BIG_FALL);
    }

    public void onKilledOther(ServerWorld world, LivingEntity other) {
        this.incrementStat(Stats.KILLED.getOrCreateStat(other.getType()));
    }

    public void slowMovement(BlockState state, Vec3d multiplier) {
        if (!this.abilities.flying) {
            super.slowMovement(state, multiplier);
        }

    }

    public void addExperience(int experience) {
        this.addScore(experience);
        this.experienceProgress += (float)experience / (float)this.getNextLevelExperience();
        this.totalExperience = MathHelper.clamp(this.totalExperience + experience, 0, 2147483647);

        while(this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float)this.getNextLevelExperience();
            if (this.experienceLevel > 0) {
                this.addExperienceLevels(-1);
                this.experienceProgress = 1.0F + f / (float)this.getNextLevelExperience();
            } else {
                this.addExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while(this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getNextLevelExperience();
            this.addExperienceLevels(1);
            this.experienceProgress /= (float)this.getNextLevelExperience();
        }

    }

    public int getEnchantmentTableSeed() {
        return this.enchantmentTableSeed;
    }

    public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
        this.experienceLevel -= experienceLevels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentTableSeed = this.random.nextInt();
    }

    public void addExperienceLevels(int levels) {
        this.experienceLevel += levels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (levels > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.age - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
            this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
            this.lastPlayedLevelUpSoundTime = this.age;
        }

    }

    public int getNextLevelExperience() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        } else {
            return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
        }
    }

    public void addExhaustion(float exhaustion) {
        if (!this.abilities.invulnerable) {
            if (!this.world.isClient) {
                this.hungerManager.addExhaustion(exhaustion);
            }

        }
    }

    public HungerManager getHungerManager() {
        return this.hungerManager;
    }

    public boolean canConsume(boolean ignoreHunger) {
        return this.abilities.invulnerable || ignoreHunger || this.hungerManager.isNotFull();
    }

    public boolean canFoodHeal() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean canModifyBlocks() {
        return this.abilities.allowModifyWorld;
    }

    public boolean canPlaceOn(BlockPos pos, Direction facing, ItemStack stack) {
        if (this.abilities.allowModifyWorld) {
            return true;
        } else {
            BlockPos blockPos = pos.offset(facing.getOpposite());
            CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos, false);
            return stack.canPlaceOn(this.world.getTagManager(), cachedBlockPosition);
        }
    }

    protected int getXpToDrop(PlayerEntity player) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    protected boolean shouldAlwaysDropXp() {
        return true;
    }

    public boolean shouldRenderName() {
        return true;
    }

    protected MoveEffect getMoveEffect() {
        return this.abilities.flying || this.onGround && this.isSneaky() ? MoveEffect.NONE : MoveEffect.ALL;
    }

    public void sendAbilitiesUpdate() {
    }

    public Text getName() {
        return new LiteralText(this.gameProfile.getName());
    }

    public EnderChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }

    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.inventory.getMainHandStack();
        } else if (slot == EquipmentSlot.OFFHAND) {
            return (ItemStack)this.inventory.offHand.get(0);
        } else {
            return slot.getType() == Type.ARMOR ? (ItemStack)this.inventory.armor.get(slot.getEntitySlotId()) : ItemStack.EMPTY;
        }
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        this.processEquippedStack(stack);
        if (slot == EquipmentSlot.MAINHAND) {
            this.onEquipStack(stack);
            this.inventory.main.set(this.inventory.selectedSlot, stack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            this.onEquipStack(stack);
            this.inventory.offHand.set(0, stack);
        } else if (slot.getType() == Type.ARMOR) {
            this.onEquipStack(stack);
            this.inventory.armor.set(slot.getEntitySlotId(), stack);
        }

    }

    public boolean giveItemStack(ItemStack stack) {
        this.onEquipStack(stack);
        return this.inventory.insertStack(stack);
    }

    public Iterable<ItemStack> getItemsHand() {
        return Lists.newArrayList(new ItemStack[]{this.getMainHandStack(), this.getOffHandStack()});
    }

    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean addShoulderEntity(NbtCompound entityNbt) {
        if (!this.hasVehicle() && this.onGround && !this.isTouchingWater() && !this.inPowderSnow) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(entityNbt);
                this.shoulderEntityAddedTime = this.world.getTime();
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(entityNbt);
                this.shoulderEntityAddedTime = this.world.getTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void dropShoulderEntities() {
        if (this.shoulderEntityAddedTime + 20L < this.world.getTime()) {
            this.dropShoulderEntity(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NbtCompound());
            this.dropShoulderEntity(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NbtCompound());
        }

    }

    private void dropShoulderEntity(NbtCompound entityNbt) {
        if (!this.world.isClient && !entityNbt.isEmpty()) {
            EntityType.getEntityFromNbt(entityNbt, this.world).ifPresent((entity) -> {
                if (entity instanceof TameableEntity) {
                    ((TameableEntity)entity).setOwnerUuid(this.uuid);
                }

                entity.setPosition(this.getX(), this.getY() + 0.699999988079071D, this.getZ());
                ((ServerWorld)this.world).tryLoadEntity(entity);
            });
        }

    }

    public abstract boolean isSpectator();

    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    public boolean isPushedByFluids() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    public Text getDisplayName() {
        MutableText mutableText = Team.decorateName(this.getScoreboardTeam(), this.getName());
        return this.addTellClickEvent(mutableText);
    }

    private MutableText addTellClickEvent(MutableText component) {
        String string = this.getGameProfile().getName();
        return component.styled((style) -> {
            return style.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/tell " + string + " ")).withHoverEvent(this.getHoverEvent()).withInsertion(string);
        });
    }

    public String getEntityName() {
        return this.getGameProfile().getName();
    }

    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        switch(pose) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case CROUCHING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0F) {
            amount = 0.0F;
        }

        this.getDataTracker().set(ABSORPTION_AMOUNT, amount);
    }

    public float getAbsorptionAmount() {
        return (Float)this.getDataTracker().get(ABSORPTION_AMOUNT);
    }

    public static UUID getUuidFromProfile(GameProfile profile) {
        UUID uUID = profile.getId();
        if (uUID == null) {
            uUID = getOfflinePlayerUuid(profile.getName());
        }

        return uUID;
    }

    public static UUID getOfflinePlayerUuid(String nickname) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
    }

    public boolean isPartVisible(PlayerModelPart modelPart) {
        return ((Byte)this.getDataTracker().get(PLAYER_MODEL_PARTS) & modelPart.getBitFlag()) == modelPart.getBitFlag();
    }

    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex >= 0 && mappedIndex < this.inventory.main.size()) {
            return StackReference.of(this.inventory, mappedIndex);
        } else {
            int i = mappedIndex - 200;
            return i >= 0 && i < this.enderChestInventory.size() ? StackReference.of(this.enderChestInventory, i) : super.getStackReference(mappedIndex);
        }
    }

    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public void setFireTicks(int ticks) {
        super.setFireTicks(this.abilities.invulnerable ? Math.min(ticks, 1) : ticks);
    }

    public Arm getMainArm() {
        return (Byte)this.dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
    }

    public void setMainArm(Arm arm) {
        this.dataTracker.set(MAIN_ARM, (byte)(arm == Arm.LEFT ? 0 : 1));
    }

    public NbtCompound getShoulderEntityLeft() {
        return (NbtCompound)this.dataTracker.get(LEFT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityLeft(NbtCompound entityNbt) {
        this.dataTracker.set(LEFT_SHOULDER_ENTITY, entityNbt);
    }

    public NbtCompound getShoulderEntityRight() {
        return (NbtCompound)this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityRight(NbtCompound entityNbt) {
        this.dataTracker.set(RIGHT_SHOULDER_ENTITY, entityNbt);
    }

    public float getAttackCooldownProgressPerTick() {
        return (float)(1.0D / this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0D);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public void resetLastAttackedTicks() {
        this.lastAttackedTicks = 0;
    }

    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    protected float getVelocityMultiplier() {
        return !this.abilities.flying && !this.isFallFlying() ? super.getVelocityMultiplier() : 1.0F;
    }

    public float getLuck() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_LUCK);
    }

    public boolean isCreativeLevelTwoOp() {
        return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
    }

    public boolean canEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty();
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        return (EntityDimensions)POSE_DIMENSIONS.getOrDefault(pose, STANDING_DIMENSIONS);
    }

    public ImmutableList<EntityPose> getPoses() {
        return ImmutableList.of(EntityPose.STANDING, EntityPose.CROUCHING, EntityPose.SWIMMING);
    }

    public ItemStack getArrowType(ItemStack stack) {
        if (!(stack.getItem() instanceof RangedWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((RangedWeaponItem)stack.getItem()).getHeldProjectiles();
            ItemStack itemStack = RangedWeaponItem.getHeldProjectile(this, predicate);
            if (!itemStack.isEmpty()) {
                return itemStack;
            } else {
                predicate = ((RangedWeaponItem)stack.getItem()).getProjectiles();

                for(int i = 0; i < this.inventory.size(); ++i) {
                    ItemStack itemStack2 = this.inventory.getStack(i);
                    if (predicate.test(itemStack2)) {
                        return itemStack2;
                    }
                }

                return this.abilities.creativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack eatFood(World world, ItemStack stack) {
        this.getHungerManager().eat(stack.getItem(), stack);
        this.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        if (this instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)this, stack);
        }

        return super.eatFood(world, stack);
    }

    protected boolean shouldRemoveSoulSpeedBoost(BlockState landingState) {
        return this.abilities.flying || super.shouldRemoveSoulSpeedBoost(landingState);
    }

    public Vec3d getLeashPos(float delta) {
        double d = 0.22D * (this.getMainArm() == Arm.RIGHT ? -1.0D : 1.0D);
        float f = MathHelper.lerp(delta * 0.5F, this.getPitch(), this.prevPitch) * 0.017453292F;
        float g = MathHelper.lerp(delta, this.prevBodyYaw, this.bodyYaw) * 0.017453292F;
        double e;
        if (!this.isFallFlying() && !this.isUsingRiptide()) {
            if (this.isInSwimmingPose()) {
                return this.getLerpedPos(delta).add((new Vec3d(d, 0.2D, -0.15D)).rotateX(-f).rotateY(-g));
            } else {
                double vec3d = this.getBoundingBox().getYLength() - 1.0D;
                e = this.isInSneakingPose() ? -0.2D : 0.07D;
                return this.getLerpedPos(delta).add((new Vec3d(d, vec3d, e)).rotateY(-g));
            }
        } else {
            Vec3d vec3d = this.getRotationVec(delta);
            Vec3d vec3d2 = this.getVelocity();
            e = vec3d2.horizontalLengthSquared();
            double h = vec3d.horizontalLengthSquared();
            float k;
            if (e > 0.0D && h > 0.0D) {
                double i = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(e * h);
                double j = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                k = (float)(Math.signum(j) * Math.acos(i));
            } else {
                k = 0.0F;
            }

            return this.getLerpedPos(delta).add((new Vec3d(d, -0.11D, 0.85D)).rotateZ(-k).rotateX(-f).rotateY(-g));
        }
    }

    public boolean isPlayer() {
        return true;
    }

    public boolean isUsingSpyglass() {
        return this.isUsingItem() && this.getActiveItem().isOf(Items.SPYGLASS);
    }

    public boolean shouldSave() {
        return false;
    }

    static {
        POSE_DIMENSIONS = ImmutableMap.builder().put(EntityPose.STANDING, STANDING_DIMENSIONS).put(EntityPose.SLEEPING, SLEEPING_DIMENSIONS).put(EntityPose.FALL_FLYING, EntityDimensions.changing(0.6F, 0.6F)).put(EntityPose.SWIMMING, EntityDimensions.changing(0.6F, 0.6F)).put(EntityPose.SPIN_ATTACK, EntityDimensions.changing(0.6F, 0.6F)).put(EntityPose.CROUCHING, EntityDimensions.changing(0.6F, 1.5F)).put(EntityPose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();
        ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PLAYER_MODEL_PARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
        MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
        LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
        RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    }

    public static enum SleepFailureReason {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(new TranslatableText("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(new TranslatableText("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(new TranslatableText("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(new TranslatableText("block.minecraft.bed.not_safe"));

        @Nullable
        private final Text message;

        private SleepFailureReason() {
            this.message = null;
        }

        private SleepFailureReason(Text message) {
            this.message = message;
        }

        @Nullable
        public Text getMessage() {
            return this.message;
        }
    }
}
