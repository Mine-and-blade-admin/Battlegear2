package mods.battlegear2.items;

import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.item.Item;

public class ItemShield extends Item {
    public ItemShield(int id) {
        super(id);
        this.setCreativeTab(BattlegearConfig.customTab);
    }
}
