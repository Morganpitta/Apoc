package net.fabricmc.morgan.mixin.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(EggEntity.class)
public abstract class EggEntityMixin extends ThrownItemEntity {

    public EggEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author Morgan
     * @reason explosive eggs
     */
    @Overwrite
    public void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.world.createExplosion(this,this.getX(),this.getY(),this.getZ(),10, Explosion.DestructionType.DESTROY);
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    ChickenEntity chickenEntity = (ChickenEntity)EntityType.CHICKEN.create(this.world);
                    chickenEntity.setBreedingAge(-24000);
                    chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                    this.world.spawnEntity(chickenEntity);
                }
            }

            this.world.sendEntityStatus(this, (byte)3);
            this.discard();
        }

    }
}
