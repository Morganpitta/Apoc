package net.fabricmc.morgan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.morgan.block.MorganBlocks;
import net.fabricmc.morgan.fluid.MorganFluids;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(MorganFluids.CORRUPTED_FLUID, MorganFluids.FLOWING_CORRUPTED_FLUID, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                1638400
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MorganFluids.CORRUPTED_FLUID, MorganFluids.FLOWING_CORRUPTED_FLUID);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier("morgan:block/corrupted_material"));
        });

        BlockRenderLayerMap.INSTANCE.putBlock(MorganBlocks.CHLORINE_GAS, RenderLayer.getTranslucent());

        // ...
    }
}