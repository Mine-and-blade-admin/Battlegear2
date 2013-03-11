package battlegear2.common;

public class CommonProxy {
	
	public void registerKeyHandelers(){}
	@ForgeSubscribe
	public void playerInterect(PlayerInteractEvent event){
	}
	
	@ForgeSubscribe
	public void playerIntereactEntity(EntityInteractEvent event){
		if(event.entityPlayer.inventory.isBattlemode()){
			event.entityPlayer.swingOffItem();
			event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
			event.setCanceled(true);
		}
	}
}
