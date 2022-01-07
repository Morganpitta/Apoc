package net.fabricmc.morgan.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

import java.util.Random;

public class WitheredBlock extends Block {
    public WitheredBlock() {
        super(AbstractBlock.Settings.of(Material.GOURD).strength(1.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).ticksRandomly());
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        player.damage(DamageSource.MAGIC, (float) Math.ceil(player.getHealth()/2));
    }


    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        entity.damage(DamageSource.MAGIC,0.5f);

    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        grow(world,random,pos,state);
    }


    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if(!world.getBlockState(pos.up()).isAir()){
            world.setBlockState(pos.up(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
        if(!world.getBlockState(pos.down()).isAir()){
            world.setBlockState(pos.down(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
        if(!world.getBlockState(pos.south()).isAir()){
            world.setBlockState(pos.south(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
        if(!world.getBlockState(pos.north()).isAir()){
            world.setBlockState(pos.north(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
        if(!world.getBlockState(pos.west()).isAir()){
            world.setBlockState(pos.west(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
        if(!world.getBlockState(pos.east()).isAir()){
            world.setBlockState(pos.east(), Blocks.WITHERED_BLOCK.getDefaultState());
        }
    }

}