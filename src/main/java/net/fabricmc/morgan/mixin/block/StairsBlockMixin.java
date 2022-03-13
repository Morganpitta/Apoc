package net.fabricmc.morgan.mixin.block;

import net.fabricmc.morgan.accessors.block.PublicStairsBlock;
import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(StairsBlock.class)
public abstract class StairsBlockMixin extends Block {
    @Shadow @Final public static DirectionProperty FACING;

    @Shadow @Final public static EnumProperty<BlockHalf> HALF;

    @Shadow @Final public static EnumProperty<StairShape> SHAPE;

    @Shadow @Final public static BooleanProperty WATERLOGGED;

    public StairsBlockMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author Morgan
     * @reason reverse stair placement
     */
    @Overwrite
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        Direction playerFacing = ctx.getPlayerFacing();
        if (((EntityExtension)ctx.getPlayer()).upsideDownGravity()){
            playerFacing = playerFacing.getOpposite();
        }
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState blockState = (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, playerFacing)).with(HALF, direction == Direction.DOWN || direction != Direction.UP && ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return (BlockState)blockState.with(SHAPE, PublicStairsBlock.getStairShape(blockState, ctx.getWorld(), blockPos));
    }

}
