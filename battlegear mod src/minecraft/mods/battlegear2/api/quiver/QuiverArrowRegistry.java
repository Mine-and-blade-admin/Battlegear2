package mods.battlegear2.api.quiver;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import mods.battlegear2.api.ISensible;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

public class QuiverArrowRegistry {

    private static Map<Item, Class<? extends EntityArrow>> itemToClasses = new HashMap<Item, Class<? extends EntityArrow>>();
    private static Map<ItemStack, Class<? extends EntityArrow>> stackToClasses = new TreeMap<ItemStack, Class<? extends EntityArrow>>(new StackComparator());
    private static Map<Class<? extends EntityArrow>, ItemStack> classToStacks = new HashMap<Class<? extends EntityArrow>, ItemStack>();
    private static List<IQuiverSelection> quiverSelectors = new ArrayList<IQuiverSelection>();
    private static List<IArrowFireHandler> fireHandlers = new ArrayList<IArrowFireHandler>();
    static{
        fireHandlers.add(new DefaultArrowFire());
    }

    /**
     * Adds an item to the known arrow lists, not metadata sensitive
     * @param itemId the item id
     * @param entityArrow the class from which the arrow entity will be constructed by the default fire handler (can be null, if custom fire handler is desired)
     */
    public static void addArrowToRegistry(Item itemId, Class<? extends EntityArrow> entityArrow){
        itemToClasses.put(itemId, entityArrow);
        if(entityArrow!=null)
            classToStacks.put(entityArrow, new ItemStack(itemId));
    }

    /**
     * Adds an item to the known arrow lists
     * @param itemId the item id
     * @param itemMetadata the item damage value, as metadata
     * @param entityArrow the class from which the arrow entity will be constructed by the default fire handler (can be null, if custom fire handler is desired)
     */
    public static void addArrowToRegistry(Item itemId, int itemMetadata, Class<? extends EntityArrow> entityArrow){
        ItemStack stack = new ItemStack(itemId, 1, itemMetadata);
        addArrowToRegistry(stack, entityArrow);
    }

    /**
     * NBT sensitive version, can be called through
     * FMLInterModComms.sendMessage("battlegear2", "Arrow", itemStack);
     * Will not fire from a quiver by {@link DefaultArrowFire}
     * @see #addArrowFireHandler(IArrowFireHandler) to enable the arrow firing from a quiver
     * @param stack holding the arrow
     */
    public static void addArrowToRegistry(ItemStack stack){
        addArrowToRegistry(stack, null);
    }

    /**
     * NBT sensitive version, can be called through
     * FMLInterModComms.sendMessage("battlegear2", "Arrow:"+classPath, itemStack);
     * where classPath is the full class path for the arrow class
     * will defer to {@link #addArrowToRegistry(ItemStack)} in case of error
     * @param stack holding the arrow
     * @param entityArrow the class from which the arrow entity will be constructed by {@link DefaultArrowFire} (can be null, if custom fire handler is desired)
     */
    public static void addArrowToRegistry(ItemStack stack, Class<? extends EntityArrow> entityArrow){
        ItemStack st = stack.copy();
        st.stackSize = 1;
        stackToClasses.put(st, entityArrow);
        if(entityArrow!=null)
            classToStacks.put(entityArrow, st);
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
        return handler != null && quiverSelectors.add(handler);
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
        return handler!=null && fireHandlers.add(handler);
    }

    /**
     * Search for an ItemStack whose item is an {@link IArrowContainer2}, to be used either by a compatible mainhand bow or offhand bow
     * @param entityPlayer the player being searched for an arrow container
     * @return the first non-null itemstack found through the quiver selection algorithms
     */
    public static ItemStack getArrowContainer(EntityPlayer entityPlayer) {
        ItemStack bow = entityPlayer.getCurrentEquippedItem();
        if(bow!=null){
            ItemStack temp = getArrowContainer(bow, entityPlayer);
            if(temp!=null)
                return temp;
        }
        bow = ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon();
        return bow!=null ? getArrowContainer(bow, entityPlayer) : null;
    }

    /**
     * Search for an ItemStack whose item is an {@link IArrowContainer2}
     * @param bow the "bow" in use, not necessarily a {@link ItemBow}
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
     * Search for an {@link EntityArrow} to be spawned, used by the default {@link ItemQuiver}
     * @param arrow the stack which should define the arrow as item
     * @param world
     * @param player player using a bow to fire an arrow
     * @param charge amount of charge in the bow
     * @return the first non-null entity arrow built through the firing handlers
     */
    public static EntityArrow getArrowType(ItemStack arrow, World world, EntityPlayer player, float charge){
        EntityArrow result;
        List<IArrowFireHandler> handlers = getFireHandlers(getSpecialBow(player), arrow, player);
        for (IArrowFireHandler handler : handlers) {
            if(handler.canFireArrow(arrow, world, player, charge)){
                result = handler.getFiredArrow(arrow, world, player, charge);
                if(result!=null){
                    return result;
                }
            }
        }
        return null;
    }

    //Get first available special bow
    private static ItemStack getSpecialBow(EntityPlayer player){
        ItemStack bow = player.getHeldItem();
        if (bow != null && bow.getItem() instanceof ISpecialBow) {
            return bow;
        }
        return ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
    }

    //Allows customization of fire handler list for custom bows
    public static List<IArrowFireHandler> getFireHandlers(ItemStack bow, ItemStack arrow, EntityPlayer player){
        if (bow != null && bow.getItem() instanceof ISpecialBow) {
            List<IArrowFireHandler> handlers = ((ISpecialBow) bow.getItem()).getFireHandlers(arrow, bow, player);
            if (handlers != null) {
                return handlers;
            }
        }
        return fireHandlers;
    }
    /**
     * @param stack
     * @return the EntityArrow class attached to the given stack, or null if none is found
     */
    public static Class<? extends EntityArrow> getArrowClass(ItemStack stack){
        Class<? extends EntityArrow> clazz = stackToClasses.get(stack);
        if(clazz!=null)
            return clazz;
        else
            return itemToClasses.get(stack.getItem());
    }

    /**
     * @param clazz
     * @return an ItemStack attached to the given EntityArrow class, defaults to vanilla arrow
     */
    public static ItemStack getItem(Class<? extends EntityArrow> clazz){
    	ItemStack temp = classToStacks.get(clazz);
        if(temp == null){
			return new ItemStack(Items.arrow);
		}else{
			return temp.copy();
		}
    }

    /**
     * Check if the given ItemStack has been registered
     * @param test the ItemStack to check
     * @return true if that ItemStack has been registered, with NBT or not
     */
    public static boolean isKnownArrow(ItemStack test){
    	return isKnownArrow(test,true)||isKnownArrow(test,false);
    }

    /**
     * Check if the given ItemStack has been registered
     * @param test the ItemStack to check
     * @param compareFullStack if the full ItemStack should be checked, or only its Item
     * @return true if that ItemStack has been registered
     */
    public static boolean isKnownArrow(ItemStack test, boolean compareFullStack){
        return test!=null && (compareFullStack?stackToClasses.containsKey(test):itemToClasses.containsKey(test.getItem()));
    }

    /**
     * Check if the given ItemStack has been registered
     * @param test the ItemStack to check
     * @param senses defines the meaningful ItemStack differences for the identification
     * @return true if a known similarity has been found
     */
    public static boolean isKnownArrow(ItemStack test, Iterable<ISensible<ItemStack>> senses){
        Predicate<ItemStack> predicate = new ISensible.Filter<ItemStack>(test, senses);
        return Iterators.any(stackToClasses.keySet().iterator(), predicate);
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

                int idDiff = stack.getItemDamage() - stack2.getItemDamage();
                if(idDiff != 0){
                    return idDiff;
                }else{
                	idDiff = Item.getIdFromItem(stack.getItem())-Item.getIdFromItem(stack2.getItem());
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
     * from registered class, with the (World, EntityLivingBase, float) constructor
     * If the arrow is unknown, the registered class is null or the constructor isn't valid, defers to other firing handlers silently
     */
    public static class DefaultArrowFire implements IArrowFireHandler {

        @Override
        public boolean canFireArrow(ItemStack arrow, World world, EntityPlayer player, float charge) {
            return isKnownArrow(arrow);
        }

        @Override
        public EntityArrow getFiredArrow(ItemStack arrow, World world, EntityPlayer player, float charge) {
            Class<? extends EntityArrow> clazz = getArrowClass(arrow);
            if(clazz != null){
                try {
                    return clazz.getConstructor(World.class, EntityLivingBase.class, Float.TYPE)
                            .newInstance(player.worldObj, player, charge);
                } catch (Exception ignored) {
                }
            }
            return null;
        }
    }
}
