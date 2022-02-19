package net.fabricmc.morgan.mixin.entity;

import net.fabricmc.morgan.entity.effect.MorganStatusEffects;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Shadow private static final TrackedData<ItemStack> STACK;
    @Shadow public ItemStack getStack() {
        return (ItemStack)this.getDataTracker().get(STACK);
    }
    @Shadow @Nullable
    private UUID owner;


    static {
        STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }


    /**
     * @author Morgan
     */
    @Overwrite
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient) {
            ItemStack itemStack = this.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if ((this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(this, i);

                if (!(player.getStackInHand(Hand.MAIN_HAND).isOf(MorganItems.ITEM_MAGNET)||player.getStackInHand(Hand.OFF_HAND).isOf(MorganItems.ITEM_MAGNET))) {

                    ((PlayerEntityExtension) player).SwitchJump();

                    for (int x = i; x > 0; x--) {
                        AnimalEntity Goat = EntityType.GOAT.spawn((ServerWorld) world, null, Text.of(player.getEntityName() + "'s Goat"), player, new BlockPos(player.getX(), player.getY() + 1, player.getZ()), SpawnReason.JOCKEY, false, false);
                        Goat.shouldRenderName();
                        Goat.setYaw(player.getYaw());
                        Goat.setPitch(player.getPitch());
                        Goat.addStatusEffect(new StatusEffectInstance(MorganStatusEffects.GOAT, 100000, 4));
                        player.giveItemStack(new ItemStack(Items.BEDROCK));
                    }
                }
                else {
                    ((PlayerEntityExtension)player).setJump(true);
                }
                if (itemStack.isEmpty()) {
                    this.discard();
                    itemStack.setCount(i);
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                player.triggerItemPickedUpByEntityCriteria((ItemEntity) (Entity) this);
            }

        }
    }
}
