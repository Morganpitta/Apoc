package net.fabricmc.morgan.mixin.entity.passive;

import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements EntityExtension{
    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean isBouncy=true;
    /**
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        int i = this.computeFallDamage(fallDistance, damageMultiplier);
        Vec3d velocity = this.getVelocity();
        if (i > 0 && velocity.y < 0.0D) {
            this.setVelocity(velocity.x, -velocity.y, velocity.z);
        }  return false;
    }
    **/


}
