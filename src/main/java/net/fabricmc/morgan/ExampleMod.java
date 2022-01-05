package net.fabricmc.morgan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.morgan.block.Blocks;
import net.fabricmc.morgan.entity.damage.DamageSource;
import net.fabricmc.morgan.entity.effect.StatusEffects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("modid");
    @Override
    public void onInitialize() {

        Blocks.init();
        StatusEffects.init();
        DamageSource.init();
    }
}
