package mods.battlegear2.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class BattlegearClientHookContainer {
	private BattlegearInGameGUI inGameGUI = new BattlegearInGameGUI();
	
	@ForgeSubscribe
	public void renderOverlay(RenderGameOverlayEvent event){
		if(event.type == ElementType.HOTBAR){
			inGameGUI.renderGameOverlay(event.partialTicks);
		}
	}
	
	@ForgeSubscribe
	public void render3rdPersonBattlemode(RenderPlayerEvent.Specials.Post event){
		BattlegearRenderHelper.renderItemIn3rdPerson(
				event.entityPlayer, 
				getRenderManager(event.renderer),
				getModelBiped(event.renderer, 0),
				getModelBiped(event.renderer, 1),
				getModelBiped(event.renderer, 2),
				event.partialTicks
				);
		event.setResult(Result.ALLOW);
	}
	
	private RenderManager getRenderManager(RenderPlayer renderer) {
		return ObfuscationReflectionHelper.getPrivateValue(Render.class, renderer, 0);
	}

	public static ModelBiped getModelBiped(RenderPlayer renderPlayer, int i){
		return ObfuscationReflectionHelper.getPrivateValue(RenderPlayer.class, renderPlayer, i);
	}

}
