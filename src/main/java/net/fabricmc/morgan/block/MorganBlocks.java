package net.fabricmc.morgan.block;

import net.fabricmc.morgan.fluid.MorganFluids;
import net.fabricmc.morgan.mixin.block.FluidBlockMixin;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;


public class MorganBlocks{
    public static final Block WITHERED_BLOCK;
    public static final Block MEAT_BLOCK;
    public static final Block WITHERED_FLUID;

    public MorganBlocks() {
    }

    private static Block register(String id, Block block) {
        return (Block)Registry.register(Registry.BLOCK, new Identifier("morgan", id), block);
    }

    static {
        WITHERED_FLUID = register("withered_fluid", new FluidBlock(MorganFluids.WITHERED_FLUID, AbstractBlock.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing()) {});
        WITHERED_BLOCK = register("withered_block", new WitheredBlock());
        MEAT_BLOCK = register("meat_block", new MeatBlock());
        Iterator var0 = Registry.BLOCK.iterator();
    }

    public static void init(){

    }
}