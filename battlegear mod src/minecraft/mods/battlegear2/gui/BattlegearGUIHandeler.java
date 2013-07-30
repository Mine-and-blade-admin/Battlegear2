package mods.battlegear2.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import mods.battlegear2.client.gui.BattleEquipGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BattlegearGUIHandeler implements IGuiHandler {

    public static final int equipID = 1;
    public static final int sigilEditor = 2;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
        switch (ID) {
            case equipID:
                return new ContainerBattle(player.inventory, !world.isRemote, player);
           /* case sigilEditor:
                return new ContainerHeraldry(player.inventory, !world.isRemote, player);*/
            default:
                return null;
        }

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
        switch (ID) {
            case equipID:
                return new BattleEquipGUI(player, world.isRemote);
           /* case sigilEditor:
                return new GUIHeraldry(player, true, world.isRemote);*/
            default:
                return null;
        }
    }

}
