package mods.battlegear2.client.gui.controls;

import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.client.Minecraft;

public class GuiSigilButton extends GuiPlaceableButton {

	public GuiSigilButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "Sigil");
	}

	@Override
	protected void clicked(Minecraft mc) {
		mc.thePlayer.openGui(Battlegear.INSTANCE, BattlegearGUIHandeler.sigilEditor, mc.theWorld,
				(int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
	}
}
