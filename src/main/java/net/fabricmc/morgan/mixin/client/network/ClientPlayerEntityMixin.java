package net.fabricmc.morgan.mixin.client.network;

import net.fabricmc.morgan.ExampleMod;
import net.fabricmc.morgan.entity.player.PlayerEntityExtension;
import net.fabricmc.morgan.item.MorganItems;
import net.fabricmc.morgan.mixin.entity.player.PlayerEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {

    public boolean hasBeenBlinded = false;
    public int defaultViewDistance = 10;
    public float defaultEntityDistanceScaling=1F;

    protected ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick",at=@At("HEAD"))
    public void tick(CallbackInfo info) {
        if ((this.getBlind()&&(MinecraftClient.getInstance().options.viewDistance>1|| MinecraftClient.getInstance().options.entityDistanceScaling>0.1F))&&!((this.getEquippedStack(EquipmentSlot.HEAD)).isOf(MorganItems.GLASSES))){
            if (!this.hasBeenBlinded) {
                this.defaultViewDistance = MinecraftClient.getInstance().options.viewDistance;
                this.defaultEntityDistanceScaling = MinecraftClient.getInstance().options.entityDistanceScaling;
            }
            this.hasBeenBlinded = true;
            MinecraftClient.getInstance().options.viewDistance = 1;
            MinecraftClient.getInstance().options.entityDistanceScaling = 0.4F;
        }
        else if (this.hasBeenBlinded&&((this.getEquippedStack(EquipmentSlot.HEAD)).isOf(MorganItems.GLASSES))){
            this.hasBeenBlinded = false;
            MinecraftClient.getInstance().options.viewDistance = this.defaultViewDistance;
            MinecraftClient.getInstance().options.entityDistanceScaling = this.defaultEntityDistanceScaling;
        }
    }
}
