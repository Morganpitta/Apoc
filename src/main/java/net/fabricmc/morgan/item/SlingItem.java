package net.fabricmc.morgan.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SlingItem extends Item implements Vanishable {
    public static final int field_30926 = 10;

    public SlingItem() {
        super(new Settings().maxDamage(384).group(ItemGroup.COMBAT));
    }
    public static final float field_30927 = 8.0F;
    public static final float field_30928 = 2.5F;

    public boolean slowsDownUser(){
        return false;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) user;
            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float tridentEntity = playerEntity.getYaw();
            float f = playerEntity.getPitch();
            float g = -MathHelper.sin(tridentEntity * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
            float h = -MathHelper.sin(f * 0.017453292F);
            float k = MathHelper.cos(tridentEntity * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
            float l = MathHelper.sqrt(g * g + h * h + k * k);
            float m = (float) (i >20? 20:i)/5;
            g *= m / l;
            h *= m / l;
            k *= m / l;
            playerEntity.addVelocity((double) g, (double) h, (double) k);
            playerEntity.addExhaustion(2);
            if (playerEntity.isOnGround()) {
                float n = 1.1999999F;
                playerEntity.move(MovementType.SELF, new Vec3d(0.0D, 1.1999999284744263D, 0.0D));

            }
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(this, 80);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
}

