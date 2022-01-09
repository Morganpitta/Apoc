package net.fabricmc.morgan.fluid;

import com.google.common.collect.UnmodifiableIterator;
import net.fabricmc.morgan.fluid.WitheredFluid;
import net.minecraft.fluid.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;


public class MorganFluids {

    public MorganFluids(){
    }

    public static void init(){
    }

    public static final FlowableFluid FLOWING_WITHERED_FLUID;
    public static final FlowableFluid WITHERED_FLUID;

    private static <T extends Fluid> T register(String id, T value) {
        return Registry.register(Registry.FLUID, new Identifier("morgan", id), value);
    }

    static {
        FLOWING_WITHERED_FLUID = register("flowing_withered_fluid", new WitheredFluid.Flowing());
        WITHERED_FLUID = register("withered_fluid", new WitheredFluid.Still());
    }
}
