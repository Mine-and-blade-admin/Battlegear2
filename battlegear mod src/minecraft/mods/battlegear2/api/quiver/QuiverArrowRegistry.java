package mods.battlegear2.api.quiver;

import java.util.*;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class QuiverArrowRegistry {

    private static Map<ItemStack, Class<? extends EntityArrow>> itemToClasses = new TreeMap<ItemStack, Class<? extends EntityArrow>>(new StackComparator());
    private static Map<Class<? extends EntityArrow>, ItemStack> classToItems = new HashMap<Class<? extends EntityArrow>, ItemStack>();
    private static List<IQuiverSelection> quiverSelectors = new ArrayList<IQuiverSelection>();
    private static List<IArrowFireHandler> fireHandlers = new ArrayList<IArrowFireHandler>();
    static{
        fireHandlers.add(new DefaultArrowFire());
    }

    /**
     * Adds an item to the known arrow lists
     * @param itemId the item id
     * @param itemMetadata the item damage value, as metadata
     * @param entityArrow the class from which the arrow entity will be constructed
     */
    public static void addArrowToRegistry(int itemId, int itemMetadata, Class<? extends EntityArrow> entityArrow){
        ItemStack stack = new ItemStack(itemId, 1, itemMetadata);
        addArrowToRegistry(stack, entityArrow);
    }

    /**
     * NBT sensitive version, can be called through
     * FMLInterModComms.sendMessage("battlegear2", "Arrow:"+classPath, itemStack);
     * where classPath is the full class path for the arrow class
     * @param stack
     * @param entityArrow the class from which the arrow entity will be constructed
     */
    public static void addArrowToRegistry(ItemStack stack, Class<? extends EntityArrow> entityArrow){
        ItemStack st = stack.copy();
        st.stackSize = 1;
        itemToClasses.put(st, entityArrow);
        classToItems.put(entityArrow, st);
    }

    /**
     * Adds a new quiver selection algorithm to the known list, can be called through
     * FMLInterModComms.sendMessage("battlegear2", "QuiverSelection", classpath);
     * where classPath is the full class path for the class implementing IQuiverSelection,
     * and said class has a default constructor to built from
     * @param handler the selection algorithm to add
     * @return true if it could be added
     */
    public static boolean addQuiverSelection(IQuiverSelection handler){
        return quiverSelectors.add(handler);
    }

    /**
     * Adds a new arrow firing handler to the known list, can be called through
     * FMLInterModComms.sendMessage("battlegear2", "FireHandler", classpath);
     * where classPath is the full class path for the class implementing IArrowFireHandler,
     * and said class has a default constructor to built from
     * @param handler the firing handler to add
     * @return true if it could be added
     */
    public static boolean addArrowFireHandler(IArrowFireHandler handler){
        return fireHandlers.add(handler);
    }

    /**
     * Search for an ItemStack whose item is an {@link #IArrowContainer2}
     * @param bow the bow in use
     * @param entityPlayer the player using it
     * @return the first non-null itemstack found through the quiver selection algorithms
     */
    public static ItemStack getArrowContainer(ItemStack bow, EntityPlayer entityPlayer) {
        ItemStack temp;
        for(IQuiverSelection handler:quiverSelectors){
            temp = handler.getQuiverFor(bow, entityPlayer);
            if(temp!=null){
                return temp;
            }
        }
        return null;
    }

    /**
     * Search for an EntityArrow to be spawned, used by the default ItemQuiver
     * @param arrow the stack which should define the arrow as item
     * @param world
     * @param player player using a bow to fire an arrow
     * @param charge amount of charge in the bow
     * @return the first non-null entity arrow built through the firing handlers
     */
    public static EntityArrow getArrowType(ItemStack arrow, World world, EntityPlayer player, float charge){
        EntityArrow result;
        for(IArrowFireHandler handler:fireHandlers){
            if(handler.canFireArrow(arrow, world, player, charge)){
                result = handler.getFiredArrow(arrow, world, player, charge);
                if(result!=null){
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * @param stack
     * @return the EntityArrow class attached to the given stack
     */
    public static Class<? extends EntityArrow> getArrowClass(ItemStack stack){
        return itemToClasses.get(stack);
    }

    /**
     * @param clazz
     * @return the ItemStack attached to the given EntityArrow class
     */
    public static ItemStack getItem(Class<? extends EntityArrow> clazz){
    	ItemStack temp = classToItems.get(clazz);
        if(temp == null){
			return new ItemStack(Item.arrow);
		}else{
			return temp.copy();
		}
    }

    /**
     *
     * @param test the ItemStack to check
     * @return true if that ItemStack is attached to an EntityArrow class
     */
    public static boolean isKnownArrow(ItemStack test){
    	return itemToClasses.containsKey(test);
    }

    /**
     * Tool class to compare ItemStack, since this class is final but doesn't implement the necessary method
     */
    static class StackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack stack, ItemStack stack2) {
            if(stack == stack2){
                return 0;
            }else{

                int idDiff = stack.itemID - stack2.itemID;
                if(idDiff != 0){
                    return idDiff;
                }else{
                	idDiff = stack.getItemDamage() - stack2.getItemDamage();
                	if(idDiff != 0){
                        return idDiff;
                    }else{
                    	int tag = 0;
                    	if(stack.hasTagCompound()){
                    		tag = stack.getTagCompound().hashCode();
                    	}
                    	int tag2 = 0;
                    	if(stack2.hasTagCompound()){
                    		tag2 = stack2.getTagCompound().hashCode();
                    	}
                    	return tag-tag2;
                    }
                }
            }
        }
    }

    /**
     * Default implementation of a arrow firing handler, which uses this registry of arrows to build an EntityArrow
     * with a constructor
     * If the arrow is unknown, or the constructor isn't valid, defers to other firing handlers silently
     */
    static class DefaultArrowFire implements IArrowFireHandler{

        @Override
        public boolean canFireArrow(ItemStack arrow, World world, EntityPlayer player, float charge) {
            return isKnownArrow(arrow);
        }

        @Override
        public EntityArrow getFiredArrow(ItemStack arrow, World world, EntityPlayer player, float charge) {
            Class clazz = getArrowClass(arrow);
            if(clazz != null){
                try {
                    return (EntityArrow)clazz.getConstructor(World.class, EntityLivingBase.class, Float.TYPE)
                            .newInstance(player.worldObj, player, charge);
                } catch (Exception ignored) {
                }
            }
            return null;
        }
    }
}
