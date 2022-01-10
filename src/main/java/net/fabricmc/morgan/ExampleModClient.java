package net.fabricmc.morgan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.morgan.fluid.MorganFluids;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(MorganFluids.WITHERED_FLUID, MorganFluids.FLOWING_WITHERED_FLUID, new SimpleFluidRenderHandler(
                new Identifier("morgan:block/withered_material"),
                new Identifier("morgan:block/withered_material"),
                10684167
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MorganFluids.WITHERED_FLUID, MorganFluids.FLOWING_WITHERED_FLUID);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier("morgan:block/withered_material"));
        });

        // ...
    }
}