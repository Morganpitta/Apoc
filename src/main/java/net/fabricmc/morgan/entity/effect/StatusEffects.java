//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.fabricmc.morgan.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StatusEffects extends net.minecraft.entity.effect.StatusEffects {

    public StatusEffects() {
    }

    public static final StatusEffect GOAT;

    private static StatusEffect register(String id, StatusEffect status) {
        return (StatusEffect) Registry.register(Registry.STATUS_EFFECT, new Identifier("morgan", id), status);
    }



    static {
        GOAT = register("goat", new GoatStatusEffect());
    }

    public static void init() {

    }
}
