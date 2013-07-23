package assets.battlegear2.common.utils;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public enum EnumBGAnimations {
	
	OffHandSwing {
		@Override
		public void processAnimation(Entity entity) {
			if(entity instanceof EntityPlayer)
				((EntityPlayer)entity).swingOffItem();
		}
	};
	
	
	public abstract void processAnimation(Entity entity);

}
