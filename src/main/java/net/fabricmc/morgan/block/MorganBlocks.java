package net.fabricmc.morgan.block;

import net.fabricmc.morgan.fluid.MorganFluids;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;


public class MorganBlocks{
    public static final Block CORRUPTED_BLOCK;
    public static final Block MEAT_BLOCK;
    public static final Block CORRUPTED_FLUID;
    public static final Block CHLORINE_GAS;
    public static final Block SUPER_ICE;

    public MorganBlocks() {
    }

    private static Block register(String id, Block block) {
        return (Block)Registry.register(Registry.BLOCK, new Identifier("morgan", id), block);
    }

    static {
        CORRUPTED_FLUID = register("corrupted_fluid", new FluidBlock(MorganFluids.CORRUPTED_FLUID, AbstractBlock.Settings.of(Material.AMETHYST).noCollision().strength(100.0F).dropsNothing().nonOpaque()) {});
        CORRUPTED_BLOCK = register("corrupted_block", new CorruptedBlock());
        MEAT_BLOCK = register("meat_block", new MeatBlock());
        CHLORINE_GAS = register("chlorine_gas",new ChlorineGas());
        SUPER_ICE = register("super_ice",new SuperIceBlock(AbstractBlock.Settings.of(Material.ICE).slipperiness((100F/91F)).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque()));
        Iterator var0 = Registry.BLOCK.iterator();
    }

    public static void init(){

    }
}