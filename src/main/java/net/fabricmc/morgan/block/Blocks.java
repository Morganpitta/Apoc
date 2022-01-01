package net.fabricmc.morgan.block;

import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;


public class Blocks {
    public static final Block WITHERED_BLOCK;

    public Blocks() {
    }

    private static Block register(String id, Block block) {
        return (Block)Registry.register(Registry.BLOCK, new Identifier("morgan", id), block);
    }

    static {
        WITHERED_BLOCK = register("withered_block", new WitheredBlock(AbstractBlock.Settings.of(Material.MOSS_BLOCK, MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_BLOCK)));
        Iterator var0 = Registry.BLOCK.iterator();
    }

    public static void init(){

    }
}