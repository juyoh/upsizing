package net.juyoh.scale.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class OrbOfScaleItem extends Item {
    public OrbOfScaleItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}
