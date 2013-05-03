package mods.battlegear2.common.utils;

import mods.battlegear2.common.inventory.CreativeTabMB_B_2;
import mods.battlegear2.common.items.ItemHeradryIcon;
import mods.battlegear2.common.items.ItemMace;
import mods.battlegear2.common.items.ItemShield;
import mods.battlegear2.common.items.ItemSpear;
import mods.battlegear2.common.items.ItemWaraxe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BattlegearConfig {
	public static final CreativeTabs customTab=new CreativeTabMB_B_2("Battlegear2");
	public static boolean forceBackSheath = false;
	//We use 27 item ids for now, valid weapons ids are from vanilla swords
	public static int[] setID=new int[8],validWeaponsID={11,12,16,20,27};
	public static final String[] itemNames=new String[]{"Banner","Quiver","Chain","Waraxe","Mace","Spear","Shield","KnightArmor"};
	public static Item[] warAxe=new Item[5],mace=new Item[5],spear=new Item[5],shield=new Item[5];
	public static Item chains,quiver,banner,heradricItem;
	public static ItemArmor[] knightArmor=new ItemArmor[4];
	
	public static void getConfig(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile(),true);
        config.load();
        for (int i=0;i<itemNames.length;i++)
        	{
        	setID[i]=config.get(config.CATEGORY_ITEM, itemNames[i]+" ID", i<3?22000+i:22003+5*(i-3), "Warning, real ID range goes from 22256 to 22282").getInt();
        	}
        forceBackSheath=config.get(config.CATEGORY_GENERAL, "Force Back Sheath", false).getBoolean(false);
        validWeaponsID=config.get(config.CATEGORY_GENERAL, "Valid Weapon IDs",new int[]{11,12,16,20,27}).getIntList();
        if (config.hasChanged())
        {        
      	  config.save();     	
        } 
	}

	public static void setItems() {
		banner=new Item(setID[0]);//You might want to set a block and tileentity
		quiver=new Item(setID[1]).setUnlocalizedName("battlegear2:"+itemNames[1]).setCreativeTab(customTab);
		chains=new Item(setID[2]).setUnlocalizedName("battlegear2:"+itemNames[2]).setCreativeTab(customTab);
		for (int i=0;i<5;i++)
		{
			warAxe[i]=new ItemWaraxe(setID[3]+i,i);//Second parameter for material (see abstract ItemWeapon)
			mace[i]=new ItemMace(setID[4]+i,i);
			spear[i]=new ItemSpear(setID[5]+i,i);
			shield[i]=new ItemShield(setID[6]+i,i);
			//You might want to use custom armor material and renderer	
			/*if(i<4)
			knightArmor[i]=new ItemKnightArmor(setID[7]+i,EnumArmorMaterial.IRON,0,i);*/
		}
		
		heradricItem = new ItemHeradryIcon(22000-1);
	}
	public static void registerRecipes() {//Those are old recipes found on your M-B topic
		for (int i=0;i<15;i++)
			{//Changing banner "color"	
			for (int j=0;j<15 && i!=j;j++)	
				GameRegistry.addShapelessRecipe(new ItemStack(banner,1,i), new Object[] 
						{
						new ItemStack(banner,1,j), new ItemStack(Item.dyePowder, 1, i)
						});
			//Making a "colored" banner
		GameRegistry.addRecipe(new ItemStack(banner,1,i), new Object[] 
				{
			"B","B","S",Character.valueOf('B'), new ItemStack(Block.cloth,1,i),Character.valueOf('S'), Item.stick});
				}
		GameRegistry.addRecipe(new ItemStack(quiver), new Object[]
				{"L L","L L","LLL",Character.valueOf('L'),Item.leather});
		for (int i=0;i<5;i++)
			{
			if (i<2)			
				GameRegistry.addRecipe(new ItemStack(shield[i]), new Object[]
						{" L ","LLL"," L ",Character.valueOf('L'),i==0?Item.leather:Block.wood});
			else 
				GameRegistry.addRecipe(new ItemStack(shield[i]), new Object[]
						{"L L","LLL"," L ",Character.valueOf('L'),i==2?Item.ingotIron:i==3?Item.diamond:Item.ingotGold});
			GameRegistry.addRecipe(new ItemStack(warAxe[i],1), new Object[]
					{"L L","LSL"," S ",Character.valueOf('S'), Item.stick,Character.valueOf('L'),i==0?Block.wood:i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.diamond:Item.ingotGold});
			GameRegistry.addRecipe(new ItemStack(mace[i],1), new Object[]
					{" LL"," LL","S  ",Character.valueOf('S'), Item.stick,Character.valueOf('L'),i==0?Block.wood:i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.diamond:Item.ingotGold});
			if (i==0)
				GameRegistry.addRecipe(new ItemStack(spear[i]), new Object[]
						{"  S"," S ","S  ",Character.valueOf('S'), Item.stick});
			else
				GameRegistry.addRecipe(new ItemStack(spear[i]), new Object[]
						{"  I"," S ",Character.valueOf('S'), new ItemStack(spear[0],1),Character.valueOf('I'),i==1?Block.cobblestone:i==2?Item.ingotIron:i==3?Item.diamond:Item.ingotGold});
			
			}//Chains and chain armor recipes
		GameRegistry.addShapelessRecipe(new ItemStack(chains,2),new Object[]{Item.ingotIron});
		GameRegistry.addRecipe(new ItemStack(Item.helmetChain),  new Object[]
				{"LLL","L L",Character.valueOf('L'),chains});
		GameRegistry.addRecipe(new ItemStack(Item.plateChain),  new Object[]
				{"L L","LLL","LLL",Character.valueOf('L'),chains});
		GameRegistry.addRecipe(new ItemStack(Item.legsChain),  new Object[]
				{"LLL","L L","L L",Character.valueOf('L'),chains});
		GameRegistry.addRecipe(new ItemStack(Item.bootsChain),  new Object[]
				{"L L","L L",Character.valueOf('L'),chains});
	}

	public static void addNames() {
		LanguageRegistry.addName(banner, itemNames[0]);
		LanguageRegistry.addName(quiver, itemNames[1]);
		LanguageRegistry.addName(chains, itemNames[2]);	
		for (int i=0;i<5;i++)
		{
			LanguageRegistry.addName(warAxe[i],itemNames[3]);
			LanguageRegistry.addName(mace[i],itemNames[4]);
			LanguageRegistry.addName(spear[i],itemNames[5]);
			LanguageRegistry.addName(shield[i],itemNames[6]);
			/*if(i<4)	
				LanguageRegistry.addName(knightArmor[i],itemNames[7]);*/
		}
	}
}
