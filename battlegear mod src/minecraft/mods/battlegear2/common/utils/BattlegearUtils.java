package mods.battlegear2.common.utils;

import java.lang.reflect.Method;
import java.util.Arrays;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

import mods.battlegear2.api.IBattlegearWeapon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class BattlegearUtils {
	
	
	private static boolean[] weapons;
	private static boolean[] mainHandDualWeapons;
	private static boolean[] offhandDualWeapons;
	
	private static final ReflectionMethod[] itemMmethods = new ReflectionMethod[]{
		new ReflectionMethod("onItemUse", "", new Class[]{ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class}),
		new ReflectionMethod("onItemRightClick", "", new Class[]{ItemStack.class, World.class, EntityPlayer.class})
	
	};

	public static boolean isWeapon(int id){
		
		if(Item.itemsList[id] instanceof IBattlegearWeapon)
			return true;
		else
			return weapons[id];
	}
	
	public static boolean isMainHand(int id){
		if(Item.itemsList[id] instanceof IBattlegearWeapon)
			return ((IBattlegearWeapon)Item.itemsList[id]).willAllowOffhandWeapon();
		else
			return mainHandDualWeapons[id];
	}
	
	public static boolean isOffHand(int id){
		if(Item.itemsList[id] instanceof IBattlegearWeapon)
			return ((IBattlegearWeapon)Item.itemsList[id]).isOffhandHandDualWeapon();
		else
			return offhandDualWeapons[id];
	}
	
	public static void scanAndProcessItems(){
		
		weapons = new boolean[Item.itemsList.length];
		mainHandDualWeapons = new boolean[Item.itemsList.length];
		offhandDualWeapons = new boolean[Item.itemsList.length];
		
		for(int i = 0; i < Item.itemsList.length; i++){
			Item item = Item.itemsList[i];
			weapons[i] = false;
			mainHandDualWeapons[i] = false;
			offhandDualWeapons[i] = false;
			if(item != null){
				
				boolean valid = item.getItemStackLimit() == 1 && item.isDamageable();
				if(valid){
					weapons[i] = item instanceof ItemSword ||
							item instanceof ItemBow ||
							item instanceof ItemTool;					
					
					
					if(weapons[i]){
						//make sure there are no special functions for offhand/mainhand weapons
						boolean rightClickFunction = checkForRightClickFunction(item);
						//only weapons can be placed in offhand
						offhandDualWeapons[i] = !(item instanceof ItemTool || item instanceof ItemBow) && !rightClickFunction;
						mainHandDualWeapons[i] = !(item instanceof ItemBow) && !rightClickFunction;
					}
				}
			}
		}
	}

	public static boolean checkForRightClickFunction(Item item) {

		try{
			if(item.getItemUseAction(null) == EnumAction.block || item.getItemUseAction(null) == EnumAction.none){
				
				
				Class c = item.getClass();
				while(!(c.equals(Item.class) || c.equals(ItemTool.class) || c.equals(ItemSword.class))){
					for (Method method : c.getDeclaredMethods()) {
						for (ReflectionMethod reflector : itemMmethods) {
							
							//TODO: Change this, need to find the new method/field to detect obfuscation
							if(reflector.getName(false).equals(method.getName()) &&
									Arrays.deepEquals(reflector.getParameterTypes(), method.getParameterTypes())){
								return true;								
							}
						}
					}
					
					c = c.getSuperclass();
				}
				
				return false;
			}else{
				return true;
			}
		}catch (NullPointerException e) {
			return true;
		}
	}

	
}
