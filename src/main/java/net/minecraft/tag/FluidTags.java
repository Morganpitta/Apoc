//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
    protected static final RequiredTagList<Fluid> REQUIRED_TAGS;
    private static final List<Tag<Fluid>> TAGS;
    public static final Identified<Fluid> WATER;
    public static final Identified<Fluid> LAVA;
    public static final Identified<Fluid> WITHERED_FLUIDS;

    private FluidTags() {
    }

    private static Identified<Fluid> register(String id) {
        Identified<Fluid> identified = REQUIRED_TAGS.add(id);
        TAGS.add(identified);
        return identified;
    }

    public static TagGroup<Fluid> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }

    /** @deprecated */
    @Deprecated
    public static List<Tag<Fluid>> getTags() {
        return TAGS;
    }

    static {
        REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.FLUID_KEY, "tags/fluids");
        TAGS = Lists.newArrayList();
        WATER = register("water");
        LAVA = register("lava");
        WITHERED_FLUIDS = register("withered_fluids");
    }
}
