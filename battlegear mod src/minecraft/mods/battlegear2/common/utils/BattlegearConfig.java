package mods.battlegear2.common.utils;

import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.blocks.BlockBanner;
import mods.battlegear2.common.blocks.BlockItemBanner;
import mods.battlegear2.common.blocks.TileEntityBanner;
import mods.battlegear2.common.heraldry.KnightArmourRecipie;
import mods.battlegear2.common.inventory.CreativeTabMB_B_2;
import mods.battlegear2.common.items.ItemDagger;
import mods.battlegear2.common.items.ItemHeradryIcon;
import mods.battlegear2.common.items.ItemKnightArmour;
import mods.battlegear2.common.items.ItemMace;
import mods.battlegear2.common.items.ItemSpear;
import mods.battlegear2.common.items.ItemWaraxe;
import mods.battlegear2.common.items.ItemWeapon;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BattlegearConfig {
	public static final CreativeTabs customTab=new CreativeTabMB_B_2("Battlegear2");
	public static boolean forceBackSheath = false;
	public static final String[] itemNames = new String[] {"heraldric","chain","quiver", "dagger","waraxe","mace","spear","shield","knight_armour"};
	public static final String[] toolTypes = new String[] {"wood", "stone", "iron", "diamond", "gold"};
	public static final String[] armourTypes = new String[] {"helmet", "plate", "legs", "boots"};
	public static final int firstDefaultItemIndex = 26201;
	public static int[] itemOffests = new int[]{0, 1, 2, 5, 10, 15, 20, 25, 30};
	
	//Valid weapons ids are from vanilla swords
	public static int[] setID=new int[8],validWeaponsID={11,12,16,20,27};
	
	public static ItemWeapon[] dagger=new ItemWeapon[5],warAxe=new ItemWeapon[5],mace=new ItemWeapon[5],spear=new ItemWeapon[5],shield=new ItemWeapon[5];
	public static Item chain,quiver,heradricItem;
	public static Block banner;
	public static ItemBlock bannerItem;
	public static ItemArmor[] knightArmor=new ItemArmor[4];
	
	
	
	
	public static void getConfig(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile(),true);
        config.load();
        
        heradricItem = new ItemHeradryIcon(config.get(config.CATEGORY_ITEM, itemNames[0], firstDefaultItemIndex).getInt());
        
        chain = new Item(config.get(config.CATEGORY_ITEM, itemNames[1], firstDefaultItemIndex+itemOffests[1]).getInt());
    	chain.setUnlocalizedName("battlegear2:chain");
    	
    	for(int i = 0; i < armourTypes.length; i++){
	    	knightArmor[i] = new ItemKnightArmour(
	    			config.get(config.CATEGORY_ITEM, itemNames[8]+"."+armourTypes[i],
	    					firstDefaultItemIndex+itemOffests[8]+i).getInt(),
	    					i);
    	}
    	
    	for(int i = 0; i < 5; i++){
    		EnumToolMaterial material = EnumToolMaterial.values()[i];
    		warAxe[i]=new ItemWaraxe(
					config.get(config.CATEGORY_ITEM, itemNames[4]+toolTypes[i], firstDefaultItemIndex+itemOffests[4]+i).getInt(),
					material, itemNames[4], i==4?2:1);
    	}
    		
    	//banner = new BlockBanner(config.get(config.CATEGORY_BLOCK, "Banner", 425).getInt());
    	banner = new BlockBanner(config.getBlock("Banner", 2650).getInt());
    	GameRegistry.registerBlock(banner, "Banner");
		GameRegistry.registerTileEntity(TileEntityBanner.class, "MBBanner");
		
		Item.itemsList[banner.blockID] = null;
		Item.itemsList[banner.blockID] = (new BlockItemBanner(banner.blockID-256));
		
		
        if(BattleGear.debug){
        	
        	forceBackSheath=config.get(config.CATEGORY_GENERAL, "Force Back Sheath", false).getBoolean(false);
        	
        	quiver = new Item(config.get(config.CATEGORY_ITEM, itemNames[2], firstDefaultItemIndex+2).getInt());
        	quiver.setUnlocalizedName("quiver");
        	
        	
        	for(int i = 0; i < 5; i++){
        		EnumToolMaterial material = EnumToolMaterial.values()[i];
        		
        		
        		dagger[i]=new ItemDagger(
        				config.get(config.CATEGORY_ITEM, itemNames[3]+"_"+toolTypes[i], firstDefaultItemIndex+itemOffests[3]+i).getInt(),
        				material, itemNames[3]);
    			
    			mace[i]=new ItemMace(
    					config.get(config.CATEGORY_ITEM, itemNames[5]+toolTypes[i], firstDefaultItemIndex+itemOffests[5]+i).getInt(),
    					material, itemNames[5]);
    			spear[i]=new ItemSpear(
    					config.get(config.CATEGORY_ITEM, itemNames[6]+toolTypes[i], firstDefaultItemIndex+itemOffests[6]+i).getInt(),
    					material, itemNames[6]);
        	}
        }
        
        //validWeaponsID=config.get(config.CATEGORY_GENERAL, "Valid Weapon IDs",new int[]{11,12,16,20,27}).getIntList();
        if (config.hasChanged())
        {        
      	  config.save();     	
        } 
	}

	public static void registerRecipes() {//Those are old recipes found on your M-B topic
		
		//2 Iron ingots = 3 chain. This is because the chain armour has increased in damage resistance
		
		GameRegistry.addShapedRecipe(new ItemStack(chain, 3), new Object[]{
			"I", "I", Character.valueOf('I'), Item.ingotIron
		});
		
		GameRegistry.addRecipe(new ItemStack(Item.helmetChain),  new Object[]
				{"LLL","L L",Character.valueOf('L'),chain});
		GameRegistry.addRecipe(new ItemStack(Item.plateChain),  new Object[]
				{"L L","LLL","LLL",Character.valueOf('L'),chain});
		GameRegistry.addRecipe(new ItemStack(Item.legsChain),  new Object[]
				{"LLL","L L","L L",Character.valueOf('L'),chain});
		GameRegistry.addRecipe(new ItemStack(Item.bootsChain),  new Object[]
				{"L L","L L",Character.valueOf('L'),chain});
		

		for(int i = 0; i < 5; i++){
			Item craftingMaterial = Item.itemsList[EnumToolMaterial.values()[i].getToolCraftingMaterial()];
			GameRegistry.addRecipe(
					new ItemStack(warAxe[i]), 
					new Object[] {"L L","LSL"," S ",
						Character.valueOf('S'), Item.stick,
						Character.valueOf('L'), 
						craftingMaterial
				});
		}
		
		for(int i = 0; i < 4; i++){
			GameRegistry.addRecipe(new KnightArmourRecipie(i));
		}
		
		
		if(BattleGear.debug){
			

			
		}
		
		/*
		if(debug){
			
			GameRegistry.addRecipe(new ItemStack(quiver), new Object[]
					{"L L","L L","LLL",
				Character.valueOf('L'),Item.leather});
			
			GameRegistry.addShapelessRecipe(new ItemStack(chain,2),Item.ingotIron);
			GameRegistry.addRecipe(new ItemStack(Item.helmetChain),  new Object[]
					{"LLL","L L",Character.valueOf('L'),chain});
			GameRegistry.addRecipe(new ItemStack(Item.plateChain),  new Object[]
					{"L L","LLL","LLL",Character.valueOf('L'),chain});
			GameRegistry.addRecipe(new ItemStack(Item.legsChain),  new Object[]
					{"LLL","L L","L L",Character.valueOf('L'),chain});
			GameRegistry.addRecipe(new ItemStack(Item.bootsChain),  new Object[]
					{"L L","L L",Character.valueOf('L'),chain});
			
			for(int i = 0; i < 5; i++){
				EnumToolMaterial material = EnumToolMaterial.values()[i];
				GameRegistry.addRecipe(
						new ItemStack(dagger[i]), 
						new Object[] {"L","S",
							Character.valueOf('S'), Item.stick,
							Character.valueOf('L'), material.customCraftingMaterial
					});
				
				GameRegistry.addRecipe(new ItemStack(warAxe[i]), 
						new Object[] {"L L","LSL"," S ",
							Character.valueOf('S'), Item.stick,
							Character.valueOf('L'), material.customCraftingMaterial
					});
				
				GameRegistry.addRecipe(new ItemStack(mace[i]), 
						new Object[] {" LL"," LL","S  ",
							Character.valueOf('S'), Item.stick,
							Character.valueOf('L'), material.customCraftingMaterial
					});
				
				if(i == 0){
					GameRegistry.addRecipe(new ItemStack(spear[i]), new Object[]
							{"  S"," S ","S  ",Character.valueOf('S'), Item.stick});
				}else{
					//May need to change this so we can craft with damaged items
					GameRegistry.addRecipe(new ItemStack(spear[i]), new Object[]
							{"  I"," S ",Character.valueOf('S'), new ItemStack(spear[0],1),
						Character.valueOf('I'),new ItemStack(spear[i].getMaterial().getToolCraftingMaterial(),1,0)});
				}

			}
			
		}*/
	}

	
}
