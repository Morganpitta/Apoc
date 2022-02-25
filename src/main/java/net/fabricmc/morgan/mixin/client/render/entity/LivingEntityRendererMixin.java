package net.fabricmc.morgan.mixin.client.render.entity;

import net.fabricmc.morgan.entity.LivingEntityExtension;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T>
        implements FeatureRendererContext<T, M> {

    /**
     * @author Morgan
     * @reason should flip upside down?
     */
    @Overwrite
    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
        String string;
        if (((LivingEntityExtension)entity).getRenderUpsideDown()){
            return true;
        }
        if ((entity instanceof PlayerEntity || entity.hasCustomName()) && ("Dinnerbone".equals(string = Formatting.strip(entity.getName().getString())) || "Grumm".equals(string))) {
            return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
        }
        return false;
    }

    public LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    /**
    @Redirect(method = "setupTransforms",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z"))
    protected boolean dinnerboneRedirect(LivingEntity entity){
        if (entity instanceof PlayerEntity){
            return true;
        }
        return shouldFlipUpsideDown(entity);
    }
    **/
}
