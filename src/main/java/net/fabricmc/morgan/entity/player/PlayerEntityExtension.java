package net.fabricmc.morgan.entity.player;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.util.math.Vec3d;

public interface PlayerEntityExtension {
    void SwitchJump();
    boolean getJump();
    void setJump(boolean bool);
    boolean getBlind();
    void setBlind(boolean bool);
    Vec3d getDeathPos();
    void setDeathPos(Vec3d pos);
    int getSleepSheep();
    void setSleepSheep(int sheep);
}
