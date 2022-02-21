package net.fabricmc.morgan.mixin.block;

import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CandleCakeBlock.class)
public abstract class CandleCakeBlockMixin extends AbstractCandleBlock {
    protected CandleCakeBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.createExplosion(player,pos.getX(),pos.getY(),pos.getZ(),2.0F, Explosion.DestructionType.BREAK);
    }
}
