package mods.battlegear2.client.gui.controls;

import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tconstruct.client.tabs.AbstractTab;
//Tinker's Construct support class, for heradlry
public class SigilTab extends AbstractTab {

	public static final Minecraft mc = Minecraft.getMinecraft();
	public SigilTab() {
		super(1, 0, 0, new ItemStack(1,1,0));
	}

	@Override
	public void onTabClicked() {
		mc.thePlayer.openGui(Battlegear.INSTANCE, BattlegearGUIHandeler.sigilEditor, mc.theWorld,
				(int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}

}
