package net.fabricmc.morgan.mixin.block;

import net.fabricmc.morgan.block.BlockExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockExtension {
    public void onSteppedOnIgnoringCrouching(World world, BlockPos pos, BlockState state, Entity entity) {
    }
}
