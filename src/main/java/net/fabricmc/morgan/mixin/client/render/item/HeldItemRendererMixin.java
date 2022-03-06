package net.fabricmc.morgan.mixin.client.render.item;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch(F)F"))
    public float getPitch(ClientPlayerEntity instance, float tickDelta){
        return instance.getPitch();
    }
}
