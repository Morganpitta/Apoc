package net.fabricmc.morgan.mixin.item;

import net.fabricmc.morgan.item.ItemExtension;
import net.fabricmc.morgan.item.ItemSettingsExtension;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemConvertible, ItemExtension {

    public boolean slowsDownUser(){
        return true;
    }

    public static class Settings implements ItemSettingsExtension {
        float weight=1;
        public Item.Settings weight(float weight) {
            this.weight = weight;
            return (Item.Settings)(Object)this;
        }
    }
}
