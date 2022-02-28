package net.fabricmc.morgan.mixin.client.render;

import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean ready;

    @Shadow private BlockView area;

    @Shadow private Entity focusedEntity;

    @Shadow private boolean thirdPerson;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow private float yaw;

    @Shadow private float pitch;

    @Shadow protected abstract void moveBy(double x, double y, double z);

    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Shadow private float lastCameraY;

    @Shadow private float cameraY;

    /**
     * @author Morgan
     */
    @Overwrite
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.setRotation(focusedEntity.getYaw(tickDelta), 180+focusedEntity.getPitch(tickDelta));
        //if (focusedEntity instanceof PlayerEntity) {((PlayerEntity) focusedEntity).sendMessage(Text.of(String.valueOf((double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY))),false);};
        this.setPos(MathHelper.lerp((double)tickDelta, focusedEntity.prevX, focusedEntity.getX()), MathHelper.lerp((double)tickDelta, focusedEntity.prevY, focusedEntity.getY()) + ( ((EntityExtension)focusedEntity).upsideDownGravity()? 2 - (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY): (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY)), MathHelper.lerp((double)tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
        if (thirdPerson) {
            if (inverseView) {
                this.setRotation(this.yaw + 180.0f, -this.pitch);
            }
            this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
        } else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
            Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
            this.setRotation(direction != null ? direction.asRotation() - 180.0f : 0.0f, 0.0f);
            this.moveBy(0.0, 0.3, 0.0);
        }
    }
}
