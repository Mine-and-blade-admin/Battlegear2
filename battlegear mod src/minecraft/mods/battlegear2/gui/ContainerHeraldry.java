package mods.battlegear2.gui;

import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerHeraldry extends ContainerLocalPlayer{

    public ContainerHeraldry(InventoryPlayer inventoryPlayer, boolean local, EntityPlayer player){
        super(local, player);
        //Heraldry slots: Cape, Weapon, Armor
        this.addSlotToContainer(new HeraldrySlot(inventoryPlayer, InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.EXTRA_ITEMS+1, -40, -22));
        this.addSlotToContainer(new HeraldrySlot(inventoryPlayer, InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.EXTRA_ITEMS+2, -20, -22));
        this.addSlotToContainer(new HeraldrySlot(inventoryPlayer, InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.EXTRA_ITEMS+3, 0, -22));
        //Default bar
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, i * 20, 184));
        }
    }
}
