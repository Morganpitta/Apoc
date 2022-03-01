package net.fabricmc.morgan.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackingCompassItem extends Item
        implements Vanishable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String PLAYER = "player";

    public TrackingCompassItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean hasPlayer(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains(PLAYER));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return TrackingCompassItem.hasPlayer(stack) || super.hasGlint(stack);
    }

    /*
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (TrackingCompassItem.hasPlayer(stack)) {
            BlockPos blockPos;
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            if (nbtCompound.contains(LODESTONE_TRACKED_KEY) && !nbtCompound.getBoolean(LODESTONE_TRACKED_KEY)) {
                return;
            }
            Optional<RegistryKey<World>> optional = CompassItem.getLodestoneDimension(nbtCompound);
            if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains(LODESTONE_POS_KEY) && (!world.isInBuildLimit(blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound(LODESTONE_POS_KEY))) || !((ServerWorld)world).getPointOfInterestStorage().hasTypeAt(PointOfInterestType.LODESTONE, blockPos))) {
                nbtCompound.remove(LODESTONE_POS_KEY);
            }
        }
    }
    */

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        this.writeNbt(entity,stack.getOrCreateNbt());
        return ActionResult.success(user.world.isClient);
        //return super.useOnEntity(stack, user, entity, hand);
    }

    /*
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            boolean bl;
            world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            boolean bl2 = bl = !playerEntity.getAbilities().creativeMode && itemStack.getCount() == 1;
            if (bl) {
                this.writeNbt(world.getRegistryKey(), blockPos, itemStack.getOrCreateNbt());
            } else {
                ItemStack itemStack2 = new ItemStack(Items.COMPASS, 1);
                NbtCompound nbtCompound = itemStack.hasNbt() ? itemStack.getNbt().copy() : new NbtCompound();
                itemStack2.setNbt(nbtCompound);
                if (!playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                this.writeNbt(world.getRegistryKey(), blockPos, nbtCompound);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return super.useOnBlock(context);
    }
    */

    private void writeNbt(LivingEntity entity, NbtCompound nbt) {
        nbt.put(PLAYER, NbtHelper.fromUuid(entity.getUuid()) );
    }
}
