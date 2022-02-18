package net.fabricmc.morgan.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.state.property.IntProperty;

import java.util.Random;

public class ChlorineGas extends Block {
    public static IntProperty CONCENTRATION=IntProperty.of("concentration",0,255);

    public ChlorineGas() {
        super(AbstractBlock.Settings.of(Material.AIR).noCollision());
        this.setDefaultState(getStateManager().getDefaultState().with(CONCENTRATION, 255));
    }

    public boolean canMobSpawnInside() {
        return true;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.createAndScheduleBlockTick(pos, this, 20);
    }

    public void spread(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int concentration = world.getBlockState(pos).get(CONCENTRATION);
        if(world.getBlockState(pos.up()).isAir()){
            world.setBlockState(pos.up(), state.with(CONCENTRATION,world.getBlockState(pos).get(CONCENTRATION)));
        }
        if(world.getBlockState(pos.down()).isAir()){
            world.setBlockState(pos.down(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
        }
        if(world.getBlockState(pos.south()).isAir()){
            world.setBlockState(pos.south(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
        }
        if(world.getBlockState(pos.north()).isAir()){
            world.setBlockState(pos.north(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
        }
        if(world.getBlockState(pos.west()).isAir()){
            world.setBlockState(pos.west(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
        }
        if(world.getBlockState(pos.east()).isAir()){
            world.setBlockState(pos.east(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
        }
    }

    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONCENTRATION);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.createAndScheduleBlockTick(pos, this, 20);
    }
}
