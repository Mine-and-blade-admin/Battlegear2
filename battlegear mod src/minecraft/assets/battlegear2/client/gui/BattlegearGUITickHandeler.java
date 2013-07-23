package assets.battlegear2.client.gui;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class BattlegearGUITickHandeler implements ITickHandler{

	private BattlegearInGameGUI inGameGUI = new BattlegearInGameGUI();
	private Minecraft mc = FMLClientHandler.instance().getClient();
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(mc.currentScreen == null){
			inGameGUI.renderGameOverlay((Float)tickData[0]);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}
