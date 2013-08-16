package mods.battlegear2.utils;

import java.awt.Color;

import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.heraldry.HeraldryIcon;
import mods.battlegear2.heraldry.HeraldryPositions;
import mods.battlegear2.heraldry.HeraldyPattern;
import mods.battlegear2.heraldry.SigilHelper;
import mods.battlegear2.inventory.CreativeTabMB_B_2;
import mods.battlegear2.items.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

public class BattlegearConfig {
	public static final CreativeTabs customTab=new CreativeTabMB_B_2("Battlegear2");
	public static boolean forceBackSheath = false;
	public static final String[] itemNames = new String[] {"heraldric","chain","quiver", "dagger","waraxe","mace","spear","shield","knight_armour"};
	public static final String[] toolTypes = new String[] {"wood", "stone", "iron", "diamond", "gold"};
    public static final String[] shieldTypes = new String[] {"wood", "hide", "iron", "diamond", "gold"};
	public static final String[] armourTypes = new String[] {"helmet", "plate", "legs", "boots"};
	public static final int firstDefaultItemIndex = 26201;
	public static int[] itemOffests = new int[]{0, 1, 2, 5, 10, 15, 20, 25, 30};
	
	//Valid weapons ids are from vanilla swords
	public static int[] setID=new int[8],validWeaponsID={11,12,16,20,27};
	
	public static ItemWeapon[] dagger=new ItemWeapon[5],warAxe=new ItemWeapon[5],mace=new ItemWeapon[5],spear=new ItemWeapon[5];
    public static ItemShield[] shield=new ItemShield[5];
	public static Item chain,quiver,heradricItem;
	public static Block banner;
	public static ItemBlock bannerItem;
	public static ItemArmor[] knightArmor=new ItemArmor[4];


	
	
	public static void getConfig(Configuration config) {
		//System.out.println("Config");
		config.load();
        heradricItem = new ItemHeradryIcon(config.get(config.CATEGORY_ITEM, itemNames[0], firstDefaultItemIndex).getInt());
        
        chain = new Item(config.get(config.CATEGORY_ITEM, itemNames[1], firstDefaultItemIndex+itemOffests[1]).getInt());
    	chain.setUnlocalizedName("battlegear2:"+itemNames[1]).func_111206_d("battlegear2:"+itemNames[1]).setCreativeTab(customTab);
		
        /*
    	for(int i = 0; i < armourTypes.length; i++){
	    	knightArmor[i] = new ItemKnightArmour(
	    			config.get(config.CATEGORY_ITEM, itemNames[8]+"."+armourTypes[i],
	    					firstDefaultItemIndex+itemOffests[8]+i).getInt(),
	    					i);
    	}
        /*
    	//banner = new BlockBanner(config.get(config.CATEGORY_BLOCK, "Banner", 425).getInt());
    	banner = new BlockBanner(config.getBlock("Banner", 2650).getInt());
    	GameRegistry.registerBlock(banner, "Banner");
		GameRegistry.registerTileEntity(TileEntityBanner.class, "MBBanner");
		
		
		Item.itemsList[banner.blockID] = null;
		bannerItem = (new BlockItemBanner(banner.blockID-256));
		Item.itemsList[banner.blockID] = bannerItem;
		*/
		

        	
        forceBackSheath=config.get(config.CATEGORY_GENERAL, "Force Back Sheath", false).getBoolean(false);


        quiver = new Item(config.get(config.CATEGORY_ITEM, itemNames[2], firstDefaultItemIndex+2).getInt());
        quiver.func_111206_d("battlegear2:"+itemNames[2]).setUnlocalizedName(itemNames[2]).setCreativeTab(customTab);
        	
        	
        for(int i = 0; i < 5; i++){
        	EnumToolMaterial material = EnumToolMaterial.values()[i];

            warAxe[i]=new ItemWaraxe(
                    config.get(config.CATEGORY_ITEM, itemNames[4]+toolTypes[i], firstDefaultItemIndex+itemOffests[4]+i).getInt(),
                    material, itemNames[4], i==4?2:1);

        	dagger[i]=new ItemDagger(
        			config.get(config.CATEGORY_ITEM, itemNames[3]+"_"+toolTypes[i], firstDefaultItemIndex+itemOffests[3]+i).getInt(),
        			material, itemNames[3]);

    		mace[i]=new ItemMace(
    				config.get(config.CATEGORY_ITEM, itemNames[5]+toolTypes[i], firstDefaultItemIndex+itemOffests[5]+i).getInt(),
    				material, itemNames[5], 0.05F + 0.05F*i);
    		spear[i]=new ItemSpear(
    				config.get(config.CATEGORY_ITEM, itemNames[6]+toolTypes[i], firstDefaultItemIndex+itemOffests[6]+i).getInt(),
    				material, itemNames[6]);

            /*
            shield[i] = new ItemShield(
                    config.get(config.CATEGORY_ITEM, itemNames[7]+shieldTypes[i], firstDefaultItemIndex+itemOffests[7]+i).getInt()
            );
            */

        }

        int i = 0;
        shield[i] = new ItemShield(
                config.get(config.CATEGORY_ITEM, itemNames[7]+shieldTypes[i], firstDefaultItemIndex+itemOffests[7]+i).getInt()
        );

        
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
		
		ItemStack woodStack = new ItemStack(Block.planks.blockID,1,OreDictionary.WILDCARD_VALUE);
		for(int i = 0; i < 5; i++){
			Item craftingMaterial = Item.itemsList[EnumToolMaterial.values()[i].getToolCraftingMaterial()];
			GameRegistry.addRecipe(
					new ItemStack(warAxe[i]),
					new Object[] {"L L","LSL"," S ",
						Character.valueOf('S'), Item.stick,
						Character.valueOf('L'),
						i!=0?craftingMaterial:woodStack
				});
            GameRegistry.addRecipe(
                    new ItemStack(mace[i]),
                    new Object[] {" LL"," LL","S  ",
                            Character.valueOf('S'), Item.stick,
                            Character.valueOf('L'),
                            i!=0?craftingMaterial:woodStack
                    });
            GameRegistry.addRecipe(
                    new ItemStack(dagger[i]),
                    new Object[] {"L","S",
                            Character.valueOf('S'), Item.stick,
                            Character.valueOf('L'),
                            i!=0?craftingMaterial:woodStack
                    });
            if(i == 0){
                GameRegistry.addRecipe(
                        new ItemStack(spear[i]),
                        new Object[] {"  S"," S ","S  ",
                                Character.valueOf('S'), Item.stick
                        });
            }else{
                GameRegistry.addRecipe(
                        new ItemStack(spear[i]),
                        new Object[] {" L","S ",
                                Character.valueOf('S'), spear[0],
                                Character.valueOf('L'), craftingMaterial
                        });
            }
		}
		
		for(int i = 0; i < 4; i++){
			//GameRegistry.addRecipe(new KnightArmourRecipie(i));
		}

        /*
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 16; y++){
				ItemStack bannerStack = new ItemStack(bannerItem);
				((IHeraldyItem)bannerStack.getItem()).setHeraldryCode(bannerStack,
						SigilHelper.packSigil(HeraldyPattern.HORIZONTAL_BLOCK, (byte) 0, (byte) 0,
                                new Color(ItemDye.dyeColors[15 - x]), new Color(ItemDye.dyeColors[15 - y]),
                                HeraldryIcon.Blank, HeraldryPositions.SINGLE, Color.WHITE, Color.WHITE)
						);
				GameRegistry.addRecipe(bannerStack,  new Object[]
						{" a "," b ", " S ",
						Character.valueOf('a'), new ItemStack(Block.cloth,0, x),
						Character.valueOf('b'), new ItemStack(Block.cloth,0, y),
						Character.valueOf('S'), Item.stick
						}
				);
			}
		}
		
		GameRegistry.addRecipe(new ItemStack(bannerItem),  new Object[]
				{" W "," W ", " S ",Character.valueOf('W'),Block.cloth, Character.valueOf('S'), Item.stick});
		*/
		

	}

	
}
