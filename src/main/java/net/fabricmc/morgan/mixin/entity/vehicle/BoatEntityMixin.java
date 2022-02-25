package net.fabricmc.morgan.mixin.entity.vehicle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "method_7548",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    public float capSlipperiness(Block instance){
        return instance.getSlipperiness()>1?1:instance.getSlipperiness();
    }
}
