package net.fabricmc.morgan.mixin.entity;

import net.fabricmc.morgan.entity.effect.StatusEffects;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    private PlayerEntity target;


    static {
        STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }


    @Overwrite
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient) {
            ItemStack itemStack = this.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if ((this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(this, i);
                ((PlayerEntityExtension) player).SwitchJump();

                for (int x=i;x>0;x--) {
                    AnimalEntity Goat = EntityType.GOAT.spawn((ServerWorld) world, null, Text.of(player.getEntityName() + "'s Goat"), player, new BlockPos(player.getX(), player.getY() + 1, player.getZ()), SpawnReason.JOCKEY, false, false);
                    Goat.shouldRenderName();
                    Goat.setYaw(player.getYaw());
                    Goat.setPitch(player.getPitch());
                    Goat.addStatusEffect(new StatusEffectInstance(StatusEffects.GOAT, 100000, 4));
                    player.giveItemStack(new ItemStack(Items.BEDROCK));
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


    private void expensiveUpdate() {
        if (this.target == null || this.target.squaredDistanceTo(this) > 64.0D) {
            this.target = this.world.getClosestPlayer(this, 8.0D);
        }
    }

    @Inject(method = "tick",at=@At("HEAD"))
    public void tick(CallbackInfo info){
        if (this.target != null && ((this.target.isDead()) ||!(this.target.getEquippedStack(EquipmentSlot.OFFHAND).isOf(MorganItems.ITEM_MAGNET) ||this.target.getEquippedStack(EquipmentSlot.MAINHAND).isOf(MorganItems.ITEM_MAGNET)))) {
            this.target = null;
        }

        if (this.target != null) {
            Vec3d vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + (double)this.target.getStandingEyeHeight() / 2.0D - this.getY(), this.target.getZ() - this.getZ());
            double d = vec3d.lengthSquared();
            if (d < 64.0D) {
                double e = 1.0D - Math.sqrt(d) / 8.0D;
                this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1D)));
            }
        }
    }
}
