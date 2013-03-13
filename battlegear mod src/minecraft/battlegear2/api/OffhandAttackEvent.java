package battlegear2.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class OffhandAttackEvent extends PlayerEvent{

	public boolean swingOffhand = true;
	public boolean shouldAttack = true;
	public EntityInteractEvent parent;
	
	
	public OffhandAttackEvent(EntityInteractEvent parent){
		super(parent.entityPlayer);
		this.parent = parent;
	}
	
	public Entity getTarget(){
		return parent.target;
	}
	
	public void setCancelParentEvent(boolean cancel){
		parent.setCanceled(cancel);
	}

	@Override
	public void setCanceled(boolean cancel) {
		super.setCanceled(cancel);
		parent.setCanceled(cancel);
	}

	@Override
	public void setResult(Result value) {
		super.setResult(value);
		parent.setResult(value);
	}
	
	
	
	
}
