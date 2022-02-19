package net.fabricmc.morgan.mixin.client.network;

import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.entity.player.PlayerInventoryExtension;
import net.fabricmc.morgan.item.ItemExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.mixin.entity.player.PlayerEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {


    protected ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public int ticksSinceSprintingChanged;
    @Shadow protected int ticksLeftToDoubleTapSprint;

    @Shadow public abstract void updateNausea();
    @Shadow public Input input;
    @Shadow public MinecraftClient client;
    @Shadow public abstract boolean isWalking();
    @Shadow public boolean inSneakingPose;
    @Shadow public abstract boolean shouldSlowDown();
    @Shadow private int ticksToNextAutojump;
    @Shadow public abstract void pushOutOfBlocks(double x, double z);
    @Shadow public ClientPlayNetworkHandler networkHandler;
    @Shadow public boolean falling;
    @Shadow private int underwaterVisibilityTicks;
    @Shadow protected abstract boolean isCamera();
    @Shadow public abstract boolean hasJumpingMount();
    @Shadow private int field_3938;
    @Shadow public float mountJumpStrength;
    @Shadow public abstract float getMountJumpStrength();
    @Shadow protected abstract void startRidingJump();
    @Shadow public float renderYaw;
    @Shadow public float renderPitch;
    @Shadow public float lastRenderYaw;
    @Shadow public float lastRenderPitch;

    public boolean hasBeenBlinded = false;
    public int defaultViewDistance = 10;
    public float defaultEntityDistanceScaling=1F;

    /**
     * @author Morgan
     */
    @Overwrite
    public void tickNewAi() {
        super.tickNewAi();
        if (this.isCamera()) {
            this.jumping = this.input.jumping;
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            if (this.hasStatusEffect(StatusEffects.NAUSEA)) {
                this.sidewaysSpeed = -this.input.movementSideways;
                this.forwardSpeed = -this.input.movementForward;
                this.renderPitch = (float) ((double) this.renderPitch + (double) (this.getPitch() - this.renderPitch) * 0.5D);
                this.renderYaw = (float) ((double) this.renderYaw + (double) (this.getYaw() - this.renderYaw) * 0.5D);
            }
            else {
                this.sidewaysSpeed = this.input.movementSideways;
                this.forwardSpeed = this.input.movementForward;
                this.renderPitch = (float) ((double) this.renderPitch + (double) (this.getPitch() - this.renderPitch) * 0.5D);
                this.renderYaw = (float) ((double) this.renderYaw + (double) (this.getYaw() - this.renderYaw) * 0.5D);
            }

        }
    }


    @Inject(method = "tick",at=@At("HEAD"))
    public void tick(CallbackInfo info) {
        if ((((PlayerEntityExtension) this).getBlind() && (MinecraftClient.getInstance().options.viewDistance > 1 || MinecraftClient.getInstance().options.entityDistanceScaling > 0.1F)) && !((this.getEquippedStack(EquipmentSlot.HEAD)).isOf(MorganItems.GLASSES))) {
            if (!this.hasBeenBlinded) {
                this.defaultViewDistance = MinecraftClient.getInstance().options.viewDistance;
                this.defaultEntityDistanceScaling = MinecraftClient.getInstance().options.entityDistanceScaling;
            }
            this.hasBeenBlinded = true;
            MinecraftClient.getInstance().options.viewDistance = 1;
            MinecraftClient.getInstance().options.entityDistanceScaling = 0.4F;
        } else if (this.hasBeenBlinded && ((this.getEquippedStack(EquipmentSlot.HEAD)).isOf(MorganItems.GLASSES))) {
            this.hasBeenBlinded = false;
            MinecraftClient.getInstance().options.viewDistance = this.defaultViewDistance;
            MinecraftClient.getInstance().options.entityDistanceScaling = this.defaultEntityDistanceScaling;
        }


    }

    @Redirect(method = "tickMovement()V",at = @At(value = "INVOKE",target="Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean onCheckIsUsingItem(ClientPlayerEntity clientPlayerEntity){
        return this.isUsingItem() && ((ItemExtension)this.getStackInHand(this.getActiveHand()).getItem()).slowsDownUser();
    }

    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target="Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V", shift = At.Shift.AFTER,ordinal = 0))
    public void tickMovement(CallbackInfo info) {
        /**
        ++this.ticksSinceSprintingChanged;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }

        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.getAbilities().flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
        this.input.tick(this.shouldSlowDown());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && ((ItemExtension)this.getStackInHand(Hand.MAIN_HAND).getItem()).slowsDownUser() && !this.hasVehicle()) {
            Input var10000 = this.input;
            var10000.movementSideways *= 0.2F;
            var10000 = this.input;
            var10000.movementForward *= 0.2F;
            this.ticksLeftToDoubleTapSprint = 0;
        }
         **/



        //ExampleMod.LOGGER.info("ding suff");
        if (((PlayerEntityExtension)this).getAffectedByWeight()) {
            float weight = ((PlayerInventoryExtension) this.getInventory()).getWeight();
            if (weight < 0 ) {weight *=-1;}
            float speedMultiplier = (float) ((640)*2.3/((weight+64)*2.3));
            Input var10000 = this.input;
            var10000.movementSideways *= speedMultiplier;
            var10000 = this.input;;
            var10000.movementForward *= speedMultiplier;
        }

        /**
        boolean bl4 = false;
        if (this.ticksToNextAutojump > 0) {
            --this.ticksToNextAutojump;
            bl4 = true;
            this.input.jumping = true;
        }

        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35D, this.getZ() + (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35D, this.getZ() - (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35D, this.getZ() - (double)this.getWidth() * 0.35D);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35D, this.getZ() + (double)this.getWidth() * 0.35D);
        }

        if (bl2) {
            this.ticksLeftToDoubleTapSprint = 0;
        }

        boolean bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.getAbilities().allowFlying;
        if ((this.onGround || this.isSubmergedInWater()) && !bl2 && !bl3 && this.isWalking() && !this.isSprinting() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS)) {
            if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.keySprint.isPressed()) {
                this.ticksLeftToDoubleTapSprint = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.isWalking() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }

        boolean bl6;
        if (this.isSprinting()) {
            bl6 = !this.input.hasForwardMovement() || !bl5;
            boolean bl7 = bl6 || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl6 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl7) {
                this.setSprinting(false);
            }
        }

        bl6 = false;
        if (this.getAbilities().allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.getAbilities().flying) {
                    this.getAbilities().flying = true;
                    bl6 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!this.isSwimming()) {
                    this.getAbilities().flying = !this.getAbilities().flying;
                    bl6 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }

        if (this.input.jumping && !bl6 && !bl && !this.getAbilities().flying && !this.hasVehicle() && !this.isClimbing()) {
            ItemStack bl7 = this.getEquippedStack(EquipmentSlot.CHEST);
            if (bl7.isOf(Items.ELYTRA) && ElytraItem.isUsable(bl7) && this.checkFallFlying()) {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }

        this.falling = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking && this.shouldSwimInFluids()) {
            this.knockDownwards();
        }

        int bl7;
        if (this.isSubmergedIn(FluidTags.WATER)) {
            bl7 = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + bl7, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }

        if (this.getAbilities().flying && this.isCamera()) {
            bl7 = 0;
            if (this.input.sneaking) {
                --bl7;
            }

            if (this.input.jumping) {
                ++bl7;
            }

            if (bl7 != 0) {
                this.setVelocity(this.getVelocity().add(0.0D, (double)((float)bl7 * this.getAbilities().getFlySpeed() * 3.0F), 0.0D));
            }
        }

        if (this.hasJumpingMount()) {
            JumpingMount bl8 = (JumpingMount)this.getVehicle();
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.mountJumpStrength = 0.0F;
                }
            }

            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                bl8.setJumpStrength(MathHelper.floor(this.getMountJumpStrength() * 100.0F));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.mountJumpStrength = 0.0F;
            } else if (bl) {
                ++this.field_3938;
                if (this.field_3938 < 10) {
                    this.mountJumpStrength = (float)this.field_3938 * 0.1F;
                } else {
                    this.mountJumpStrength = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
                }
            }
        } else {
            this.mountJumpStrength = 0.0F;
        }

        super.tickMovement();

        if (this.onGround && this.getAbilities().flying && !this.client.interactionManager.isFlyingLocked()) {
            this.getAbilities().flying = false;
            this.sendAbilitiesUpdate();
        }

         **/
    }
}
