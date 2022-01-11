package net.fabricmc.morgan.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;

import net.minecraft.world.World;

public class CorruptedBlock extends Block {
    public CorruptedBlock() {
        super(AbstractBlock.Settings.of(Material.AMETHYST).strength(1.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).ticksRandomly().resistance(1200));
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        player.damage(DamageSource.MAGIC, (float) Math.ceil(player.getHealth()/2));
        player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel()/2);
    }


    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        entity.damage(DamageSource.MAGIC,1f);

    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        grow(world,random,pos,state);
    }


    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if(!(world.getBlockState(pos.up()).isAir()||world.getBlockState(pos.up()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.up()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.up(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.up(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.down()).isAir()||world.getBlockState(pos.down()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.down()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.down(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.down(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.south()).isAir()||world.getBlockState(pos.south()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.south()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.south(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.south(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.north()).isAir()||world.getBlockState(pos.north()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.north()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.north(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.north(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.west()).isAir()||world.getBlockState(pos.west()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.west()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.west(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.west(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
        if(!(world.getBlockState(pos.east()).isAir()||world.getBlockState(pos.east()).isIn(BlockTags.CANTBEWITHERED))){
            if (world.getBlockState(pos.east()).isIn(BlockTags.FLUIDS)) {
                world.setBlockState(pos.east(), MorganBlocks.CORRUPTED_FLUID.getDefaultState());
            }
            else {
                world.setBlockState(pos.east(), MorganBlocks.CORRUPTED_BLOCK.getDefaultState());
            }
        }
    }

}