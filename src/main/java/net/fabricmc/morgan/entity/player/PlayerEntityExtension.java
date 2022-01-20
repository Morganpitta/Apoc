package net.fabricmc.morgan.entity.player;

public interface PlayerEntityExtension {
    void SwitchJump();
    boolean getJump();
    void setJump(boolean bool);
    boolean getBlind();
    void setBlind(boolean bool);
}
