 package org.bukkit.craftbukkit.event;

 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.item.ItemStack;
 import org.bukkit.event.player.PlayerInteractEvent;

 public class CraftEventFactory
 {
   public static org.bukkit.event.player.PlayerInteractEvent callPlayerInteractEvent(EntityPlayer player, org.bukkit.event.block.Action action, ItemStack stack){
	return callPlayerInteractEvent(player, action, 0, 256, 0, 0, stack);
   }
   
   public static org.bukkit.event.player.PlayerInteractEvent callPlayerInteractEvent(EntityPlayer player, org.bukkit.event.block.Action action, int x, int y, int z, int face, ItemStack stack){
	return new PlayerInteractEvent();
   }
 }