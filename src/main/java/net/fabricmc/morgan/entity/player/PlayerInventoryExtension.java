package net.fabricmc.morgan.entity.player;

import net.minecraft.item.ItemStack;

public interface PlayerInventoryExtension {

    int canAddStack(ItemStack stack);

    int canAddStack(int slot, ItemStack stack);

    boolean canInsertStack(ItemStack stack);

    boolean canInsertStack(int slot, ItemStack stack);

    void getWeight();
}
