package net.fabricmc.morgan.mixin.block;

import net.fabricmc.fabric.mixin.networking.accessor.MinecraftClientAccessor;
import net.fabricmc.morgan.accessors.block.PublicDoorBlock;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin{

    @Shadow
    private static Block register(String id, Block block) {
        return null;
    }


    @Redirect(method = "<clinit>",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;",ordinal = 180))
    private static Block ironDoor(String id, Block block) {
        return register("iron_door", new PublicDoorBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.METAL).nonOpaque()));
    }
}
