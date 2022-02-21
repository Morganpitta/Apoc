package net.fabricmc.morgan.mixin.entity.player;

import com.google.common.collect.Lists;
import net.fabricmc.morgan.entity.player.PlayerInventoryExtension;
import net.fabricmc.morgan.tag.MorganItemTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable, PlayerInventoryExtension {

    @Shadow public static final int ITEM_USAGE_COOLDOWN = 5;
    @Shadow public static final int MAIN_SIZE = 36;
    @Shadow private static final int HOTBAR_SIZE = 9;
    @Shadow public static final int OFF_HAND_SLOT = 40;
    @Shadow public static final int NOT_FOUND = -1;
    @Shadow public static final int[] ARMOR_SLOTS = new int[]{0, 1, 2, 3};
    @Shadow public static final int[] HELMET_SLOTS = new int[]{3};
    @Shadow public DefaultedList<ItemStack> main;
    @Shadow public DefaultedList<ItemStack> armor;
    @Shadow public  DefaultedList<ItemStack> offHand;
    @Shadow private List<DefaultedList<ItemStack>> combinedInventory;
    @Shadow public int selectedSlot;
    @Shadow public PlayerEntity player;
    @Shadow private int changeCount;
    protected Random random;

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void init(PlayerEntity player, CallbackInfo ci) {
        this.random = new Random();
    }

    @Shadow public abstract int getEmptySlot();
    @Shadow public abstract int addStack(ItemStack stack);
    @Shadow public abstract int addStack(int slot, ItemStack stack);
    @Shadow public abstract int getOccupiedSlotWithRoomForStack(ItemStack stack);

    @Shadow public abstract ItemStack removeStack(int slot);

    /**
    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.canCombine(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), stack)) {
            return this.selectedSlot;
        } else if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        } else {
            for(int i = 0; i < this.main.size(); ++i) {
                if (this.canStackAddMore((ItemStack)this.main.get(i), stack)) {
                    return i;
                }
            }

            return -1;
        }
    }
     *
     * @return*/

    public void dropRandomUsedSlot(){
            List<Integer> list = Lists.newArrayList();
            for(int slot = 0; slot < this.size(); ++slot) {
                ItemStack itemStack = this.getStack(slot);
                if (!itemStack.isEmpty()) {
                    list.add(slot);
                }
            }
            if (!(list.size() == 0)) {
                int slot = list.get(random.nextInt(list.size()));
                ItemEntity entity = this.player.dropItem(this.getStack(slot), true, false);
                if (entity != null) {
                    entity.setPickupDelay(100);
                }
                this.removeStack(slot);
            }

        //this.combinedInventory.get(random.nextInt(this.combinedInventory.size()));
    }

    public float getWeight(){
        float weight=0;
        //main inventory
        for(int i = 0; i < this.main.size(); ++i) {
            if(!this.main.get(i).isEmpty()) {
                //ExampleMod.LOGGER.info((ItemStack) this.main.get(i));
                weight+=this.main.get(i).getCount()*getWeightOfItem(this.main.get(i));
            }
        }
        //armor
        for(int i = 0; i < this.armor.size(); ++i) {
            if(!this.armor.get(i).isEmpty()) {
                //ExampleMod.LOGGER.info((ItemStack) this.armor.get(i));
                weight+=this.armor.get(i).getCount()*getWeightOfItem(this.armor.get(i));
            }
        }
        //offhand
        if(!this.offHand.get(0).isEmpty()) {
            //ExampleMod.LOGGER.info((ItemStack) this.offHand.get(0));
            weight+=this.offHand.get(0).getCount()*getWeightOfItem(this.offHand.get(0));
        }
        //ExampleMod.LOGGER.info(weight);
        return weight;
    }

    private float getWeightOfItem(ItemStack itemStack) {
        if(itemStack.isIn(MorganItemTags.WEIGHTLESS)){
            return 0;
        }
        else {
            return 1;
        }
    }

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
