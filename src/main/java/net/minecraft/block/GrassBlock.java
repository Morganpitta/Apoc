//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

public class GrassBlock extends SpreadableBlock implements Fertilizable {
    public GrassBlock(Settings settings) {
        super(settings);
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(DamageSource.IN_FIRE, 1.0F);
            entity.setOnFireFor(1);

        }

        super.onSteppedOn(world, pos, state, entity);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = Blocks.GRASS.getDefaultState();

        label46:
        for(int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;

            for(int j = 0; j < i / 16; ++j) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
                    continue label46;
                }
            }

            BlockState j = world.getBlockState(blockPos2);
            if (j.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable)blockState.getBlock()).grow(world, random, blockPos2, j);
            }

            if (j.isAir()) {
                PlacedFeature placedFeature;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) {
                        continue;
                    }

                    placedFeature = (PlacedFeature)((RandomPatchFeatureConfig)((ConfiguredFeature)list.get(0)).getConfig()).feature().get();
                } else {
                    placedFeature = VegetationPlacedFeatures.GRASS_BONEMEAL;
                }

                placedFeature.generateUnregistered(world, world.getChunkManager().getChunkGenerator(), random, blockPos2);
            }
        }

    }
}
