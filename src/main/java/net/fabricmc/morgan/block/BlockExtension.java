package net.fabricmc.morgan.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockExtension {
     void onSteppedOnIgnoringCrouching(World world, BlockPos pos, BlockState state, Entity entity);
}
