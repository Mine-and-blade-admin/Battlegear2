package mods.battlegear2.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerLocalPlayer extends Container{
    /**
     * Determines if inventory manipulation should be handled.
     */
    public boolean isLocalWorld = false;
    public final EntityPlayer thePlayer;

    public ContainerLocalPlayer(boolean local, EntityPlayer player) {
        this.thePlayer = player;
        this.isLocalWorld = local;
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }
}
