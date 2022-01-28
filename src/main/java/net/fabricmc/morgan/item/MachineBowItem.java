package net.fabricmc.morgan.item;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MachineBowItem extends Item {
    public MachineBowItem() {
        super(new Settings().maxDamage(384).group(ItemGroup.COMBAT));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int ticks = getMaxUseTime(stack) - remainingUseTicks;
        if (!world.isClient) {
            ArrowEntity arrowEntity = new ArrowEntity(world, user);
            arrowEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 3F, ticks/20);
            world.spawnEntity(arrowEntity);
            user.setPitch(user.getPitch()+ticks/20);
        }
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.success(itemStack);
    }
}
