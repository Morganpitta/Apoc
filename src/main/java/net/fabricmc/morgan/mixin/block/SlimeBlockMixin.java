package net.fabricmc.morgan.mixin.block;

import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.world.entity.Bounciness;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin extends TransparentBlock {
    protected SlimeBlockMixin(Settings settings) {
        super(settings);
    }


    /**
     * @author Morgan
     * @reason make slime blocks bouncier
     */
    @Overwrite
    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0D) {
            double d = entity instanceof LivingEntity ? 1.0D : 0.8D;
            double e=1;
            if (!entity.world.isClient()) {
                e = ((EntityExtension) entity).getBouncy() ? Bounciness.Bounciness : 0;
                e++;
            }
            entity.setVelocity(vec3d.x, -vec3d.y * d*e, vec3d.z);
        }

    }

}