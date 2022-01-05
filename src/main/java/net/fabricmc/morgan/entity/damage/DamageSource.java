package net.fabricmc.morgan.entity.damage;

public class DamageSource extends net.minecraft.entity.damage.DamageSource {
    protected DamageSource(String name) {
        super(name);
    }

    public static final net.minecraft.entity.damage.DamageSource GOAT = (new net.minecraft.entity.damage.DamageSource("goat")).setBypassesArmor();

    public static void init(){

    }
}
