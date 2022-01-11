package net.fabricmc.morgan.fluid;

import net.minecraft.fluid.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class MorganFluids {

    public MorganFluids(){
    }

    public static void init(){
    }

    public static final FlowableFluid FLOWING_CORRUPTED_FLUID;
    public static final FlowableFluid CORRUPTED_FLUID;

    private static <T extends Fluid> T register(String id, T value) {
        return Registry.register(Registry.FLUID, new Identifier("morgan", id), value);
    }

    static {
        FLOWING_CORRUPTED_FLUID = register("flowing_corrupted_fluid", new CorruptedFluid.Flowing());
        CORRUPTED_FLUID = register("corrupted_fluid", new CorruptedFluid.Still());
    }
}
