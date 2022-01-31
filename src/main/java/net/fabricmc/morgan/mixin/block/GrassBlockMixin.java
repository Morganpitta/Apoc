package net.fabricmc.morgan.mixin.block;

import net.fabricmc.morgan.item.MorganItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GrassBlock.class)
public abstract class GrassBlockMixin extends SpreadableBlock implements Fertilizable {
    protected GrassBlockMixin(Settings settings) {
        super(settings);
    }

    public void onSteppedOnIgnoringCrouching(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)&&!((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).isOf(MorganItems.WET_SHOES)) {
            entity.damage(DamageSource.IN_FIRE, 1.0F);
            entity.setOnFireFor(1);

        }
    }
}
