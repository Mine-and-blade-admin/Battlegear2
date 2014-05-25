package mods.battlegear2.utils;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.shield.ShieldType;
import mods.battlegear2.heraldry.BlockFlagPole;
import mods.battlegear2.heraldry.ItemBlockFlagPole;
import mods.battlegear2.heraldry.KnightArmourRecipie;
import mods.battlegear2.heraldry.TileEntityFlagPole;
import mods.battlegear2.inventory.CreativeTabMB_B_2;
import mods.battlegear2.items.*;
import mods.battlegear2.recipies.DyeRecipie;
import mods.battlegear2.recipies.QuiverRecipie2;
import mods.battlegear2.recipies.ShieldRemoveArrowRecipie;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BattlegearConfig {
    public static final String MODID = "battlegear2:";
    private static Configuration file;
	public static final CreativeTabs customTab=new CreativeTabMB_B_2("Battlegear2");
	public static boolean forceBackSheath = false, arrowForceRendered = true, enableSkeletonQuiver = true;
	public static boolean enableGUIKeys = false, enableGuiButtons = true;
	public static final String[] itemNames = {"heraldric","chain","quiver","dagger","waraxe","mace","spear","shield","knight.armour", "mb.arrow", "flagpole"};
	public static final String[] renderNames = {"spear", "shield", "bow", "quiver", "flagpole"};
    public static final String[] toolTypes = {"wood", "stone", "iron", "diamond", "gold"};
    public static final String[] shieldTypes = {"wood", "hide", "iron", "diamond", "gold"};
	public static final String[] armourTypes = {"helmet", "plate", "legs", "boots"};
	public static final String[] enchantsName = {"BashWeight", "BashPower", "BashDamage", "ShieldUsage", "ShieldRecovery", "BowLoot", "BowCharge"};
	public static int[] enchantsId = {125, 126, 127, 128, 129, 130, 131};
	public static ItemWeapon[] dagger=new ItemWeapon[toolTypes.length],warAxe=new ItemWeapon[toolTypes.length],mace=new ItemWeapon[toolTypes.length],spear=new ItemWeapon[toolTypes.length];
    public static ItemShield[] shield=new ItemShield[shieldTypes.length];
	public static Item chain,quiver,heradricItem,MbArrows;
	public static BlockFlagPole banner;
	public static ItemArmor[] knightArmor=new ItemArmor[armourTypes.length];
    private static String[] comments = new String[4];
	public static String[] disabledItems = new String[0];
    public static String[] disabledRecipies = new String[0];
    public static String[] disabledRenderers = new String[0];

    public static double[] skeletonArrowSpawnRate = new double[ItemMBArrow.names.length];
	public static int[] quiverBarOffset = new int[2], shieldBarOffset = new int[2], battleBarOffset = new int[4];
	
	public static void getConfig(Configuration config) {
        file = config;
		config.load();
		
		StringBuffer sb = new StringBuffer();
        sb.append("This will disable completely the provided item, along with their renderers and recipes including them.\n");
        sb.append("These should all be placed on separate lines between the provided \'<\' and \'>\'.  \n");
        sb.append("The valid values are: \n");
        int count = 0;
        for(int i = 0; i < itemNames.length; i++){
            sb.append(itemNames[i]);
            sb.append(", ");
            count++;
            if(count % 5 == 0){
                sb.append("\n");
            }
        }
        comments[0] = sb.toString();
        disabledItems = config.get(config.CATEGORY_GENERAL, "Disabled Items", new String[0], comments[0]).getStringList();
        Arrays.sort(disabledItems);

        if(Arrays.binarySearch(disabledItems, itemNames[0]) < 0){
            heradricItem = new HeraldryCrest().setCreativeTab(customTab).setUnlocalizedName(MODID+itemNames[0]).setTextureName(MODID+"bg-icon");
        }

        if(Arrays.binarySearch(disabledItems, itemNames[10]) < 0){
            banner = (BlockFlagPole)new BlockFlagPole().setCreativeTab(customTab).setBlockName(MODID+itemNames[10]);
            GameRegistry.registerBlock(banner, ItemBlockFlagPole.class, MODID+itemNames[10]);
            GameRegistry.registerTileEntity(TileEntityFlagPole.class, MODID+itemNames[10]);
        }

        if(Arrays.binarySearch(disabledItems, itemNames[1]) < 0){
        	chain = new Item().setUnlocalizedName(MODID+itemNames[1]).setTextureName(MODID+itemNames[1]).setCreativeTab(customTab);
        }
        enableGUIKeys=config.get(config.CATEGORY_GENERAL, "Enable GUI Keys", false).getBoolean(false);
        enableGuiButtons=config.get(config.CATEGORY_GENERAL, "Enable GUI Buttons", true).getBoolean(true);
        
        for(int i=0; i<enchantsName.length; i++){
        	enchantsId[i] = config.get("EnchantmentsID", enchantsName[i], enchantsId[i]).getInt();
        }
        config.get("Coremod", "ASM debug Mode", false, "Only use for advanced bug reporting when asked by a dev.");
        
        if(Arrays.binarySearch(disabledItems, itemNames[2]) < 0){
        	quiver = new ItemQuiver().setUnlocalizedName(MODID+itemNames[2]).setTextureName(MODID+"quiver/"+itemNames[2]).setCreativeTab(customTab);
        }
        if(Arrays.binarySearch(disabledItems, itemNames[9]) < 0){
        	MbArrows = new ItemMBArrow().setUnlocalizedName(MODID + itemNames[9]).setTextureName(MODID + itemNames[9]).setCreativeTab(customTab).setContainerItem(Items.arrow);
        }
        String category = "Skeleton CustomArrow Spawn Rate";
        config.addCustomCategoryComment(category, "The spawn rate (between 0 & 1) that Skeletons will spawn with Arrows provided from this mod");

        //default 10% for everything but ender (which is 0%)
        for(int i = 0; i < ItemMBArrow.names.length; i++){
            skeletonArrowSpawnRate[i] = config.get(category, ItemMBArrow.names[i], i!=1 && i!=5?0.1F:0).getDouble(i!=1?0.1F:0);
        }
        
        sb = new StringBuffer();
        sb.append("This will disable the crafting recipie for the provided item/blocks.\n");
        sb.append("It should be noted that this WILL NOT remove the item from the game, it will only disable the recipe.\n");
        sb.append("In this way the items may still be obtained through creative mode and cheats, but playes will be unable to craft them.\n");
        sb.append("These should all be placed on separate lines between the provided \'<\' and \'>\'. The valid values are: \n");

        count = 0;
        for(int i = 1; i < itemNames.length; i++){

            if(i != 9){
                sb.append(itemNames[i]);
                sb.append(", ");
                count++;
                if(count % 5 == 0){
                    sb.append("\n");
                }
            }
        }

        for(int i = 0; i < ItemMBArrow.names.length; i++){
            sb.append(itemNames[9]);
            sb.append('.');
            sb.append(ItemMBArrow.names[i]);
            sb.append(", ");
            count++;
            if(count % 5 == 0){
                sb.append("\n");
            }
        }

        int last_comma = sb.lastIndexOf(",");
        if(last_comma > 0){
            sb.deleteCharAt(last_comma);
        }
        comments[1] = sb.toString();
        disabledRecipies = config.get(config.CATEGORY_GENERAL, "Disabled Recipies", new String[0], comments[1]).getStringList();
        Arrays.sort(disabledRecipies);

        category = "Rendering";
        config.addCustomCategoryComment(category, "This category is client side, you don't have to sync its values with server in multiplayer.");
        sb = new StringBuffer();
        sb.append("This will disable the special rendering for the provided item.\n");
        sb.append("These should all be placed on separate lines between the provided \'<\' and \'>\'.  \n");
        sb.append("The valid values are: \n");
        for(int i = 0; i < renderNames.length; i++){
            sb.append(renderNames[i]);
            sb.append(", ");
        }
        comments[2] = sb.toString();
        disabledRenderers = config.get(category, "Disabled Renderers", new String[0], comments[2]).getStringList();
        Arrays.sort(disabledRenderers);
        comments[3] = "Change to move this bar in your gui";
        String[] pos = {"horizontal", "vertical"};
        for(int i = 0; i<2; i++){
            quiverBarOffset[i] = config.get(category, "Quiver hotbar relative "+pos[i]+" position", 0, comments[3]).getInt();
            shieldBarOffset[i] = config.get(category, "Shield bar relative "+pos[i]+" position", 0, comments[3]).getInt();
            battleBarOffset[i] = config.get(category, "Offhand hotbar relative "+pos[i]+" position", 0, comments[3]).getInt();
            battleBarOffset[i+2] = config.get(category, "Mainhand hotbar relative "+pos[i]+" position", 0, comments[3]).getInt();
        }
        arrowForceRendered = config.get(category, "Render arrow with bow uncharged", true).getBoolean(true);
        forceBackSheath=config.get(category, "Force Back Sheath", false).getBoolean(false);
        enableSkeletonQuiver=config.get(category, "Render quiver on skeleton back", true).getBoolean(true);

        ShieldType[] types = {ShieldType.WOOD, ShieldType.HIDE, ShieldType.IRON, ShieldType.DIAMOND, ShieldType.GOLD};
        for(int i = 0; i < 5; i++){
        	ToolMaterial material = ToolMaterial.values()[i];
        	if(Arrays.binarySearch(disabledItems, itemNames[4]) < 0){
	            warAxe[i]=new ItemWaraxe(material, itemNames[4], i==4?2:1);
        	}
        	if(Arrays.binarySearch(disabledItems, itemNames[3]) < 0){
	        	dagger[i]=new ItemDagger(material, itemNames[3], 0.5F, -2);
        	}
        	if(Arrays.binarySearch(disabledItems, itemNames[5]) < 0){
	    		mace[i]=new ItemMace(material, itemNames[5], 0.05F + 0.05F*i);
        	}
        	if(Arrays.binarySearch(disabledItems, itemNames[6]) < 0){
	    		spear[i]=new ItemSpear(material, itemNames[6], 3, 2.0F);
        	}
        	if(Arrays.binarySearch(disabledItems, itemNames[7]) < 0){
                shield[i] = new ItemShield(types[i]);
        	}
            if(i!=4 && Arrays.binarySearch(disabledItems, itemNames[8]) < 0){
                knightArmor[i] = new ItemKnightArmour(i);
            }
        }
        if (config.hasChanged()){        
        	config.save();     	
        }
        try{
            for(Field f: BattlegearConfig.class.getFields()){
                if(Item.class.isAssignableFrom(f.getType())){
                    Item it = (Item)f.get(null);
                    if(it!=null){
                        GameRegistry.registerItem(it, it.getUnlocalizedName());
                    }
                }
            }
        }catch(Exception e){

        }
	}

	public static void registerRecipes() {
		
		//2 Iron ingots = 3 chain. This is because the chain armour has increased in damage resistance

		if(chain!=null){
	        if(Arrays.binarySearch(disabledRecipies, itemNames[1]) < 0)
	            GameRegistry.addShapedRecipe(new ItemStack(chain, 3),
	                "I", "I", 'I', Items.iron_ingot
	            );
	        if(Arrays.binarySearch(disabledRecipies, "chain.armour") < 0){
	            //Chain armor recipes
	            GameRegistry.addRecipe(new ItemStack(Items.chainmail_helmet),
	                    "LLL","L L",'L',chain);
	            GameRegistry.addRecipe(new ItemStack(Items.chainmail_chestplate),
	                    "L L","LLL","LLL",'L',chain);
	            GameRegistry.addRecipe(new ItemStack(Items.chainmail_leggings),
	                    "LLL","L L","L L",'L',chain);
	            GameRegistry.addRecipe(new ItemStack(Items.chainmail_boots),
	                    "L L","L L",'L',chain);
	        }
		}

		if(quiver!=null){
	        //Quiver recipes :
	        if(Arrays.binarySearch(disabledRecipies, itemNames[2])  < 0)
	            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(quiver),
	                "X X", "X X","XXX",'X', Items.leather));

            RecipeSorter.register("battlegear:quiverfilling", QuiverRecipie2.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	        GameRegistry.addRecipe(new QuiverRecipie2());
		}
        RecipeSorter.register("battlegear:dyeing", DyeRecipie.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        GameRegistry.addRecipe(new DyeRecipie());

        
		//Weapon recipes
		String woodStack = "plankWood";
		for(int i = 0; i < 5; i++){
			Item craftingMaterial = ToolMaterial.values()[i].func_150995_f();
            if(warAxe[i]!=null && Arrays.binarySearch(disabledRecipies, itemNames[4])  < 0){
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(warAxe[i]), "L L","LSL"," S ",
                            'S', "stickWood",
                            'L', i!=0?craftingMaterial:woodStack));
            }
            if(mace[i]!=null && Arrays.binarySearch(disabledRecipies, itemNames[5])  < 0) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mace[i]), " LL"," LL","S  ",
                                'S', "stickWood",
                                'L', i!=0?craftingMaterial:woodStack));
            }
            if(dagger[i]!=null && Arrays.binarySearch(disabledRecipies, itemNames[3])  < 0){
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dagger[i]), "L","S",
                                'S', "stickWood",
                                'L', i!=0?craftingMaterial:woodStack));
            }

            if(spear[i]!=null && Arrays.binarySearch(disabledRecipies, itemNames[6])  < 0){
                if(i == 0){
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spear[i]), "  S"," S ","S  ",
                                    'S', "stickWood"));
                }else{
                    GameRegistry.addRecipe(new ItemStack(spear[i]), " L","S ",
                                    'S', spear[0],
                                    'L', craftingMaterial);
                }
            }
		}

        if(Arrays.binarySearch(disabledItems, itemNames[7]) < 0 && Arrays.binarySearch(disabledRecipies, itemNames[7]) < 0){
            //Wood Shield
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shield[0]), " W ","WWW", " W ",
                            'W', woodStack));
            //Hide Shield
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shield[1]), " H ","HWH", " H ",
                            'W', woodStack,
                            'H', Items.leather));
            //Iron Shield
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shield[2]), "I I","IWI", " I ",
                            'W', woodStack,
                            'I', Items.iron_ingot));
            //Diamond Shield
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shield[3]), "I I","IWI", " I ",
                            'W', woodStack,
                            'I', Items.diamond));
            //Gold Shield
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shield[4]), "I I","IWI", " I ",
                            'W', woodStack,
                            'I', Items.gold_ingot));
            RecipeSorter.register("battlegear:shieldarrowtaking", ShieldRemoveArrowRecipie.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
            GameRegistry.addRecipe(new ShieldRemoveArrowRecipie());
        }

        if(MbArrows!=null){
	        for(int i=0;i<ItemMBArrow.component.length;i++){
		        if(Arrays.binarySearch(disabledRecipies, itemNames[9]+"."+ItemMBArrow.names[i]) < 0){
		            GameRegistry.addRecipe(new ItemStack(MbArrows, 1, i), "G","A",
		                            'G', ItemMBArrow.component[i],
		                            'A', Items.arrow
		                    );
		            if(i!=2 && i!=3){//We can't have those components being duplicated by an "Infinity" bow
		            	GameRegistry.addShapelessRecipe(new ItemStack(ItemMBArrow.component[i]), new ItemStack(MbArrows, 1, i));
		            }
		        }
	        }
        }

        RecipeSorter.register("battlegear:knightarmor", KnightArmourRecipie.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		for(int i = 0; i < 4; i++){
			GameRegistry.addRecipe(new KnightArmourRecipie(i));
		}

        if(banner!=null){
            for(int i = 0; i < 7; i++){
                Object temp = i < 4 ? new ItemStack(Blocks.log, 1, i):i==4?Items.iron_ingot:new ItemStack(Blocks.log2, 1, i-5);
                GameRegistry.addRecipe(new ItemStack(banner, 4, i), "W", "W", "W", 'W', temp);
            }
        }
        /*
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 16; y++){
				ItemStack bannerStack = new ItemStack(bannerItem);
				((IHeraldyItem)bannerStack.getItem()).setHeraldryCode(bannerStack,
						SigilHelper.packSigil(HeraldryPattern.HORIZONTAL_BLOCK, (byte) 0, (byte) 0,
                                new Color(ItemDye.dyeColors[15 - x]), new Color(ItemDye.dyeColors[15 - y]),
                                HeraldryIcon.Blank, HeraldryPositions.SINGLE, Color.WHITE, Color.WHITE)
						);
				GameRegistry.addRecipe(bannerStack,
						" a "," b ", " S ",
						'a', new ItemStack(Block.cloth,0, x),
						'b', new ItemStack(Block.cloth,0, y),
						'S', Item.stick
						
				);
			}
		}
		
		GameRegistry.addRecipe(new ItemStack(bannerItem), 
				" W "," W ", " S ",'W',Block.cloth, 'S', Item.stick);
		*/

	}

    public static void refreshConfig(){
        try{
            Arrays.sort(disabledRenderers);
            file.get("Rendering", "Disabled Renderers", new String[0], comments[2]).set(disabledRenderers);
            file.get("Rendering", "Render arrow with bow uncharged", true).set(arrowForceRendered);
            file.get("Rendering", "Force Back Sheath", false).set(forceBackSheath);
            file.get("Rendering", "Render quiver on skeleton back", true).set(enableSkeletonQuiver);
            file.get(file.CATEGORY_GENERAL, "Enable GUI Keys", false).set(enableGUIKeys);
            file.get(file.CATEGORY_GENERAL, "Enable GUI Buttons", true).set(enableGuiButtons);
            file.save();
            Battlegear.proxy.registerItemRenderers();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void refreshGuiValues(){
        try{
            String[] pos = {"horizontal", "vertical"};
            for(int i = 0; i<2; i++){
                file.get("Rendering", "Quiver hotbar relative "+pos[i]+" position", 0, comments[3]).set(quiverBarOffset[i]);
                file.get("Rendering", "Shield bar relative "+pos[i]+" position", 0, comments[3]).set(shieldBarOffset[i]);
                file.get("Rendering", "Offhand hotbar relative "+pos[i]+" position", 0, comments[3]).set(battleBarOffset[i]);
                file.get("Rendering", "Mainhand hotbar relative "+pos[i]+" position", 0, comments[3]).set(battleBarOffset[i+2]);
            }
            file.save();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
