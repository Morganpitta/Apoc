package net.fabricmc.morgan.mixin.client;

import net.fabricmc.morgan.entity.EntityExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Shadow private double lastMouseUpdateTime;

    @Shadow public abstract boolean isCursorLocked();

    @Shadow @Final private MinecraftClient client;

    @Shadow private double cursorDeltaX;

    @Shadow private double cursorDeltaY;

    @Shadow @Final private SmoothUtil cursorXSmoother;

    @Shadow @Final private SmoothUtil cursorYSmoother;

    /**
     * @author Morgan
     * @reason invertng x mouse for gravity
     */
    @Overwrite
    public void updateMouse() {
        double l;
        double k;
        double d = GlfwUtil.getTime();
        double e = d - this.lastMouseUpdateTime;
        this.lastMouseUpdateTime = d;
        if (!this.isCursorLocked() || !this.client.isWindowFocused()) {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            return;
        }
        double f = this.client.options.mouseSensitivity * (double)0.6f + (double)0.2f;
        double g = f * f * f;
        double h = g * 8.0;
        if (this.client.options.smoothCameraEnabled) {
            double i = this.cursorXSmoother.smooth(this.cursorDeltaX * h, e * h);
            double j = this.cursorYSmoother.smooth(this.cursorDeltaY * h, e * h);
            k = i;
            l = j;
        } else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            k = this.cursorDeltaX * g;
            l = this.cursorDeltaY * g;
        } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            k = this.cursorDeltaX * h;
            l = this.cursorDeltaY * h;
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
        int i = 1;
        int j = 1;
        if (this.client.options.invertYMouse) {
            i = -1;
        }
        if (((EntityExtension)this.client.player).upsideDownGravity()) {
            j = -1;
        }
        this.client.getTutorialManager().onUpdateMouse(k, l);
        if (this.client.player != null) {
            this.client.player.changeLookDirection(k*j, l * (double)i);
        }
    }
}
