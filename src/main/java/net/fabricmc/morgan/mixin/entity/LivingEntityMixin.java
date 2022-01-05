package net.fabricmc.morgan.mixin.entity;

import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtension {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow abstract protected int computeFallDamage(float fallDistance, float damageMultiplier);

    @Shadow abstract public SoundEvent getFallSound(int distance);

    @Shadow abstract protected void playBlockFallSound();


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

}
