package net.fabricmc.morgan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.morgan.block.MorganBlocks;
import net.fabricmc.morgan.entity.damage.DamageSource;
import net.fabricmc.morgan.entity.effect.StatusEffects;
import net.fabricmc.morgan.fluid.MorganFluids;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.server.command.BouncinessCommand;
import net.fabricmc.morgan.server.command.BouncyCommand;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
    public static final Identifier CAN_JUMP_PACKET_ID = new Identifier("morgan", "can_jump");
    public static final Identifier BOUNCY_PACKET_ID = new Identifier("morgan", "bouncy");
    public static final Logger LOGGER = LogManager.getLogger("modid");
    @Override
    public void onInitialize() {

        MorganBlocks.init();
        StatusEffects.init();
        DamageSource.init();
        BouncinessCommand.init();
        BouncyCommand.init();
        MorganFluids.init();
        MorganItems.init();
    }
}