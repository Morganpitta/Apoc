package net.fabricmc.morgan.mixin.entity.player;

import net.fabricmc.morgan.entity.player.PlayerInventoryExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable, PlayerInventoryExtension {

    @Shadow public abstract int getEmptySlot();
    @Shadow public PlayerEntity player;
    @Shadow public abstract int addStack(ItemStack stack);
    @Shadow public abstract int addStack(int slot, ItemStack stack);
    @Shadow public abstract int getOccupiedSlotWithRoomForStack(ItemStack stack);

    public int canAddStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.canAddStack(i, stack);
    }

    public int canAddStack(int slot, ItemStack stack) {
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);


        int j = i;
        if (i > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }


        if (j == 0) {
            return i;
        } else {
            i -= j;
            return i;
        }
    }


    public boolean canInsertStack(ItemStack stack) {
        return this.canInsertStack(-1, stack);
    }

    public boolean canInsertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) {
                        slot = this.getEmptySlot();
                    }

                    if (slot >= 0) {
                        return true;
                    } else if (this.player.getAbilities().creativeMode) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int x = stack.getCount();
                    int i;
                    do {
                        i = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(this.canAddStack(stack));
                        } else {
                            stack.setCount(this.canAddStack(slot, stack));
                        }
                    } while (!stack.isEmpty() && stack.getCount() < i);

                    if (stack.getCount() == i && this.player.getAbilities().creativeMode) {
                        stack.setCount(x);
                        return true;
                    } else {
                        boolean bl = stack.getCount() < i;
                        stack.setCount(x);
                        return bl;
                    }
                }
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
                CrashReportSection crashReportSection = crashReport.addElement("Item being added");
                crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
                crashReportSection.add("Item data", stack.getDamage());
                crashReportSection.add("Item name", () -> {
                    return stack.getName().getString();
                });
                throw new CrashException(crashReport);
            }
        }
    }
}
