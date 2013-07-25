package assets.battlegear2.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;

public class BattlegearDependencyException extends CustomModLoadingErrorDisplayException{
	
	private String[] dependencies;
	
	public BattlegearDependencyException(String[] dependencies){
		this.dependencies = dependencies;
	}

	@Override
	public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
	}

	@Override
	public void drawScreen(GuiErrorScreen errorScreen,
			FontRenderer fontRenderer, int mouseRelX, int mouseRelY,
			float tickTime) {
		
		errorScreen.drawCenteredString(
				fontRenderer, "Required Dependencies for Mine & Blade: Battlegear 2 not found",
				errorScreen.width / 2, (errorScreen.height - 18*(dependencies.length+2))/2, 0xFFFF00);
		errorScreen.drawString(
				fontRenderer, "The following are required:",
				errorScreen.width / 6, (errorScreen.height - 18*(dependencies.length+1))/2, 0xFFFFFF);
		
		for(int i = 0; i < dependencies.length; i++){
			errorScreen.drawString(
					fontRenderer, dependencies[i],
					errorScreen.width / 5, (errorScreen.height - 18*(dependencies.length-i))/2, 0xFFFFFF);
		}
	}

}
