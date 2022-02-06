package net.fabricmc.morgan.tag;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MorganItemTags {
    public static final Tag<Item> WEIGHTLESS = TagFactory.ITEM.create(new Identifier("morgan", "weightless"));
    public static final Tag<Item> EXPLOSIVE = TagFactory.ITEM.create(new Identifier("morgan", "explosives"));

    public MorganItemTags() {
    }
}
