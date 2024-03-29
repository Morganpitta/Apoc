package net.fabricmc.morgan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.morgan.block.MorganBlocks;
import net.fabricmc.morgan.entity.damage.DamageSource;
import net.fabricmc.morgan.entity.effect.MorganStatusEffects;
import net.fabricmc.morgan.fluid.MorganFluids;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.server.command.BouncinessCommand;
import net.fabricmc.morgan.server.command.BouncyCommand;
import net.fabricmc.morgan.server.command.DeathCommand;
import net.fabricmc.morgan.tag.MorganItemTags;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
    public static final Identifier CAN_JUMP_PACKET_ID = new Identifier("morgan", "can_jump");
    public static final Identifier BOUNCY_PACKET_ID = new Identifier("morgan", "bouncy");
    public static final Identifier BLIND_PACKET_ID = new Identifier("morgan", "blind");
    public static final Identifier CLIENT_PITCH_ID = new Identifier("morgan", "pitch");
    public static final Identifier DEATH_PACKET_ID = new Identifier("morgan", "death");
    public static final Identifier SHEEP_PACKET_ID = new Identifier("morgan", "sheep");
    public static final Identifier WEIGHT_PACKET_ID = new Identifier("morgan", "weight");
    public static final Identifier FORGETFUL_PACKET_ID = new Identifier("morgan", "forgetful");
    public static final Identifier GRAVITY_PACKET_ID = new Identifier("morgan", "gravity");
    public static final Identifier RENDER_UPSIDE_DOWN_PACKET_ID = new Identifier("morgan", "render_upside_down");
    public static final Identifier UPSIDE_DOWN_PACKET_ID = new Identifier("morgan", "upside_down");
    public static final Logger LOGGER = LogManager.getLogger("modid");
    @Override
    public void onInitialize() {

        MorganBlocks.init();
        MorganStatusEffects.init();
        DamageSource.init();
        BouncinessCommand.init();
        BouncyCommand.init();
        DeathCommand.init();
        MorganFluids.init();
        MorganItems.init();
    }
}
