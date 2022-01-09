package net.fabricmc.morgan.item;

import net.fabricmc.morgan.block.MeatBlock;
import net.fabricmc.morgan.block.MorganBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class MorganItems {
    public static final Item MEAT_BLOCK;

    public MorganItems() {
    }

    public static void init(){

    }

    private static <T> Optional<T> createEmptyOptional(T of) {
        return Optional.empty();
    }

    private static Item register(Block block) {
        return register(new BlockItem(block, new Item.Settings()));
    }

    private static Item register(Block block, ItemGroup group) {
        return register(new BlockItem(block, (new Item.Settings()).group(group)));
    }

    private static Item register(Block block, Optional<ItemGroup> group) {
        return (Item)group.map((groupx) -> {
            return register(block, groupx);
        }).orElseGet(() -> {
            return register(block);
        });
    }

    private static Item register(Block block, ItemGroup group, Block... blocks) {
        BlockItem blockItem = new BlockItem(block, (new Item.Settings()).group(group));
        Block[] var4 = blocks;
        int var5 = blocks.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Block block2 = var4[var6];
            Item.BLOCK_ITEMS.put(block2, blockItem);
        }

        return register(blockItem);
    }

    private static Item register(BlockItem item) {
        return register((Block)item.getBlock(), (Item)item);
    }

    protected static Item register(Block block, Item item) {
        return register(Registry.BLOCK.getId(block), item);
    }

    private static Item register(String id, Item item) {
        return register(new Identifier(id), item);
    }

    private static Item register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return (Item)Registry.register(Registry.ITEM, id, item);
    }

    static {
        MEAT_BLOCK = register(MorganBlocks.MEAT_BLOCK, ItemGroup.FOOD);
    }
}
