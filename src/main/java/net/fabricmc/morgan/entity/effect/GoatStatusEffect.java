package net.fabricmc.morgan.entity.effect;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GoatStatusEffect extends StatusEffect {
    public GoatStatusEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // whether beneficial or harmful for entities
                0x40E0D0); // color in RGB
    }

    // This method is called every tick to check whether it should apply the status effect or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof LivingEntity) {
            entity.damage(DamageSource.WITHER, amplifier+1);
        }
    }
}