package assets.battlegear2.common;

import assets.battlegear2.api.IHeraldyItem;
import assets.battlegear2.common.heraldry.HeraldyRecipie;
import assets.battlegear2.common.items.ItemHeradryIcon;
import assets.battlegear2.common.utils.EnumBGAnimations;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;

public class CommonProxy {

	public void registerKeyHandelers(){
		
		for(Item item : Item.itemsList){
			if(item != null && item instanceof IHeraldyItem){
				GameRegistry.addRecipe(new HeraldyRecipie(item));
			}
		}
		
	}
	
	public void throwDependencyError(String[] dependencies){}
	
	public void throwError(String message1, String message2){};

	public void registerTextures(Object iconRegister) {}

	public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

	public void registerTickHandelers() {
		TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.SERVER);
	}

	public MovingObjectPosition getMouseOver(float tickPart, float maxDist) {
		return null;
	}

	public void attackCreatureWithItem(EntityPlayer entityPlayer, Entity target) {}

	public Icon getBackgroundIcon(int i) {
		return null;
	}

}
