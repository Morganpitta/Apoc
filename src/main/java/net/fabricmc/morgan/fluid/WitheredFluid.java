package net.fabricmc.morgan.fluid;

import net.fabricmc.morgan.block.MorganBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public abstract class WitheredFluid extends FlowableFluid {
    public WitheredFluid() {
    }

    protected boolean hasRandomTicks() {
        return true;
    }

    public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        if(!(world.getBlockState(pos.up()).isAir()||world.getBlockState(pos.up()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.up()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.up(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.up(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.down()).isAir()||world.getBlockState(pos.down()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.down()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.down(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.down(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.south()).isAir()||world.getBlockState(pos.south()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.south()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.south(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.south(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.north()).isAir()||world.getBlockState(pos.north()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.north()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.north(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.north(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.west()).isAir()||world.getBlockState(pos.west()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.west()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.west(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.west(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.east()).isAir()||world.getBlockState(pos.east()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.east()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.east(), MorganBlocks.WITHERED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.east(), MorganBlocks.WITHERED_BLOCK.getDefaultState());
            }
        }
    }

    @Override
    public Fluid getFlowing() {
        return MorganFluids.FLOWING_WITHERED_FLUID;
    }

    @Override
    public Fluid getStill() {
        return MorganFluids.WITHERED_FLUID;
    }

    @Override
    public Item getBucketItem() {
        return null;
    }

    @Override
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
    }

    @Nullable
    public ParticleEffect getParticle() {
        return null;
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    public int getFlowSpeed(WorldView world) {
        return 4;
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return (BlockState) MorganBlocks.WITHERED_FLUID.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == MorganFluids.WITHERED_FLUID || fluid == MorganFluids.FLOWING_WITHERED_FLUID;
    }

    @Override
    public int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        //return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
        return false;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }


    public static class Flowing extends WitheredFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(new Property[]{LEVEL});
        }

        @Override
        public int getLevel(FluidState state) {
            return (Integer)state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends WitheredFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
