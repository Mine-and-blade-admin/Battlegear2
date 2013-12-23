package mods.battlegear2.api.quiver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class QuiverArrowRegistry {

    private static Map<ItemStack, Class<? extends EntityArrow>> itemToClasses = new TreeMap<ItemStack, Class<? extends EntityArrow>>(new StackComparator());
    private static Map<Class<? extends EntityArrow>, ItemStack> classToItems = new HashMap<Class<? extends EntityArrow>, ItemStack>();
    
    public static void addArrowToRegistry(int itemId, int itemMetadata, Class<? extends EntityArrow> entityArrow){
        ItemStack stack = new ItemStack(itemId, 1, itemMetadata);
        addArrowToRegistry(stack, entityArrow);
    }
    
    public static void addArrowToRegistry(ItemStack stack, Class<? extends EntityArrow> entityArrow){
        ItemStack st = stack.copy();
        st.stackSize = 1;
        itemToClasses.put(st, entityArrow);
        classToItems.put(entityArrow, st);
    }

    public static Class<? extends EntityArrow> getArrowClass(ItemStack stack){
        return itemToClasses.get(stack);
    }
    
    public static ItemStack getItem(Class<? extends EntityArrow> clazz){
    	ItemStack temp = classToItems.get(clazz);
        if(temp == null){
			return new ItemStack(Item.arrow);
		}else{
			return temp.copy();
		}
    }
    
    public static boolean isKnownArrow(ItemStack test){
    	return (itemToClasses.containsKey(test));
    }

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
}
