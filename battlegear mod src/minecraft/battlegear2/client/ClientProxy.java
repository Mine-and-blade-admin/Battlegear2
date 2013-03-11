package battlegear2.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import battlegear2.client.keybinding.BattlegearKeyHandeler;
import battlegear2.common.CommonProxy;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerKeyHandelers() {
		KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
	}
@ForgeSubscribe
	public void playerInterect(PlayerInteractEvent event){
			if(event.entityPlayer.inventory.isBattlemode()){
				Minecraft mc = FMLClientHandler.instance().getClient();
				switch (event.action) {
				case LEFT_CLICK_BLOCK:
					System.out.println("clicked");
					break;
				case RIGHT_CLICK_BLOCK:
					//TODO add support for tools, although I think this could
					//require too many edits and is likely not viable
					event.entityPlayer.swingOffItem();
					event.useItem = Result.DENY;
					break;
				case RIGHT_CLICK_AIR:
					ItemStack mainHand = event.entityPlayer.getCurrentEquippedItem();
					if(mainHand == null || BattlegearUtils.isMainHand(mainHand.itemID)){
						event.entityPlayer.swingOffItem();
						event.setCanceled(true);
						break;
					}else{
							break;
					}
				}
			}
		}	
}
