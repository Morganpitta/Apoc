package net.fabricmc.morgan.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MeatBlock extends Block {
    public MeatBlock() {
        super(AbstractBlock.Settings.of(Material.CAKE).breakInstantly());
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if(entity.isPlayer()){
            ((PlayerEntity) entity).getHungerManager().add(1,1);
        }
    }
}
