package net.fabricmc.morgan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.morgan.block.MorganBlocks;
import net.fabricmc.morgan.entity.EntityExtension;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.fluid.MorganFluids;
import net.fabricmc.morgan.mixin.entity.player.PlayerEntityMixin;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //register fluid texture
        FluidRenderHandlerRegistry.INSTANCE.register(MorganFluids.CORRUPTED_FLUID, MorganFluids.FLOWING_CORRUPTED_FLUID, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                1638400
        ));

        //makes the fluids seethorugh
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MorganFluids.CORRUPTED_FLUID, MorganFluids.FLOWING_CORRUPTED_FLUID);

        //registers the texture
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier("morgan:block/corrupted_material"));
        });

        //Can jump network stuff
        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.CAN_JUMP_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean CanJump = buf.readBoolean();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((PlayerEntityExtension)client.player).setJump(CanJump);
            });
        });

        //Bouncy stuff
        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.BOUNCY_PACKET_ID, (client, handler, buf, responseSender) -> {
            boolean Bouncy = buf.readBoolean();
            client.execute(() -> {
                // Everything in this lambda is run on the render thread
                ((EntityExtension)client.player).setBouncy(Bouncy);
            });
        });

        //makes chlorine block seethrough
        BlockRenderLayerMap.INSTANCE.putBlock(MorganBlocks.CHLORINE_GAS, RenderLayer.getTranslucent());

        // ...
    }
}