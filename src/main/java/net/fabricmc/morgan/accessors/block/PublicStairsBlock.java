package net.fabricmc.morgan.accessors.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.state.State;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class PublicStairsBlock extends StairsBlock {
    public PublicStairsBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
    }

    public static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction3;
        Object direction2;
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (StairsBlock.isStairs(blockState) && state.get(HALF) == blockState.get(HALF) && ((Direction)(direction2 = blockState.get(FACING))).getAxis() != state.get(FACING).getAxis() && PublicStairsBlock.isDifferentOrientation(state, world, pos, ((Direction)direction2).getOpposite())) {
            if (direction2 == direction.rotateYCounterclockwise()) {
                return StairShape.OUTER_LEFT;
            }
            return StairShape.OUTER_RIGHT;
        }
        direction2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (StairsBlock.isStairs((BlockState)direction2) && state.get(HALF) == ((State)direction2).get(HALF) && (direction3 = (Direction) ((State)direction2).get(FACING)).getAxis() != state.get(FACING).getAxis() && PublicStairsBlock.isDifferentOrientation(state, world, pos, direction3)) {
            if (direction3 == direction.rotateYCounterclockwise()) {
                return StairShape.INNER_LEFT;
            }
            return StairShape.INNER_RIGHT;
        }
        return StairShape.STRAIGHT;
    }

    public static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !StairsBlock.isStairs(blockState) || blockState.get(FACING) != state.get(FACING) || blockState.get(HALF) != state.get(HALF);
    }
}
