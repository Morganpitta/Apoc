//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.fabricmc.morgan.mixin.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
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
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
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
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension, EntityExtension {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    public int SleepSheep = 0;
    public boolean CanJump=true;
    public boolean isBlind=true;
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
    public int Goats = 0;
    @Shadow protected HungerManager hungerManager = new HungerManager();
    @Shadow private final PlayerAbilities abilities = new PlayerAbilities();
    @Shadow public void incrementStat(Identifier stat) {this.incrementStat(Stats.CUSTOM.getOrCreateStat(stat));}
    @Shadow  public void incrementStat(Stat<?> stat) {
        this.increaseStat((Stat)stat, 1);
    }
    @Shadow public void increaseStat(Stat<?> stat, int amount) {}
    @Shadow public abstract void addExhaustion(float exhaustion) ;

    @Inject(method = "writeCustomDataToNbt",at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt,CallbackInfo info) {
        nbt.putInt("SleepSheep", this.SleepSheep);
    }
    @Inject(method = "readCustomDataFromNbt",at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt,CallbackInfo info) {
        this.SleepSheep = nbt.getInt("SleepSheep");
    }

    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this.getEntityName()=="Zenxuss"&&!this.getBlind()) {
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
