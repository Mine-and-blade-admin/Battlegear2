package mods.battlegear2.client.gui.controls;

import cpw.mods.fml.client.config.GuiUtils;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiSigilButton extends GuiPlaceableButton {

	public GuiSigilButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "Sigil");
	}

	@Override
	protected void openGui(Minecraft mc) {
		BattlegearSigilGUI.open(mc.thePlayer);
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattlegearSigilGUI.class;
	}

    @Override
    protected void drawTextureBox(int hoverState){
        GuiUtils.drawContinuousTexturedBox(CREATIVE_TABS, this.xPosition, this.yPosition, 0, 64 + (hoverState > 0 ? 36 : 0), this.width, this.height, TAB_DIM, TAB_DIM, TAB_BORDER, TAB_BORDER, TAB_BORDER, TAB_BORDER, this.zLevel);
    }
}
