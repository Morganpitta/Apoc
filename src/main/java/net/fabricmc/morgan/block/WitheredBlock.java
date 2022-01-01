package net.fabricmc.morgan.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

import java.util.Random;

public class WitheredBlock extends Block implements Fertilizable {
    public WitheredBlock(Settings settings) {
        super(settings);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL.generate(world, world.getChunkManager().getChunkGenerator(), random, pos.up());
    }

}