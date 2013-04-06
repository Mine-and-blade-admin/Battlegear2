package mods.battlegear2.common.utils;

import mods.battlegear2.common.items.ItemChain;
import mods.battlegear2.common.items.ItemMace;
import mods.battlegear2.common.items.ItemShield;
import mods.battlegear2.common.items.ItemSpear;
import mods.battlegear2.common.items.ItemWaraxe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class BattlegearConfig {
	
	public static boolean forceBackSheath = false;
	public static int[] setID,validWeaponsID;
	public static String[] itemNames=new String[]{"Chain","WarAxe","Mace","Spear","Shield","KnightArmour","Quiver","Banner"};
	public static Item chains,warAxe,mace,spear,shield,knightArmour,quiver,banner;
	
	public static void getConfig(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile(),true);
        config.load();
        for (int i=0;i<itemNames.length;i++)
        	{
        	setID[i]=config.get(config.CATEGORY_ITEM, itemNames[i]+" ID", 22000+i).getInt();
        	}
        forceBackSheath=config.get(config.CATEGORY_GENERAL, "Force Back Sheath", false).getBoolean(false);
        validWeaponsID=config.get(config.CATEGORY_GENERAL, "Valid Weapon IDs",new int[]{11,12,16,20,27}).getIntList();
        if (config.hasChanged())
        {        
      	  config.save();     	
        } 
	}

	public static void setItems() {
		chains=new ItemChain(setID[0]);
		warAxe=new ItemWaraxe(setID[1]);
		mace=new ItemMace(setID[2]);
		spear=new ItemSpear(setID[3]);
		shield=new ItemShield(setID[4]);
	}
	public static void registerRecipes() {
		for (int i=0;i<15;i++)
			{
			for (int j=0;j<15 && i!=j;j++)		
				GameRegistry.addShapelessRecipe(new ItemStack(banner,1,i), new Object[] 
						{
						new ItemStack(banner,1,j), new ItemStack(Item.dyePowder, 1, i)
						});
			
		GameRegistry.addRecipe(new ItemStack(banner,1,i), new Object[] 
				{
			"B","B","S",Character.valueOf('B'), new ItemStack(Block.cloth,1,i),Character.valueOf('S'), Item.stick});
				}
		GameRegistry.addRecipe(new ItemStack(quiver), new Object[]
				{"L L","L L","LLL",Character.valueOf('L'),Item.leather});
		for (int i=0;i<5;i++)
			{
			if (i<2)			
				GameRegistry.addRecipe(new ItemStack(shield,1,i), new Object[]
						{" L ","LLL"," L ",Character.valueOf('L'),i==0?Item.leather:Block.wood});
			else 
				GameRegistry.addRecipe(new ItemStack(shield,1,i), new Object[]
						{"L L","LLL"," L ",Character.valueOf('L'),i==2?Item.ingotIron:i==3?Item.ingotGold:Item.diamond});
			GameRegistry.addRecipe(new ItemStack(warAxe,1,i), new Object[]
					{"L L","LSL"," S ",Character.valueOf('S'), Item.stick,Character.valueOf('L'),i==0?Block.wood:i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.ingotGold:Item.diamond});
			GameRegistry.addRecipe(new ItemStack(mace,1,i), new Object[]
					{" LL"," LL","S  ",Character.valueOf('S'), Item.stick,Character.valueOf('L'),i==0?Block.wood:i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.ingotGold:Item.diamond});
			if (i==0)
				GameRegistry.addRecipe(new ItemStack(spear,1,i), new Object[]
						{"  S"," S ","S  ",Character.valueOf('S'), Item.stick});
			else
				GameRegistry.addRecipe(new ItemStack(spear,1,i), new Object[]
						{"  I"," S ",Character.valueOf('S'), new ItemStack(spear,1,0),Character.valueOf('I'),i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.ingotGold:Item.diamond});
			
			}
		GameRegistry.addShapelessRecipe(new ItemStack(chains,2),new Object[]{Item.ingotIron});	
	}
}
